package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.diagnostics.common.VehicleStatusEnum;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.WebConfig;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.iot.web.ui.common.PagingComponent;
import com.vaadin.ui.CustomComponent;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by luis on 24.11.14.
 */
abstract class VehicleAlarmComponent<T> extends CustomComponent
        implements OnDiagCustomComponent, OnDiagVehiclePanel {


    private static final Logger LOG = Logger.getLogger(VehicleAlarmComponent.class);
    //private final VerticalLayout mainLayout = new VerticalLayout();
    protected final PagingComponent<T> mainLayout;
    protected Vehicle myVehicle;


    VehicleAlarmComponent(Class clazz){

        mainLayout = new PagingComponent<>(clazz);
        buildMainLayout();
    }


    @Override
    public void buildMainLayout() {

        mainLayout.setPageSize(WebConfig.ALARM_ITEMS_PER_PAGE);
        mainLayout.setSizeFull();
        mainLayout.setImmediate(true);
        setCompositionRoot(mainLayout);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {
        mainLayout.setCaptionNames(currentLanguage);

    }

    @Override
    public void closeDown() {

        mainLayout.clear();
        myVehicle = null;
    }

    @Override
    public void setVehicle(Vehicle v) {

        if(v == null){
            return;
        }

//        if(this.myVehicle != null){
//
//            if(this.myVehicle.equals(v)){
//                return;
//            }
//        }

        this.myVehicle = v;
        populateAlarmLayout();

    }

    @Override
    public Vehicle getMyVehicle() {
        return this.myVehicle;
    }

    @Override
    public void updateStatus() {

        if(this.myVehicle == null){
            return;
        }

        if(this.myVehicle.getStatus() == VehicleStatusEnum.ST_OFFLINE){
            mainLayout.updateStatus();
        }
     }

    protected void addAlarm(Integer id, Object snapShotAlarmTagValue) {
        this.mainLayout.addItem(id ,snapShotAlarmTagValue);
    }

    protected void removeAlarm(Integer id){
        this.mainLayout.remove(id);
    }

    abstract protected void populateAlarmLayout();

    protected void populateAlarmLayout(List<?> listOfAlarms) {

        mainLayout.clear();
        mainLayout.addAll(listOfAlarms);
        mainLayout.showFirstPage();
    }


    abstract public void setAlarmRefresh();

}
