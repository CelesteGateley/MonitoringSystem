package xyz.fluxinc.noxmonitoring.adapters;

import xyz.fluxinc.noxmonitoring.corba.CentralControlPOA;
import xyz.fluxinc.noxmonitoring.corba.LocalControlServer;

public class CentralControl extends CentralControlPOA {

    @Override
    public void register(String location) {

    }

    @Override
    public void deregister(String location) {

    }

    @Override
    public void confirmed_alarm(LocalControlServer server) {

    }
}
