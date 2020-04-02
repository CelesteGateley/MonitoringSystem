package xyz.fluxinc.noxmonitoring.sensors;

import xyz.fluxinc.noxmonitoring.corba.IllegalSensorAccessException;

public class NoxSensor extends Sensor {

    private boolean isDefault = true;
    private double value;

    public NoxSensor() {}

    @Override
    public double getValue() throws IllegalSensorAccessException {
        if (isDefault) {
            return -1;
        }
        if (!this.isEnabled()) { throw new IllegalSensorAccessException("Attempted to get value of disabled sensor"); }
        return value;
    }

    @Override
    public void setValue(double value) throws IllegalSensorAccessException {
        if (!this.isEnabled()) { throw new IllegalSensorAccessException("Attempted to set value of disabled sensor"); }
        this.value = value;
        isDefault = false;
    }
}
