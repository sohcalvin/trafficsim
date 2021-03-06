package simtraffic.models;

public enum Behaviour {
    RELAX(3, 5, 8),
    NORMAL(15, 4, 6),
    RUSH(20, 3, 4);
   
    
    private int preferredSpeed; 	// Base on number of car length per unit time
    private int tailgateDistance; 	// Base on number of car length
    private int cutinDistance;		// Base on number of car length
    
    private Behaviour(int preferredSpeed, int tailgateDistance, int cutinDistance){
	this.preferredSpeed = preferredSpeed;
	this.tailgateDistance = tailgateDistance;
	this.cutinDistance = cutinDistance;
	
    }
    public int getPreferredSpeed() {
        return preferredSpeed;
    }

    public int getTailgateDistance() {
        return tailgateDistance;
    }

    public int getCutinDistance() {
        return cutinDistance;
    }
  

}
