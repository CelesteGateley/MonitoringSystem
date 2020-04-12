package xyz.fluxinc.noxmonitoring;

import org.omg.CORBA.TRANSIENT;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.adapters.MonitorStation;
import xyz.fluxinc.noxmonitoring.corba.IllegalSensorAccessException;
import xyz.fluxinc.noxmonitoring.corba.IllegalStationAccessException;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;
import xyz.fluxinc.noxmonitoring.orbmanagement.MonitorStationOrb;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.*;

public class RunMonitorStation extends Thread{

    private static MonitorStation station;
    private static String[] argsS;
    private static Map<String, Object> argsMap;
    private static JFrame frame;

    public static void main(String[] args) {
        argsS = args;
        argsMap = getArgs(args);
        if (!((boolean) argsMap.get("nogui"))) {
            argsMap.put("location", JOptionPane.showInputDialog(null, "Please enter the location of the monitor station", "Enter Location Name", JOptionPane.QUESTION_MESSAGE));
            argsMap.put("server", JOptionPane.showInputDialog(null, "Please enter the server for the monitor station", "Enter Server Name", JOptionPane.QUESTION_MESSAGE));
        }
        station = new MonitorStation((String) argsMap.get("location"));

        Thread thread = new RunMonitorStation();
        thread.start();
        if (!((boolean) argsMap.get("nogui"))) {
            frame = new JFrame("Sensor Value Control (" + argsMap.get("location") + ")");
            frame.setSize(500, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 200, 0);
            slider.setMajorTickSpacing(25);
            slider.setMinorTickSpacing(5);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);

            slider.addChangeListener((ChangeEvent e) -> {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    if (station.is_enabled()) {
                        try {
                            station.setSensorValue(MonitorType.nitrous_oxide, source.getValue());
                        } catch (IllegalStationAccessException | IllegalSensorAccessException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            frame.add(slider);
            frame.setVisible(true);
        }
    }

    public static Map<String, Object> getArgs(String[] args) {
        Map<String, Object> argsMap = new HashMap<>();
        List<String> argsList = Arrays.asList(args);

        argsMap.put("nogui", argsList.contains("-nogui"));
        String serverName = argsList.contains("-server") ? argsList.get(argsList.indexOf("-server") + 1) : "DEFAULT";
        argsMap.put("server", serverName);
        String locationName = argsList.contains("-location") ? argsList.get(argsList.indexOf("-location") + 1) : "DEFAULT";
        argsMap.put("location", locationName);

        return argsMap;
    }

    @Override
    public void run() {
        super.run();
        LocalServerOrb serverOrb = new LocalServerOrb(argsS);
        MonitorStationOrb orb = new MonitorStationOrb(argsS, station);
        Runtime.getRuntime().addShutdownHook(new ShutdownHook((String) argsMap.get("location"), (String) argsMap.get("server"), serverOrb));
        try {
            orb.bind((String) argsMap.get("location"));
            serverOrb.getObject((String) argsMap.get("server")).register((String) argsMap.get("location"));
        } catch (CannotProceed | InvalidName | NotFound | ServantNotActive | WrongPolicy | TRANSIENT cannotProceed) {
            cannotProceed.printStackTrace();
        }

        orb.runServer();
    }

    private static class ShutdownHook extends Thread {
        private final String location;
        private final String server;
        private final LocalServerOrb orb;

        public ShutdownHook(String location, String server, LocalServerOrb orb) {
            this.location = location;
            this.server = server;
            this.orb = orb;
        }

        @Override
        public void run() {
            super.run();
            try {
                orb.getObject(server).deregister(location);
            } catch (CannotProceed | InvalidName | NotFound cannotProceed) {
                cannotProceed.printStackTrace();
            }
        }
    }
}
