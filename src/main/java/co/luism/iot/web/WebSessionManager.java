package co.luism.iot.web;

import com.vaadin.server.*;
import org.apache.log4j.Logger;

/**
 * Created by luis on 10.11.14.
 */
public class WebSessionManager  {


    private static final Logger LOG = Logger.getLogger(WebSessionManager.class);
    private static volatile int activeSessions = 0;
    private static final WebSessionManager instance = new WebSessionManager();


    WebSessionManager(){

    }

    public static WebSessionManager getInstance(){
        return instance;
    }

    public static Integer getActiveSessions() {
        return activeSessions;
    }

    public WebDetachListener addDetachListener(OnlineDiagnoseUI myUI){
        WebDetachListener myDetach = new WebDetachListener(myUI);

        return myDetach;
    }

    private class WebDetachListener implements ClientConnector.DetachListener {

        public OnlineDiagnoseUI myUI;
        public WebDetachListener(OnlineDiagnoseUI myUI){
            this.myUI = myUI;
        }
        @Override
        public void detach(ClientConnector.DetachEvent detachEvent) {
            LOG.debug("UI detached");
            this.myUI.closeDown();

        }
    }
}
