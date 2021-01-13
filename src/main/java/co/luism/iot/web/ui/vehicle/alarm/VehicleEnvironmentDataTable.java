package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.diagnostics.enterprise.AlarmEnvironmentData;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.HistoryAlarmTagValue;
import co.luism.diagnostics.webmanager.WebManagerFacade;

import java.util.List;

/**
 * Created by luis on 28.11.14.
 */
public class VehicleEnvironmentDataTable extends VehicleTableComponent{

    private static final String ID ="id";
    private static final String EVENT_TS = "timeStampSeconds";

    public VehicleEnvironmentDataTable() {
        super(AlarmEnvironmentData.class);
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

        this.eventColumns = new String[] {ID, "alarmTagHistoryInfoId","eventIndex", "categoryIndex" ,EVENT_TS, "timeStampMilliSeconds", "value"};

    }

    @Override
    void setTableId() {
        this.tableId = ID;
    }

    @Override
    void setTableSort() {
        this.tableSort = EVENT_TS;
    }

    @Override
    void populateTable() {

            List myDataList  = WebManagerFacade.getInstance().getAlarmEnvironmentData(this.getMyVehicle().getVehicleId(), 25);
            populateTable(myDataList);
    }

}

