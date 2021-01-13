package co.luism.iot.web.ui.vehicle.instruments;


import co.luism.iot.web.ui.vehicle.instruments.gauge.GaugeConfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis on 28.01.15.
 */
@XmlRootElement(name = "MMIConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class MMIConfig implements java.io.Serializable {

    private String fleetName = "F-1";
    private int pageColumns = 3;
    private int pageLines = 1;
    public ArrayList<GaugeConfig> gaugeConfigList = new ArrayList<>();

    MMIConfig(){

        for(int y= 0; y< pageLines; y++){
            for(int x = 0; x < pageColumns; x++){
                GaugeConfig g = new GaugeConfig(x, y);
                gaugeConfigList.add(g);

                if(x == 0) {
                    g.setName("GAUGE_BATTERY");

                }

                if(x == 1) {
                    g.setName("GAUGE_SPEED");
                    g.setUnits("Km/h");
                    g.setSize(300);
                    g.setMaxValue(400);
                    g.setHighLightSet(1);

                }

                if(x == 2) {
                    g.setName("GAUGE_TANK");


                }

                g.setProcessId(x+1);
                g.setDomId("g" + x);


            }
        }
    }

    public GaugeConfig getGaugeConfig(int column, int line) {
        for(GaugeConfig gf : gaugeConfigList){
            if(gf.getColumnPos() != column){
                continue;
            }

            if(gf.getLinePos() != line){
                continue;
            }

            return gf;
        }

        return null;
    }

    public String getFleetName() {
        return fleetName;
    }

    public void setFleetName(String fleetName) {
        this.fleetName = fleetName;
    }

    public int getPageColumns() {
        return pageColumns;
    }

    public void setPageColumns(int pageColumns) {
        this.pageColumns = pageColumns;
    }

    public int getPageLines() {
        return pageLines;
    }

    public void setPageLines(int pageLines) {
        this.pageLines = pageLines;
    }

    public List<GaugeConfig> getGaugeConfigList() {
        return gaugeConfigList;
    }

    public void setGaugeConfigList(ArrayList<GaugeConfig> gaugeConfigList) {
        this.gaugeConfigList = gaugeConfigList;
    }
}
