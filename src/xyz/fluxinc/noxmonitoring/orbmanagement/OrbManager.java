package xyz.fluxinc.noxmonitoring.orbmanagement;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public abstract class OrbManager<T> {

    private ORB orb;
    private Object nameServiceObject;
    private NamingContextExt namingContextExt;
    private POA portableObjectAdapter;

    public OrbManager(String[] args) {
        try {
            orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            portableObjectAdapter = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            portableObjectAdapter.the_POAManager().activate();

            // Get a reference to the Naming service
            nameServiceObject = orb.resolve_initial_references("NameService");
            if (nameServiceObject == null) {
                System.out.println("nameServiceObj = null");
                return;
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            namingContextExt = NamingContextExtHelper.narrow(nameServiceObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void bind(String name) throws ServantNotActive, WrongPolicy, CannotProceed, InvalidName, NotFound;

    public abstract T getObject(String name) throws CannotProceed, InvalidName, NotFound;

    public ORB getOrb() {
        return orb;
    }

    public Object getNameServiceObject() {
        return nameServiceObject;
    }

    public NamingContextExt getNamingContextExt() {
        return namingContextExt;
    }

    public POA getPortableObjectAdapter() {
        return portableObjectAdapter;
    }

    public void runServer() {
        orb.run();
    }


}
