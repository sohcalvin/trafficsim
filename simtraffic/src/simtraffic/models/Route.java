package simtraffic.models;

import java.util.ArrayList;

public class Route {
	
	ArrayList<Segment> segments = new ArrayList<Segment>();
	
	public Route(){
	
	}
	public void addSegment(Segment segment){
		segments.add(segment);
	}
	public boolean enter(Vehicle vehicle) throws RunningException{
		return segments.get(0).enterLane(vehicle);
	}
	public Segment getFirstSegment() throws ConfigurationException{
		if(segments.size() == 0) throw new ConfigurationException("Route has empty segments");
		return segments.get(0);
	}
	
	public ArrayList<Segment> getSegments(){
		return segments;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(Segment seg : segments){
			buf.append(seg.toString());
		}
		return buf.toString();
		
		
	}
	

}
