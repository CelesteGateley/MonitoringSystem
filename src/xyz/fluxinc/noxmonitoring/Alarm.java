package xyz.fluxinc.noxmonitoring;

import xyz.fluxinc.noxmonitoring.corba.MonitorType;

import java.time.Instant;

public class Alarm {

    private long timestamp;
    private String location;
    private MonitorType type;
    private double value;

    public Alarm(String location, MonitorType type, double value) {
        timestamp = Instant.now().getEpochSecond();
        this.location = location;
        this.type = type;
        this.value = value;
    }

    public long getTimestamp() { return timestamp; }

    public MonitorType getType() { return type; }

    public double getValue() { return value; }

    public String getLocation() { return location; }
}
