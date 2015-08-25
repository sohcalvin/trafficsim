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
	
	private Range distanceOfVehicleAhead(){
	    Position nextPosition = this;
	    int v =0;
	    int endType = Position.Range.END_CLEAR;
	    while(v < visibilityDistance ){
			nextPosition = nextPosition.next();
			if(nextPosition == null){
				endType = Position.Range.END_NULL;
				break;
			}
			if(nextPosition.getVehicle() != null){
				endType = Position.Range.END_VEHICLE;
				break;
			}
			v++;
	    }
	    //if(v > 0) nextPosition = nextPosition.prior();
	    return new Position.Range(v,endType);
	}
	private Range distanceOfVehicleBehind(){
	    Position nextPosition = this;
	    int v =0;
	    int endType = Position.Range.END_CLEAR;
	    while(v > -visibilityDistance ){
			nextPosition = nextPosition.prior();
			if(nextPosition == null){
				endType = Position.Range.END_NULL;
				break;
			}
			if( nextPosition.getVehicle() != null) {
				endType = Position.Range.END_VEHICLE;
				break;
			}
			v--;
	    }
	   // if(v < 0) nextPosition = nextPosition.next();
	    return new Position.Range(-v,endType);
	}
	private Position roll(int distance){
		Position finalPosition = null;
		for(int i = 0; i < distance; i++){
			
		}
		return finalPosition;
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
	    
	    Range rangeAhead = this.distanceOfVehicleAhead();
	    int distanceAhead = rangeAhead.getDistance();
	    int endType = rangeAhead.getEndType();
	    switch (endType) {
	    	case Position.Range.END_VEHICLE :
	    		int maxForwardDistance = distanceAhead - tailgateDistance;
	    		if(requestedDistance <= maxForwardDistance ){
	    			
	    		}else{
	    			
	    		}
	    		break;
	    	
	    	default :
	    		break;
	    }
	    
	    
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
	

	
	private class Range{
		public static final int END_CLEAR = -1;   // Clear position after this range
		public static final int END_NULL = -2;     // End of road(null position) after this range
		public static final int END_VEHICLE = -3; // Vehicle exisit right after this range
			
		private int distance;
		private int endType;
	
		public Range(int dist, int end_type){
			this.distance = dist;
			this.endType = end_type;
			
		}
		public String toString(){
			return distance + "(" + endType + ")";
		}
		public int getDistance() {
			return distance;
		}
		public int getEndType() {
			return endType;
		}
	}
	
	
	

}
