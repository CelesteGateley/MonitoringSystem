package xyz.fluxinc.noxmonitoring.implementations;

import xyz.fluxinc.noxmonitoring.corba.MonitorType;

import java.util.HashMap;
import java.util.Map;

public class Station {

    private Map<MonitorType, Double> sensors;

    public Station() {
        sensors = new HashMap<>();
        sensors.put(MonitorType.nitrous_oxide, 1.0D);
    }

    public double getValue(MonitorType sensor) {
        return sensors.getOrDefault(sensor, 0.0D);
    }

    public void setValue(MonitorType sensor, double value) {
        sensors.replace(sensor, value);
    }

    public MonitorType[] getSensors() {
        return (MonitorType[]) sensors.keySet().toArray();
    }
}
