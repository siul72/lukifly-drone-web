package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.datacollector.common.DCDPullerEvent;
import co.luism.datacollector.common.DCDPullerEventHandler;
import co.luism.diagnostics.enterprise.*;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by luis on 24.11.14.
 */
public class VehicleAlarmHistoryComponent extends VehicleAlarmComponent<AlarmHistoryGui>  implements DCDPullerEventHandler{

    private static final Logger LOG = Logger.getLogger(VehicleAlarmCurrentComponent.class);

    public VehicleAlarmHistoryComponent(){
        super(AlarmHistoryGui.class);

    }

    @Override
    public void startUp(){

    }

    @Override
    public void closeDown(){
        String vehicleId;

        if(this.myVehicle != null){
           vehicleId = this.myVehicle.getVehicleId();
            WebManagerFacade.getInstance().removeVehicleAlarmHistoryListener(vehicleId, this);
        }

        super.closeDown();
    }

    @Override
    public void setVehicle(Vehicle vehicle){

        String oldVehicleId = null;
        if(this.myVehicle != null){
            oldVehicleId = this.myVehicle.getVehicleId();
        }

        if(vehicle != null){
            WebManagerFacade.getInstance().replaceVehicleAlarmHistoryListener(oldVehicleId, vehicle.getVehicleId(), this);
        }

        super.setVehicle(vehicle);
    }

    @Override
    public void setAlarmRefresh() {

    }

    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {

    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {

    }

    @Override
    protected void populateAlarmLayout() {

        if(this.myVehicle == null){
            LOG.warn("vehicle no ready");
            return;
        }

        List<?> listOfAlarms = WebManagerFacade.getInstance().getAlarmTagHistoryInfo(this.myVehicle.getVehicleId(), 100);
        super.populateAlarmLayout(listOfAlarms);
    }

    @Override
    public void handleEvent(DCDPullerEvent dcdEventPullerEvent) {
        if(this.myVehicle == null){
            return;
        }

        AlarmValueHistoryInfo alarmValueHistoryInfo = dcdEventPullerEvent.getAlarmValueHistoryInfo();
        if(this.myVehicle.getVehicleId().equals(alarmValueHistoryInfo.getVehicleId())){
            LOG.debug("add new alarm data history");
             super.addAlarm(alarmValueHistoryInfo.getId(), alarmValueHistoryInfo);
        }
    }

}