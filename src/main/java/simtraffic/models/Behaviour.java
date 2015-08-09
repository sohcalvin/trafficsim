package simtraffic.models;

public enum Behaviour {
    RELAX(12, 10, 8),
    NORMAL(15, 8, 6),
    RUSH(20, 6, 4);
   
    
    private int preferredSpeed;
    private int tailgateDistance;
    private int cutinDistance;
    
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
