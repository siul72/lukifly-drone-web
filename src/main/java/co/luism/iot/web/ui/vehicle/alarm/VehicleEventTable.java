package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.diagnostics.enterprise.HistoryAlarmTagValue;
import co.luism.diagnostics.webmanager.WebManagerFacade;

import java.util.List;

/**
 * Created by luis on 28.11.14.
 */
public class VehicleEventTable extends VehicleTableComponent {

    private static final String ID = "id";
    private static final String ALARM_ID = "tagId";
    private static final String ALARM_TS = "timeStamp";
    private static final String ALARM_TS_MS = "milliSeconds";
    private static final String ALARM_VALUE = "value";
    private static final String ALARM_STATUS = "status";
    private static final String ALARM_ACK = "ack";

    public VehicleEventTable() {
        super(HistoryAlarmTagValue.class);
        startUp();
    }

    @Override
    public void startUp(){
        setTableId();
        setTableSort();
        setEventColumns();
    }

    @Override
    void setEventColumns() {

        this.eventColumns = new String[]{ID, ALARM_ID, ALARM_TS, ALARM_TS_MS, ALARM_VALUE, ALARM_STATUS, ALARM_ACK};

    }

    @Override
    void setTableId() {
        this.tableId = ID;
    }

    @Override
    void setTableSort() {
        this.tableSort = ALARM_TS;
    }

    @Override
    void populateTable() {
        List myDataList = WebManagerFacade.getInstance().getLastVehicleHistoryAlarmTagValues(this.getMyVehicle().getVehicleId(), 25);
        populateTable(myDataList);
    }

}
