package co.luism.iot.web.ui.vehicle.instruments.gauge;

import com.vaadin.shared.ui.JavaScriptComponentState;


@SuppressWarnings("serial")
public class GaugeState extends JavaScriptComponentState {
    public int changeType = 0;
    public int value = 0;
    public String domId = null;
    public int gsize = 200;
    public String title = "";
    public String units = "";
    public int maxValue = 100;
    public int minValue = 0;
    public String[] majorTicks = {"0", "20", "40", "60", "80", "100"};
    public int minorTicks = 10;
    public String highlights;
    public boolean alarm = false;


}
