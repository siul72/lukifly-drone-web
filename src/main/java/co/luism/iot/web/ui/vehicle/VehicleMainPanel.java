package co.luism.iot.web.ui.vehicle;

import co.luism.common.DateTimeUtils;
import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.OnDiagTabSheet;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.common.VehicleTabNameEnum;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.iot.web.pages.OnDiagMainView;
import co.luism.iot.web.ui.common.BorderLayout;
import co.luism.iot.web.ui.common.HorizontalSplitter;
import co.luism.iot.web.ui.common.OnDiagPageTab;
import co.luism.iot.web.ui.vehicle.alarm.AlarmCounter;
import co.luism.iot.web.ui.vehicle.alarm.VehicleAlarmPanel;
import co.luism.iot.web.ui.vehicle.instruments.GaugeInstrumentPanel;
import co.luism.iot.web.ui.vehicle.map.VehicleMapComponent;
import co.luism.iot.web.ui.vehicle.vnc.VncClientComponent;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luis on 07.11.14.
 */
public class VehicleMainPanel extends CustomComponent implements OnDiagCustomComponent, OnDiagVehiclePanel {

    private static final Logger LOG = Logger.getLogger(VehicleMainPanel.class);
    private OnDiagMainView onDiagMainViewParent;

    private GaugeInstrumentPanel tabGaugeInstrumentPanel;
    private VehicleAlarmPanel tabAlarmPanel;
    //private final Label processNotImplemented
    private VehicleMapComponent tabMapPanel;
    private VncClientComponent tabVncPanel;
    private VehicleHistoryData vehicleHistoryData;
    private OnDiagTabSheet vehicleTabSheet;

    private final HorizontalLayout topHorizontal = new HorizontalLayout();
    private final VerticalLayout centerVertical = new VerticalLayout();
    private final VerticalLayout footer = new VerticalLayout();
    private final Image closeButton = new Image(null, new ThemeResource("icons/close_window.png"));
    private final Label vehicleName = new Label("V_NAME");
    private final Label vehicleStatus = new Label("STATUS_OFF");
    //private final Label timeLeft = new Label("00:00:00");
    //tabs on tabsheet
    private final Map<VehicleTabNameEnum, OnDiagPageTab> vehicleTabMap = new HashMap<>();
    private final Map<VehicleTabNameEnum, Component> componentMap = new HashMap<>();
    private final VehicleDropPanel myDropPanel = new VehicleDropPanel();
    //private final Image refreshSession = new Image(null, new ThemeResource("icons/session_refresh.png"));

    //public volatile int timeCountDown = -1;
    private BorderLayout mainLayout = new BorderLayout();
    private volatile Vehicle myVehicle;
    private String currentLanguage = "en";
    private VehicleStatusEnum panelStatus = VehicleStatusEnum.ST_NONE;
    private AlarmCounter alarmCounter = new AlarmCounter();
    private String headerCurrentStyleName = "";


    public VehicleMainPanel() {


    }

    public void init(OnDiagMainView parent){
        this.onDiagMainViewParent = parent;
        startUp();

    }

    public void setVehicleList(List vehicleList) {
        this.myDropPanel.init(vehicleList);
    }

    @Override
    public void startUp() {

        tabGaugeInstrumentPanel = new GaugeInstrumentPanel();
        tabAlarmPanel = new VehicleAlarmPanel();
        tabAlarmPanel.startUp();
        //processNotImplemented = new Label("Not implemented");
        tabMapPanel = new VehicleMapComponent();
        tabVncPanel = new VncClientComponent();
        vehicleHistoryData = new VehicleHistoryData();
        vehicleTabSheet = new OnDiagTabSheet();

        vehicleTabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {

                TabSheet tabSheet = selectedTabChangeEvent.getTabSheet();
                Component tabComponent = tabSheet.getSelectedTab();


            }
        });


        MouseEvents.ClickListener hideInstruments = new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent clickEvent) {
                if (clickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {

                    close();
                }
            }
        };

//        refreshSession.addClickListener(new MouseEvents.ClickListener() {
//            @Override
//            public void click(MouseEvents.ClickEvent clickEvent) {
//
//                if (myVehicle == null) {
//                    return;
//                }
//
//                if (myVehicle.getStatus() == VehicleStatusEnum.ST_ONLINE) {
//                    timeCountDown = WebConfig.INSTRUMENT_TIME_OUT;
//                    String newTime = DateTimeUtils.splitToComponentTimes(timeCountDown);
//                    timeLeft.setValue(newTime);
//                }
//
//            }
//        });


        vehicleTabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                Component tabComponent = selectedTabChangeEvent.getTabSheet().getSelectedTab();

                if (tabComponent instanceof VehicleAlarmPanel) {
                    ((VehicleAlarmPanel) tabComponent).setVehicle(myVehicle);
                }

                if (tabComponent instanceof VncClientComponent) {
                    ((VncClientComponent) tabComponent).start();
                } else {
                    tabVncPanel.stop();

                }
            }
        });

        vehicleTabSheet.setCloseHandler(new TabSheet.CloseHandler() {
            @Override
            public void onTabClose(TabSheet tabsheet, Component tabContent) {
                TabSheet.Tab tab = tabsheet.getTab(tabContent);
                //Notification.show("Closing " + tab.getCaption());


                if (tabContent instanceof VncClientComponent) {
                    ((VncClientComponent) tabContent).stop();
                }


                // We need to close it explicitly in the handler
                tabsheet.removeTab(tab);
            }

        });

        buildMainLayout();
        closeButton.setId("close_vehicle");
        closeButton.addClickListener(hideInstruments);
    }

    @Override
    public void closeDown() {

        this.tabGaugeInstrumentPanel.closeDown();
        this.myDropPanel.closeDown();
        this.tabAlarmPanel.closeDown();
        this.tabMapPanel.closeDown();
        this.tabVncPanel.closeDown();
        this.vehicleHistoryData.closeDown();

        close();

    }


    @Override
    public void buildMainLayout() {

        setSizeFull();

        //top header
        topHorizontal.addComponent(closeButton);
        topHorizontal.addComponent(vehicleName);
        topHorizontal.addComponent(vehicleStatus);
        //topHorizontal.addComponent(signal[0]);
        topHorizontal.addComponent(alarmCounter);
        //topHorizontal.addComponent(refreshSession);
        //topHorizontal.addComponent(timeLeft);

        vehicleName.setStyleName("h2");
        vehicleName.setSizeUndefined();
        vehicleStatus.setSizeUndefined();
//        refreshSession.setStyleName("v-session-refresh");
//        timeLeft.setSizeUndefined();
//        timeLeft.setStyleName("h3");
//        timeLeft.setImmediate(true);
        topHorizontal.setImmediate(true);

        topHorizontal.setComponentAlignment(closeButton, Alignment.MIDDLE_LEFT);
        topHorizontal.setComponentAlignment(vehicleName, Alignment.MIDDLE_CENTER);
        topHorizontal.setComponentAlignment(vehicleStatus, Alignment.MIDDLE_CENTER);
        //topHorizontal.setComponentAlignment(signal[0], Alignment.MIDDLE_CENTER);
        topHorizontal.setComponentAlignment(alarmCounter, Alignment.MIDDLE_CENTER);
        //topHorizontal.setComponentAlignment(refreshSession, Alignment.MIDDLE_CENTER);
        //topHorizontal.setComponentAlignment(timeLeft, Alignment.MIDDLE_CENTER);
        topHorizontal.setMargin(true);

        mainLayout.addComponent(topHorizontal, BorderLayout.Constraint.NORTH);

        buildVehicleTabSheet(vehicleTabSheet);
        centerVertical.addComponent(vehicleTabSheet);
        centerVertical.addComponent(myDropPanel);
        vehicleTabSheet.setVisible(false);
        myDropPanel.setVisible(true);

        mainLayout.addComponent(centerVertical, BorderLayout.Constraint.CENTER);

        footer.addComponent(new HorizontalSplitter());
        footer.addComponent(vehicleHistoryData);

        mainLayout.addComponent(footer, BorderLayout.Constraint.SOUTH);
        mainLayout.setSizeFull();
        mainLayout.setNSEWVisible(false);
        mainLayout.getSouth().setHeight("20px");
        mainLayout.expand();

        setElementIds();
        setCompositionRoot(mainLayout);
    }

    private void buildVehicleTabSheet(TabSheet vehicleTabSheet) {
        //Process Data Table
        //Map
        //VNC
        OnDiagPageTab vehicleTab;
        this.componentMap.put(VehicleTabNameEnum.V_TAB_MAIN, tabGaugeInstrumentPanel);
        this.componentMap.put(VehicleTabNameEnum.V_TAB_ALARM, tabAlarmPanel);
        //this.componentMap.put(VehicleTabNameEnum.V_TAB_PROCESS_DATA, processNotImplemented);
        //this.componentMap.put(VehicleTabNameEnum.V_TAB_SPEED_INSTRUMENTS, speedNotImplemented);
        this.componentMap.put(VehicleTabNameEnum.V_TAB_LOCATION, tabMapPanel);
        this.componentMap.put(VehicleTabNameEnum.V_TAB_VNC, tabVncPanel);


        tabAlarmPanel.setImmediate(true);


        for (VehicleTabNameEnum t : VehicleTabNameEnum.values()) {

            Component c = this.componentMap.get(t);
            if (c == null) {
                continue;
            }


            vehicleTab = new OnDiagPageTab<>(c, t);
            this.vehicleTabMap.put(t, vehicleTab);
            String tabName = ((VehicleTabNameEnum) vehicleTab.getTab()).getName();
            String tabIcon = ((VehicleTabNameEnum) vehicleTab.getTab()).getIcon();
            String name = LanguageManager.getInstance().getValue(this.currentLanguage, tabName);
            vehicleTabSheet.addTab(vehicleTab.getComponent(), name, new ThemeResource(tabIcon));
            vehicleTabSheet.getTab(vehicleTab.getComponent())
                    .setId(PageElementId.VEHICLE_TAB_PREFIX +
                            vehicleTab.getComponent().getClass().getSimpleName());

        }


    }

    @Override
    public void setCaptionNames(String currentLanguage) {
        this.currentLanguage = currentLanguage;

        for (VehicleTabNameEnum e : this.componentMap.keySet()) {

            Component c = this.componentMap.get(e);
            TabSheet.Tab tab = vehicleTabSheet.getTab(c);
            tab.setCaption(LanguageManager.getInstance().getValue(this.currentLanguage, e.getName()));
        }

        this.tabAlarmPanel.setCaptionNames(currentLanguage);
        this.tabGaugeInstrumentPanel.setCaptionNames(currentLanguage);
        this.myDropPanel.setCaptionNames(currentLanguage);


    }

    public void close() {

        stopProcessData();
        this.panelStatus = VehicleStatusEnum.ST_NONE;
        myVehicle = null;
        tabAlarmPanel.closeDown();
        tabVncPanel.closeDown();
        showInstruments();
    }

    public Vehicle getMyVehicle() {

        return myVehicle;
    }

    private boolean showInstruments() {
        boolean v = false;

        if (myVehicle != null) {
            v = true;
            vehicleName.setValue(String.format("%s %s", myVehicle.getVehicleType(), myVehicle.getVehicleNumber()));
            this.alarmCounter.setValue(myVehicle.getActiveAlarms().size());

            topHorizontal.setSizeUndefined();
            topHorizontal.setExpandRatio(vehicleName, 5);
            topHorizontal.setExpandRatio(vehicleStatus, 1);
            //topHorizontal.setExpandRatio(alarmCounter, 1.5f);
            //topHorizontal.setExpandRatio(signal[0], 1.5f);
            topHorizontal.setSizeFull();
            mainLayout.setSizeFull();
        } else {
            topHorizontal.setExpandRatio(vehicleName, 1);
        }

        mainLayout.getNorth().setVisible(v);
        mainLayout.getSouth().setVisible(v);
        vehicleTabSheet.setVisible(v);
        myDropPanel.setVisible(!v);
        mainLayout.expand();

        return v;

    }

    @Override
    public void setVehicle(Vehicle vehicle) {

        if(vehicle.equals(this.myVehicle)){
            return;
        }

        if(this.myVehicle != null){
            stopProcessData();
        }

        this.myVehicle = vehicle;
        //startProcessData();

        this.tabGaugeInstrumentPanel.setVehicle(vehicle);
        this.tabAlarmPanel.setVehicle(vehicle);
        this.tabMapPanel.setVehicle(this.myVehicle);
        this.tabVncPanel.setVehicle(this.myVehicle);
        this.vehicleHistoryData.setVehicle(this.myVehicle);

        showInstruments();
        updateStatus();


    }

    @Override
    public void updateStatus() {

        if (myVehicle != null) {

            if (this.panelStatus != myVehicle.getStatus()) {
                this.panelStatus = myVehicle.getStatus();
                vehicleStatus.setValue(LanguageManager.getInstance().
                        getValue(currentLanguage, myVehicle.getStatus().toString()));

                switch (myVehicle.getStatus()) {
                    case ST_OFFLINE:
                        headerCurrentStyleName = "v-instruments-offline";
                        //this.timeCountDown = -1;
                        this.tabAlarmPanel.updateStatus();
                        break;
                    case ST_ONLINE:
                        headerCurrentStyleName = "v-instruments-online";
                        startProcessData();
                        //this.timeCountDown = WebConfig.INSTRUMENT_TIME_OUT;
                        //String newTime = DateTimeUtils.splitToComponentTimes(timeCountDown);
                        //timeLeft.setValue(newTime);
                        break;
                    case ST_SUSPENDED:
                        headerCurrentStyleName = "v-instruments-suspended";
                        //this.timeCountDown = -1;
                        break;

                }

                topHorizontal.setStyleName(headerCurrentStyleName);
            }

        }
    }

    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {
        this.tabGaugeInstrumentPanel.setProcessValue(tag, genericTagValue);
    }

    public void setAlarmCounter(AlarmCounter alarmCounter) {

    }

    public OnDiagMainView getOnDiagMainViewParent() {
        return onDiagMainViewParent;
    }

    public void setAlarmRefresh() {
        this.tabAlarmPanel.setAlarmRefresh();
    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {
        this.alarmCounter.setValue(this.myVehicle.getActiveAlarms().size());
        this.tabGaugeInstrumentPanel.setAlarmValue(tag, snapShotAlarmTagValue);
        this.tabAlarmPanel.setAlarmValue(tag, snapShotAlarmTagValue);
    }

    public GaugeInstrumentPanel getTabGaugeInstrumentPanel() {
        return tabGaugeInstrumentPanel;
    }

    private void setElementIds() {

        this.closeButton.setId(this.closeButton.getClass().getSimpleName());
        this.alarmCounter.setId(this.alarmCounter.getClass().getSimpleName());

    }


    public boolean centerMap(Vehicle myVehicle) {
        this.tabMapPanel.centerMap(myVehicle);
        return this.myDropPanel.centerMap(myVehicle);

    }

    public boolean isInMap(Vehicle myVehicle) {

        return this.myDropPanel.isInMap(myVehicle);
    }

    public void startProcessData() {
        if(this.myVehicle != null){
            this.myVehicle.incrementSessionCount();

        }
    }


    public void stopProcessData() {
        if(this.myVehicle != null){
            this.myVehicle.decrementSessionCount();

        }
    }
}
