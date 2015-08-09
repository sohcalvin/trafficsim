package simtraffic.models;

import java.util.ArrayList;

public class Vehicle {
	private long id;
	private Behaviour behaviour = null;
	private Route route = null;
	private Position position = null;
	private ArrayList<Integer> timeCount = new ArrayList<Integer>();
	private ArrayList<Position> journey = new ArrayList<Position>();

	public Vehicle(long id, Behaviour behaviour) {
		this.id = id;
		this.behaviour = behaviour;
	}
	public long getId() {
		return id;
	}
	public Position getPosition(){
		return position;
	}
	public void setPosition(Position pos){
		this.position = pos;
		this.journey.add(new Position(pos.getSegment(), pos.getRowCoord(), pos.getColumnCoord(),pos.getLoopTimeCount()));
		//this.journey.add(new Position(pos.getSegment(), pos.getRowCoord(), pos.getColumnCoord()));
		//this.timeCount.add(timeCount);

	}
	public Position getNextPreferredPosition(){
	    if(position == null){
		return null;
	    }
	    int preferredSpeed = behaviour.getPreferredSpeed();
	    int distanceOfVehicleAhead = position.distanceOfNextVehicleAhead();
	    int maxDistancePossible = distanceOfVehicleAhead - behaviour.getTailgateDistance();
	    if(preferredSpeed < maxDistancePossible){
		
	    }
	    
	    return null; 
	    
	}
	
	public void setRoute(Route route) throws ConfigurationException {
		this.route = route;
		Segment firstSeg = route.getFirstSegment();
		firstSeg.queueVehicle(this);
	}
	public boolean isSafeToCutIn(Position positionAhead) throws RunningException{
		
		if(position.getRowCoord() != positionAhead.getRowCoord()){ // Not in same row, so it's ok
			return true;
		}
		
		int distanceApart = position.distanceApart(positionAhead);
		if(distanceApart > behaviour.getTailgateDistance()){
			return true;
		}
		
		return false;
		
	}
	

	public String toString() {
		return "{ \"ID\": " + id + "}";
	}
	public String toStringJourney(){
		StringBuilder b  = new StringBuilder();
		b.append("[");
		int last = journey.size();
		for(Position p : journey){
			b.append(p.toString());
			if(--last > 0 ) b.append(",");
		
		}
		b.append("]");
		return b.toString();
		
		
	}


}
