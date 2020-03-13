package xyz.fluxinc.PortableObjectAdapters;

import xyz.fluxinc.Implementations.Station;
import xyz.fluxinc.MonitoringSystem.MonitorStationPOA;
import xyz.fluxinc.MonitoringSystem.MonitorType;

public class MonitorStation extends MonitorStationPOA {

    private Station station;

    private MonitorStation() {
        station = new Station();
    }

    @Override
    public MonitorType[] get_available_sensors() {
        return station.getSensors();
    }

    @Override
    public double get_sensor_value(MonitorType sensor) {
        return station.getValue(sensor);
    }

    @Override
    public void enable_sensor(MonitorType sensor) {
    }

    @Override
    public void enable_station() {

    }

    @Override
    public void disable_sensor(MonitorType sensor) {

    }

    @Override
    public void disable_station() {

    }
}
