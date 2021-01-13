package co.luism.iot.web.ui.vehicle;
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

import co.luism.common.DateTimeUtils;
import co.luism.datacollector.messages.DCLifeSignParamEnum;
import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.User;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.common.WebUtils;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.pages.OnDiagMainView;
import co.luism.iot.web.ui.common.HorizontalSplitter;
import co.luism.iot.web.ui.vehicle.alarm.AlarmCounter;
import co.luism.iot.web.ui.vehicle.utils.FavouriteIcon;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.ui
 * Created by luis on 02.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class VehicleGui extends CustomComponent implements OnDiagCustomComponent {

    private final GridLayout mainLayout = new GridLayout(6,10);
    private Image ico;
    private final Label vehicleName = new Label("Name");
    private FavouriteIcon fav;
    private final Label type = new Label("type");
    private final Label fleet = new Label("fleet");
    private final Label org = new Label("organization");
    private final Label sms = new Label("0000");
    private final Label status = new Label("OFFLINE");
    private final Label position = new Label("Lat:0.0, Long:0.0");
    private final Label vehicleId = new Label();
    private final Label update = new Label("Last updated on:2014-09-01 15:30");
    private Image target;

    //private final Label sessionNumber = new Label("client cnx:0");
    private final AlarmCounter alarmCounter = new AlarmCounter();
    private final Vehicle myVehicle;
    private final User myUser;
    private VehicleStatusEnum vehicleStatus = VehicleStatusEnum.ST_NONE;
    private OnDiagMainView parentMain;
    //private SessionStatusEnum sessionCount = SessionStatusEnum.SS_ST_NONE;

    public VehicleGui(User user, Vehicle v, OnDiagMainView parentMain){
        this.myUser = user;
        this.myVehicle = v;
        this.parentMain = parentMain;

    }

    @Override
    public void startUp() {

        ThemeResource themeResource;
        String imagePath = String.format("icons/fleet/%s.png", this.myVehicle.getMyFleet().getIcon());
        if(WebUtils.themeResourceExist(imagePath)){
            themeResource = new ThemeResource(imagePath);
        } else {
            themeResource = new ThemeResource("icons/fleet/default_fleet.png");
        }
        ico = new Image(null, themeResource);

        MouseEvents.ClickListener findInMap = new MouseEvents.ClickListener() {

            @Override
            public void click(MouseEvents.ClickEvent event) {
                if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
                    centerActiveMap();

                }
            }
        };

        if(this.myUser.isFavourite(this.myVehicle.getVehicleId())){
           fav = new FavouriteIcon(true);
        } else {
            fav = new FavouriteIcon(false);
        }

        fav.setImmediate(true);
        fav.setId(PageElementId.VEHICLE_GUI_FAV);


        fav.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent clickEvent) {
                if (clickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
                    boolean favourite = !fav.getFavourite();
                    myUser.setFavourite(myVehicle.getVehicleId(), favourite);
                    fav.setFavourite(favourite);
                    if(parentMain.getFavouriteIcon().getFavourite()){
                        setVisible(favourite);
                    }
                }
            }
        });

        Resource res = new ThemeResource("icons/target.png");
        // Display the image without caption
        target = new Image(null, res);
        target.addClickListener(findInMap);
        target.setStyleName("v-find-map");

        boolean ret = this.parentMain.isInMap(myVehicle);
        target.setVisible(ret);

        buildMainLayout();
        bindData();

    }

    private void centerActiveMap() {
        boolean ret = this.parentMain.centerMap(this.myVehicle);

        if(!ret){
            new Notification(LanguageManager.getInstance().getValue("NOT_FOUND_IN_MAP") , Notification.Type.ASSISTIVE_NOTIFICATION)
                    .show(Page.getCurrent());
        }
    }

    @Override
    public void closeDown() {

    }

    @Override
    public void buildMainLayout() {

        mainLayout.setMargin(true);
        mainLayout.setWidth(WebConfig.VEHICLE_GUI_WIDTH, Unit.PIXELS);
        setHeight(WebConfig.VEHICLE_GUI_HEIGHT, Unit.PIXELS);

        mainLayout.addComponent(ico, 0, 0);
        mainLayout.addComponent(vehicleName, 1, 0, 2, 0);
        vehicleName.setSizeUndefined();
        //mainLayout.addComponent(signal[0], 3, 0);
        mainLayout.addComponent(alarmCounter, 3, 0, 4, 0);
        mainLayout.addComponent(fav, 5, 0);
        mainLayout.addComponent(new HorizontalSplitter(), 0, 1, 5, 1 );
        mainLayout.addComponent(vehicleId, 0, 2, 5, 2);
        vehicleId.setSizeUndefined();
        mainLayout.addComponent(type, 0, 3, 2, 3);
        type.setSizeUndefined();
        mainLayout.addComponent(sms, 3, 3, 5, 3);
        sms.setSizeUndefined();
        mainLayout.addComponent(fleet, 0, 4, 5, 4);
        fleet.setSizeUndefined();
        mainLayout.addComponent(org, 0, 5, 3, 5);
        org.setSizeUndefined();
        mainLayout.addComponent(status, 4, 5);
        status.setSizeUndefined();
        mainLayout.addComponent(new HorizontalSplitter(), 0, 6, 5, 6);
        mainLayout.addComponent(position, 0, 7, 4, 7);
        position.setSizeUndefined();
        mainLayout.addComponent(target, 5, 7);
        target.setId(PageElementId.VEHICLE_GUI_CENTER_MAP);
        mainLayout.addComponent(update, 0, 8, 5, 8);
        //mainLayout.addComponent(sessionNumber, 0, 9, 5, 9);

        vehicleName.setStyleName("h2");
        type.setStyleName("h4");
        fleet.setStyleName("h3");
        org.setStyleName("h3");
        vehicleId.setStyleName("h4");
        sms.setStyleName("h4");
        status.setStyleName("h4");
        position.setStyleName("h4");

        mainLayout.setComponentAlignment(ico, Alignment.MIDDLE_LEFT);
        mainLayout.setComponentAlignment(vehicleName, Alignment.MIDDLE_CENTER);
        mainLayout.setComponentAlignment(alarmCounter, Alignment.MIDDLE_RIGHT);

        mainLayout.setComponentAlignment(sms, Alignment.MIDDLE_LEFT);
        mainLayout.setComponentAlignment(position, Alignment.MIDDLE_LEFT);
        mainLayout.setComponentAlignment(fav, Alignment.MIDDLE_CENTER);

        mainLayout.setColumnExpandRatio(1, 1);
        mainLayout.setStyleName("v-offline");
        setCompositionRoot(mainLayout);

    }

    private void bindData() {

        if(myVehicle == null){
            return;
        }

        mainLayout.setId(PageElementId.VEHICLE_ID_PREFIX + this.myVehicle.getVehicleId());
        position.setId(PageElementId.GPS_FIX_ID_PREFIX + this.myVehicle.getVehicleId());

        this.vehicleName.setValue(String.format("%s %s", myVehicle.getVehicleType(), myVehicle.getVehicleNumber()));
        this.type.setValue(myVehicle.getVehicleType());
        this.fleet.setValue(String.format("%s %s", myVehicle.getMyFleet().getName(),
                myVehicle.getMyFleet().getMyConfiguration().getProjectCode()));
        this.org.setValue(myVehicle.getMyFleet().getMyConfiguration().getMyOrganization().getName());
        this.sms.setValue(myVehicle.getSmsNumber());
        this.vehicleId.setValue(myVehicle.getVehicleId());
        this.alarmCounter.setValue(this.myVehicle.getActiveAlarms().size());

        this.updateStatus();
        this.updatePosition();


    }

    public void updatePosition() {
        if(this.myVehicle == null){
            return;
        }
        GenericTagValue genericTagValue;
        //check gps status
        genericTagValue = this.myVehicle.getSnapShotValue(GenericTagValue.class,
                EventTypeEnum.TAG_DATA_TYPE_SYSTEM, DCLifeSignParamEnum.GPS_STATUS.name());

        if(genericTagValue == null){
            return;
        }

        if(genericTagValue.getValue() == 0 || genericTagValue.getValue() > WebConfig.MAX_GPS_STATUS_VALUE){
            String value = String.format("No GPS Fix");
            this.position.setValue(value);
            return;
        }

        genericTagValue = this.myVehicle.getSnapShotValue(GenericTagValue.class, EventTypeEnum.TAG_DATA_TYPE_SYSTEM, DCLifeSignParamEnum.LATITUDE.name());
        if(genericTagValue == null){
            return;
        }
        double latitude = genericTagValue.getValue() * genericTagValue.getScale();
        genericTagValue = this.myVehicle.getSnapShotValue(GenericTagValue.class, EventTypeEnum.TAG_DATA_TYPE_SYSTEM, DCLifeSignParamEnum.LONGITUDE.name());

        if(genericTagValue == null){
            return;
        }
        double longitude = genericTagValue.getValue() * genericTagValue.getScale();
        String value = String.format("Lat:%.2f, Long:%.2f", latitude, longitude);
        this.position.setValue(value);

    }

    public Vehicle getMyVehicle() {
        return myVehicle;
    }

    public AlarmCounter getAlarmCounter() {
        return alarmCounter;
    }

//    public void setNumberOfSessions() {
//        String sessionCount = String.format("%s%d %s", LanguageManager.getInstance().getValue("V_CLIENT_CNX"),
//                myVehicle.getSessionCount(), myVehicle.getSessionStatus().name());
//        this.sessionNumber.setValue(sessionCount);
//
//    }

    @Override
    public void setCaptionNames(String currentLanguage) {

    }

    public void updateStatus() {

        if(this.vehicleStatus != myVehicle.getStatus()){
            this.vehicleStatus = myVehicle.getStatus();
            String ts = DateTimeUtils.getTimeStringUtc(myVehicle.getStatusUpdateTime());

            this.update.setValue(String.format("%s %s", LanguageManager.getInstance().getValue("LAST_UPDATE")
                    , ts));

            this.status.setValue(LanguageManager.getInstance().getValue(myVehicle.getStatus().toString()));

            switch (myVehicle.getStatus()){
                case ST_ONLINE:
                    mainLayout.setStyleName("v-online");

                    break;
                case ST_OFFLINE:
                    mainLayout.setStyleName("v-offline");

                    break;
                case ST_SUSPENDED:
                    mainLayout.setStyleName("v-suspended");
                 break;

            }

        }

//        if(this.sessionCount != myVehicle.getSessionStatus()){
//            this.sessionCount = myVehicle.getSessionStatus();
//
//            String sessionCount = String.format("%s%d %s", LanguageManager.getInstance().getValue("V_CLIENT_CNX"),
//                    myVehicle.getSessionCount(), myVehicle.getSessionStatus().name()  );
//            this.sessionNumber.setValue(sessionCount);
//
//        }

        this.updatePosition();


    }


    public void updateAlarmCount() {

      this.alarmCounter.setValue(this.myVehicle.getActiveAlarms().size());

    }

    public boolean isFavourite() {
        return fav.getFavourite();
    }
}
