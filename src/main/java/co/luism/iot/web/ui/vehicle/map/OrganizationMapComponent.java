package co.luism.iot.web.ui.vehicle.map;

import co.luism.datacollector.DataCollectorDataScanner;
import co.luism.datacollector.messages.DCLifeSignParamEnum;
import co.luism.diagnostics.common.DiagnosticsEvent;
import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.SnapShotGenericValue;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.diagnostics.interfaces.IDiagnosticsEventHandler;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.common.WebUtils;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapMoveListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by luis on 03.02.15.
 */
public class OrganizationMapComponent extends CustomComponent implements OnDiagCustomComponent, IDiagnosticsEventHandler {

    private static final Logger LOG = Logger.getLogger(OrganizationMapComponent.class);
    private GoogleMap googleMap;
    private Map<Vehicle, DCGoogleMapMarker> vehicleGoogleMapMarkerMap;
    private List<GoogleMapPolygon> spider;
    private List<DCNumberMarker> numberMarkers;
    private List<List<DCGoogleMapMarker>> myClusterList;
    private static final UI myUI = UI.getCurrent();
    private GoogleMapInfoWindow infoWindow;
    private static final String apiKey = "";
    private int zoomLevel = 9;

    public OrganizationMapComponent() {

    }

    public void init(Collection<Vehicle> vehicleCollection){
        startUp();
        addMarkers(vehicleCollection);
        doCluster();
        LOG.debug("...done init");
    }

    @Override
    public void startUp(){

        googleMap = new GoogleMap(null, null, null);
        googleMap.setImmediate(false);
        spider = new ArrayList<>();
        numberMarkers = new ArrayList<>();
        myClusterList = new ArrayList<>();
        infoWindow = new GoogleMapInfoWindow();
        infoWindow.setHeight("300px");
        infoWindow.setMaxWidth(180);

        googleMap.addMapMoveListener(new MapMoveListener() {
            @Override
            public void mapMoved(int myZoomLevel, LatLon center, LatLon boundsNE,
                                 LatLon boundsSW) {

                if(zoomLevel!= myZoomLevel){
                    zoomLevel = myZoomLevel;
                    if(spider == null){
                        doCluster();


                    } else {

                        if(spider.size() > 0){
                            removeSpiderAndRevertPositions();
                        } else {
                            doCluster();
                        }
                    }
                }

            }
        });

//        InfoWindowClosedListener closeInfoWindowListener = new InfoWindowClosedListener() {
//            @Override
//            public void infoWindowClosed(GoogleMapInfoWindow googleMapInfoWindow) {
//                if(spider == null){
//                    return;
//                }
//
//                if(spider.size() <= 0){
//                    return;
//                }
//
//                removeSpiderAndRevertPositions();
//            }
//        };
//        googleMap.addInfoWindowClosedListener(closeInfoWindowListener);

        MarkerClickListener markerListener = new MarkerClickListener() {
            @Override
            public void markerClicked(GoogleMapMarker googleMapMarker) {

                if(googleMapMarker instanceof DCGoogleMapMarker){
                    String info = ((DCGoogleMapMarker) googleMapMarker).getInfo();

                    if(info == null){
                        return;
                    }

                    infoWindow.setAnchorMarker(googleMapMarker);
                    infoWindow.setContent(info);
                    infoWindow.setHeight(((DCGoogleMapMarker) googleMapMarker).getInfoHeight());
                    googleMap.openInfoWindow(infoWindow);
                }

                if(googleMapMarker instanceof DCNumberMarker) {
                    GoogleMapUtils.findNearMarkersAndSplit((DCNumberMarker) googleMapMarker, googleMap, spider);
                }


            }
        };

        googleMap.addMarkerClickListener(markerListener);
        vehicleGoogleMapMarkerMap = new HashMap<>();

        buildMainLayout();
        setDefaultCenter();
        DataCollectorDataScanner.getInstance().addListener(this);

        LOG.debug("The UI is " + myUI);

    }

    private boolean doCluster() {

        List<List<DCGoogleMapMarker>> cluster = GoogleMapUtils.cluster(vehicleGoogleMapMarkerMap.values(),
                WebConfig.MAP_PIXEL_ICONS_MIN_DISTANCE, zoomLevel);

        boolean doRefresh = updateNonClusters();

        if(!clusterChange(cluster)){
            if(doRefresh){
                //googleMap.setVisualRefreshEnabled(true);
                return true;
            }
            //LOG.debug("cluster not changed...");
            return false;
        }

        //remove all number markers
        for(DCNumberMarker m : numberMarkers){
            if(googleMap.getMarkers().contains(m)){
                googleMap.removeMarker(m);
            }
        }

        numberMarkers.clear();

        for(List<DCGoogleMapMarker> clusters : cluster){
            //add a number marker
            DCNumberMarker m = new DCNumberMarker(clusters);
            numberMarkers.add(m);
            googleMap.addMarker(m);

            for(DCGoogleMapMarker marker : clusters){
                if(googleMap.getMarkers().contains(marker)){
                    googleMap.removeMarker(marker);
                }
            }
        }

        //googleMap.setVisualRefreshEnabled(true);

        return true;
    }

    private boolean updateNonClusters(){
        boolean doRefresh = false;

        for(DCGoogleMapMarker marker : vehicleGoogleMapMarkerMap.values()){

            if(marker.isCluster()){
                continue;
            }

            if(!marker.isReady()){
                continue;
            }

            if(!googleMap.getMarkers().contains(marker)){
                googleMap.addMarker(marker);
                doRefresh = true;
            }


        }

        return doRefresh;

    }

    private boolean clusterChange(List<List<DCGoogleMapMarker>> cluster) {

        if(cluster.size() <= 0){
            if(myClusterList.size() > 0){
                myClusterList.clear();
                return true;
            }

            return false;
        }

        if(myClusterList.size() <= 0){
            for(List<DCGoogleMapMarker> newClusterList : cluster){
                myClusterList.add(newClusterList);
            }

            return true;
        }

        boolean foundEqual = false;

        for(List<DCGoogleMapMarker> newClusterList : cluster){

            for(List<DCGoogleMapMarker> oldClusterList : myClusterList){
               if(newClusterList.equals(oldClusterList)){
                 foundEqual = true;
               }
            }

            if(!foundEqual){
                myClusterList.clear();
                for(List<DCGoogleMapMarker> oldClusterList : myClusterList){
                    myClusterList.add(oldClusterList);
                }

                return true;
            }
        }

        return false;
    }

    private void removeSpiderAndRevertPositions() {

        for(GoogleMapPolygon googleMapPolygon : spider){
            googleMap.removePolygonOverlay(googleMapPolygon);
        }

        spider.clear();

        for(DCGoogleMapMarker vMarker : vehicleGoogleMapMarkerMap.values()){

            if(!googleMap.getMarkers().contains(vMarker)){
                continue;
            }

            if(!vMarker.isMoved()){
                continue;
            }

            vMarker.setMoved(false);
            vMarker.setPosition(vMarker.getRealPos());

        }

        myClusterList.clear();

        doCluster();

    }

    @Override
    public void closeDown() {

        DataCollectorDataScanner.getInstance().removeListener(this);

    }

    private void addMarkers(Collection<Vehicle> vehicleCollection) {

        for(Vehicle v : vehicleCollection){
            this.vehicleGoogleMapMarkerMap.put(v, new DCGoogleMapMarker(v));
        }


        for(Vehicle v : vehicleCollection){
            setVehicleMarker(v);
        }

        doCluster();

    }


    private void setVehicleMarker(Vehicle v){

        DCGoogleMapMarker vehicleMarker = vehicleGoogleMapMarkerMap.get(v);
        setMarkerInfo(v, vehicleMarker);
        updatePosition(v);

    }

    private void setVehicleStatus(DCGoogleMapMarker vehicleMarker, int status) {

        if(status == vehicleMarker.getStatus()){
            return;
        }

        vehicleMarker.setStatus(status);

    }

    private void updatePosition(Vehicle vehicle){

        DCGoogleMapMarker vehicleMarker = vehicleGoogleMapMarkerMap.get(vehicle);

        if(vehicleMarker == null){
            return;
        }

        Map<DCLifeSignParamEnum, SnapShotGenericValue> myPositionValueList = DataCollectorDataScanner.getInstance().getPositionForVehicle(vehicle);

        SnapShotGenericValue snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.TRAIN_STATUS);

        if (snapShotGenericValue != null) {

            int status = (int)(snapShotGenericValue.getValue() * snapShotGenericValue.getScale());
            setVehicleStatus(vehicleMarker, status);
        } else {
            setVehicleStatus(vehicleMarker, VehicleStatusEnum.ST_OFFLINE.getValue());
            //LOG.warn("no status update");
        }

        snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.GPS_STATUS);

        if (snapShotGenericValue == null) {
            //LOG.warn("no value yet for Latitude");
            //setDefaultCenter();
            return;
        }

        if(snapShotGenericValue.getValue() == 0){

            return;
        }

        snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.LATITUDE);

        if (snapShotGenericValue == null) {
            //LOG.warn("no value yet for Latitude");
            //setDefaultCenter();
            return;
        }
        double latitude = snapShotGenericValue.getValue() * snapShotGenericValue.getScale();
        snapShotGenericValue = myPositionValueList.get(DCLifeSignParamEnum.LONGITUDE);

        if (snapShotGenericValue == null) {
            //LOG.warn("no value yet for Longitude");
            //setDefaultCenter();
            return;
        }

        double longitude = snapShotGenericValue.getValue() * snapShotGenericValue.getScale();
        LatLon pos = new LatLon(latitude, longitude);
        vehicleMarker.setPosition(pos);
        vehicleMarker.setReady(true);
        LOG.debug(String.format("new position for %s", vehicle.getVehicleId()));


    }



    private void setDefaultCenter() {

        googleMap.setZoom(zoomLevel);
        LatLon pos = new LatLon(WebConfig.DEFAULT_LATITUDE, WebConfig.DEFAULT_LONGITUDE);
        googleMap.setCenter(pos);
    }

    private void updateMarkerInfo(Vehicle v){

        DCGoogleMapMarker vehicleMarker = vehicleGoogleMapMarkerMap.get(v);

        if(!googleMap.getMarkers().contains(vehicleMarker)){
            return;
        }

        setMarkerInfo(v, vehicleMarker);

    }

    private void setMarkerInfo(Vehicle v, DCGoogleMapMarker vehicleMarker){


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("<center>%s %s<center><br>", v.getVehicleType(), v.getVehicleNumber()));
        stringBuilder.append(v.getMyFleet().getName());
        stringBuilder.append("<br>");
        stringBuilder.append("\u26A1⚡⚡ Train Data ⚡⚡⚡<br>");
        int count = 3;

        Collection<SnapShotGenericValue> mySnapShotList = DataCollectorDataScanner.getInstance().getValuesForVehicle(v);

        if(mySnapShotList == null){
            return;
        }

        for(SnapShotGenericValue snv : mySnapShotList){

            DataTag t = WebManagerFacade.getInstance().getTagById(snv.getTagId());
            if(t == null){
                continue;
            }

            String tagName = t.getName();
            Double scaledValue =  snv.getValue() * snv.getScale();
            stringBuilder.append(String.format("%s:%s %s<br>", tagName, WebUtils.fmt2(scaledValue),
                    t.getEngUnits()));
            count++;
        }


        vehicleMarker.setInfo(stringBuilder.toString());
        vehicleMarker.setInfoLines(count);

    }

    @Override
    public void buildMainLayout() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        googleMap.setZoom(10);
        googleMap.setSizeFull();

        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);

        googleMap.setHeight("700px");
        //googleMap.setImmediate(true);
        content.addComponent(googleMap);
        content.setExpandRatio(googleMap, 1.0f);

        setCompositionRoot(content);
    }

    @Override
    public void setCaptionNames(String currentLanguage) {

    }

    @Override
    public void handleDiagnosticsEvent(DiagnosticsEvent diagnosticsEvent) {

        Vehicle vehicle = diagnosticsEvent.getCurrentVehicle();
        if(vehicle == null){
            LOG.error(String.format("vehicle is null on event %s", diagnosticsEvent.getTagType().toString()));
            return;
        }
        DCGoogleMapMarker vehicleMarker = this.vehicleGoogleMapMarkerMap.get(vehicle);

        if(vehicleMarker == null){
            LOG.error(String.format("no marker for vehicle %s marker size %d", vehicle.getVehicleId(),
                    vehicleGoogleMapMarkerMap.size()));
            return;
        }

        EventTypeEnum eventType = diagnosticsEvent.getTagType();

        switch (eventType) {
            case TAG_DATA_HISTORY_POSITION_REFRESH:
                updateMarkerInfo(vehicle);
                updatePosition(vehicle);
                if(doCluster()){
                    if(myUI != null){
                        myUI.access(new MapUpdateRunnable());
                    }

                }
                break;

            case TAG_DATA_HISTORY_NEW_DATA:
            case TAG_DATA_HISTORY_REFRESH:
                updateMarkerInfo(vehicle);
                break;
        }
    }

    public boolean centerMap(Vehicle myVehicle) {
        DCGoogleMapMarker marker = vehicleGoogleMapMarkerMap.get(myVehicle);
        if(marker == null){
            return false;
        }

        LatLon p = marker.getPosition();

        if(p == null || googleMap == null){
            return false;
        }

//        if(!googleMap.getMarkers().contains(marker)){
//            return false;
//        }

        googleMap.setCenter(p);

        return true;
    }

    public boolean isInMap(Vehicle myVehicle) {

        if(myVehicle == null || vehicleGoogleMapMarkerMap == null){
            return false;
        }

        DCGoogleMapMarker marker = vehicleGoogleMapMarkerMap.get(myVehicle);

        if(marker == null){
            return false;
        }

        if(googleMap.getMarkers().contains(marker)){
            return true;
        }

        for(DCNumberMarker numberMarker : numberMarkers){
            if(numberMarker.getVehicleMarkers() == null){
                continue;
            }

            if(numberMarker.getVehicleMarkers().contains(marker)){
                return googleMap.getMarkers().contains(numberMarker);
            }
        }

        return false;
    }

    private class MapUpdateRunnable implements Runnable {


        @Override
        public void run() {

            //googleMap.setVisualRefreshEnabled(true);
        }
    }
}
