package xyz.fluxinc.noxmonitoring.sensors;

import xyz.fluxinc.noxmonitoring.corba.IllegalSensorAccessException;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;

public abstract class Sensor {

    private boolean isEnabled;

    public abstract double getValue() throws IllegalSensorAccessException;
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public void enable() {
        isEnabled = true;
    }
    
    public void disable() {
        isEnabled = false;
    }

    public abstract void setValue(double value) throws IllegalSensorAccessException;

    public static double getMaximumValue(MonitorType type) {
        if (type == MonitorType.nitrous_oxide) {
            return 100;
        }
        return 1;
    }
}
