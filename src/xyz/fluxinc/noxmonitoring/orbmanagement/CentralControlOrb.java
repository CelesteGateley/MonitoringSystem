package xyz.fluxinc.noxmonitoring.orbmanagement;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.adapters.CentralControl;
import xyz.fluxinc.noxmonitoring.corba.CentralControlHelper;

public class CentralControlOrb extends OrbManager<xyz.fluxinc.noxmonitoring.corba.CentralControl> {

    private CentralControl station;

    public CentralControlOrb(String[] args, CentralControl centralControl) {
        super(args);
        this.station = centralControl;
    }

    public CentralControlOrb(String[] args) {
        super(args);
    }

    @Override
    public void bind(String name) throws ServantNotActive, WrongPolicy, CannotProceed, InvalidName, NotFound {
        Object ref = getPortableObjectAdapter().servant_to_reference(station);

        NameComponent[] stationName = getNamingContextExt().to_name(name);
        getNamingContextExt().rebind(stationName, CentralControlHelper.narrow(ref));
    }

    @Override
    public xyz.fluxinc.noxmonitoring.corba.CentralControl getObject(String name) throws CannotProceed, InvalidName, NotFound {
        return CentralControlHelper.narrow(getNamingContextExt().resolve_str(name));
    }
}
