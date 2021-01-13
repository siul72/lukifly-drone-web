package co.luism.iot.web.ui.vehicle.vnc;

 
import co.luism.datacollector.common.DCParserAckNackEvent;
import co.luism.datacollector.common.DCParserAckNackEventHandler;
import co.luism.diagnostics.common.FunctionEnum;
import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.ui.common.VncClientLayout;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luis on 26.11.14.
 */
public class VncClientComponent extends CustomComponent implements OnDiagCustomComponent,
        DCParserAckNackEventHandler {
    private static final Logger LOG = Logger.getLogger(VncClientComponent.class);
    private final VncClientLayout mainLayout = new VncClientLayout();
    private final VncComponent vncComponent = new VncComponent();
    private final Label status = new Label("not connected");
    private VncClientConnectionStatus vncStatus = VncClientConnectionStatus.ST_VNC_DISCONNECTED;
    private Vehicle vehicle;
    private Integer port;
    private final HorizontalLayout top = new HorizontalLayout();
    private Timer myTimer = null;
    private static final int countTime = 5;
    private Integer configuredConnectionTimeout;
    private Integer currentConnectionTimeout;

    public VncClientComponent(){

       startUp();
    }

    @Override
    public void startUp() {

        this.configuredConnectionTimeout = WebConfig.VNC_CONNECTION_TIMEOUT;
        buildMainLayout();

    }


    @Override
    public void buildMainLayout() {
        top.addComponent(status);
        status.setId("vnc_status");
        mainLayout.addStatusLabel(top);
        mainLayout.addDisplay(vncComponent);
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setCaptionNames(String currentLanguage) {

    }

    @Override
    public void closeDown() {

        stop();
        this.vehicle = null;
        WebManagerFacade.getInstance().removeDCParserAckNackListener(this);

    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void stop(){

        if(this.vncStatus == VncClientConnectionStatus.ST_VNC_DISCONNECTED){
            return;
        }

        vncComponent.stop();
        this.status.setValue("DISCONNECTED");
        this.vncStatus = VncClientConnectionStatus.ST_VNC_DISCONNECTED;
        this.port = 0;

        if(vehicle != null){
            VncConnectionManager.getInstance().setFreePort(this.vehicle.getVehicleId());
            WebManagerFacade.getInstance().sendVncStopMessage(this.vehicle.getVehicleId(), port);
        }

    }

    public void setVehicle(Vehicle vehicle){
        this.vehicle = vehicle;
        WebManagerFacade.getInstance().addDCParserAckNackListener(this);
    }

    public void start(){

        if(this.vehicle == null){
            LOG.warn("no vehicle is set");
            return;
        }

        if(this.vehicle.getStatus() != VehicleStatusEnum.ST_ONLINE){
            return;
        }

        //if status not stop do nothing
        if(this.vncStatus != VncClientConnectionStatus.ST_VNC_DISCONNECTED){
            LOG.warn("status is not disconnected");
            return;
        }

        Integer port = VncConnectionManager.getInstance().getAvailablePort(vehicle.getVehicleId(), this);

        if(port == null){
            this.status.setValue("NO AVAILABLE PORT NOW - TRY AGAIN LATER");
            return;
        }


        if(!WebManagerFacade.getInstance().sendVncStartMessage(vehicle.getVehicleId(), port)){
            this.status.setValue("UNABLE TO START VNC TUNNEL FOR PORT "+ port);
            VncConnectionManager.getInstance().setFreePort(this.vehicle.getVehicleId());
            return;
        }

        this.port = port;
        this.status.setValue("CONNECTING TO PORT "+ this.port);
        this.vncStatus = VncClientConnectionStatus.ST_VNC_STARTING;
        this.vncComponent.reset();
        this.vncComponent.setPort(this.port);
        this.myTimer = new Timer();
        this.myTimer.schedule(new TimeOutTask(), 1000*countTime, 1000*countTime);
        this.currentConnectionTimeout = this.configuredConnectionTimeout;

    }


    @Override
    public void handleEvent(DCParserAckNackEvent dcParserAckNackEvent) {

        if(this.vehicle == null){
            LOG.debug("vehicle is null");
            return;
        }

        if(!dcParserAckNackEvent.getVehicleId().equals(this.vehicle.getVehicleId())){
            LOG.debug("is not for me");
            VncConnectionManager.getInstance().setFreePort(this.vehicle.getVehicleId());
            this.vncStatus = VncClientConnectionStatus.ST_VNC_DISCONNECTED;
            return;
        }

        if(dcParserAckNackEvent.getSourceMessage().equals(FunctionEnum.FC_START_VNC_SERVER)){
            if(dcParserAckNackEvent.getResult() == FunctionEnum.FC_ACK){
                //if event ack
                //vncComponent.start();
                this.status.setValue("CONNECTED");
                this.vncStatus = VncClientConnectionStatus.ST_VNC_CONNECTED;
                this.vncComponent.start();
                return;
            }

            if(dcParserAckNackEvent.getResult() == FunctionEnum.FC_NACK){
                //if event nack
                this.status.setValue("FAIL TO CONNECT");
                VncConnectionManager.getInstance().setFreePort(this.vehicle.getVehicleId());
                this.vncStatus = VncClientConnectionStatus.ST_VNC_DISCONNECTED;

                return;
            }

        } else {
            LOG.debug("this source message is not for me");
        }


    }

    private class TimeOutTask extends TimerTask {

        public void run() {
            //LOG.debug("TIMER EVENT");
            checkConnectionStatus();
        }
    }

    private void checkConnectionStatus() {

        if(this.vncStatus != VncClientConnectionStatus.ST_VNC_STARTING){
            this.myTimer.cancel();
            this.myTimer.purge();
            this.myTimer = null;
            return;
        }

        if(this.vncStatus == VncClientConnectionStatus.ST_VNC_STARTING){

            this.currentConnectionTimeout = this.currentConnectionTimeout - countTime;

            if(this.currentConnectionTimeout <= 0){
                //quit connection
                this.status.setValue("Fail To Connect, Try Again Later!");
                VncConnectionManager.getInstance().setFreePort(this.vehicle.getVehicleId());
                this.vncStatus = VncClientConnectionStatus.ST_VNC_DISCONNECTED;
                this.myTimer.cancel();
                this.myTimer.purge();
                this.myTimer = null;
                return;
            }

            this.status.setValue(String.format("CONNECTING TO PORT %d - quit in %d seconds...", this.port,
                    this.currentConnectionTimeout));



        }

    }


}
