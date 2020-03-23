package xyz.fluxinc.noxmonitoring.sensors;

public class NoxSensor extends Sensor {

    private boolean isDefault = true;
    private double value;

    public NoxSensor() {}

    @Override
    public double getValue() {
        if (isDefault) {
            return -1;
        }
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
        isDefault = false;
    }
}
