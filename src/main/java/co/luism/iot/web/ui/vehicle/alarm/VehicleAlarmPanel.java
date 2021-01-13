package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.iot.web.common.AlarmTabNameEnum;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.iot.web.ui.common.OnDiagPageTab;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luis on 13.11.14.
 */
public class VehicleAlarmPanel extends CustomComponent implements OnDiagCustomComponent, OnDiagVehiclePanel {

    private static final Logger LOG = Logger.getLogger(VehicleAlarmPanel.class);

    private TabSheet alarmTabSheet;
    private VehicleAlarmCurrentComponent tabCurrentAlarmsPanel;
    private VehicleAlarmHistoryComponent tabHistoryAlarmsPanel;
    private ImportExportComponent tabImportExportAlarmsPanel;
    //private final VehicleEventTable tabAlarmTablePanel = new VehicleEventTable();
    //private final VehicleEnvironmentDataTable tabEnvironmentDataPanel = new VehicleEnvironmentDataTable();

    //tabs on tabsheet
    private Map<AlarmTabNameEnum, OnDiagPageTab> vehicleTabMap;
    private Map<AlarmTabNameEnum, Component> componentMap;
    private String currentLanguage = "en";
    private Vehicle myVehicle;



    public VehicleAlarmPanel() {

    }

    @Override
    public void startUp(){

        tabCurrentAlarmsPanel = new VehicleAlarmCurrentComponent();
        tabHistoryAlarmsPanel = new VehicleAlarmHistoryComponent();
        tabImportExportAlarmsPanel = new ImportExportComponent();
        tabImportExportAlarmsPanel.startUp();
        //private final VehicleEventTable tabAlarmTablePanel = new VehicleEventTable();
        //private final VehicleEnvironmentDataTable tabEnvironmentDataPanel = new VehicleEnvironmentDataTable();
        vehicleTabMap = new HashMap<>();
        componentMap = new HashMap<>();
        alarmTabSheet = new TabSheet();

        alarmTabSheet.setCloseHandler(new TabSheet.CloseHandler() {
            @Override
            public void onTabClose(TabSheet tabsheet, Component tabContent) {
                TabSheet.Tab tab = tabsheet.getTab(tabContent);
                Notification.show("Closing " + tab.getCaption());


                if (tabContent instanceof OnDiagCustomComponent) {
                    //((OnDiagCustomComponent)tabContent).closeDown();
                }


                // We need to close it explicitly in the handler
                tabsheet.removeTab(tab);
            }

        });


        alarmTabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                Component tabComponent = selectedTabChangeEvent.getTabSheet().getSelectedTab();

                if (tabComponent instanceof OnDiagVehiclePanel) {
                    ((OnDiagVehiclePanel) tabComponent).setVehicle(myVehicle);
                }
            }
        });


        buildMainLayout();

    }

    @Override
    public void buildMainLayout() {

        buildTabSheet();
        setCompositionRoot(alarmTabSheet);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {
        this.currentLanguage = currentLanguage;
        for (AlarmTabNameEnum e : componentMap.keySet()) {

            Component c = componentMap.get(e);
            TabSheet.Tab tab = alarmTabSheet.getTab(c);
            tab.setCaption(LanguageManager.getInstance().getValue(this.currentLanguage, e.getName()));
        }

        tabCurrentAlarmsPanel.setCaptionNames(currentLanguage);
        tabHistoryAlarmsPanel.setCaptionNames(currentLanguage);
        tabImportExportAlarmsPanel.setCaptionNames(currentLanguage);
        //this.tabAlarmTablePanel.setCaptionNames(currentLanguage);
        //this.tabEnvironmentDataPanel.setCaptionNames(currentLanguage);
    }

    @Override
    public void closeDown() {
        tabCurrentAlarmsPanel.closeDown();
        tabHistoryAlarmsPanel.closeDown();
        tabImportExportAlarmsPanel.closeDown();
        //tabAlarmTablePanel.closeDown();
        //tabEnvironmentDataPanel.closeDown();
        //alarmTabSheet.removeAllComponents();

    }

    @Override
    public void setVehicle(Vehicle v) {
        myVehicle = v;
        Component tabComponent = alarmTabSheet.getSelectedTab();

        if (tabComponent instanceof OnDiagVehiclePanel) {
            ((OnDiagVehiclePanel) tabComponent).setVehicle(myVehicle);
        }
    }

    @Override
    public Vehicle getMyVehicle() {
        return myVehicle;
    }

    @Override
    public void updateStatus() {
        tabCurrentAlarmsPanel.updateStatus();
    }

    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {

    }


    private void buildTabSheet() {

        OnDiagPageTab AlarmTab;

        componentMap.put(AlarmTabNameEnum.ALARM_TAB_CURRENT, tabCurrentAlarmsPanel);
        componentMap.put(AlarmTabNameEnum.ALARM_TAB_HISTORY, tabHistoryAlarmsPanel);
        componentMap.put(AlarmTabNameEnum.ALARM_TAB_IMPORT, tabImportExportAlarmsPanel);
        //this.componentMap.put(AlarmTabNameEnum.ALARM_TAB_RAW_DATA, tabAlarmTablePanel);
        //this.componentMap.put(AlarmTabNameEnum.ALARM_TAB_RAW_ENV, tabEnvironmentDataPanel);

        tabCurrentAlarmsPanel.setImmediate(true);
        tabHistoryAlarmsPanel.setImmediate(true);
        tabImportExportAlarmsPanel.setImmediate(true);
        //tabAlarmTablePanel.setImmediate(true);


        for (AlarmTabNameEnum t : AlarmTabNameEnum.values()) {

            Component c = componentMap.get(t);
            if (c == null) {
                continue;
            }

            AlarmTab = new OnDiagPageTab<>(c, t);
            vehicleTabMap.put(t, AlarmTab);
            String name = LanguageManager.getInstance().getValue(this.currentLanguage,
                    ((AlarmTabNameEnum) AlarmTab.getTab()).getName());
            alarmTabSheet.addTab(AlarmTab.getComponent(), name,
                    new ThemeResource(((AlarmTabNameEnum) AlarmTab.getTab()).getIcon()));

            alarmTabSheet.getTab(AlarmTab.getComponent())
                    .setId(PageElementId.ALARM_TAB_PREFIX +
                            AlarmTab.getComponent().getClass().getSimpleName());

        }

    }

    public void setAlarmRefresh() {

        tabCurrentAlarmsPanel.setAlarmRefresh();
    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {
        tabCurrentAlarmsPanel.setAlarmValue(tag, snapShotAlarmTagValue);
    }
}

