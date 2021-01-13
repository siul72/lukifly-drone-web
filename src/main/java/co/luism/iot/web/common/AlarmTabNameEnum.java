package co.luism.iot.web.common;

/**
 * Created by luis on 13.11.14.
 */
public enum AlarmTabNameEnum {

    ALARM_TAB_CURRENT("CUR_ALARMS", "icons/tab_alarm_current.png"),
    ALARM_TAB_HISTORY("HISTORY_ALARMS", "icons/tab_alarm_history.png"),
    ALARM_TAB_RAW_DATA("ALL_EVENTS", "icons/tab_alarm_table.png"),
    ALARM_TAB_RAW_ENV("ALL_ENVIRONMENT", "icons/tab_alarm_table.png"),
    ALARM_TAB_IMPORT("IMPORT_ALARMS", "icons/tab_alarm_import.png");

    private final String name;
    private final String icon;

    AlarmTabNameEnum(String name, String iconLocation){
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
