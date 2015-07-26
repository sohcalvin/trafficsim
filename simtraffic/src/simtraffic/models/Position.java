package simtraffic.models;

public class Position {
	
	private Segment segment = null;
	private int rowInSegment = -1;
	private int columnInSegment = -1;
	private int loopTimeCount = -1;
	
	public int getLoopTimeCount() {
		return loopTimeCount;
	}
	public String toString(){
		if(segment == null) return "NullSeg";
		return new StringBuffer().append("S:" + segment.getId())
				.append(",")
				.append( rowInSegment )
				.append(",")
				.append( columnInSegment )
				.append(",T:")
				.append( loopTimeCount )
				.toString();
		
	}
	public Position(Segment seg, int rowInSegment, int columnInSegment,int timeLoop){
		segment = seg;
		this.rowInSegment = rowInSegment;
		this.columnInSegment = columnInSegment;
		this.loopTimeCount = timeLoop;
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
	public Position next(int timeLoop){
		int newColumnInSegment = columnInSegment + 1;
		if(segment.withinSegment(rowInSegment, newColumnInSegment)){
			return new Position(segment, rowInSegment, newColumnInSegment, timeLoop);
		}else{
			Segment segmentAfterThis = segment.getSegmentAfterThis();
			if(segmentAfterThis != null){
				return new Position(segmentAfterThis, rowInSegment, 0, timeLoop);
			}else{
				return null;
			}
		}
	}
	public Vehicle getVehicle(){
		return segment.getVehicleAt(rowInSegment, columnInSegment); 
	}
	
	private boolean isActive(){
		if(segment != null && getRowCoord() >= 0 && getColumnCoord() >= 0){
			return true;
		}
		return false;
	}
	
	
	
	
	

}
