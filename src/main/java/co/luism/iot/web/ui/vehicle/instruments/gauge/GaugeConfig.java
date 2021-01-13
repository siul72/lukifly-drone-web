package co.luism.iot.web.ui.vehicle.instruments.gauge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by luis on 28.01.15.
 */
@XmlRootElement(name = "GaugeConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class GaugeConfig {

    private Integer columnPos;
    private Integer linePos;
    private String name;
    private String domId;
    private Integer processId;
    private Integer configurationId;
    private String processTagName;
    private Integer size = 200;
    private String units = "%";
    private Integer minValue = 0;
    private Integer maxValue = 100;
    private Integer highLightSet = 0;

    public GaugeConfig(){

    }

    public GaugeConfig(int x, int y){
        this.columnPos =x;
        this.linePos =y;
    }

    public Integer getColumnPos() {
        return columnPos;
    }

    public void setColumnPos(Integer columnPos) {
        this.columnPos = columnPos;
    }

    public Integer getLinePos() {
        return linePos;
    }

    public void setLinePos(Integer linePos) {
        this.linePos = linePos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomId() {
        return domId;
    }

    public void setDomId(String domId) {
        this.domId = domId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getHighLightSet() {
        return highLightSet;
    }

    public void setHighLightSet(Integer highLightSet) {
        this.highLightSet = highLightSet;
    }

    public String getProcessTagName() {
        return processTagName;
    }

    public void setProcessTagName(String processTagName) {
        this.processTagName = processTagName;
    }

    public Integer getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(Integer configurationId) {
        this.configurationId = configurationId;
    }
}
