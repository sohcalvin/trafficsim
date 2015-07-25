package simtraffic.models;

public class Position {
	
	Segment segment = null;
	int rowInSegment = -1;
	int columnInSegment = -1;
	
	public String toString(){
		if(segment == null) return "NullSeg";
		return new StringBuffer().append("S:" + segment.getId())
				.append(",")
				.append( rowInSegment )
				.append(",")
				.append( columnInSegment )
				.toString();
		
	}
	public Position(){
	}
	public Position(Segment seg, int rowInSegment, int columnInSegment){
		set(seg,rowInSegment, columnInSegment);
	}
	
	public void set(Segment seg, int rowInSegment, int columnInSegment){
		this.segment = seg;
		this.rowInSegment = rowInSegment;
		this.columnInSegment = columnInSegment;
	}
	public void set(int rowInSegment, int columnInSegment) throws RunningException{
		if(segment == null) throw new RunningException("Attempting to set row/column when segment is null");
		this.rowInSegment = rowInSegment;
		this.columnInSegment = columnInSegment;
		
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
	public Position next(){
		int newColumnInSegment = columnInSegment + 1;
		if(segment.withinSegment(rowInSegment, newColumnInSegment)){
			return new Position(segment, rowInSegment, newColumnInSegment);
		}else{
			return null; // TO DO
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
