package co.luism.iot.web.ui.vehicle;


import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.ui.vehicle.map.OrganizationMapComponent;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by luis on 07.11.14.
 */
public class VehicleDropPanel extends CustomComponent implements OnDiagCustomComponent {

    private static final Logger LOG = Logger.getLogger(VehicleDropPanel.class);
    private String currentLanguage = "en";
    // Create the drag target
    private VerticalLayout mainLayout = new VerticalLayout();
    private final Label target = new Label("DROP_HERE");
    private final OrganizationMapComponent mainMap = new OrganizationMapComponent();

    public VehicleDropPanel(){
        startUp();
    }

    @Override
    public void startUp() {
        buildMainLayout();
        setCaptionNames(currentLanguage);
    }


    @Override
    public void buildMainLayout() {

        setSizeFull();
        mainLayout.setSizeFull();
        target.setStyleName("h2");
        mainLayout.addComponent(target);
        target.setSizeUndefined();
        mainLayout.setComponentAlignment(target, Alignment.MIDDLE_CENTER);
        mainLayout.setId(PageElementId.VEHICLE_DROP_TARGET);
        mainLayout.addComponent(mainMap);
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setCaptionNames(String language){

        this.currentLanguage = language;
        target.setValue(LanguageManager.getInstance().getValue(currentLanguage, "DRAG_TARGET"));
    }

    @Override
    public void closeDown() {
        this.mainMap.closeDown();
    }


    public void init(List vehicleList) {
        this.mainMap.init(vehicleList);
    }

    public boolean centerMap(Vehicle myVehicle) {
        return this.mainMap.centerMap(myVehicle);
    }

    public boolean isInMap(Vehicle myVehicle) {

        return this.mainMap.isInMap(myVehicle);
    }
}
