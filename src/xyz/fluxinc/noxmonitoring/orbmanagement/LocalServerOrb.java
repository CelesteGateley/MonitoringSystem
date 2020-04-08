package xyz.fluxinc.noxmonitoring.orbmanagement;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.adapters.LocalControlServer;
import xyz.fluxinc.noxmonitoring.corba.LocalControlServerHelper;

public class LocalServerOrb extends OrbManager<xyz.fluxinc.noxmonitoring.corba.LocalControlServer> {

    private LocalControlServer server;

    public LocalServerOrb(String[] args, LocalControlServer server) {
        super(args);
        this.server = server;
    }

    public LocalServerOrb(String[] args) {
        super(args);
    }

    @Override
    public void bind(String name) throws ServantNotActive, WrongPolicy, CannotProceed, InvalidName, NotFound {
        Object ref = getPortableObjectAdapter().servant_to_reference(server);

        NameComponent[] serverName = getNamingContextExt().to_name(name);
        getNamingContextExt().rebind(serverName, LocalControlServerHelper.narrow(ref));
    }

    @Override
    public xyz.fluxinc.noxmonitoring.corba.LocalControlServer getObject(String name) throws CannotProceed, InvalidName, NotFound {
        return LocalControlServerHelper.narrow(getNamingContextExt().resolve_str(name));
    }
}
