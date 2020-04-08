package xyz.fluxinc.noxmonitoring.adapters;

import xyz.fluxinc.noxmonitoring.corba.CentralControlPOA;
import xyz.fluxinc.noxmonitoring.corba.LocalControlServer;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;

import java.util.ArrayList;
import java.util.List;
import java.util.spi.LocaleServiceProvider;

public class CentralControl extends CentralControlPOA {

    private LocalServerOrb serverOrb;
    private List<String> serverList;

    public CentralControl(LocalServerOrb serverOrb) {
        this.serverOrb = serverOrb;
        serverList = new ArrayList<>();
    }


    @Override
    public void register(String location) {
        serverList.add(location);
    }

    @Override
    public void deregister(String location) {
        serverList.remove(location);
    }

    @Override
    public void confirmed_alarm(LocalControlServer server) {
        for (LogEntry entry : server.get_logs()) {
            System.out.println(entry.location + ": (" + entry.type.toString() + ") " + entry.value);
        }
    }
}
