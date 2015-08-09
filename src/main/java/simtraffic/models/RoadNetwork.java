package simtraffic.models;

import java.util.HashMap;

import simtraffic.builders.VehicleFactory;

public class RoadNetwork {
    public final static int UNIT_DIST = 5; // real world metres
    public final static int UNIT_TIME = 5; // real world seconds

    HashMap<Integer, Segment> segments = new HashMap<Integer, Segment>();

    public RoadNetwork() {
    }

    private void init() {
	// Initializing road segments
	int segLength1 = 50;
	int segLength2 = 50;
	Segment seg1 = new Segment(1, 2, segLength1, 1, 20);
	Segment seg2 = new Segment(2, 3, segLength2, 1 + segLength1, 20);

	seg2.setSegmentBeforeThis(seg1); // seg1-->seg2
	seg1.setSegmentAfterThis(seg2);

	segments.put(1, seg1);
	segments.put(2, seg2);

    }

    private static RoadNetwork instance = null;

    public static RoadNetwork getInstance() {
	if (instance == null) {
	    synchronized (RoadNetwork.class) {
		if (instance == null) {
		    instance = new RoadNetwork();
		    instance.init();

		}
	    }
	}
	return instance;
    }

    public Route makeRoute(int[] segmentids) {
	int len = segmentids.length;
	Route route = new Route();
	for (int i = 0; i < len; i++) {
	    route.addSegment(segments.get(segmentids[i]));
	}
	return route;
    }

}
