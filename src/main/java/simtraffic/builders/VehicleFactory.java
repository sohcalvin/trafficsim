package simtraffic.builders;

import java.util.concurrent.atomic.AtomicLong;

import simtraffic.models.Behaviour;
import simtraffic.models.Route;
import simtraffic.models.Vehicle;

public class VehicleFactory {
	private AtomicLong idCounter = new AtomicLong(0);

	private VehicleFactory() {
	}

	private static VehicleFactory instance = null;

	public static VehicleFactory getInstance() {
		if (instance == null) {
			synchronized (VehicleFactory.class) {
				if (instance == null) {
					instance = new VehicleFactory();
				}
			}
		}
		return instance;
	}

	public Vehicle makeVehicle(Behaviour behaviour) {
		Vehicle veh = new Vehicle(idCounter.incrementAndGet(), behaviour);
		return veh;

	}

}
