package xyz.fluxinc.noxmonitoring;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.adapters.MonitorStation;
import xyz.fluxinc.noxmonitoring.orbmanagement.MonitorStationOrb;

public class RunStationServer {

    private static MonitorStation station;

    public static void main(String[] args) throws WrongPolicy, InvalidName, ServantNotActive, CannotProceed, NotFound {
        station = new MonitorStation("SOUTH_LONDON_2123");
        MonitorStationOrb orbManager = new MonitorStationOrb(args, station);
        orbManager.bind("MS_SOUTH_LONDON_2123");
        orbManager.runServer();
    }
}
