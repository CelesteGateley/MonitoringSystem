package xyz.fluxinc.noxmonitoring;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import xyz.fluxinc.noxmonitoring.corba.IllegalSensorAccessException;
import xyz.fluxinc.noxmonitoring.corba.IllegalStationAccessException;
import xyz.fluxinc.noxmonitoring.corba.MonitorStation;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;
import xyz.fluxinc.noxmonitoring.orbmanagement.MonitorStationOrb;

public class Main {

    public static void main(String[] args) throws InvalidName, NotFound, CannotProceed, IllegalStationAccessException, IllegalSensorAccessException {
        MonitorStationOrb orb = new MonitorStationOrb(args);
        MonitorStation station = orb.getObject("SOUTH_LONDON_2123");
        for (MonitorType type : station.get_available_sensors()) {
            System.out.println(station.get_sensor_value(type));
        }
    }
}
