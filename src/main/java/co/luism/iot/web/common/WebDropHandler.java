package co.luism.iot.web.common;

import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.pages.OnDiagMainView;
import co.luism.iot.web.ui.vehicle.VehicleGui;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.DragAndDropWrapper;
import org.apache.log4j.Logger;

/**
 * Created by luis on 28.10.14.
 */
public class WebDropHandler implements DropHandler {

        private static final long serialVersionUID = -5709370299130660699L;
        private static final Logger LOG = Logger.getLogger(WebDropHandler.class);
        private final OnDiagMainView parent;

        public WebDropHandler(OnDiagMainView parent) {
            this.parent = parent;
        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            // Accept all drops from anywhere
            return AcceptAll.get();
        }

        @Override
        public void drop(DragAndDropEvent event) {
            DragAndDropWrapper.WrapperTransferable t =
                    (DragAndDropWrapper.WrapperTransferable) event.getTransferable();

            DragAndDropWrapper j = (DragAndDropWrapper)t.getSourceComponent();
            Object d = j.getData();
            if(d instanceof Vehicle){
                Vehicle v = (Vehicle) d;
                //if the vehicle is alread drop exit
                if(parent.getVehicleMainPanel().getMyVehicle() != null){
                    if(parent.getVehicleMainPanel().getMyVehicle().equals(v)){
                        LOG.warn("vehicle already dropped");
                        return;
                    }
                }


                VehicleGui vg = parent.getMyVehicleGuiList().get(v.getVehicleId());

                if(vg != null){
                    parent.getVehicleMainPanel().setAlarmCounter(vg.getAlarmCounter());
                    parent.getVehicleMainPanel().setVehicle(v);

                } else {
                    LOG.debug("vehicle gui not found");
                }
            } else {
                LOG.error("drag not vehicle");
            }

            LOG.debug("finish drop...");

        }

}
