package co.luism.iot.web.ui.vehicle.vnc;

import co.luism.iot.web.common.WebConfig;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by luis on 27.11.14.
 */
public class VncConnectionManager {
    private static final Logger LOG = Logger.getLogger(VncConnectionManager.class);

    private static final VncConnectionManager instance = new VncConnectionManager();
    private Set<VncPort> vncPortSet = new TreeSet<>();
    private Map<String, VncPort> assignedPortMap = new HashMap<>();
    private Map<Integer  , VncClientComponent> componentMap = new HashMap<>();

    public static VncConnectionManager getInstance(){
        return instance;
    }

    VncConnectionManager(){
        generateMap();
    }

    private void generateMap() {

        Integer startPort = WebConfig.VNC_PORT_START_NUMBER;

        for(int i = 0; i < WebConfig.VNC_NUMBER_OF_AVAILABLE_PORTS; i ++){
            vncPortSet.add(new VncPort(startPort +i));
        }
    }

    public Integer getAvailablePort(String vehicleID, VncClientComponent c){

        VncPort p = assignedPortMap.get(vehicleID);

        if(p == null){
            p = getFreePort();
            if(p == null){
                LOG.warn("there is no free vnc port");
                return null;
            }
            p.setVehicleId(vehicleID);
            assignedPortMap.put(vehicleID, p);
            componentMap.put(p.getPort(), c);
            return p.getPort();
        }

        //return null if no port is available
        return null;

    }

    private VncPort getFreePort(){

        for(VncPort p : vncPortSet){
            if(p.isFree()){
                p.setFree(false);
                p.setNumberOfClients(1);
                return p;
            }
        }

        return null;
    }


    public void setFreePort(String vehicleId){
        VncPort p = assignedPortMap.get(vehicleId);

        if(p == null){
            LOG.warn("there is no port assigned for "+ vehicleId);
            return;
        }

        Integer nClient = p.getNumberOfClients();

        if(nClient <= 1){
            //free the connection from the map
            p.setFree(true);
            assignedPortMap.remove(vehicleId);
            componentMap.remove(p.getPort());
            return;
        }

        //else have more connections
        p.setNumberOfClients(--nClient);
    }

    public void register(VncTunnel vncTunnel) {
        LOG.debug(String.format("register tunnel on port %d", vncTunnel.getPort()));

    }


    public void unregister(VncTunnel vncTunnel) {

        //check and remove connection
        VncClientComponent c = this.componentMap.get(vncTunnel.getPort());

        if(c != null){
            LOG.debug("stop vnc client");
            c.stop();
        }

    }
}
