package xyz.fluxinc.noxmonitoring.adapters;

import xyz.fluxinc.noxmonitoring.corba.LocalControlServerPOA;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;
import xyz.fluxinc.noxmonitoring.corba.MonitorStation;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;
import xyz.fluxinc.noxmonitoring.implementations.ControlServer;

public class LocalControlServer extends LocalControlServerPOA {

    private ControlServer server;

    public LocalControlServer(ControlServer server) {
        this.server = server;
    }

    @Override
    public void register() {

    }

    @Override
    public void deregister() {

    }

    @Override
    public MonitorStation[] get_available_stations() {
        return new MonitorStation[0];
    }

    @Override
    public void report_value(MonitorStation station, MonitorType type, double sensor_value) {

    }

    @Override
    public LogEntry[] get_logs() {
        return new LogEntry[0];
    }
}
