package xyz.fluxinc.noxmonitoring.poas;

import xyz.fluxinc.noxmonitoring.implementations.Station;
import xyz.fluxinc.noxmonitoring.corba.MonitorStationPOA;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;

public class MonitorStation extends MonitorStationPOA {

    private Station station;
    private String location;

    private MonitorStation(String location) {
        station = new Station();
        this.location = location;
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
    public String get_location() {
        return location;
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

    public void setSensorValue(MonitorType type, double value) {
        station.setValue(type, value);
    }
}
