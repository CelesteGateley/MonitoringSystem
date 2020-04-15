package xyz.fluxinc.noxmonitoring.adapters;

import xyz.fluxinc.noxmonitoring.corba.IllegalSensorAccessException;
import xyz.fluxinc.noxmonitoring.corba.IllegalStationAccessException;
import xyz.fluxinc.noxmonitoring.corba.LocalControlServer;
import xyz.fluxinc.noxmonitoring.corba.MonitorStationPOA;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;
import xyz.fluxinc.noxmonitoring.sensors.NoxSensor;
import xyz.fluxinc.noxmonitoring.sensors.Sensor;

import java.util.LinkedHashMap;
import java.util.Map;

public class MonitorStation extends MonitorStationPOA {

    private String location;
    private Map<MonitorType, Sensor> sensors;
    private boolean isEnabled = true;
    private LocalControlServer server;

    public MonitorStation(String location) {
        this.location = location;
        sensors = new LinkedHashMap<>();
        sensors.put(MonitorType.nitrous_oxide, new NoxSensor());
        sensors.get(MonitorType.nitrous_oxide).enable();
    }

    @Override
    public MonitorType[] get_available_sensors() throws IllegalStationAccessException {
        if (!isEnabled) {
            throw new IllegalStationAccessException("Attempt made to read value from disabled station");
        }
        return sensors.keySet().toArray(new MonitorType[0]);
    }

    @Override
    public double get_sensor_value(MonitorType sensor) throws IllegalSensorAccessException, IllegalStationAccessException {
        if (!isEnabled) {
            throw new IllegalStationAccessException("Attempt made to read value from disabled station");
        }
        if (!sensors.containsKey(sensor)) {
            throw new IllegalSensorAccessException("Attempt made to read value from unknown sensor");
        }
        if (!sensors.get(sensor).isEnabled()) {
            throw new IllegalSensorAccessException("Attempt made to read value from disabled sensor");
        }
        return sensors.get(sensor).getValue();
    }

    @Override
    public String get_location() {
        return location;
    }

    @Override
    public void enable_sensor(MonitorType sensor) throws IllegalStationAccessException, IllegalSensorAccessException {
        if (!isEnabled) {
            throw new IllegalStationAccessException("Attempt made to enable sensor on disabled station");
        }
        if (!sensors.containsKey(sensor)) {
            throw new IllegalSensorAccessException("Attempt made to enable an unknown sensor");
        }
        if (sensors.get(sensor).isEnabled()) {
            throw new IllegalSensorAccessException("Attempt made to enable already enabled sensor");
        }
        sensors.get(sensor).enable();
    }

    @Override
    public void enable_station() throws IllegalStationAccessException {
        if (isEnabled) {
            throw new IllegalStationAccessException("Attempt made to enable already enabled sensor");
        }
        isEnabled = false;
    }

    @Override
    public boolean is_enabled() {
        return isEnabled;
    }

    @Override
    public boolean is_sensor_enabled(MonitorType sensor) {
        return sensors.get(sensor).isEnabled();
    }

    @Override
    public void disable_sensor(MonitorType sensor) throws IllegalStationAccessException, IllegalSensorAccessException {
        if (!isEnabled) {
            throw new IllegalStationAccessException("Attempt made to enable sensor on disabled station");
        }
        if (!sensors.containsKey(sensor)) {
            throw new IllegalSensorAccessException("Attempt made to disable unknown sensor");
        }
        if (!sensors.get(sensor).isEnabled()) {
            throw new IllegalSensorAccessException("Attempt made to disable already disabled sensor");
        }
        sensors.get(sensor).disable();
    }

    @Override
    public void disable_station() throws IllegalStationAccessException {
        if (!isEnabled) {
            throw new IllegalStationAccessException("Attempt made to disable already disabled sensor");
        }
        isEnabled = true;
    }

    public void assignServer(LocalControlServer server) {
        this.server = server;
    }

    public void setSensorValue(MonitorType type, double value) throws IllegalStationAccessException, IllegalSensorAccessException {
        if (!isEnabled) {
            throw new IllegalStationAccessException("Attempt made to set sensor value on disabled station");
        }
        if (!sensors.containsKey(type)) {
            throw new IllegalSensorAccessException("Attempt made to set sensor value on unknown sensor");
        }
        if (!sensors.get(type).isEnabled()) {
            throw new IllegalSensorAccessException("Attempt made to set sensor value on disabled sensor");
        }
        sensors.get(type).setValue(value);
        if (value >= Sensor.getMaximumValue(type) && server != null) {
            server.report_value(this.location, type, value);
        }
    }

}
