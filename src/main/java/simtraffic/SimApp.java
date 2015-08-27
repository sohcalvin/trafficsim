package simtraffic;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
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
import simtraffic.models.Repository;
import simtraffic.models.RepositoryMongodb;
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
	public static void main(String[] args) throws InterruptedException, ConfigurationException, RunningException,FileNotFoundException {
			
		RoadNetwork roadNetwork = RoadNetwork.getInstance();
		Route route = roadNetwork.makeRoute(new int[]{1,2});
		
		// Instantiates and queues vehicles to enter routes
		addVehicles(1,Behaviour.RELAX, route);
		//addVehicles(2,Behaviour.NORMAL, route);
		addVehicles(1,Behaviour.RUSH, route);
		addVehicles(1,Behaviour.RELAX, route);
		addVehicles(1,Behaviour.RUSH, route);
				
		int timeLoop = 60;  // time loops
		for(int t =0 ; t < timeLoop ; t++){
		    	ArrayList<Segment> segments =  route.getSegments();
			int maxIdx = segments.size()-1;
			for(int s=maxIdx; s>=0; s--){
				segments.get(s).moveVehicles(t);
			}
			System.out.println("After loop " + t);
			System.out.println(route);
		}
		
		PrintStream ps = new PrintStream( new File("./src/main/server/www-root/resources/simit.json"));
		writeOut(ps);
		
	//	writeToMongo(allVehicles);
	//	generateSimData();
			
	}
	private static void writeOut(PrintStream out){
		out.println("[");
		int num = allVehicles.size();
		int tFrom =0;
		int tTo = 60;
		for(Vehicle v : allVehicles){
			v.print(out,tFrom, tTo);
			if(--num > 0) out.println(",");
			else out.println("");
		}
		out.println("]");
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
	    	Repository repo = new RepositoryMongodb("localhost", 27017,"simdata","vehicle");
	    	repo.open().drop();
		try {
			for (Vehicle v : vehicles) {
				repo.writeVehicle(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			repo.close();
		}
	}
	private static void generateSimData(){
	    Repository repo = new RepositoryMongodb("localhost", 27017,"simdata","vehicle");
	    repo.open();
	    repo.generateJson(null);
	    repo.close();
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
