package co.luism.iot.web.ui.vehicle.map;

import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.OnDiagProperties;
import co.luism.iot.web.common.WebConfig;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

/**
 * Created by luis on 03.02.15.
 */
public class DCGoogleMapMarker extends GoogleMapMarker {

    private int status = -1;
    private LatLon realPos = new LatLon();
    private String info;
    private int infoLines;
    private String infoHeight;
    private boolean cluster = false;
    private boolean moved = false;
    private boolean ready = false;
    private static final String imagePath = "VAADIN/themes/mytheme/icons/fleet";
    private final String mapPointerFile;

    public DCGoogleMapMarker(Vehicle vehicle){
        setAnimationEnabled(false);

        setDraggable(false);

        mapPointerFile = vehicle.getMyFleet().getMapPointer();
        //set caption
        //String caption = String.format("%s:%s:%s",vehicle.getVehicleType(), vehicle.getVehicleNumber(),
        //        vehicle.getMyFleet().getName());
        setOptimized(OnDiagProperties.getInstance().getOptimizeMarkers());
        setCaption(vehicle.getVehicleId());


    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        String fleetIcon;
        String statusString;

        if(status == VehicleStatusEnum.ST_ONLINE.getValue()){
            statusString = "on.png";
        } else {
            statusString = "off.png";
        }

        fleetIcon = String.format("%s/%s_%s", imagePath, mapPointerFile, statusString);
        setIconUrl(fleetIcon);
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public LatLon getRealPos() {
        return realPos;
    }

    public void setRealPos(LatLon realPos) {
        this.realPos = realPos;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public int getInfoLines() {
        return infoLines;
    }

    public void setInfoLines(int infoLines) {
        this.infoLines = infoLines;
        this.infoHeight = String.format("%dpx", infoLines * WebConfig.INFO_WINDOW_NUMBER_PIXELS_PER_LINE);
    }

    public String getInfoHeight() {
        return infoHeight;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
