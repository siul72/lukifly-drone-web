package co.luism.iot.web.common;

import java.util.Map;

/**
 * Created by luis on 07.11.14.
 */
public enum VehicleTabNameEnum {

    V_TAB_MAIN("V_TAB_MAIN", "icons/tab_main_symbol.png"),
    V_TAB_ALARM("V_TAB_ALARM", "icons/tab_alarm_symbol.png"),
    V_TAB_PROCESS_DATA("V_TAB_PROCESS", "icons/tab_process_symbol.png"),
    V_TAB_SPEED_INSTRUMENTS("V_TAB_SPEED", "icons/tab_speed_symbol.png"),
    V_TAB_LOCATION("V_TAB_MAP", "icons/tab_map_symbol.png"),
    V_TAB_VNC("V_TAB_VNC", "icons/tab_vnc_symbol.png"),
    V_TAB_CHARTS("V_TAB_CHARTS", "icons/tab_vnc_symbol.png");

    private final String name;
    private final String icon;

    VehicleTabNameEnum(String name, String iconLocation){
        this.name = name;
        this.icon = iconLocation;
    }

    public String getName(){
        return this.name;
    }

    public String getIcon(){
        return this.icon;
    }
}
