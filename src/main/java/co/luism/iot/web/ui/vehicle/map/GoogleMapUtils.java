package co.luism.iot.web.ui.vehicle.map;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by luis on 05.02.15.
 */
public class GoogleMapUtils {

    private static final String[] colorPallet = new String[2];
    static {
        colorPallet[0] = "#FF0000";
        colorPallet[1] = "#0000FF";
    }

    //You might wonder where did number 268435456 come from?
    // It is half of the earth circumference in pixels at zoom level 21.
    // You can visualize it by thinking of full map. Full map size is 536870912 × 536870912 pixels.
    // Center of the map in pixel coordinates is 268435456,268435456 which in latitude and longitude would be 0,0.
    private static final double OFFSET = 268435456;
    private static final double RADIUS = 85445659.4471; /* $offset / pi() */

    private static double lonToX(double lon) {
        return Math.round(OFFSET + RADIUS * lon * Math.PI / 180);
    }

    private static double latToY(double lat) {
        return Math.round(OFFSET - RADIUS *
                Math.log((1 + Math.sin(lat * Math.PI / 180)) /
                        (1 - Math.sin(lat * Math.PI / 180))) / 2);
    }

    private static int pixelDistance(double lat1, double lon1, double lat2, double lon2, int zoom) {

        double x1 = lonToX(lon1);
        double y1 = latToY(lat1);

        double x2 = lonToX(lon2);
        double y2 = latToY(lat2);

        double r = Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));

        int v = (int)r >> (21 - zoom);

        return  v;
    }

    public static List<List<DCGoogleMapMarker>>  cluster(Collection<DCGoogleMapMarker> markers, int distance, int zoom) {
        List<List<DCGoogleMapMarker>> clustered = new ArrayList<>();

    /* Loop until all markers have been compared. */
        Iterator<DCGoogleMapMarker> markerIterator = markers.iterator();
        Iterator<DCGoogleMapMarker> compareToIterator;

        while(markerIterator.hasNext()) {
            DCGoogleMapMarker marker = markerIterator.next();
            marker.setCluster(false);

        }

        markerIterator = markers.iterator();

        while(markerIterator.hasNext()){

            DCGoogleMapMarker marker = markerIterator.next();

            if(!marker.isReady()){
                continue;
            }

            if(marker.isCluster()){
                continue;
            }

            List<DCGoogleMapMarker> myCluster = new ArrayList<>();

            compareToIterator = markers.iterator();
            boolean isCluster = false;
            while (compareToIterator.hasNext()){

                DCGoogleMapMarker compareTo = compareToIterator.next();
                if(!compareTo.isReady()){
                    continue;
                }

                if(compareTo.isCluster()){
                    continue;
                }

                if(compareTo.equals(marker)){
                    continue;
                }

                int pixels = pixelDistance(marker.getPosition().getLat() , marker.getPosition().getLon(),
                        compareTo.getPosition().getLat(), compareTo.getPosition().getLon(), zoom);

                if(distance>pixels){
                    myCluster.add(compareTo);
                    compareTo.setCluster(true);
                    isCluster = true;
                }
            }

            if(isCluster){
                myCluster.add(marker);
                marker.setCluster(true);
                clustered.add(myCluster);
            }

        }

        return clustered;


    }

    public static void findNearMarkersAndSplit(DCNumberMarker googleNumberMapMarker, GoogleMap googleMap, List<GoogleMapPolygon> spider) {

         List<DCGoogleMapMarker> myList = googleNumberMapMarker.getVehicleMarkers();

        if(myList == null){
            return;
        }

        //googleMap.getZoom()
        double lng_radius = 0.0003 * ( 20 * (21 - googleMap.getZoom())),         // degrees of longitude separation
                lat_to_lng = 111.23 / 71.7,  // lat to long proportion in Warsaw
                angle = 0.5,                 // starting angle, in radians
                step = 2 * Math.PI / myList.size(),
                lat_radius = lng_radius / lat_to_lng;

        int colorIndex = 0;
        for(DCGoogleMapMarker marker : myList){
            LatLon newPos = new LatLon();
            LatLon oldPos = marker.getPosition();
            double newLat, newLong;
            newLong = oldPos.getLon() + (Math.cos(angle) * lng_radius);
            newLat = oldPos.getLat() + (Math.sin(angle) * lat_radius);
            newPos.setLat(newLat);
            newPos.setLon(newLong);
            angle += step;
            marker.setMoved(true);
            marker.setRealPos(marker.getPosition());
            marker.setPosition(newPos);

            if(marker.isAnimationEnabled()){
                marker.setAnimationEnabled(false);
            }

            List<LatLon> coordinates = new ArrayList<>();

            coordinates.add(oldPos);
            coordinates.add(newPos);
            GoogleMapPolygon rad = new GoogleMapPolygon(coordinates);
            rad.setStrokeColor(colorPallet[colorIndex]);
            colorIndex = 1 - colorIndex;
            rad.setStrokeWeight(3);
            spider.add(rad);
            googleMap.addPolygonOverlay(rad);
            googleMap.addMarker(marker);

        }

        googleMap.removeMarker(googleNumberMapMarker);

        //googleMap.setVisualRefreshEnabled(true);



    }
}
