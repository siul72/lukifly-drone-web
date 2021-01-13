package co.luism.iot.web.ui.vehicle;

import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.pages.OnDiagMainView;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * Created by luis on 27.10.14.
 */
public class VehicleLoader implements Runnable {

    private static final Logger LOG = Logger.getLogger(VehicleLoader.class);
    volatile float myValue;
    private OnDiagMainView parent;
    private ProgressBar progressBarVehicleLoad;
    private Label progressStatus;
    private List<Vehicle> listOfVehicles;
    private boolean runLoop = true;
    private int currentPointer;
    private final int countValues;

    public VehicleLoader(OnDiagMainView parent, List<Vehicle> myList, ProgressBar p, Label s) {

        this.parent = parent;
        this.listOfVehicles = myList;
        this.progressBarVehicleLoad = p;
        this.progressStatus = s;
        this.currentPointer = 0;
        this.countValues = myList.size();
    }

    public void loadInitialValues(int numberOfValues) {

        if (numberOfValues >= this.countValues) {
            numberOfValues = this.countValues;
        }

        LOG.debug(String.format(">>load initial %d values", numberOfValues));
        try {
            parent.populateFleetLayout(this.listOfVehicles.subList(0, numberOfValues));
        } catch (IllegalArgumentException e) {
            LOG.error(e);
        }

        this.currentPointer = numberOfValues;
        LOG.debug(String.format("<<finish load"));
    }


    @Override
    public void run() {
        LOG.debug(String.format("Number vehicles to load = %d", countValues - currentPointer));
        progressBarVehicleLoad.setVisible(true);
        progressStatus.setVisible(true);
        progressBarVehicleLoad.setEnabled(true);
        progressStatus.setValue("start load...");
        //UI.getCurrent().setPollInterval(100);

        while (currentPointer < countValues && runLoop) {

            LOG.debug(String.format("VehicleLoader run wait %d ms", WebConfig.MS_SLEEP_BEFORE_NEXT_LOAD));
            try {
                Thread.sleep(WebConfig.MS_SLEEP_BEFORE_NEXT_LOAD);
            } catch (InterruptedException e) {
                LOG.error(e);
            }

            LOG.debug("VehicleLoader run start");

            long startTime = System.currentTimeMillis();

            int nValues = WebConfig.NUMBER_OF_VEHICLE_LOAD;
            if (currentPointer + nValues > countValues) {
                nValues = countValues - currentPointer;
            }

            final int finalNValues = nValues;
            final int finalIndex = currentPointer;
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {

                    myValue = (float) (finalIndex + finalNValues) / (float) countValues;
                    progressBarVehicleLoad.setValue(myValue);
                    int p = (int) (myValue * 100);
                    progressStatus.setValue(String.format("%02d percent", p));
                    try {
                        parent.populateFleetLayout(listOfVehicles.subList(finalIndex, finalIndex + finalNValues));
                    } catch (IllegalArgumentException e) {
                        LOG.error(e);
                    }


                }
            });

            currentPointer = currentPointer + WebConfig.NUMBER_OF_VEHICLE_LOAD;

            long endTime = System.currentTimeMillis() - startTime;

            LOG.debug(String.format("Loaded from %d to %d until %d, next %d, took %d ms",
                    finalIndex, finalIndex + finalNValues,
                    countValues, WebConfig.NUMBER_OF_VEHICLE_LOAD, endTime));

            if (finalIndex + finalNValues > WebConfig.NUMBER_MAX_VEHICLE_LOAD) {
                LOG.error(String.format("Number of maximum vehicle load reached %d", WebConfig.NUMBER_MAX_VEHICLE_LOAD));
                break;

            }


        }


        UI.getCurrent().access(new Runnable() {
            @Override
            public void run() {
                progressBarVehicleLoad.setVisible(false);

                if (currentPointer >= countValues) {
                    progressStatus.setVisible(false);
                } else {

                    int p = (int) (((float) (currentPointer) / (float) countValues) * 100);
                    progressStatus.setValue(String.format("%02d percent loaded", p));

                }
            }
        });

        progressBarVehicleLoad.setVisible(false);
        LOG.debug("stop vehicle load run");
    }

    public void closeDown() {
        this.runLoop = false;
    }
}

