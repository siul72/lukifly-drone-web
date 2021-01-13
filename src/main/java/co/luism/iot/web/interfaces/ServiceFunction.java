package co.luism.iot.web.interfaces;

/**
 * Created by luis on 19.12.14.
 */
public enum ServiceFunction {

    GET_VID,
    GET_GAUGE_VALUE,
    GET_VEHICLE_STATUS,
    SET_ACTIVATE_ALL_ONLINE_PROCESS_DATA,
    SET_RESTART_DATA_SCAN_COLLECTOR,
    SET_DEACTIVATE_ALL_ONLINE_PROCESS_DATA,
    SET_RESTART_WEB_MANAGER;


    public static ServiceFunction getEnum(String value){

        for(ServiceFunction v : ServiceFunction.values()){

            if(v.name().equals(value)){
                return v;
            }
        }

        return null;
    }
}
