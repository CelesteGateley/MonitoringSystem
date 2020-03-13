package xyz.fluxinc.OrbManagement;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.MonitoringSystem.MonitorStation;

public class MonitorStationOrb extends OrbManager<MonitorStation> {

    public MonitorStationOrb(String[] args) {
        super(args);
    }

    @Override
    public void bind(String name) throws ServantNotActive, WrongPolicy, CannotProceed, InvalidName, NotFound {

    }

    @Override
    public MonitorStation getObject(String name) throws CannotProceed, InvalidName, NotFound {
        return null;
    }
}
