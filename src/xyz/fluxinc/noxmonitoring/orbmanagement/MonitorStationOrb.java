package xyz.fluxinc.noxmonitoring.orbmanagement;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.corba.MonitorStationHelper;
import xyz.fluxinc.noxmonitoring.adapters.MonitorStation;

public class MonitorStationOrb extends OrbManager<xyz.fluxinc.noxmonitoring.corba.MonitorStation> {

    private MonitorStation station;

    public MonitorStationOrb(String[] args, MonitorStation station) {
        super(args);
        this.station = station;
    }

    public MonitorStationOrb(String[] args) {
        super(args);
    }

    @Override
    public void bind(String name) throws ServantNotActive, WrongPolicy, CannotProceed, InvalidName, NotFound {
        Object ref = getPortableObjectAdapter().servant_to_reference(station);

        NameComponent[] stationName = getNamingContextExt().to_name(name);
        getNamingContextExt().rebind(stationName, MonitorStationHelper.narrow(ref));
    }

    @Override
    public xyz.fluxinc.noxmonitoring.corba.MonitorStation getObject(String name) throws CannotProceed, InvalidName, NotFound {
        return MonitorStationHelper.narrow(getNamingContextExt().resolve_str(name));
    }
}
