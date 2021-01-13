package co.luism.iot.web.pages;

import co.luism.diagnostics.common.DiagnosticsEvent;
import co.luism.diagnostics.interfaces.IDiagnosticsEventHandler;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by luis on 29.10.14.
 */
public class WebFeederThread extends Thread implements IDiagnosticsEventHandler {

    private static final Logger LOG = Logger.getLogger(WebFeederThread.class);
    private boolean runLoop = true;
    private final LinkedBlockingQueue<DiagnosticsEvent> buffer = new LinkedBlockingQueue<DiagnosticsEvent>(1000);
    private final OnDiagMainView parent;


    public WebFeederThread(OnDiagMainView parent) {
        this.parent = parent;
    }


    @Override
    public void run() {
        LOG.debug("Start FeederThread loop");
        WebManagerFacade.getInstance().addListener(this);
        while (runLoop) {

            // Calling wait() will block this thread until another thread
            // calls notify() on the object.
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                LOG.error(e);
                break;
            }
            try {
                final DiagnosticsEvent currentEvent = this.buffer.take();

                if (!runLoop) {
                    break;
                }

                if (currentEvent == null) {
                    continue;
                }

                UI.getCurrent().access(new WebFeederThreadAccessRunnable(currentEvent));

            } catch (InterruptedException e) {
                LOG.error(e);
            }


        }

        WebManagerFacade.getInstance().removeListener(this);

        LOG.debug("Exit FeederThread");
    }


    @Override
    public void handleDiagnosticsEvent(DiagnosticsEvent diagnosticsEvent) {

        try {
            this.buffer.put(diagnosticsEvent);
        } catch (InterruptedException e) {
            LOG.error(e);
        }

    }

    public void closeDown() {

        this.runLoop = false;
        try {
            this.buffer.put(new DiagnosticsEvent(this));
        } catch (InterruptedException e) {
            LOG.error(e);
        }

    }

    class WebFeederThreadAccessRunnable implements Runnable {

        private final DiagnosticsEvent currentEvent;

        WebFeederThreadAccessRunnable(DiagnosticsEvent currentEvent) {

            this.currentEvent = currentEvent;
        }

        @Override
        public void run() {

            if (currentEvent == null) {
                return;
            }

            switch (currentEvent.getTagType()) {
                case TAG_DATA_TYPE_EVENT:
                case TAG_DATA_TYPE_PD:
                    LOG.debug("Number of events=" + currentEvent.getListOfUpdatedItems().size());
                    parent.sendDataTagToDisplay(currentEvent);
                    break;


                case TAG_DATA_TYPE_SYSTEM:
                    parent.updateVehicleStatus(currentEvent.getCurrentVehicle());
                    break;

                case ALARM_REFRESH:
                    parent.sendDataTagToDisplay(currentEvent);
                    break;
            }


        }

    }
}
