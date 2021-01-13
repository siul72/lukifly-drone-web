package co.luism.iot.web.interfaces;

import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.DataScanCollector;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.pages.OnDiagMainView;
import co.luism.iot.web.pages.OnDiagViewChangeListener;
import co.luism.iot.web.ui.vehicle.instruments.MMIConfig;
import co.luism.iot.web.ui.vehicle.instruments.MMIConfigManager;
import co.luism.iot.web.ui.vehicle.instruments.gauge.GaugeConfig;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by luis on 19.12.14.
 */
public final class ServiceInterface {


    private static final Logger LOG = Logger.getLogger(ServiceInterface.class);
    private static ServiceInterface instance = new ServiceInterface(6052);
    private static boolean runLoop = true;
    private final List<Socket> clientSocketList = new ArrayList<>();
    private final int port;
    private ServerSocket socketServer = null;


    private ServiceInterface(int port) {

        this.port = port;

    }


    static public ServiceInterface getInstance() {
        return instance;
    }

    public void start() {

        ServerThread st = new ServerThread();
        st.setName(ServerThread.class.getSimpleName());
        st.start();

    }

    private void parse(String frame) {

        String[] blocks = frame.split(";");

        ServiceFunction f = ServiceFunction.getEnum(blocks[0]);

        if(f == null){
            LOG.equals("ServiceFunction is invalid");
            return;
        }

        if(f.name().startsWith("GET_")){
            getValue(f, blocks);
            return;
        }

        if(f.name().startsWith("SET_")){
            setValue(f, blocks);
            return;
        }


    }

    private void setValue(ServiceFunction f, String[] blocks) {
        boolean result = false;

        switch (f){
            case SET_ACTIVATE_ALL_ONLINE_PROCESS_DATA:
                if(blocks.length >= 3){
                    result = onlineProcessDataAction(true, blocks[1], blocks[2]);
                }

                break;
            case SET_DEACTIVATE_ALL_ONLINE_PROCESS_DATA:
                if(blocks.length >= 3){
                    result = onlineProcessDataAction(false, blocks[1], blocks[2]);
                }
                break;
            case SET_RESTART_DATA_SCAN_COLLECTOR:
                result = restartDataScanCollector();
                break;
            case SET_RESTART_WEB_MANAGER:

                    result = restartWebManager();

                break;
        }

        if(result){
            sendFrame("ACK;");
        } else {
            sendFrame("NACK;");
        }
    }

    private boolean restartWebManager() {

        return WebManagerFacade.getInstance().restartWebManager(this.getClass());
    }

    private boolean restartDataScanCollector() {

        WebManagerFacade.getInstance().reloadDataScanner();
        return true;
    }

    private boolean onlineProcessDataAction(boolean activate, String fleetName, String dataScannerId) {

        Integer dsId;

        try {

            dsId = Integer.parseInt(dataScannerId);
        } catch (NumberFormatException ex){
            LOG.error(ex);
            return false;
        }


        MMIConfig mmi = MMIConfigManager.getInstance().getMMIConfig(fleetName);

        if(mmi == null){
            LOG.warn("mmi is null");
            return false;
        }

        //check create datascanner
        Map<String, Object> myRestrictions = new HashMap<>();
        myRestrictions.put("name", fleetName);
        Fleet fleet = Fleet.read(Fleet.class, myRestrictions);
        if(fleet == null){
            LOG.error(String.format("Fleet %s not found", fleetName));
            return false;
        }

        myRestrictions.clear();
        myRestrictions.put("dataScanCollectorId", dsId);
        DataScanCollector dataScanCollector = DataScanCollector.read(DataScanCollector.class, myRestrictions);

        if(dataScanCollector == null){
            dataScanCollector = new DataScanCollector();
            dataScanCollector.setDataScanCollectorId(dsId);
            dataScanCollector.setMyFleet(fleet);
            dataScanCollector.setFleetId(fleet.getFleetId());
            dataScanCollector.setPullTime(10);
            dataScanCollector.create();
            LOG.info("new dataScanCollector created ");
        }


        boolean ret = true;

        for(GaugeConfig gf : mmi.gaugeConfigList){
           DataTag t = WebManagerFacade.getInstance().getTagByName(EventTypeEnum.TAG_DATA_TYPE_PD, gf.getConfigurationId(), gf.getProcessTagName());

            if(activate){
                t.setProcess(gf.getProcessId());
                t.setDataScanCollectorId(dsId);
            } else {
                t.setProcess(0);
                t.setDataScanCollectorId(null);
            }

            if(!t.update()){
                LOG.warn("not able to update tag");
                ret = false;
            }
        }

        return ret;
    }


    private void getValue(ServiceFunction f, String[] blocks) {

        Object o = OnDiagViewChangeListener.getInstance().getCurrentView();
        OnDiagMainView mainView = null;
        int value = 0;
        if (o instanceof OnDiagMainView) {
            mainView = (OnDiagMainView) o;
        }

        switch (f) {
            case GET_VID:
                if(mainView == null) {
                    sendFrame("MAIN_VIEW_NOT_OPEN;");
                    return;
                }


                Vehicle v = mainView.getVehicleMainPanel().getMyVehicle();

                if (v == null) {
                    sendFrame("-1;");
                    return;
                }

                sendFrame(String.format("%s;", v.getVehicleId()));
                break;

            case GET_GAUGE_VALUE:
                if(mainView == null) {
                    sendFrame("MAIN_VIEW_NOT_OPEN;");
                    return;
                }
                if(blocks.length >= 2){
                    value = getGaugeValue(mainView, blocks[1]) ;
                }

                sendFrame(String.format("%d;", value));
                break;

            case GET_VEHICLE_STATUS:
                if(blocks.length >= 2){
                    sendFrame(getVehicleStatus(blocks[1]));
                } else {
                    sendFrame("NACK;");
                }
             break;

            default:
                sendFrame("NACK;");
                break;
        }


    }

    private String getVehicleStatus(String vid) {

        Vehicle v = WebManagerFacade.getInstance().getVehicle(vid);
        if(v == null){
            return "NOT_FOUND;";
        }

        return String.format("%s;" , v.getStatus().name());
    }

    private int getGaugeValue(OnDiagMainView mainView, String gaugeName) {

         return mainView.getVehicleMainPanel().getTabGaugeInstrumentPanel().getGaugeCurrentValue(gaugeName);
    }

    public void close() {
        //LOG.debug("Close");
        runLoop = false;

        //close all client connections
        for (Socket s : clientSocketList) {

            if (s.isConnected()) {
                try {

                    s.close();
                } catch (IOException e) {

                    LOG.error(e);
                }
            }
        }

        clientSocketList.clear();


        try {
            if (this.socketServer != null) {
                this.socketServer.close();
            }

        } catch (IOException e) {


            LOG.error(e);
        }


    }

    public boolean sendFrame(String frame) {

        boolean sendFlag = false;

        //append # to the frame;
        frame = frame.concat("#");

        for (Socket s : this.clientSocketList) {


            if (!s.isConnected() && s.isClosed()) {
                LOG.error("Socket is not connected");
                return sendFlag;
            }

            PrintWriter os;
            try {
                os = new PrintWriter(s.getOutputStream());

                //os.write(frame);
                os.println(frame);
                os.flush();
                sendFlag = true;
            } catch (IOException e) {

                LOG.error("Socket error:" + e);
            }
        }

        if (!sendFlag) {
            LOG.info("No socket found to send frame");
        }

        //LOG.debug("#### TX frame:" + frame);
        return sendFlag;
    }

    private class ServerThread extends Thread {

        @Override
        public void run() {
            LOG.info(this.getName() + "Test Socket Thread started");
            clientSocketList.clear();
            while (runLoop) {
                try {
                    if (socketServer == null) {
                        LOG.info(String.format("Try Listening on port %d ......", port));
                        socketServer = new ServerSocket();
                        socketServer.setReuseAddress(true);
                    }

                    if (!socketServer.isBound()) {
                        socketServer.bind(new InetSocketAddress(port));
                    }

                    if (socketServer != null) {
                        //LOG.debug("Accept New Connections on " + socketServer.getLocalPort());
                        Socket s = socketServer.accept();
                        //LOG.info(String.format(String.format("Received connection from %s", s.getRemoteSocketAddress().toString())));
                        if (!clientSocketList.isEmpty()) {
                            LOG.info(String.format("Connection not allowed"));
                            s.close();

                        } else {

                            ClientThread ct = new ClientThread(s);
                            //ct.setName(myClient.getIndex().toString());
                            ct.start();
                            clientSocketList.add(s);
                            //LOG.info(String.format("We have now %d active connections!", clientSocketList.size()));

                        }


                    } else {
                        LOG.error(String.format("Not able to use port %d, maybe already in use", port));
                    }

                } catch (Exception e) {

                    LOG.error("Connection Error:" + e);
                } finally {

                    if (!runLoop) {
                        LOG.error("Close Server Listener");

                    }

                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        LOG.error(e);
                    }
                }
            }
        }
    }

    class ClientThread extends Thread {

        String line = null;
        BufferedReader is = null;
        PrintWriter os = null;
        Socket c = null;

        public ClientThread(Socket c) {
            this.c = c;
        }

        @Override
        public void run() {
            LOG.info(this.getName() + " Thread started");
            //String address =( (InetSocketAddress)s.getRemoteSocketAddress()).getHostName();

            try {

                is = new BufferedReader(new InputStreamReader(c.getInputStream()));
                os = new PrintWriter(c.getOutputStream());

            } catch (IOException e) {
                LOG.error("IO error in client thread " + e);
            }

            try {

                while (runLoop) {
                    line = is.readLine();
                    if (line == null) {
                        //LOG.debug("read null >>> exit");
                        break;
                    }

                    String frame = String.format("%s", line);
                    parse(frame);
                    LOG.info(String.format("###RX %s", frame));


                }


            } catch (IOException e) {

                line = this.getName(); //reused String line for getting thread name
                LOG.error("Client " + line + " terminated " + e);
            } catch (NullPointerException e) {
                line = this.getName();
                LOG.error("Client " + line + " Closed " + e);
                //reused String line for getting thread name

            } finally {
                try {
                    //LOG.info(String.format("Connection Closing from %s", c.getRemoteSocketAddress().toString()));

                    if (is != null) {

                        is.close();
                        //LOG.debug(" Socket Input Stream Closed");
                    }

                    if (os != null) {
                        os.flush();
                        os.close();
                        //LOG.debug("Socket Out Stream Closed");
                    }

                    if (c != null) {
                        c.close();
                        //LOG.debug("Socket Closed");
                    }

                } catch (IOException ie) {
                    LOG.debug("Socket Close Error " + ie);
                }
                if (c != null) {

                    clientSocketList.remove(c);
                }

                //LOG.info(String.format("We have now %d active connections!", clientSocketList.size()));
            }//end finally

            LOG.info(this.getName() + " Thread stop");
        }
    }


}
