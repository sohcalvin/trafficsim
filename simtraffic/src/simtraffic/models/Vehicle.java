package simtraffic.models;

import java.util.ArrayList;

public class Vehicle implements Runnable {
	private long id;
	private int safeDistance = 5; // Should change to configurable for each vehicle
	private Operator operator = null;
	private Route route = null;
	private Position position = null;
	private ArrayList<Position> journey = new ArrayList<Position>();

	public Vehicle(long id, Operator operator) {
		this.id = id;
		this.operator = operator;
	}

	@Override
	public void run() {
//		try {
//			while (!route.enter(this)) {
//				Thread.sleep(1000);
//			} 
//			System.out.println("Vehicle " + this.id + " entered route");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}catch(RunningException re){
//			re.printStackTrace();
//		}

	}

	
	public Position getPosition(){
		return position;
	}
	public void setPosition(Position pos){
		position = pos;
		journey.add(new Position(pos.getSegment(), pos.getRowCoord(), pos.getColumnCoord(),pos.getLoopTimeCount()));

	}
	public void setRoute(Route route) throws ConfigurationException {
		this.route = route;
		Segment firstSeg = route.getFirstSegment();
//		position.set(firstSeg, -1, -1);
		firstSeg.queueVehicle(this);
	}
	public boolean isSafeToCutIn(Position positionAhead) throws RunningException{
		
		if(position.getRowCoord() != positionAhead.getRowCoord()){ // Not in same row, so it's ok
			return true;
		}
		
		int distanceApart = position.distanceApart(positionAhead);
		if(distanceApart > safeDistance){
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
	//public ArrayList<Position>  getJourney(){ return journey; }
	

}
