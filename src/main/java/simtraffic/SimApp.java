package simtraffic;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import simtraffic.builders.VehicleFactory;
import simtraffic.models.Behaviour;
import simtraffic.models.ConfigurationException;
import simtraffic.models.Position;
import simtraffic.models.RoadNetwork;
import simtraffic.models.Route;
import simtraffic.models.RunningException;
import simtraffic.models.Segment;
import simtraffic.models.Vehicle;

public class SimApp {
    	private static ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();
	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws ConfigurationException
	 * @throws RunningException
	 */
	/**
	 * @param argsj
	 * @throws InterruptedException
	 * @throws ConfigurationException
	 * @throws RunningException
	 */
	public static void main(String[] args) throws InterruptedException, ConfigurationException, RunningException {
			
		RoadNetwork roadNetwork = RoadNetwork.getInstance();
		Route route = roadNetwork.makeRoute(new int[]{1,2});
		
		// Instantiates and queues vehicles to enter routes
		addVehicles(10,Behaviour.RELAX, route);
		addVehicles(10,Behaviour.NORMAL, route);
		addVehicles(10,Behaviour.RUSH, route);
				
		int timeLoop = 200;  // time loops
		for(int t =0 ; t < timeLoop ; t++){
			ArrayList<Segment> segments =  route.getSegments();
			int maxIdx = segments.size()-1;
			for(int s=maxIdx; s>=0; s--){
				segments.get(s).moveVehicles(t);
			}
			System.out.println("After loop " + t);
			System.out.println(route);
		}
		
		StringBuffer data = new StringBuffer();
		int last = allVehicles.size();
		
		
		for(Vehicle v : allVehicles){
			data.append(v.toStringJourney());
			if(--last > 0) data.append(",\n");
		}
		
		System.out.println("[\n" + data.toString() + "\n]");
		writeToMongo(allVehicles);
	}
	private static void addVehicles(int number, Behaviour behaviour, Route route) throws ConfigurationException{
	    VehicleFactory vehicleFactory = VehicleFactory.getInstance();
	    for (int i = 0; i < number; i++) {
		Vehicle v = vehicleFactory.makeVehicle(behaviour);
		v.setRoute(route);
		allVehicles.add(v);
	    }
	}
	private static void writeToMongo(ArrayList<Vehicle> vehicles){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase db = mongoClient.getDatabase("simdata");
		MongoCollection<Document> collection = db.getCollection("vehicle");
		try{
        		for(Vehicle v : vehicles){
        			Position p = v.getPosition();
        			Document document = new Document("vid",v.getId())
        			.append("y", p.getRowCoord())
        			.append("x", p.getColumnCoord())
        			.append("segid", p.getSegment().getId());
        			
        			collection.insertOne(document);
        		}
		}catch(Exception e){
		    e.printStackTrace();
		}finally{
		    mongoClient.close();
		}
		
		
	}
	
//    private static void notused() {
//	 ExecutorService es = Executors.newCachedThreadPool();
//	 int vehNum = 1000;
//	 Vehicle[] vehicles = new Vehicle[1000];
//	 for(int i=0; i<vehNum; i++){
//	 Vehicle v = vehicleFactory.makeVehicle();
//	 v.setRoute(route);
//	 vehicles[i] = v;
//	 }
//	
//	 for(int t=0; t <1000 ; t++){
//	 for(int i=0; i < vehNum; i++){
//	 vehicles[i].moveForward();
//	 }
//	
//	 }
//    }

}
