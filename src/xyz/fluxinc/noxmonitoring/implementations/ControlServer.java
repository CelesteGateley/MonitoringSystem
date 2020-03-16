package xyz.fluxinc.noxmonitoring.implementations;

import xyz.fluxinc.noxmonitoring.corba.LogEntry;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;
import xyz.fluxinc.noxmonitoring.poas.MonitorStation;

import java.util.ArrayList;
import java.util.List;

public class ControlServer {

    private List<MonitorStation> stations;
    private List<LogEntry> logs;
    private static final double MAXIMUM_ALLOWED_NOX_LEVEL = 20;

    public ControlServer() {
        stations = new ArrayList<>();
        logs = new ArrayList<>();
    }

    public void register(MonitorStation station) {
        stations.add(station);
    }

    public void deregister(MonitorStation station) {
        stations.remove(station);
    }

    public LogEntry[] getLogs() {
        return (LogEntry[]) logs.toArray();
    }

    public void logReport(String location, MonitorType type, double value) {
        LogEntry entry = new LogEntry(location, type, value);
        logs.add(entry);
        if (value >= MAXIMUM_ALLOWED_NOX_LEVEL) {
            // TODO: Send to HQ
        }
    }
}
