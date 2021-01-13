package co.luism.iot.web.ui.vehicle.instruments.gauge;

import co.luism.diagnostics.webmanager.LanguageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JavaScript({ "gauge.js", "gauge_connector.js" })
public class Gauge extends AbstractJavaScriptComponent {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Gauge.class);
    private final String name;
    private final int highLightSet;
    private Map<Integer, String> myMap = new HashMap<Integer, String>();
    private int currentValue;
    private Integer tagId;
    private boolean currentAlarm;
    private String title = "";
    private List<GaugeHighLight[]> highLightList = new ArrayList<>();


    Gauge(String name, Integer tagId, String domId, int size, String units, int maxValue, int minValue, int highLightSet) {
        this.name = name;
        this.tagId = tagId;
        this.highLightSet = highLightSet;
        getState().domId = domId;
        getState().gsize = size;
        getState().units = units;


        myMap.put(0, "#C9FFCE");
        myMap.put(1, "#00FF15");
        myMap.put(2, "#FFA3A3");
        myMap.put(3, "#FF8000");

        
        //setVehicleList with default values
        getState().maxValue = maxValue;  
        getState().minValue = minValue;
         
        int currValue = minValue;
        int step= (maxValue - minValue)/5;

        for(int i = 0; i <=  5; i++){
            getState().majorTicks[i] = Integer.toString(currValue);
            currValue += step;
        }
        
        int start = (minValue + step);

        initHighLights();

        if(highLightList.size() > this.highLightSet) {
            GaugeHighLight[] highLightArray = highLightList.get(this.highLightSet);

            if (highLightArray != null) {

                for (int i = 0; i < highLightArray.length; i++) {

                    highLightArray[i].from = start;
                    start += step;
                    highLightArray[i].to = start;
                    highLightArray[i].color = myMap.get(i);

                }

                updateHighLights(highLightArray);

            }
        }

        
    }

    private void initHighLights() {

       GaugeHighLight[] highLightArray = {new GaugeHighLight(20, 40, "#eee"),
                new GaugeHighLight(40, 60, "#eee"),
                new GaugeHighLight(60, 80, "#ccc"),
                new GaugeHighLight(80, 100, "#999")};


        highLightList.add(highLightArray);



    }

    public Gauge(GaugeConfig gf, Integer tagId) {
        this(gf.getName(), tagId, gf.getDomId(), gf.getSize(), gf.getUnits(), gf.getMaxValue(), gf.getMinValue(), gf.getHighLightSet());
    }

    public void setValue(long value) {

        if( currentValue != value){
            getState().changeType = 0;
            getState().value = (int)value;
            currentValue = (int)value;
        }

    }


    public void setAlarm(boolean b) {

        if( currentAlarm != b){
            getState().changeType = 2;
            getState().alarm = b;
            currentAlarm = b;
        }
    }

    public void setTitle(String language) {

        String newTitle = LanguageManager.getInstance().getValue(language, name);

        if (!newTitle.equals(title)) {
            getState().changeType = 3;
            getState().title = newTitle;
            this.title = newTitle;
        }

    }
    
    @Override
    protected GaugeState getState() {
        return (GaugeState) super.getState();
    }
    
    private void updateHighLights(GaugeHighLight[] highLightArray){
        Gson gson = new GsonBuilder().create();
        getState().highlights = gson.toJsonTree(highLightArray).getAsJsonArray().toString();
        //getState().highlights = myCustomArray.toString();
        //LOG.debug("highlights for:"+ getState().domId +":" + myCustomArray);
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public boolean isCurrentAlarm() {
        return currentAlarm;
    }

    public String getName() {
        return name;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getMinValue(){
        return getState().minValue;
    }

    public Integer getMaxValue(){
        return getState().maxValue;
    }
}
