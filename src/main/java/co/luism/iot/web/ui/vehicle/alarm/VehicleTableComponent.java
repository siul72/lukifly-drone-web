package co.luism.iot.web.ui.vehicle.alarm;


import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.common.ProcessEnum;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by luis on 07.11.14.
 */
abstract class VehicleTableComponent<T> extends CustomComponent implements OnDiagCustomComponent, OnDiagVehiclePanel {

    private static final Logger LOG = Logger.getLogger(VehicleTableComponent.class);
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final Class clazz;
    private Vehicle myVehicle;
    private String currentLanguage = "en";

    private final Table eventsAlarmList = new Table();
    private BeanContainer<Integer, T> alarmElements;

    protected String[] eventColumns;
    protected String tableId;
    protected String tableSort;

    abstract void setEventColumns();

    abstract void setTableId();

    abstract void setTableSort();

    public VehicleTableComponent(Class clazz) {
        this.clazz = clazz;
        buildMainLayout();


    }

    @Override
    public void buildMainLayout() {

        setSizeFull();

        mainLayout.addComponent(eventsAlarmList);
        eventsAlarmList.setWidth(100, Unit.PERCENTAGE);

        mainLayout.setSizeFull();
        setCompositionRoot(mainLayout);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {

        this.currentLanguage = currentLanguage;
        for (String s : eventColumns) {
            eventsAlarmList.setColumnHeader(s, LanguageManager.getInstance().getValue(this.currentLanguage, s));
        }
    }

    @Override
    public void closeDown() {

        if (alarmElements != null) {
            alarmElements.removeAllItems();
        }
        this.myVehicle = null;
    }

    private void initAlarmList() {

        if (alarmElements != null) {
            return;
        }
        alarmElements = new BeanContainer<Integer, T>(this.clazz);
        alarmElements.setBeanIdProperty(this.tableId);

        eventsAlarmList.setContainerDataSource(alarmElements);
        eventsAlarmList.setPageLength(alarmElements.size());
        // sort one container property Id
        eventsAlarmList.setSortContainerPropertyId(this.tableSort);
        eventsAlarmList.setSortAscending(false);

        eventsAlarmList.setVisibleColumns((Object[]) eventColumns);
        eventsAlarmList.setSelectable(false);
        eventsAlarmList.setImmediate(true);


    }

    public void setValue(EventTypeEnum type, DataTag t) {

        if (!this.clazz.equals(DataTag.class)) {
            return;
        }

        GenericTagValue tv = this.myVehicle.getSnapShotValue(GenericTagValue.class, type, t.getName());

        if (tv == null) {
            LOG.error(String.format("no valid snapshot value for tag %s", t.getName()));
            return;
        }

        if (t.getProcess() == null) {
            LOG.error(String.format("process value not found for tag %s", t.getName()));
            return;
        }


        ProcessEnum p = ProcessEnum.values()[t.getProcess()];
        if (p == null) {
            LOG.error(String.format("process not found for tag %s", t.getName()));
            return;
        }

        switch (type) {
            case TAG_DATA_TYPE_EVENT:
                //write it to the table
                alarmElements.addBean((T) tv);
                eventsAlarmList.setPageLength(alarmElements.size());
                eventsAlarmList.sort();
                break;
            case TAG_DATA_TYPE_NONE:
            default:
                break;

        }
    }

    public void setVehicle(Vehicle myTrain) {

        if (this.myVehicle != null) {
            if (this.myVehicle.equals(myTrain)) {
                return;
            }
        }
        this.myVehicle = myTrain;
        initAlarmList();
        populateTable();

    }

    @Override
    public Vehicle getMyVehicle() {
        return this.myVehicle;
    }

    @Override
    public void updateStatus() {

    }

    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {

    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {

    }

    abstract void populateTable();


    protected final void populateTable(List myDataList) {
        if (myDataList == null) {
            return;
        }


        alarmElements.addAll(myDataList);
        eventsAlarmList.setPageLength(myDataList.size());
        eventsAlarmList.sort();
    }


}
