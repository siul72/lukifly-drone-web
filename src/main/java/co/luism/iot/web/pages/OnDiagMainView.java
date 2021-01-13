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

/**
 * @file main.cpp
 * @brief
 * @author L. Coelho
 * @date 2014-01-29
 */

package co.luism.iot.web.pages;


import co.luism.diagnostics.common.DiagnosticsEvent;
import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.*;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.common.WebDropHandler;
import co.luism.iot.web.common.WebPageEnum;
import co.luism.iot.web.interfaces.ParentView;
import co.luism.iot.web.ui.common.HorizontalSplitter;
import co.luism.iot.web.ui.main.FilterPopUp;
import co.luism.iot.web.ui.vehicle.VehicleGui;
import co.luism.iot.web.ui.vehicle.VehicleLoader;
import co.luism.iot.web.ui.vehicle.VehicleMainPanel;
import co.luism.iot.web.ui.vehicle.utils.FavouriteIcon;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Title;
import com.vaadin.event.MouseEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main Page Class
 * <img src="doc-files/main_main_javadoc.png" alt="Example of the Main GUI"/>
 */

@SuppressWarnings("serial")
@PreserveOnRefresh
@Title("Online Diagnostics - Main")
public class OnDiagMainView extends CustomComponent implements View, ParentView {

    private static final Logger LOG = Logger.getLogger(OnDiagMainView.class);
    public final static WebPageEnum NAME = WebPageEnum.main;
    private PageHeader headerLayout;
    private StatusFrame statusFrame;
    private FilterPopUp filterPopUp;
    private Button clearFilter;
    private VehicleLoader myVehicleLoader;
    private VerticalLayout fleetLayout;
    private Map<String, VehicleGui> myVehicleGuiList;
    private WebFeederThread myFeeder;
    private ProgressBar progressBarVehicleLoad;
    private Label progressStatus;
    private WebDropHandler dragAndDropHandler;
    private VehicleMainPanel vehicleMainPanel;
    private User user;
    private FavouriteIcon favouriteIcon;

    MouseEvents.ClickListener favClickListener = new MouseEvents.ClickListener() {
        @Override
        public void click(MouseEvents.ClickEvent clickEvent) {
            if (clickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
                boolean favouriteFilter = !favouriteIcon.getFavourite();
                favouriteIcon.setFavourite(favouriteFilter);
                if(favouriteFilter){
                   showFavouriteVehicles();
                } else {
                   showAllVehicles();
                }
            }
        }
    };



    Button.ClickListener filterClickListener = new Button.ClickListener() {
        @Override
        public void buttonClick(Button.ClickEvent clickEvent) {
            showAllVehicles();

        }
    };

    public OnDiagMainView() {

    }

    private void startFeeder() {

        myFeeder.start();
    }

    @Override
    public void startUp(){
        headerLayout = new PageHeader(this);
        statusFrame = new StatusFrame();
        filterPopUp = new FilterPopUp();
        filterPopUp.init(this);
        clearFilter = new Button();
        fleetLayout = new VerticalLayout();
        myVehicleGuiList = new HashMap<>();
        myFeeder = new WebFeederThread(this);
        progressBarVehicleLoad = new ProgressBar(new Float(0.0));
        progressStatus = new Label("not running");
        dragAndDropHandler = new WebDropHandler(this);
        vehicleMainPanel = new VehicleMainPanel();
        vehicleMainPanel.init(this);
        favouriteIcon = new FavouriteIcon(false);

        buildLayout();

    }

    @Override
    public void closeDown() {
        vehicleMainPanel.closeDown();
        myVehicleLoader.closeDown();
        myFeeder.closeDown();
        try {
            myFeeder.join(3000);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        LOG.info("exit from main view and feeder thread");

    }

    @Override
    public void buildLayout() {
        //create Main Layout
        VerticalLayout mainLayout = new VerticalLayout();
        setCompositionRoot(mainLayout);

        //set main layout
        mainLayout.setSizeFull();
        setWidth("100%");
        setHeight("100%");

        //Header
        mainLayout.addComponent(headerLayout);
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        mainLayout.addComponent(splitPanel);
        splitPanel.setSplitPosition(WebConfig.VEHICLE_GUI_WIDTH + 40, Unit.PIXELS);

        //right side
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setMargin(false);
        DragAndDropWrapper targetWrapper = new DragAndDropWrapper(vehicleMainPanel);
        targetWrapper.setDropHandler(dragAndDropHandler);
        rightLayout.addComponent(targetWrapper);

        splitPanel.setSecondComponent(rightLayout);

        //left side
        VerticalLayout leftLayout = new VerticalLayout();
        HorizontalLayout topHorizontal = new HorizontalLayout();

        leftLayout.addComponent(statusFrame);

        PopupView popupFilter = new PopupView(filterPopUp);
        popupFilter.setWidth("32px");
        popupFilter.setHeight("32px");
        popupFilter.setStyleName("filter-popup");
        topHorizontal.addComponent(popupFilter);
        topHorizontal.setWidth("300px");
        topHorizontal.addComponent(clearFilter);
        clearFilter.setVisible(false);
        topHorizontal.addComponent(favouriteIcon);
        favouriteIcon.addClickListener(favClickListener);
        favouriteIcon.setId(PageElementId.FAVOURITE_FILTER);
        topHorizontal.setExpandRatio(popupFilter, 1);
        topHorizontal.setComponentAlignment(favouriteIcon, Alignment.MIDDLE_RIGHT);

        leftLayout.addComponent(topHorizontal);
        leftLayout.setComponentAlignment(topHorizontal, Alignment.TOP_CENTER);
        leftLayout.addComponent(new HorizontalSplitter());
        leftLayout.addComponent(progressBarVehicleLoad);
        progressBarVehicleLoad.setWidth(WebConfig.VEHICLE_GUI_WIDTH, Unit.PIXELS);
        progressBarVehicleLoad.setSizeFull();
        progressBarVehicleLoad.setImmediate(true);
        progressBarVehicleLoad.setEnabled(false);
        progressBarVehicleLoad.setVisible(false);
        leftLayout.addComponent(progressStatus);
        progressStatus.setImmediate(true);
        progressStatus.setVisible(false);

        leftLayout.addComponent(fleetLayout);
        splitPanel.setFirstComponent(leftLayout);

        mainLayout.setExpandRatio(splitPanel, 1);
        leftLayout.setMargin(true);
        rightLayout.setMargin(true);

        clearFilter.addClickListener(filterClickListener);

    }

    public VehicleMainPanel getVehicleMainPanel() {
        return vehicleMainPanel;
    }

    public Map<String, VehicleGui> getMyVehicleGuiList() {
        return myVehicleGuiList;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        user = getSession().getAttribute(User.class);
        if (user == null) {
            LOG.error("user is invalid in session!");
            return;
        }

        startUp();
        headerLayout.setUserName(user);
        filterPopUp.populateFleetComboBoxes(user);

        List vehicleList = WebManagerFacade.getInstance().getAllVehicles(user);
        myVehicleLoader = new VehicleLoader(this, vehicleList, this.progressBarVehicleLoad, this.progressStatus);
        myVehicleLoader.loadInitialValues(WebConfig.NUMBER_OF_VEHICLE_LOAD);
        new Thread(myVehicleLoader).start();
        startFeeder();
        this.vehicleMainPanel.setVehicleList(vehicleList);
        startUpVehicleList();

        updateLanguageGui(user.getLanguage());

    }

    private void startUpVehicleList() {
        for(VehicleGui vg : myVehicleGuiList.values()){
            vg.startUp();
        }
    }

    public void populateFleetLayout(List<Vehicle> listOfVehicles) {

        for (Vehicle v : listOfVehicles) {
            VehicleGui vg = new VehicleGui(this.user, v, this);
            this.myVehicleGuiList.put(v.getVehicleId(), vg);
            this.statusFrame.addVehicle(v);
            DragAndDropWrapper wrapper = new DragAndDropWrapper(vg);
            wrapper.setSizeUndefined();
            //wrapper.setDragImageComponent(vg);
            wrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
            wrapper.setData(v);
            fleetLayout.addComponent(wrapper);
        }

        this.statusFrame.updateAlarmCounter();

    }

    @Override
    public void updateLanguageGui(String currentLanguage) {

        if (vehicleMainPanel == null) {

            return;
        }

        clearFilter.setCaption(LanguageManager.getInstance().getValue(currentLanguage, "CLEAR_FILTER"));
        vehicleMainPanel.setCaptionNames(currentLanguage);
        filterPopUp.setCaptionNames(currentLanguage);
    }

    @Override
    public void switchDataSource(Class aClass) {

    }

    @Override
    public WebPageEnum getNAME() {
        return NAME;
    }



    public void updateVehicleStatus(Vehicle currentVehicle) {


        VehicleGui vg = myVehicleGuiList.get(currentVehicle.getVehicleId());
        statusFrame.updateStatus(currentVehicle);

        if (vg != null) {
            vg.updateStatus();
        }

        if (currentVehicle.equals(vehicleMainPanel.getMyVehicle())) {
            vehicleMainPanel.updateStatus();
        }


    }

    public void sendDataTagToDisplay(DiagnosticsEvent event) {

        //check if the event vehicle is on
        if (event.getCurrentVehicle() == null) {
            LOG.error(String.format("The event don't have vehicle assigned"));
            return;
        }

        //update vehicle panel
        setValueVehicleMainPanel(event);
        //update Alarm Count
        updateAlarmCountAndEvents(event);

    }

    private void updateAlarmCountAndEvents(DiagnosticsEvent event) {

        Vehicle vehicle = event.getCurrentVehicle();
        VehicleGui vehicleGui = this.myVehicleGuiList.get(vehicle.getVehicleId());
        EventTypeEnum type = event.getTagType();
        switch (type) {
            case ALARM_REFRESH:
                if (vehicleGui != null) {
                    vehicleGui.updateAlarmCount();
                }

                statusFrame.updateAlarms(vehicle);

                if (event.getCurrentVehicle().equals(vehicleMainPanel.getMyVehicle())) {
                    vehicleMainPanel.setAlarmRefresh();
                }
                break;

            case TAG_DATA_TYPE_EVENT:
                //get tag value
                for (Integer id : event.getListOfUpdatedItems()) {
                    DataTag tag = WebManagerFacade.getInstance().getTagBySourceId(type, vehicle.getConfigurationId(), id);
                    SnapShotAlarmTagValue snapShotAlarmTagValue = vehicle.getAlarmTag(tag.getTagId());
                    vehicleGui.updateAlarmCount();
                    statusFrame.updateAlarms(vehicle);
                    if (event.getCurrentVehicle().equals(vehicleMainPanel.getMyVehicle())) {
                        vehicleMainPanel.setAlarmValue(tag, snapShotAlarmTagValue);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setValueVehicleMainPanel(DiagnosticsEvent event) {
        Vehicle vehicle = event.getCurrentVehicle();
        //otherwise the values are dropped
        if (vehicle.equals(vehicleMainPanel.getMyVehicle())) {
            EventTypeEnum type = event.getTagType();

            switch (type) {
                case TAG_DATA_TYPE_PD:

                    for (Integer id : event.getListOfUpdatedItems()) {
                        DataTag tag = WebManagerFacade.getInstance().getTagBySourceId(type, vehicle.getConfigurationId(), id);
                        if (tag != null) {
                            GenericTagValue genericTagValue = vehicle.getSnapShotValue(GenericTagValue.class, type, tag);
                            vehicleMainPanel.setProcessValue(tag, genericTagValue);
                        } else {
                            LOG.warn(String.format("found no tag sourceId %d and type %d",
                                    id, type.getValue()));
                        }

                    }
                    break;
                case TAG_DATA_TYPE_EVENT:
                default:
                    break;

            }


        } else {
            LOG.debug(String.format("panel for vehicle %s is not shown", event.getCurrentVehicle().getVehicleId()));
        }

    }

    @Override
    public void filterVehicles(Organization org, Fleet f, String type) {

        int countHidden = 0;

        for (VehicleGui vg : myVehicleGuiList.values()) {
            Vehicle v = vg.getMyVehicle();

            if (type != null) {
                if (!v.getVehicleType().equals(type)) {
                    vg.setVisible(false);
                    countHidden++;
                    continue;
                }
            }

            if (f != null) {

                if (!v.getMyFleet().equals(f)) {
                    vg.setVisible(false);
                    countHidden++;
                    continue;
                }

            }

            if (org != null) {

                if (!v.getMyFleet().getMyConfiguration().getMyOrganization().equals(org)) {
                    vg.setVisible(false);
                    countHidden++;
                    continue;
                }

            }

            vg.setVisible(true);
        }

        if(countHidden > 0){
            clearFilter.setVisible(true);
        } else {
            clearFilter.setVisible(false);
        }
    }

    private void showFavouriteVehicles() {

        for (VehicleGui vg : myVehicleGuiList.values()) {
           if(!vg.isFavourite()){
               vg.setVisible(false);
           } else {
               vg.setVisible(true);
           }

        }

    }

    private void showAllVehicles(){
        for (VehicleGui vg : myVehicleGuiList.values()){
            vg.setVisible(true);
        }

        clearFilter.setVisible(false);
    }

    public boolean centerMap(Vehicle myVehicle) {
        return this.vehicleMainPanel.centerMap(myVehicle);

    }

    public boolean isInMap(Vehicle myVehicle) {

        return this.vehicleMainPanel.isInMap(myVehicle);
    }

    public FavouriteIcon getFavouriteIcon() {
        return favouriteIcon;
    }
}