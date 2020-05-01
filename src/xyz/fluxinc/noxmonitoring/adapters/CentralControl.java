package xyz.fluxinc.noxmonitoring.adapters;

import javafx.application.Platform;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import xyz.fluxinc.noxmonitoring.Alarm;
import xyz.fluxinc.noxmonitoring.corba.CentralControlPOA;
import xyz.fluxinc.noxmonitoring.corba.LocalControlServer;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;
import xyz.fluxinc.noxmonitoring.resources.ControlScreenController;

import java.util.*;

public class CentralControl extends CentralControlPOA {

    private final LocalServerOrb serverOrb;
    private final List<String> serverList;
    private final ControlScreenController controlScreenController;
    private static final long probeTime = 1000 * 30;

    public CentralControl(LocalServerOrb serverOrb, ControlScreenController controlScreenController) {
        this.serverOrb = serverOrb;
        serverList = new ArrayList<>();
        this.controlScreenController = controlScreenController;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateUI();
            }
        }, 1000, probeTime);
    }

    public List<String> getServerList() {
        return serverList;
    }

    @Override
    public void register(String location) {
        serverList.add(location);
        if (controlScreenController != null) {
            updateUI();
        }
    }

    @Override
    public void deregister(String location) {
        serverList.remove(location);
        if (controlScreenController != null) {
            updateUI();
        }
    }

    @Override
    public void update_stations(String location) {
        Map<String, List<String>> serversByStation = new LinkedHashMap<>();
        for (String server : serverList) {
            try {
                serversByStation.put(server, Arrays.asList(serverOrb.getObject(server).get_available_stations()));
            } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
                cannotProceed.printStackTrace();
            }
        }
        Platform.runLater(() -> controlScreenController.updateServerList(serversByStation));
    }

    @Override
    public void confirmed_alarm(String location, MonitorType type) {
        Platform.runLater(() -> controlScreenController.addAlarm(new Alarm(location, type, System.currentTimeMillis())));
        updateUI();
    }

    private Map<String, LogEntry[]> getAllLogs() {
        Map<String, LogEntry[]> entries = new LinkedHashMap<>();
        for (String server : serverList) {
            try {
                entries.put(server, serverOrb.getObject(server).get_logs());
            } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
                cannotProceed.printStackTrace();
            }
        }
        return entries;
    }

    private void updateUI() {
        Map<String, LogEntry[]> logs = getAllLogs();
        Map<String, List<String>> stations = new LinkedHashMap<>();
        for (String location : serverList) {
            try {
                LocalControlServer server = serverOrb.getObject(location);
                stations.put(location, new ArrayList<>());
                for (String station : server.get_available_stations()) {
                    stations.get(location).add(station);
                }
            } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
                cannotProceed.printStackTrace();
            }
        }
        Platform.runLater(() -> controlScreenController.updateServerList(logs, stations));
    }
}
