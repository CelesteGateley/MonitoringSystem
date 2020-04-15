package xyz.fluxinc.noxmonitoring.adapters;

import javafx.application.Platform;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import xyz.fluxinc.noxmonitoring.corba.CentralControlPOA;
import xyz.fluxinc.noxmonitoring.corba.LocalControlServer;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;
import xyz.fluxinc.noxmonitoring.resources.ControlScreenController;

import java.util.*;
import java.util.spi.LocaleServiceProvider;

public class CentralControl extends CentralControlPOA {

    private LocalServerOrb serverOrb;
    private List<String> serverList;
    private ControlScreenController controlScreenController;
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

    public List<String> getServerList() { return serverList; }

    @Override
    public void register(String location) {
        serverList.add(location);
        System.out.println("Local Server Registered: " + location);
        if (controlScreenController != null) {
            updateUI();
        } else {
            System.out.println("Control Screen Controller is null");
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
    public void confirmed_alarm(String location) {
        try {
            LocalControlServer server = serverOrb.getObject(location);
            for (LogEntry entry : server.get_logs()) {
                System.out.println(entry.location + ": (" + entry.type.toString() + ") " + entry.value);
            }
            updateUI();
        } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
            cannotProceed.printStackTrace();
        }
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
        Platform.runLater(() -> {
            controlScreenController.updateServerList(logs);
        });
    }
}
