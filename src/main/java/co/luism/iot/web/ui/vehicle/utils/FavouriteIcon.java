package co.luism.iot.web.ui.vehicle.utils;

import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * Created by luis on 13.02.15.
 */
public class FavouriteIcon extends Image {

    Boolean favourite;


    public FavouriteIcon(Boolean favourite){
        setWidth("32px");
        setHeight("32px");
        setFavourite(favourite);

    }

    public void setFavourite(boolean favourite){
        this.favourite = favourite;

        if(favourite){
            setStyleName("fav-on");
        } else {
            setStyleName("fav-off");
        }
    }

    public Boolean getFavourite() {
        return favourite;
    }
}
