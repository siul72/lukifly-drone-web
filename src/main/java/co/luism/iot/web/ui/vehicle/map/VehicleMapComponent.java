package co.luism.iot.web.ui.vehicle.map;

import co.luism.datacollector.DataCollectorDataScanner;
import co.luism.datacollector.messages.DCLifeSignParamEnum;
import co.luism.diagnostics.common.DiagnosticsEvent;
import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotGenericValue;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.diagnostics.interfaces.IDiagnosticsEventHandler;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.common.WebUtils;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Created by luis on 07.11.14.
 */
public class VehicleMapComponent extends CustomComponent implements OnDiagCustomComponent, IDiagnosticsEventHandler {

    private static final Logger LOG = Logger.getLogger(VehicleMapComponent.class);
    private static final String imagePath = "VAADIN/themes/mytheme/icons/fleet";
    private GoogleMap googleMap;
    private GoogleMapMarker vehicleMarker;
    //private Map<DCLifeSignParamEnum, Integer> positionTagIdMap;
    private Vehicle vehicle;


    private static final String apiKey = "";

    public VehicleMapComponent() {
        startUp();
    }

    @Override
    public void startUp() {
        googleMap = new GoogleMap(null, null, null);
        //positionTagIdMap = DataCollectorDataScanner.getInstance().getPositionTagIdMap();
        buildMainLayout();

    }

    @Override
    public void closeDown() {

        DataCollectorDataScanner.getInstance().removeListener(this);
        vehicle = null;

    }


    public void setVehicle(Vehicle v) {

        if(v == null){
            return;
        }

        if(vehicle == null){
            DataCollectorDataScanner.getInstance().addListener(this);
        } else {
            if(v.equals(vehicle)){
                return;
            }
        }

        vehicle = v;
        setVehicleMarker();

    }

    private void setVehicleMarker(){

        LatLon pos = getPosition();
        if(pos == null){
            if(vehicleMarker != null) {
                googleMap.removeMarker(vehicleMarker);
                vehicleMarker = null;
            }
            return;
        }

        if(vehicleMarker != null){
            googleMap.removeMarker(vehicleMarker);
        } else {
            vehicleMarker = new GoogleMapMarker();
        }

        updateMarkerCaption();
        vehicleMarker.setDraggable(false);
        String fleetIcon = String.format("%s/%s.png", imagePath, vehicle.getMyFleet().getIcon());
        vehicleMarker.setIconUrl(fleetIcon);
        googleMap.addMarker(vehicleMarker);
        vehicleMarker.setPosition(pos);
        //googleMap.setVisualRefreshEnabled(true);
        googleMap.setCenter(pos);

    }

    private void updatePosition(){
        LatLon pos = getPosition();
        if(pos == null || vehicleMarker == null){
            return;
        }
        vehicleMarker.setPosition(pos);
        //googleMap.setVisualRefreshEnabled(true);
        return;
    }

    private LatLon getPosition(){

        Map<DCLifeSignParamEnum, SnapShotGenericValue> myPositionValueList = DataCollectorDataScanner.getInstance().getPositionForVehicle(vehicle);

        SnapShotGenericValue snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.GPS_STATUS);

        if (snapShotGenericValue == null) {
            LOG.warn("no value yet for Status");
            setDefaultCenter();
            return null;
        }

        if(snapShotGenericValue.getValue() == 0){
            LOG.warn("Status is 0 , position is invalid");
            return null;
        }


        snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.LATITUDE);

        if (snapShotGenericValue == null) {
            LOG.warn("no value yet for Latitude");
            setDefaultCenter();
            return null;
        }

        double latitude = snapShotGenericValue.getValue() * snapShotGenericValue.getScale();

        snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.LONGITUDE);

        if (snapShotGenericValue == null) {
            LOG.warn("no value yet for Longitude");
            setDefaultCenter();
            return null;
        }

        double longitude = snapShotGenericValue.getValue() * snapShotGenericValue.getScale();
        LatLon pos = new LatLon(latitude, longitude);
        return pos;
    }

    private void setDefaultCenter() {

        googleMap.setZoom(8);
        LatLon pos = new LatLon(WebConfig.DEFAULT_LATITUDE, WebConfig.DEFAULT_LONGITUDE);
        googleMap.setCenter(pos);
    }

    private void updateMarkerCaption(){

        if(vehicle == null || vehicleMarker == null){
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s %s",vehicle.getVehicleType(), vehicle.getVehicleNumber()));
        stringBuilder.append("\n");
        stringBuilder.append(vehicle.getMyFleet().getName());
        stringBuilder.append("\n");

        Collection<SnapShotGenericValue> mySnapShotList = DataCollectorDataScanner.getInstance().getValuesForVehicle(vehicle);

        if(mySnapShotList == null){
            return;
        }


        if(mySnapShotList.size() > 0){
            stringBuilder.append("\u26A1⚡⚡ Train Data ⚡⚡⚡\n");
        }

        for(SnapShotGenericValue snv : mySnapShotList){

            DataTag t = WebManagerFacade.getInstance().getTagById(snv.getTagId());
            if(t == null){
                continue;
            }

            String tagName = t.getName();
            Double scaledValue =  snv.getValue() * snv.getScale();
            stringBuilder.append(String.format("%s:%s\n", tagName, WebUtils.fmt2(scaledValue)));
        }

        vehicleMarker.setCaption(stringBuilder.toString());

    }


    @Override
    public void buildMainLayout() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        googleMap.setZoom(10);
        googleMap.setSizeFull();

        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);

        googleMap.setHeight("540px");
        googleMap.setImmediate(true);
        content.addComponent(googleMap);
        content.setExpandRatio(googleMap, 1.0f);


        setCompositionRoot(content);
    }

    @Override
    public void setCaptionNames(String currentLanguage) {

    }



    @Override
    public void handleDiagnosticsEvent(DiagnosticsEvent diagnosticsEvent) {

        if(vehicle == null){
            return;
        }

        if(diagnosticsEvent.getCurrentVehicle().getVehicleId().equals(vehicle.getVehicleId())){

            switch (diagnosticsEvent.getTagType()){
                case TAG_DATA_HISTORY_POSITION_REFRESH:
                    if(vehicleMarker != null){
                        if(vehicleMarker.isAnimationEnabled()){
                            vehicleMarker.setAnimationEnabled(false);
                        }
                    }

                    updatePosition();
                 break;

                case TAG_DATA_HISTORY_NEW_DATA:
                case TAG_DATA_HISTORY_REFRESH:
                    updateMarkerCaption();
                break;
            }
        }
    }

    public boolean centerMap(Vehicle myVehicle) {

        if(this.vehicle == null){
            return false;
        }

        if(!myVehicle.equals(this.vehicle)){
            return false;
        }

        if(vehicleMarker == null || googleMap == null){
            return false;
        }

        LatLon p = vehicleMarker.getPosition();

        if(p == null){
            return false;
        }

        if(!googleMap.getMarkers().contains(vehicleMarker)){
            return false;
        }

        googleMap.setCenter(p);

        return true;
    }
}
