package simtraffic.models;

public class Position {
       
	private final int visibilityDistance = 30;
	private Segment segment = null;
	private int rowInSegment = -1;
	private int columnInSegment = -1;
	
	public Position(Segment seg, int rowInSegment, int columnInSegment){
		segment = seg;
		this.rowInSegment = rowInSegment;
		this.columnInSegment = columnInSegment;
	}
	
	public String toString(){
		if(segment == null) return "NullSeg";
		int absoluteX = columnInSegment + segment.getXCoord();
		int absoluteY = rowInSegment+ segment.getYCoord();
		
		return new StringBuffer().append("{\"x\":")
				.append( absoluteX )
				.append(",\"y\":")
				.append( absoluteY )
				.append("}")
				.toString();
		
	}



	public Segment getSegment(){
		return segment;
	}
	public int getRowCoord(){ 
		return rowInSegment; 
	}
	public int getColumnCoord(){ 
		return columnInSegment; 
	}
	public int getAbsoluteX(){
	    return columnInSegment + segment.getXCoord();
	}
	public int getAbsoluteY(){
	    return rowInSegment+ segment.getYCoord();
	}
	public int distanceApart(Position p) throws RunningException{ // Doesn't care if it's different row
		int ajoinCheck = segment.ajoining(p.getSegment());
		switch(ajoinCheck){
		case Segment.AJOIN_NOT :
			throw new RunningException("Position distanceApart check called for non-ajoining segments");
		case Segment.AJOIN_SAME :
			return Math.abs(getColumnCoord() - p.getColumnCoord());
		case Segment.AJOIN_TAIL :
			int thisPositionToEndOfSegment = segment.getMaxColumnIndex() - getColumnCoord();
			int pPositionToStartOfSegment = p.getColumnCoord();
			return thisPositionToEndOfSegment + pPositionToStartOfSegment;
		case Segment.AJOIN_HEAD :	
			int pPositionToEndOfSegment = p.getSegment().getMaxColumnIndex() - p.getColumnCoord();
			int thisPositionToStartOfSegment = getColumnCoord();
			return pPositionToEndOfSegment + thisPositionToStartOfSegment;
		default :
			throw new RunningException("Position distanceApart check unknown condition");
						
		}
		
	
	
	}
	// null if no more position
	public Position next(){
		int newColumnInSegment = columnInSegment + 1;
		if(segment.withinSegment(rowInSegment, newColumnInSegment)){
			return new Position(segment, rowInSegment, newColumnInSegment);
		}else{
			Segment segmentAfterThis = segment.getSegmentAfterThis();
			if(segmentAfterThis != null){
				return new Position(segmentAfterThis, rowInSegment, 0);
			}else{
				return null;
			}
		}
	}
	// null if no more position
	public Position prior(){
		int newColumnInSegment = columnInSegment - 1;
		if(segment.withinSegment(rowInSegment, newColumnInSegment)){
			return new Position(segment, rowInSegment, newColumnInSegment);
		}else{
			Segment segmentBeforeThis = segment.getSegmentBeforeThis();
			if(segmentBeforeThis != null){
				return new Position(segmentBeforeThis, rowInSegment, segment.getMaxColumnIndex());
			}else{
				return null;
			}
		}
	}
	
	private int distanceOfVehicleAhead(){
	    Position nextPosition = this;
	    int v =0;
	    while(v < visibilityDistance ){
		nextPosition = nextPosition.next();
		if(nextPosition == null) return visibilityDistance;
		if(nextPosition.getVehicle() != null) break;
		v++;
	    }
	    return v;
	}
	private int distanceOfVehicleBehind(){
	    Position nextPosition = this;
	    int v =0;
	    while(v > -visibilityDistance ){
		nextPosition = nextPosition.prior();
		if(nextPosition == null) return visibilityDistance;
		if( nextPosition.getVehicle() != null) break;
		v--;
	    }
	    return -v;
	}
	
	public void audit(){
	    System.out.println("Current : " + this);
	    System.out.println("Ahead : " + distanceOfVehicleAhead());
	    System.out.println("Behind : " + distanceOfVehicleBehind());
	    
	}
	
	// null if no more position
	public Position nextFurthestPositionAhead(int requestedDistance, int tailgateDistance){
	    int i =0;
	    Position furthestEmptyPosition = null;
	    Position nextPosition = this;
	    
	    while(true){
		if(i++ > visibilityDistance) break;
		
		nextPosition = nextPosition.next(); 
		if(nextPosition == null) break;
		if( nextPosition.getVehicle() != null) {
		    for(int j=0; j <tailgateDistance; j++){ // adjust for tailgate
			nextPosition = nextPosition.prior(); 
		    }
		    furthestEmptyPosition = nextPosition;
		    break;
		}
		furthestEmptyPosition = nextPosition;
		if(i > requestedDistance) break;
		
	    }
	    return furthestEmptyPosition;
	}
	public Position nextOptimumPosition(int requestedDistance, int tailgateDistance)throws RunningException{
	    Position current = this;
	    Position currentLaneNextPosition = current.nextFurthestPositionAhead(requestedDistance, tailgateDistance);
	    if(currentLaneNextPosition == null ) return null;
	    if(currentLaneNextPosition.distanceApart(current) < requestedDistance){
		    //Position anotherLane = current.getSegment().
	    }
	    return currentLaneNextPosition;
		
	    
	    
	}
	
	
	
	
	private Position reverse(int distance){
	    Position priorPosition = this;
	    int i =0;
	    while(true){
		if(i++ > visibilityDistance) break;
		priorPosition = priorPosition.prior();
		if(priorPosition == null  || priorPosition.getVehicle() != null) {
		    break;
		}
				
	    }
	    return priorPosition;
	    
	}
	
	public Vehicle getVehicle(){
		return segment.getVehicleAt(rowInSegment, columnInSegment); 
	}
	

	
	
	
	
	

}
