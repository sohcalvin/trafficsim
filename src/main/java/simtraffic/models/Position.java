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
	public int distanceOfNextVehicleAhead(){
		int i =0;
		while(true){
			if(i++ > visibilityDistance) break;
			Position nextPosition = this.next(); 
			if(nextPosition == null) continue;
			if(nextPosition.getVehicle() == null )continue;
			else break;
		}
		return i;
	}
	
	// null if no more position
	public Position nextFurthestPositionAhead(int requestedDistance){
	    int i =0;
	    
	    Position furthestEmptyPosition = null;
	    Position nextPosition = this;
	    while(true){
		if(i++ > visibilityDistance) break;
		
		nextPosition = nextPosition.next(); 
		if(nextPosition == null  || nextPosition.getVehicle() != null) {
		    break;
		}
		furthestEmptyPosition = nextPosition;
		if(i > requestedDistance) break;
		
	    }
	    
	    return furthestEmptyPosition;
	}
	
	
	public Vehicle getVehicle(){
		return segment.getVehicleAt(rowInSegment, columnInSegment); 
	}
	

	
	
	
	
	

}
