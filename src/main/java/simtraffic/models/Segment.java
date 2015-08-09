package simtraffic.models;

import java.util.LinkedList;

public class Segment {
	public static final int POS_ACCEPT = 0;
	public static final int POS_DENY = 1;
	public static final int POS_ERROR_HEAD = 2;
	public static final int POS_ERROR_TAIL = 3;
	
	public static final int AJOIN_NOT  = 0;
	public static final int AJOIN_SAME = 1;
	public static final int AJOIN_HEAD = 2;
	public static final int AJOIN_TAIL = 3;
	
	
	private int id;
	private int maxRowIndex;
	private int maxColumnIndex;
	private int xCoord;
	public int getxCoord() {
		return xCoord;
	}
	public int getyCoord() {
		return yCoord;
	}
	private int yCoord;
	
	private Segment segmentBeforeThis = null;
	private Segment segmentAfterThis = null;
	private LinkedList<Vehicle> vehiclesQueuingToEnter = new LinkedList<Vehicle>();
	private Vehicle[][] segmentGrid = null;
	 
	
	private Vehicle[][] getSegmentGrid() {
		return segmentGrid;
	}
	public Segment getSegmentAfterThis() {
		return segmentAfterThis;
	}
		
	public void setSegmentBeforeThis(Segment seg){
		segmentBeforeThis = seg;
	}
	public void setSegmentAfterThis(Segment seg){
		segmentAfterThis = seg;
	}
	
	
	public Segment(int id, int rows, int columns, int xCoord, int yCoord){
		this.id = id;
		this.maxRowIndex = rows-1;
		this.maxColumnIndex = columns-1;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		segmentGrid = new Vehicle[rows][columns];
		
	}
	public int getId(){ return id;}
	public int getMaxRowIndex() { return maxRowIndex ; }
	public int getMaxColumnIndex() { return maxColumnIndex ; }
	private Vehicle vehicleBehind(int row, int column){ 
		Vehicle vehicleBehind = null;
		if(column == 0){ 		// at start of segment so got to check segmentBeforeThis
			if(segmentBeforeThis != null){  // No need to recurse further back to other segments 
											// because we assume segments is long enough that it doesn't matter 
				vehicleBehind = segmentBeforeThis.vehicleBehind(row, segmentBeforeThis.maxColumnIndex+1); // Added 1 to maxColumnIndex bec it's subtrated later
			}
		}else{
			int columnStart = column-1;
			for(int c=columnStart; c>=0; c--){
				Vehicle v = segmentGrid[row][c];
				if(v != null){
					vehicleBehind =  v;
					break;
				}
			}
		}
		return vehicleBehind;
	}
	public void queueVehicle(Vehicle v){
		v.setPosition(new Position(this,-1,-1,-1));
		vehiclesQueuingToEnter.add(v);
	}
	private synchronized boolean enterLane(Vehicle vehicleEntering, int timeLoopNumber) throws RunningException{
		return enterLane(vehicleEntering, 0,0, timeLoopNumber);
	}

	private synchronized boolean enterLane(Vehicle vehicleEntering, int entryRow, int entryColumn, int timeLoopNumber) throws RunningException{
		
		if(segmentGrid[entryRow][entryColumn] != null) return false; // already occupied
		
		Position entryPosition = new Position(this, entryRow,entryColumn, timeLoopNumber);
		Vehicle vehicleBehind = vehicleBehind(entryRow,entryColumn);
		boolean allowEntry = false;
		if(vehicleBehind == null) allowEntry = true;
		else{
			if(vehicleBehind.isSafeToCutIn(entryPosition))
			allowEntry = true;
		}
		
		if(allowEntry){
			vehicleEntering.setPosition(entryPosition);
			segmentGrid[entryRow][entryColumn] = vehicleEntering;
		}
		
		return allowEntry;
	}
	private void moveInLane(Vehicle v, int timeLoopNumber){
		Position pCurrent = v.getPosition();
		Position pNext = pCurrent.next(timeLoopNumber);
			
		if(pNext != null){
			if(pNext.getVehicle() == null  && pNext.distanceOfNextVehicleAhead()>4 ){
				v.setPosition(pNext);
				Segment segmentAfterThis = pNext.getSegment();
				segmentAfterThis.setVehicleAt(v,pNext);
				this.setVehicleAt(null, pCurrent);
			}else{
				v.setPosition(pCurrent);
				System.out.println("TODO : segmentAfterThis is null from position " + pCurrent);
			}
		}else{
			segmentGrid[pCurrent.getRowCoord()][ pCurrent.getColumnCoord()] = null;
			System.out.println("Vehicle " +v+ " journey finished. Next position is null from position " + pCurrent);
			
		}
		
		
	}
	private void setVehicleAt(Vehicle v, Position p){
	    segmentGrid[p.getRowCoord()][ p.getColumnCoord()] = v;
	}
	
	
	
	public void moveVehicles(int timeLoopNumber) throws RunningException{
		for(int c =maxColumnIndex; c>=0; c--){ // Front first
			for(int r= maxRowIndex;r>=0; r--){ // Fastest lane first
				Vehicle v = segmentGrid[r][c];
				if(v != null){
					moveInLane(v,timeLoopNumber);
				}
			}
		}
		moveNextVehicleFromQueue(timeLoopNumber);
	}

	private void moveNextVehicleFromQueue(int timeLoopNumber) throws RunningException{
		
		
		Vehicle v = vehiclesQueuingToEnter.pollFirst();
		//System.out.println(">>>>>v:" +v);
		if(v != null ){
			if( !enterLane(v, timeLoopNumber)){
				vehiclesQueuingToEnter.addFirst(v);
			}
		}
		for(Vehicle v2 : vehiclesQueuingToEnter){
			//System.out.println(">>>>>v2:" +v2);
			v2.setPosition(new Position(this,0,0,timeLoopNumber));;
		}
	
	}
	
	
	public boolean withinSegment(int rowIndex, int columnIndex){
		if( rowIndex >= 0  && rowIndex <= maxRowIndex && columnIndex >=0 && columnIndex <= maxColumnIndex ){
			return true;
		}
		return false;
	}
	public Vehicle getVehicleAt(int rowIndex, int columnIndex){
		return segmentGrid[rowIndex][columnIndex];
	}
	
	

	private int canPosition(int rowCoord, int columnCoord) {
		if(columnCoord > maxColumnIndex ) return POS_ERROR_TAIL;
		if(columnCoord < 0 ) return POS_ERROR_HEAD;
		
		if(segmentGrid[rowCoord][columnCoord] == null){
			return POS_ACCEPT;
		}else{
			return POS_DENY;
		}
		
		
	}
	public boolean isSameSegment(Segment seg){
		return (id == seg.getId());
	}
	public int ajoining(Segment seg){
		if(isSameSegment(seg)) return AJOIN_SAME;
		if(segmentBeforeThis != null && segmentBeforeThis.isSameSegment(seg)) return AJOIN_HEAD;
		if(segmentAfterThis != null && segmentAfterThis.isSameSegment(seg)) return AJOIN_TAIL;
		return AJOIN_NOT;
	}
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(int r=0; r <= maxRowIndex ; r++){
			for(int c=0; c <= maxColumnIndex ; c++){
				buf.append(segmentGrid[r][c]);
			}
			buf.append("\n");
			
		}
		return buf.toString();
		
	} 
	
	

}
