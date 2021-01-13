package co.luism.iot.web.pages;

/*
  ____        _ _ _                   _____           _
 |  __ \     (_) | |                 / ____|         | |
 | |__) |__ _ _| | |_ ___  ___      | (___  _   _ ___| |_ ___ _ __ ___  ___
 |  _  // _` | | | __/ _ \/ __|      \___ \| | | / __| __/ _ \ '_ ` _ \/ __|
 | | \ \ (_| | | | ||  __/ (__       ____) | |_| \__ \ ||  __/ | | | | \__ \
 |_|  \_\__,_|_|_|\__\___|\___|     |_____/ \__, |___/\__\___|_| |_| |_|___/
                                            __/ /
 Railtec Systems GmbH                      |___/
 6052 Hergiswil

 SVN file informations:
 Subversion Revision $Rev: $
 Date $Date: $
 Commmited by $Author: $
*/

import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.OnDiagProperties;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.common.WebUtils;
import co.luism.iot.web.ui.vehicle.alarm.AlarmCounter;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.ui
 * Created by luis on 24.09.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class StatusFrame extends CustomComponent {

    private final GridLayout mainLayout = new GridLayout(6,10);
    private final Label vehicleConnections = new Label();
    private final AlarmCounter alarmCounter = new AlarmCounter();
    private final Label memoryUsage  = new Label();
    private final Label version = new Label();
    private Integer activeConnections = 0;
    private Integer initialMemory = 0;

    private final Map<String, VehicleStatus> vehicleList = new ConcurrentHashMap<>();

    public StatusFrame() {
        mainLayout.setMargin(true);
        mainLayout.setWidth(WebConfig.VEHICLE_GUI_WIDTH, Unit.PIXELS);
        setHeight(WebConfig.STATUS_GUI_HEIGHT, Unit.PIXELS);
        mainLayout.setStyleName("v-offline");
        setCompositionRoot(mainLayout);

        vehicleConnections.setStyleName("h2");
        vehicleConnections.setSizeUndefined();

        memoryUsage.setStyleName("h3");
        memoryUsage.setSizeUndefined();

        version.setSizeUndefined();
        version.setValue(OnDiagProperties.getInstance().getSoftwareVersion());

        mainLayout.addComponent(vehicleConnections);
        mainLayout.addComponent(version);
        mainLayout.addComponent(memoryUsage);
        mainLayout.addComponent(alarmCounter);

        mainLayout.setComponentAlignment(vehicleConnections, Alignment.MIDDLE_CENTER);
        mainLayout.setComponentAlignment(version, Alignment.MIDDLE_CENTER);
        mainLayout.setComponentAlignment(memoryUsage, Alignment.MIDDLE_CENTER);
        mainLayout.setComponentAlignment(alarmCounter, Alignment.MIDDLE_RIGHT);


        //numainLayout.setColumnExpandRatio(0, 1);

        updateConnections();

    }


    public void addVehicle(Vehicle vehicle){
        this.vehicleList.put(vehicle.getVehicleId(), new VehicleStatus(vehicle.getVehicleId(), vehicle.getActiveAlarms().size()));

    }

    public void updateAlarms(Vehicle vehicle) {
        VehicleStatus vs = this.vehicleList.get(vehicle.getVehicleId());

        if(vs == null ){
            return;
        }

        vs.alarmCount = vehicle.getActiveAlarms().size();

        updateAlarmCounter();

    }

    public void updateAlarmCounter() {
        Integer totalCount = 0;
        for(VehicleStatus vehicleStatus : this.vehicleList.values()){
            totalCount = totalCount + vehicleStatus.alarmCount;

        }

        alarmCounter.setValue(totalCount);
    }

    public void updateStatus(Vehicle currentVehicle) {

        VehicleStatus vs = this.vehicleList.get(currentVehicle.getVehicleId());

        if(vs == null ){
            return;
        }

        if(currentVehicle.getStatus().equals(VehicleStatusEnum.ST_OFFLINE)){
            removeConnection(vs);
            return;
        }

        if(currentVehicle.getStatus().equals(VehicleStatusEnum.ST_ONLINE)){
            addConnection(vs);
            return;
        }
    }

    private void addConnection(VehicleStatus vs){

        if(vs != null){
           if(!vs.connected){
               vs.connected = true;
               activeConnections++;
               updateConnections();
           }
        }
    }

    private void removeConnection(VehicleStatus vs){


        if(vs != null){
            if(vs.connected){
                vs.connected = false;
                activeConnections--;
                updateConnections();
            }
        }

    }


    private void updateConnections(){
        this.vehicleConnections.setValue(String.format("✔ %d/%d", this.activeConnections, this.vehicleList.size()));
        updateMemAndCpu();
    }


    private void updateMemAndCpu(){

        Integer currentMemory = WebUtils.getUsedMemory();
        if(initialMemory == 0){
            initialMemory = currentMemory;
        }

        int diff = currentMemory - initialMemory;

        this.memoryUsage.setValue((String.format("%dMB \u2206%dKB",  currentMemory/1024, diff)));

    }




    class VehicleStatus {
        String vId;
        Boolean connected;
        Integer alarmCount;

        public VehicleStatus(String vId, Integer alarmCount){
            this.vId = vId;
            this.alarmCount = alarmCount;
            this.connected = false;
        }
    }



}
