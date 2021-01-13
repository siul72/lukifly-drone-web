package co.luism.iot.web;


import co.luism.diagnostics.interfaces.WebManagerStartJob;
import co.luism.iot.web.common.OnDiagProperties;
import co.luism.iot.web.interfaces.ServiceInterface;
import co.luism.iot.web.ui.vehicle.instruments.MMIConfigManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener
public class OnDiagStartupJob implements ServletContextListener {

    static Logger LOG = Logger.getLogger(OnDiagStartupJob.class);
    static boolean isRunning = false;
    static WebManagerStartJob myStartJob;


    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        LOG.info("ServletContextListener destroyed");
        myStartJob.stop();
        LOG.info("webManager stopped");
        ServiceInterface.getInstance().close();
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent arg0) {

        myStartJob = new WebManagerStartJob(OnDiagStartupJob.class);
        if (!isRunning) {
            new Thread(myStartJob).start();
            LOG.info("launch webManager");
            isRunning = true;
        }

        ServiceInterface.getInstance().start();
        OnDiagProperties.getInstance();
        MMIConfigManager.getInstance();

        LOG.info("ServletContextListener started");
    }


}

