package simtraffic;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import simtraffic.builders.VehicleFactory;
import simtraffic.models.ConfigurationException;
import simtraffic.models.Position;
import simtraffic.models.RoadNetwork;
import simtraffic.models.Route;
import simtraffic.models.RunningException;
import simtraffic.models.Segment;
import simtraffic.models.Vehicle;

public class SimApp {

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
		System.out.println("Running SimApp");
		VehicleFactory vehicleFactory = VehicleFactory.getInstance();
		RoadNetwork roadNetwork = RoadNetwork.getInstance();
		Route route = roadNetwork.makeRoute(new int[]{1,2});
		
		
	//	ExecutorService es = Executors.newCachedThreadPool();
		
//		int vehNum = 1000;
//		Vehicle[] vehicles = new Vehicle[1000]; 
//		for(int i=0; i<vehNum; i++){
//			Vehicle v = vehicleFactory.makeVehicle();
//			v.setRoute(route);
//			vehicles[i] = v;
//		}
//		
//		for(int t=0; t <1000 ; t++){
//			for(int i=0; i < vehNum; i++){
//				vehicles[i].moveForward();
//			}
//			
//		}
		
		// Instantiates and queues vehicles to enter routes
		ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();
		for (int i = 0; i < 5; i++) {
			Vehicle v = vehicleFactory.makeVehicle();
			v.setRoute(route);
			allVehicles.add(v);
			//es.execute(v);
		}
		System.out.println("Empty route");
		System.out.println(route);
		
		int timeLoop = 35;  // time loops
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
		
		
		
		
		//es.shutdown();
		
	
		//es.awaitTermination(100, TimeUnit.SECONDS); 
		
	}

}
