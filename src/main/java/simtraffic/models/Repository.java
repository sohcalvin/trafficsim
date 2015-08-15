package simtraffic.models;

import java.io.File;

public interface Repository {
    
    Repository open();
    Repository drop();
    Repository close();
    void writeVehicle(Vehicle vehicle);
    void generateJson(File file);
    
    
    
}
