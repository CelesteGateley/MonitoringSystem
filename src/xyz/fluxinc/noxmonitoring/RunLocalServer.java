package xyz.fluxinc.noxmonitoring;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.adapters.LocalControlServer;
import xyz.fluxinc.noxmonitoring.orbmanagement.CentralControlOrb;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;
import xyz.fluxinc.noxmonitoring.orbmanagement.MonitorStationOrb;

import javax.swing.*;
import java.util.Map;

public class RunLocalServer extends Thread {

    private static LocalServerOrb orb;
    private static Map<String, Object> argsMap;

    public static void main(String[] args) throws WrongPolicy, InvalidName, ServantNotActive, CannotProceed, NotFound {
        argsMap = RunMonitorStation.getArgs(args);
        if (!((boolean) argsMap.get("nogui"))) {
            argsMap.put("location", JOptionPane.showInputDialog(null, "Please enter the location of the monitor station", "Enter Location Name", JOptionPane.QUESTION_MESSAGE));
            argsMap.put("server", JOptionPane.showInputDialog(null, "Please enter the server for the monitor station", "Enter Server Name", JOptionPane.QUESTION_MESSAGE));
        }
        System.out.println("Running Server " + argsMap.get("location") + " Controlled By " + argsMap.get("server"));
        MonitorStationOrb stationOrb = new MonitorStationOrb(args);
        CentralControlOrb serverOrb = new CentralControlOrb(args);
        LocalControlServer server = new LocalControlServer(stationOrb, serverOrb, (String) argsMap.get("location"), (String) argsMap.get("server"));
        orb = new LocalServerOrb(args, server);
        RunLocalServer runLocalServer = new RunLocalServer();
        runLocalServer.start();
        serverOrb.getObject((String) argsMap.get("server")).register((String) argsMap.get("location"));

    }

    @Override
    public void run() {
        try {
            orb.bind((String) argsMap.get("location"));
            orb.runServer();
        } catch (ServantNotActive | WrongPolicy | CannotProceed | InvalidName | NotFound servantNotActive) {
            servantNotActive.printStackTrace();
        }

    }

}
