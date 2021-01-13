package co.luism.iot.web.ui.vehicle.alarm;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;


/**
 * Created by luis on 19.11.14.
 */
public class AlarmCounter extends Label {

    Integer count = 0;
    //private Image ico = new Image(null, new ThemeResource("icons/alarm_icon.png"));
    //private Label dummy = new Label();
    //private Label alarmLabel = new Label("NULL");
    //private HorizontalLayout mainLayout = new HorizontalLayout();



    public AlarmCounter(){
        setWidth("32px");
        setHeight("32px");
        setValue(String.format("%d", count));
        setStyleName("alarm");
        //mainLayout.setSizeUndefined();
        //mainLayout.addValueLabel(alarmLabel);
        //setCompositionRoot(mainLayout);

    }

    public void setValue(int size) {
        if(size == count){
            return;
        }
        count = size;
        setValue(String.format("%d", size));

    }

}
