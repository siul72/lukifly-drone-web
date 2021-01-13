package co.luism.iot.web.interfaces;

import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.diagnostics.enterprise.Vehicle;

/**
 * Created by luis on 13.11.14.
 */
public interface OnDiagVehiclePanel {

    void setVehicle(Vehicle v);
    Vehicle getMyVehicle();
    void updateStatus();
    void setProcessValue(DataTag tag, GenericTagValue genericTagValue);
    void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue);

}
