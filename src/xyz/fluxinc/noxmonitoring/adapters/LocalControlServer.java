package xyz.fluxinc.noxmonitoring.adapters;

import org.omg.CORBA.TRANSIENT;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import xyz.fluxinc.noxmonitoring.Alarm;
import xyz.fluxinc.noxmonitoring.corba.CentralControl;
import xyz.fluxinc.noxmonitoring.corba.MonitorStation;
import xyz.fluxinc.noxmonitoring.corba.*;
import xyz.fluxinc.noxmonitoring.orbmanagement.CentralControlOrb;
import xyz.fluxinc.noxmonitoring.orbmanagement.MonitorStationOrb;

import java.util.*;

public class LocalControlServer extends LocalControlServerPOA {

    private final String controlServer;
    private final CentralControlOrb centralControlOrb;
    private final List<String> stations;
    private final MonitorStationOrb orb;
    private final String location;
    private final List<LogEntry> logs;
    private final List<Alarm> confirmedAlarms;
    private final Map<MonitorType, Long> sentAlarmTimes;
    // Run 15s. Fine for testing purposes, but in field report every hour
    private static final long checkTime = 1000 * 15;

    public LocalControlServer(MonitorStationOrb orb, CentralControlOrb centralControlOrb, String location, String controlServer) {
        stations = new ArrayList<>();
        this.orb = orb;
        this.centralControlOrb = centralControlOrb;
        logs = new ArrayList<>();
        confirmedAlarms = new ArrayList<>();
        this.location = location;
        this.controlServer = controlServer;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                probeValues();
            }
        }, 1000, checkTime);

        sentAlarmTimes = new LinkedHashMap<>();
    }

    @Override
    public void register(String location) {
        stations.add(location);
        try {
            MonitorStation station = orb.getObject(location);
            if (station.is_enabled()) {
                for (MonitorType type : station.get_available_sensors()) {
                    if (station.is_sensor_enabled(type)) {
                        LogEntry entry = new LogEntry();
                        entry.timestamp = System.currentTimeMillis();
                        entry.location = location;
                        entry.type = type;
                        entry.value = station.get_sensor_value(type);
                        logs.add(entry);
                    }
                }
            }
            centralControlOrb.getObject(controlServer).update_stations(this.location);
        } catch (CannotProceed | InvalidName | NotFound | IllegalStationAccessException | IllegalSensorAccessException cannotProceed) {
            cannotProceed.printStackTrace();
        }

        System.out.println("Monitor Station at " + location + " has been registered!");
    }

    @Override
    public void deregister(String location) {
        stations.remove(location);
        try {
            centralControlOrb.getObject(controlServer).update_stations(this.location);
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
        System.out.println("Monitor Station at " + location + " has been deregistered!");
    }

    @Override
    public String get_location() {
        return location;
    }

    @Override
    public String[] get_available_stations() {
        return stations.toArray(String[]::new);
    }

    @Override
    public void report_value(String location, MonitorType type, double sensor_value) {
        try {
            MonitorStation station = orb.getObject(location);
            System.out.println(station.get_location() + ": " + sensor_value + " (" + type + ")");
            logs.add(new LogEntry(System.currentTimeMillis(), station.get_location(), type, sensor_value));
            if (sensor_value > Alarm.getRedAlarm(type)) {
                confirmedAlarms.add(new Alarm(station.get_location(), type, sensor_value));
                int alarmCount = 1;
                // Saves constantly checking the time each time, and a couple seconds margin of error on expiring alerts is acceptable
                long currentTime = System.currentTimeMillis();
                String currentAlarmLocation = station.get_location();
                for (int i = 0; i < confirmedAlarms.size(); i++) {
                    Alarm alarm = confirmedAlarms.get(i);
                    // Expire Alarms after 60 minutes
                    if (currentTime - alarm.getTimestamp() > 60 * 60) {
                        confirmedAlarms.remove(i);
                        i--;
                    }
                    if (alarm.getType() == type && !currentAlarmLocation.equals(alarm.getLocation())) {
                        alarmCount++;
                    }
                }
                if (alarmCount > 2) {
                    // Only send an alarm once every 10 minutes
                    if (!sentAlarmTimes.containsKey(type) || sentAlarmTimes.get(type) > System.currentTimeMillis() + (1000 * 60 * 10)) {
                        CentralControl centralControl = centralControlOrb.getObject(controlServer);
                        centralControl.confirmed_alarm(this.location, type);
                        sentAlarmTimes.put(type, System.currentTimeMillis());
                    }
                }
            }
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
    }

    @Override
    public LogEntry[] get_logs() {
        return logs.toArray(LogEntry[]::new);
    }

    public void probeValues() {
        System.out.println("Probing Values");
        for (String station : stations) {
            try {
                MonitorStation oStation = orb.getObject(station);
                for (MonitorType type : oStation.get_available_sensors()) {
                    if (oStation.is_sensor_enabled(type) && oStation.is_enabled()) {
                        report_value(oStation.get_location(), type, oStation.get_sensor_value(type));
                    }
                }
            } catch (CannotProceed | InvalidName | NotFound | TRANSIENT cannotProceed) {
                cannotProceed.printStackTrace();
                stations.remove(station);
            } catch (IllegalStationAccessException | IllegalSensorAccessException ignored) {
            }
        }
    }
}
