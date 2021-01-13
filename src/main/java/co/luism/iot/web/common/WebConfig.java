package co.luism.iot.web.common;/*
  ____        _ _ _                   _____           _
 |  __ \     (_) | |                 / ____|         | |
 | |__) |__ _ _| | |_ ___  ___      | (___  _   _ ___| |_ ___ _ __ ___  ___
 |  _  // _` | | | __/ _ \/ __|      \___ \| | | / __| __/ _ \ '_ ` _ \/ __|
 | | \ \ (_| | | | ||  __/ (__       ____) | |_| \__ \ ||  __/ | | | | \__ \
 |_|  \_\__,_|_|_|\__\___|\___|     |_____/ \__, |___/\__\___|_| |_| |_|___/
                                            __/ /
 Railtec Systems GmbH                      |___/
 6052 Hergiswil

 SVN file informations:
 Subversion Revision $Rev: $
 Date $Date: $
 Commmited by $Author: $
*/

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.common
 * Created by luis on 08.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */


public class WebConfig {


    public final static Integer VEHICLE_GUI_WIDTH = 300;
    public static final Integer VEHICLE_GUI_HEIGHT = 200;
    public static final Integer STATUS_GUI_HEIGHT = 40;
    //public static final double DEFAULT_LATITUDE = 8.30716;
    //public static final double DEFAULT_LONGITUDE = 46.9913;
    public static final double DEFAULT_LATITUDE = 46.9913;
    public static final double DEFAULT_LONGITUDE = 8.30716;
    public static final int VNC_NUMBER_OF_AVAILABLE_PORTS = 20;
    public static final Integer VNC_PORT_START_NUMBER = 5900;
    public static final String VNC_PROTOCOL = "vnc";
    public static final String VNC_SERVER_ADDRESS = "127.0.0.1";
    public static final String VNC_SERVER_PASSWORD = "suseroot";
    public static final int ALARM_ITEMS_PER_PAGE = 7;

    public static final int NUMBER_OF_VEHICLE_LOAD = 40;
    public static final long MS_SLEEP_BEFORE_NEXT_LOAD = 10000;
    public static final int NUMBER_MAX_VEHICLE_LOAD = 120;
    public static final int INSTRUMENT_TIME_OUT = 1800;
    public static final Integer VNC_CONNECTION_TIMEOUT = 30;
    public static final int MAX_GPS_STATUS_VALUE = 10;
    public static final int INFO_WINDOW_NUMBER_PIXELS_PER_LINE = 20;
    public static final int MAP_PIXEL_ICONS_MIN_DISTANCE = 40;
}
