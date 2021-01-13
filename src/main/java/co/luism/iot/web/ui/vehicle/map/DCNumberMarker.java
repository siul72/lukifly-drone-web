package co.luism.iot.web.ui.vehicle.map;


import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import java.util.List;

/**
 * Created by luis on 05.02.15.
 */
public class DCNumberMarker extends GoogleMapMarker {
    private static final String imagePath = "VAADIN/themes/mytheme/icons/numbers";
    private List<DCGoogleMapMarker> vehicleMarkers;


    public DCNumberMarker(List<DCGoogleMapMarker> clusters) {
        String fleetIcon;
        if(clusters.size() > 1 && clusters.size() < 10){
           fleetIcon = String.format("%s/number_%s.png", imagePath, clusters.size());
        } else {
            fleetIcon = String.format("%s/number_10.png", imagePath);
        }


        setIconUrl(fleetIcon);
        setPosition(clusters.get(0).getPosition());
        setAnimationEnabled(false);
        vehicleMarkers = clusters;
    }

    public List<DCGoogleMapMarker> getVehicleMarkers() {
        return vehicleMarkers;
    }

}
