package xyz.fluxinc.noxmonitoring.sensors;

public abstract class Sensor {

    private boolean isEnabled;

    public abstract double getValue();
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public void enable() {
        isEnabled = true;
    }
    
    public void disable() {
        isEnabled = false;
    }

    public abstract void setValue(double value);
}
