package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by luis on 13.11.14.
 */
public class VehicleAlarmCurrentComponent extends VehicleAlarmComponent<AlarmCurrentGui> {


    private static final Logger LOG = Logger.getLogger(VehicleAlarmCurrentComponent.class);


    VehicleAlarmCurrentComponent() {

        super(AlarmCurrentGui.class);
        startUp();
    }

    @Override
    public void startUp() {
        buildMainLayout();
    }


    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {

    }

    public void setAlarmRefresh() {
        populateAlarmLayout();
    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {

        if(snapShotAlarmTagValue == null){
            LOG.error("snapShotAlarmTagValue is null");
            return;
        }

        if (snapShotAlarmTagValue.getValue() == 0) {
            removeAlarm(snapShotAlarmTagValue.getTagId());
        } else {
            addAlarm(snapShotAlarmTagValue.getTagId(), snapShotAlarmTagValue);
        }
    }

    @Override
    protected void populateAlarmLayout() {

        if (this.myVehicle == null) {
            LOG.warn("vehicle no ready");
            return;
        }

        List<?> listOfAlarms = this.myVehicle.getActiveAlarms();
        super.populateAlarmLayout(listOfAlarms);
    }
}
