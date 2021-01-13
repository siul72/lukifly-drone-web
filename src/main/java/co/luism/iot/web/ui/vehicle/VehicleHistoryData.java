package co.luism.iot.web.ui.vehicle;

import co.luism.datacollector.DataCollectorDataScanner;
import co.luism.diagnostics.common.DiagnosticsEvent;
import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.*;
import co.luism.diagnostics.interfaces.IDiagnosticsEventHandler;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.iot.web.ui.vehicle.utils.TagNameGenerator;
import co.luism.iot.web.ui.vehicle.utils.TagValueGenerator;
import co.luism.iot.web.ui.vehicle.utils.TimeStampGenerator;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by luis on 27.01.15.
 */
public class VehicleHistoryData extends CustomComponent implements OnDiagCustomComponent , OnDiagVehiclePanel, IDiagnosticsEventHandler {

    private static final Logger LOG = Logger.getLogger(VehicleHistoryData.class);
    private Vehicle vehicle = null;
    private VerticalLayout mainLayout = new VerticalLayout();
    private final BeanContainer<Integer, SnapShotGenericValue> dataElements = new BeanContainer<>(SnapShotGenericValue.class);
    private final Table dataTable = new Table();
    private String currentLanguage;
    private static final String[] tableColumns =  new String[]{"tagId", "value", "timeStamp"};
    private static final String tableId = "id";
    private static final String tableSort = "tagId";


    public VehicleHistoryData(){
        startUp();

    }

    @Override
    public void startUp() {

        initList();
        buildMainLayout();
    }

    @Override
    public void buildMainLayout() {

        setSizeFull();

        mainLayout.addComponent(dataTable);
        dataTable.setWidth(100, Unit.PERCENTAGE);

        mainLayout.setSizeFull();
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setCaptionNames(String currentLanguage) {

        this.currentLanguage = currentLanguage;
        for (String s : tableColumns) {
            dataTable.setColumnHeader(s, LanguageManager.getInstance().getValue(this.currentLanguage, s));
        }

    }

    @Override
    public void closeDown() {
       DataCollectorDataScanner.getInstance().removeListener(this);

    }

    @Override
    public void setVehicle(Vehicle v) {

        if(vehicle == null){
            DataCollectorDataScanner.getInstance().addListener(this);
        }

        vehicle = v;
        updateData();
    }

    private void initList() {
        dataElements.setBeanIdProperty(tableId);
        dataTable.setContainerDataSource(dataElements);
        dataTable.setPageLength(dataElements.size());
        // sort one container property Id
        dataTable.setSortContainerPropertyId(tableSort);
        dataTable.setSortAscending(false);
        dataTable.setVisibleColumns((Object[]) tableColumns);
        dataTable.setSelectable(false);
        dataTable.setImmediate(true);
        dataTable.addGeneratedColumn("tagId", new TagNameGenerator());
        dataTable.addGeneratedColumn("value", new TagValueGenerator());
        dataTable.addGeneratedColumn("timeStamp", new TimeStampGenerator());
    }

    private void updateData() {
        //get current data list
        Collection<SnapShotGenericValue> mySnapShotList = DataCollectorDataScanner.getInstance().getValuesForVehicle(vehicle);
        dataElements.removeAllItems();

        if(mySnapShotList == null){
            LOG.warn("history is empty for " + vehicle.getVehicleId());
            return;
        }

        //dataTable.removeAllItems();
        for(SnapShotGenericValue v : mySnapShotList){
            dataElements.addBean(v);

        }

        dataTable.setPageLength(dataElements.size());
        dataTable.sort();

    }

    @Override
    public Vehicle getMyVehicle() {
        return vehicle;
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

    @Override
    public void handleDiagnosticsEvent(DiagnosticsEvent diagnosticsEvent) {
        if(vehicle == null){
            return;
        }

        if(diagnosticsEvent.getCurrentVehicle().getVehicleId().equals(vehicle.getVehicleId())){

            switch (diagnosticsEvent.getTagType()){
                case TAG_DATA_HISTORY_REFRESH:
                case TAG_DATA_HISTORY_POSITION_REFRESH:
                    dataTable.sort();
                break;
                case TAG_DATA_HISTORY_NEW_DATA:
                    updateData();
                break;
            }


        }
    }
}
