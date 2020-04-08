package xyz.fluxinc.noxmonitoring.adapters;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import xyz.fluxinc.noxmonitoring.Alarm;
import xyz.fluxinc.noxmonitoring.corba.*;
import xyz.fluxinc.noxmonitoring.corba.MonitorStation;
import xyz.fluxinc.noxmonitoring.orbmanagement.MonitorStationOrb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocalControlServer extends LocalControlServerPOA {

    private List<String> stations;
    private MonitorStationOrb orb;
    private String location;
    private CentralControl controlServer;
    private List<LogEntry> logs;
    private List<Alarm> confirmedAlarms;
    // Run Every Minute. Fine for testing purposes, but in field report every hour
    private static final long checkTime = 1000 * 60;
    private Timer timer;

    public LocalControlServer(String[] args, String location, String controlServer) {
        stations = new ArrayList<>();
        orb = new MonitorStationOrb(args);
        logs = new ArrayList<>();
        confirmedAlarms = new ArrayList<>();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                probeValues();
            }
        }, 1000, checkTime);
    }

    @Override
    public void register(String location) {
        stations.add(location);
    }

    @Override
    public void deregister(String location) {
        stations.remove(location);
    }

    @Override
    public String get_location() {
        return location;
    }

    @Override
    public MonitorStation[] get_available_stations(){
        List<MonitorStation> oStations = new ArrayList<>();
        for (String location : stations) {
            try {
                MonitorStation station = orb.getObject(location);
                if (station == null) { continue; }
                oStations.add(station);
            } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
                cannotProceed.printStackTrace();
            }
        }
        return oStations.toArray(new MonitorStation[oStations.size()]);
    }

    @Override
    public void report_value(MonitorStation station, MonitorType type, double sensor_value) {
        logs.add(new LogEntry(station.get_location(), type, sensor_value));
        // TODO: Implement proper default valuing
        // TODO: Adjust Report Value to Fit Real Life
        if (sensor_value > 100) {
            confirmedAlarms.add(new Alarm(station.get_location(), type, sensor_value));
            int alarmCount = 0;
            // Saves constantly checking the time each time, and a couple seconds margin of error on expiring alerts is acceptable
            long currentTime = Instant.now().getEpochSecond();
            for (int i = 0; i < confirmedAlarms.size(); i++) {
                Alarm alarm = confirmedAlarms.get(i);
                // Expire Alarms after 60 minutes
                if (currentTime - alarm.getTimestamp() > 60*60) {
                    confirmedAlarms.remove(i);
                    i--;
                }
                if (alarm.getType() == type) {
                    alarmCount++;
                }
            }
            if (alarmCount > 2) {
                controlServer.confirmed_alarm((xyz.fluxinc.noxmonitoring.corba.LocalControlServer) this);
            }
        }
    }

    @Override
    public LogEntry[] get_logs() {
        return logs.toArray(new LogEntry[logs.size()]);
    }

    public void probeValues() {
        for (String station : stations) {
            try {
                MonitorStation oStation = orb.getObject(station);
                for (MonitorType type : oStation.get_available_sensors()) {
                    report_value(oStation, type, oStation.get_sensor_value(type));
                }
            } catch (CannotProceed | InvalidName | NotFound | IllegalStationAccessException | IllegalSensorAccessException cannotProceed) {
                cannotProceed.printStackTrace();
            }
        }
    }
}
