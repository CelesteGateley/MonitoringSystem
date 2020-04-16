package xyz.fluxinc.noxmonitoring;

import xyz.fluxinc.noxmonitoring.corba.MonitorType;

public class Alarm {

    private final long timestamp;
    private final String location;
    private final MonitorType type;
    private final double value;

    public Alarm(String location, MonitorType type, double value) {
        timestamp = System.currentTimeMillis();
        this.location = location;
        this.type = type;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MonitorType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public String getLocation() {
        return location;
    }

    public static double getAmberAlarm(MonitorType type) {
        if (type == MonitorType.nitrous_oxide) {
            return 50.0;
        }
        return 0.0;
    }

    public static double getRedAlarm(MonitorType type) {
        if (type == MonitorType.nitrous_oxide) {
            return 100.0;
        }
        return 0.0;
    }
}
