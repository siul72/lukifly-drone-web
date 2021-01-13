package co.luism.iot.web.tests;/*
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

import co.luism.common.DateTimeUtils;
import co.luism.common.Utils;
import co.luism.diagnostics.enterprise.User;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.diagnostics.interfaces.WebManagerStartJob;
import co.luism.iot.web.ui.vehicle.instruments.MMIConfigManager;
import co.luism.iot.web.ui.vehicle.map.DCGoogleMapMarker;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.tests
 * Created by luis on 08.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OndiagWebTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(OndiagWebTest.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OndiagWebTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */


    public void test001Translate() {
        String t = LanguageManager.getInstance().getValue("en", "PLEASE_LOGIN_TEXT");
        System.out.println(t);

    }

    public void test002MMIConfig() {
        WebManager.getInstance().init(this.getClass());
        MMIConfigManager.getInstance().save();
        MMIConfigManager.getInstance().load();

    }

    class TCPClient {
        private Socket clientSocket;
        private PrintWriter outToServer;
        private BufferedReader rcv;


        public boolean init(String host, int port) {
            try {
                clientSocket = new Socket(host, port);
            } catch (IOException e) {
                LOG.error(e);
                return false;
            }
            try {
                outToServer = new PrintWriter(clientSocket.getOutputStream());
                rcv = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                LOG.error("IO error in client thread " + e);
                return false;
            }

            return true;
        }

        public boolean write(String data) {
            outToServer.println(data);
            outToServer.flush();
            return true;
        }

        public String readLine() {
            try {
                return rcv.readLine();
            } catch (IOException e) {
                LOG.error(e);
                return null;
            }
        }

        public boolean isConnected() {

            return (readLine() != null);
        }

        public void close() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

//    public void test002TestInterface() {
//
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(6052));
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        cli.write("GET_VID;");
//        String frame = cli.readLine();
//        System.out.println(frame);
//
//    }



//
//    public void test002Translate(){
//
//        String order = "";
//
//        for(Language l : LanguageManager.getInstance().getActiveLanguages()){
//            order = order.concat(l.getName());
//            order = order.concat(",");
//        }
//
//        System.out.println("Ordered active languages "+ order);
//    }
//
//    public void test003Translate(){
//
//       LanguageManager.getInstance().loadAllTranslations(Language.class);
//
//    }
//
//    public void test03SendLS(){
//        //String vid = "12345678901234567890123456789012";
//        //String vid ="0F9588574DC9D1CFBF5749AAD0395D76";
//        //String vid ="4F44F8BB5C33E7AC3134D7B077C93641";
//        String vid = "187F864C0BE6FBE5BF3202910317BC35";
//        Integer count = 0;
//        Integer seqN = 0;
//        long longitude = 83071600;//8.30716
//        long latitude = 469913000;//46.9913
//        int ix = 10;
//        int sourceIx = 20;
//
//
//
//        //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.init("192.168.5.171",51313));
//        //send hello message
//        cli.write(String.format("3;%d;0;0;0;%s;0;0;CH;0;60;", DateTimeUtils.getCurrentTimeStampSeconds(), vid));
//
//
//        //send alarms
//        while (count++ < 30){
//
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//
//            }
//
//            latitude = latitude + 100000;
//            longitude = longitude + 10000;
//
//            String frame = String.format("4;4;%d;%d;0;%d;%d;1;",
//                    DateTimeUtils.getCurrentTimeStampSeconds(), seqN++, latitude, longitude);
//            cli.write(frame);
//
//
//        }
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//
//        }
//
//        cli.close();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//
//        }
//
//    }

    public void test04TestCluster(){
        WebManagerStartJob myStartJob = new WebManagerStartJob(this.getClass());
        new Thread(myStartJob).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        Map<Vehicle, DCGoogleMapMarker> vehicleGoogleMapMarkerMap = new HashMap<>();
        User u = WebManagerFacade.getInstance().getUser("user");
        List<Vehicle> vehicleList = WebManagerFacade.getInstance().getAllVehicles(u);

        for(Vehicle v : vehicleList){
            vehicleGoogleMapMarkerMap.put(v, new DCGoogleMapMarker(v));
        }

    }



//
//    public void test004SendProcessData(){
//
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        //send hello message
//        cli.write("3;6;0;0;0;1234567890;0;0;CH;0;60;");
//
//
//        Integer count = 0;
//        Integer seqN = 0;
//        long longitude = 83071600;//8.30716
//        long latitude = 469913000;//46.9913
//        Timestamp ts;
//        while (count++ < 3){
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//                cli.close();
//                break;
//            }
//
//            seqN++;
//            ts = Utils.getCurrentTimeStamp();
//
//            //send LIFE SIGN Message 4 Times
//            String frame = String.format("4;4;%d;%d;0;%d;%d;0;",
//                    ts.getTime(), seqN, latitude, longitude);
//            cli.write(frame);
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//                cli.close();
//                break;
//            }
//
//            //send process data
//            //7;1;1415718580;9;4:27;;
//            seqN++;
//            ts = Utils.getCurrentTimeStamp();
//            frame = String.format("7;3;%d;%d;4:27;3:10;2:20;;", ts.getTime(), seqN);
//            cli.write(frame);
//
//        }
//
//        cli.close();
//    }
//
//    public void test005SendAlarmData(){
//        //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        //send hello message
//        cli.write("3;6;0;0;0;1234567890;0;0;CH;0;60;");
//
//
//        Integer count = 0;
//        Integer seqN = 0;
//
//        Timestamp ts;
//        int ix = 0;
//        int sourceIx = 0;
//        while (count++ < 15){
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//                cli.close();
//                break;
//            }
//
//            seqN++;
//            ts = Utils.getCurrentTimeStamp();
//            long timeNow = ts.getTime();
//
//            String frame = String.format("12;1;%d;%d;<getNewEvents;%d,%d,%d,11,%d,4>", timeNow,
//                    seqN, ix++,
//                    timeNow-10, timeNow - 5,  sourceIx++);
//            cli.write(frame);
//
//
//        }
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//            cli.close();
//
//        }
//
//        cli.close();
//    }
//
//    public void test006SendActiveAlarmData(){
//        //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        //send hello message
//        cli.write("3;6;0;0;0;1234567890;0;0;CH;0;60;");
//
//
//        Integer count = 0;
//        Integer seqN = 0;
//
//        int ix = 10;
//        int sourceIx = 0;
//
//
//        Long timeNow = Utils.getCurrentTimeStamp().getTime();
//        timeNow = timeNow - 5000;
//        //Integer endTS = (int)(timeNow / 1000);
//        //Integer endTSms = (int)(timeNow % 1000);
//        Integer endTS = 0;
//        Integer endTSms = 0;
//        timeNow = timeNow - 5000;
//        Integer startTS = (int)(timeNow / 1000);
//        Integer starTSms = (int)(timeNow % 1000);
//
//        //send alarm data
//        //v_index,ON_time_sec, ON_time_ms ,OFF_time_sec, OFF_time_ms,active_ev_count,ev_code,status_info
//        //7;1;1415718580;9;4:27;;
//        seqN++;
//
//        //ev_index,ON_time_sec, ON_time_ms ,OFF_time_sec, OFF_time_ms,active_ev_count,ev_code,status_info
//        String frame = String.format("12;1;%d;%d;<getNewEvents;%d,%d,%d,%d,%d,11,%d,4>",
//                timeNow/1000, seqN++, ix,
//                startTS, starTSms, endTS, endTSms,
//                sourceIx);
//        cli.write(frame);
//
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//            cli.close();
//
//        }
//
//        cli.close();
//    }
//
//    public void test007SyncAlarmDataBase(){
//
//
//        Integer count = 0;
//        Integer seqN = 0;
//        long longitude = 83071600;//8.30716
//        long latitude = 469913000;//46.9913
//        int ix = 10;
//        int sourceIx = 20;
//        Timestamp ts;
//
//
//        //connect disconnect several times
//        for (int i = 0 ; i < 3; i++){
//
//            //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//            TCPClient cli = new TCPClient();
//            assertTrue(cli.setVehicleList(51313));
//
//            //send hello message
//            cli.write("3;6;0;0;0;1234567890;0;0;CH;0;60;");
//
//            seqN++;
//            ts = Utils.getCurrentTimeStamp();
//
//            //send LIFE SIGN Message
//            String frame = String.format("4;4;%d;%d;0;%d;%d;0;",
//                    ts.getTime(), seqN, latitude, longitude);
//            cli.write(frame);
//
//            //send alarms
//            while (count++ < 3){
//
//
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    LOG.error(e);
//                    cli.close();
//                    break;
//                }
//
//                //send alarm data
//                //7;1;1415718580;9;4:27;;
//                seqN++;
//                ts = Utils.getCurrentTimeStamp();
//                long timeNow = ts.getTime();
//                frame = String.format("12;1;%d;%d;<getNewEvents;%d,%d,0,11,%d,4>", timeNow,
//                        seqN, ix++,
//                        timeNow-10,  sourceIx++);
//                cli.write(frame);
//
//            }
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//
//            }
//
//            cli.close();
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//
//            }
//        }
//
//        System.out.println(">> wait for sync");
//
//        String vid = "1234567890";
//        Vehicle v = WebManagerFacade.getInstance().getVehicle(vid);
//
//        //wait until is in sync
//        count = 0;
//        while(v.getSyncStatus() != VehicleSyncStatusEnum.SYNC_STATUS_DB_TO_VEHICLE_OK && count++ < 10){
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//
//            }
//        }
//        System.out.println("<< wait for sync");
//
//        System.out.println(String.format("%s have %d active alarms %d", vid, v.getActiveAlarms().size(), count));
//
//        assertTrue( v.getActiveAlarms().size() == 3);
//
//    }
//
//    public void test008CleanActiveAlarmList(){
//
//        //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        //send hello message
//        cli.write("3;6;0;0;0;1234567890;0;0;CH;0;60;");
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//        cli.write("12;1;1415013185;196;<getNewEvents;ENOACTIVEEVENTS>;");
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//
//        cli.close();
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//
//
//    }
//
//    public void test009SendLS(){
//
//        Integer count = 0;
//        Integer seqN = 0;
//        long longitude = 83071600;//8.30716
//        long latitude = 469913000;//46.9913
//
//
//        //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        String vid = "12345678901234567890123456789012";
//
//        //send hello message
//        cli.write(String.format("3;6;%d;%d;0;%s;0;0;CH;0;60;",
//                Utils.getCurrentTimeStampSeconds(), seqN++, vid));
//
//
//        //send alarms
//        while (count++ < 10){
//
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//
//            }
//
//            latitude = latitude + 1000000;
//            longitude = longitude + 100000;
//
//            //send LIFE SIGN Message
//            String frame = String.format("4;4;%d;%d;0;%d;%d;0;",
//                    Utils.getCurrentTimeStampSeconds(), seqN++, latitude, longitude);
//            cli.write(frame);
//
//
//        }
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//        cli.close();
//
//
//
//    }
//
//    public void test010HandleEnvData(){
//        Integer count = 0;
//        Integer seqN = 0;
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//        //send hello message
//        cli.write(String.format("3;6;%d;%d;0;1234567890;0;0;CH;0;60;",
//                Utils.getCurrentTimeStampSeconds(), seqN++));
//
//        long longitude = 83071600;//8.30716
//        long latitude = 469913000;//46.9913
//
//        int eventIx = 13;
//
//        int sourceIx = 1;
//        Long timeNow = Utils.getCurrentTimeStamp().getTime();
//        timeNow = timeNow - 5000;
//        Integer endTS = (int)(timeNow / 1000);
//        Integer endTSms = (int)(timeNow % 1000);
//        timeNow = timeNow - 5000;
//        Integer startTS = (int)(timeNow / 1000);
//        Integer starTSms = (int)(timeNow % 1000);
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//        //send alarm data
//        //7;1;1415718580;9;4:27;;
//        //ev_index,ON_time_sec, ON_time_ms ,OFF_time_sec, OFF_time_ms,active_ev_count,ev_code,status_info
//        String frame = String.format("12;1;%d;%d;<getNewEvents;%d,%d,%d,%d,%d,11,%d,4>",
//                timeNow/1000, seqN++, eventIx,
//                startTS, starTSms, endTS, endTSms,
//                sourceIx);
//        cli.write(frame);
//        LOG.debug(cli.readLine());
//
//        //send LS
//        while (count++ < 60){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//
//            }
//
//
//            frame = cli.readLine();
//            //System.out.println(">>>>> CLI_TEST: " + frame);
//            if(frame == null){
//                break;
//            }
//
//            DataFactory df = new DataFactory();
//
//            //if ask for get new event
//            //reply something
//            Integer endCount = 0;
//            String dataValue = "";
//            if (frame.contains(String.format("getEnvData;%d;0",eventIx))) {
//
//                if(count < 10) {
//
//
//                    for(int i = 0 ; i < 10; i++){
//                        int c = df.getNumberBetween(0, 255);
//                        dataValue = dataValue.concat(String.format("%02X",c));
//                    }
//
//                    //reply with data
//                    frame = String.format("12;1;%d;%d;<getEnvData;%d;0;DATA;%d;%d;%s>",
//                            Utils.getCurrentTimeStampSeconds(), seqN, eventIx ,(int)(timeNow/1000), (int)(timeNow % 1000), dataValue);
//                    cli.write(frame);
//                    timeNow = timeNow + 1000;
//                    //System.out.println("<<<<< CLI_TEST:" + frame);
//                } else {
//
//                    frame = String.format("12;1;%d;%d;<getEnvData;%d;0;END;>",
//                            Utils.getCurrentTimeStampSeconds(), seqN, eventIx);
//                    cli.write(frame);
//                    //break;
//                    endCount++;
//
//                }
//
//            }
//
//            if (frame.contains(String.format("getEnvData;%d;1",eventIx))) {
//
//                if(count < 10) {
//
//                    for(int i = 0 ; i < 10; i++){
//                        int c = df.getNumberBetween(0, 255);
//                        dataValue = dataValue.concat(String.format("%02X",c));
//                    }
//
//                    //reply with data
//                    frame = String.format("12;1;%d;%d;<getEnvData;%d;1;DATA;%d;%d;%s>",
//                            Utils.getCurrentTimeStampSeconds(), seqN, eventIx, (int)(timeNow/1000), (int)(timeNow % 1000), dataValue);
//                    cli.write(frame);
//                    timeNow = timeNow + 1000;
//                    //System.out.println("<<<<< CLI_TEST:" + frame);
//                } else {
//
//                    frame = String.format("12;1;%d;%d;<getEnvData;%d;1;END;>",
//                            Utils.getCurrentTimeStampSeconds(), seqN, eventIx);
//                    cli.write(frame);
//                    //break;
//                    endCount++;
//
//                }
//
//            }
//
//            if(endCount >= 2){
//                break;
//            }
//        }
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//
//        cli.close();
//    }
//
//    public void test011VNCHandling(){
//        //12;1;1415017784;35;<getNewEvents;86,1415017726,1415017772,11,28,4>
//        Integer count = 0;
//        Integer seqN = 0;
//        TCPClient cli = new TCPClient();
//        assertTrue(cli.setVehicleList(51313));
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//        //send hello message
//        cli.write(String.format("3;6;%d;%d;0;1234567890;0;0;CH;0;360;",
//                Utils.getCurrentTimeStamp().getTime(), seqN++));
//
//        long longitude = 83071600;//8.30716
//        long latitude = 469913000;//46.9913
//        Timestamp ts;
//        int ix = 123;
//        int sourceIx = 8;
//
//        ts = Utils.getCurrentTimeStamp();
//        long timeNow = ts.getTime();
//        String frame;
//
//        int senddata = 0;
//        //send LS
//        while (count++ < 60){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error(e);
//
//            }
//
//            frame = cli.readLine();
//            LOG.debug(">>>>> CLI_TEST: " + frame);
//            if(frame == null){
//                break;
//            }
//
//            //if ask for get new event
//            //reply something
//            //13;1;<Time Stamp>;<Sequence Number>;<portnumber>;
//            if (frame.startsWith("13;")) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    LOG.error(e);
//
//                }
//
//                //send alarmAck
//                //2;2;<Time Stamp>;<Sequence Number>;<ReqMessage ID>;<ReqSequence Number>;
//
//                frame = String.format("2;2;%d;%d;13;0;",
//                        Utils.getCurrentTimeStamp().getTime(), seqN);
//                cli.write(frame);
//
//                LOG.debug("<<<<< CLI_TEST:" + frame);
//
//
//            }
//
//
//        }
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            LOG.error(e);
//
//        }
//
//
//        cli.close();
//    }


}