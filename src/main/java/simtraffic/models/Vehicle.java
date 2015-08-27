package simtraffic.models;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bson.Document;

public class Vehicle {
	private long id;
	private Behaviour behaviour = null;
	
	private Route route = null;
	private Position position = null;
	private TreeMap<Integer,Position> journey = new TreeMap<Integer,Position>();

	
	
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
	public SortedMap<Integer, Position> getJourney() {
	    return journey;
	}
	public Behaviour getBehaviour() {
	    return behaviour;
	}
	public void setPosition(Position pos, int timeCount){
	    	Position currentPosition = getPosition();
	    	if(currentPosition != null){
	    	    Segment currentSegment = currentPosition.getSegment();
	    	    if(currentSegment.withinSegment(currentPosition.getRowCoord(), currentPosition.getColumnCoord())){
	    		currentSegment.setVehicleAt(null, currentPosition);
	    	    }
	    	}
	    	
		this.position = pos;
		if(pos.getRowCoord()>= 0 && pos.getColumnCoord() >= 0){
		    this.position.getSegment().setVehicleAt(this, this.position);
		}
		//this.journey.add(new Position(pos.getSegment(), pos.getRowCoord(), pos.getColumnCoord()));
		this.journey.put(timeCount,new Position(pos.getSegment(), pos.getRowCoord(), pos.getColumnCoord()));
		//this.timeCount.add(timeCount);
		

	}
	public Position getNextPreferredPosition(){
	    if(position == null){
	    	return null;
	    }
	    Position availablePosition =  position.nextFurthestPositionAhead(behaviour);
	    return availablePosition;
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
		for(Map.Entry<Integer, Position> e : journey.entrySet()){
		    Integer t = e.getKey();
		    Position p = e.getValue();
		    b.append(p.toString());
		    if(--last > 0 ) b.append(",");
		}
		
		b.append("]");
		return b.toString();
		
		
	}
	public void print(PrintStream out, int tFrom, int tTo){

		Position prevPosition = null;
		for(int i= tFrom ; i <= tTo ; i++){
				if(i==tFrom) out.print("[");
				Position p = journey.get(i);
				if(p == null){
					if(prevPosition == null){
						Map.Entry<Integer, Position> ceil = journey.ceilingEntry(i);
						p=ceil.getValue();
					}else{
						p = prevPosition;
					}
				}else{
					prevPosition = p;
				}
				out.print(p);
				if(i < tTo) out.print(",");
				else out.print("]");
				
		}
					
	}
	


}
