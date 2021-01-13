package co.luism.iot.web.ui.main;

/*
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

import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.enterprise.Organization;
import co.luism.diagnostics.enterprise.User;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.iot.web.common.BeanComboBox;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.ParentView;
import co.luism.iot.web.pages.OnDiagPermissionManager;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.ui
 * Created by luis on 03.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class FilterPopUp extends CustomComponent implements OnDiagCustomComponent, PopupView.Content {

    private static final Logger LOG = Logger.getLogger(FilterPopUp.class);

    private BeanComboBox<Organization> orgCombo;
    private BeanComboBox<Fleet> fleetCombo;
    private BeanComboBox<String> carTypeCombo;

    private ParentView parentFilter;
    private User currentUser;
    private VerticalLayout mainLayout;

    public FilterPopUp() {

    }

    public void init(ParentView parent){
        startUp();
        parentFilter = parent;

    }

    @Override
    public void startUp() {

        Property.ValueChangeListener orgComboListener = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                Organization org = (Organization) orgCombo.getValue();
                bindFleetSelector(org);
                filterVehicles();
                LOG.debug(">>orgComboListener");

            }
        };

        Property.ValueChangeListener fleetComboListener = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                Fleet f = (Fleet) fleetCombo.getValue();
                bindTypeSelector(f);
                filterVehicles();
                LOG.debug(">>fleetComboListener");

            }
        };

        Property.ValueChangeListener carTypeComboListener = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LOG.debug(">>carTypeComboListener");
                filterVehicles();
            }
        };


        orgCombo = new BeanComboBox<>(Organization.class, orgComboListener);
        fleetCombo = new BeanComboBox<>(Fleet.class, fleetComboListener);
        carTypeCombo = new BeanComboBox<>(String.class, carTypeComboListener);

        mainLayout = new VerticalLayout();
        buildMainLayout();

    }

    @Override
    public void closeDown() {

    }

    @Override
    public void buildMainLayout() {

        mainLayout.addComponent(orgCombo);
        mainLayout.addComponent(fleetCombo);
        mainLayout.addComponent(carTypeCombo);
        setCompositionRoot(mainLayout);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {

        orgCombo.setCaption(LanguageManager.getInstance().getValue(currentLanguage, Organization.class.getSimpleName()));
        fleetCombo.setCaption(LanguageManager.getInstance().getValue(currentLanguage, Fleet.class.getSimpleName()));
        carTypeCombo.setCaption(LanguageManager.getInstance().getValue(currentLanguage, "Type"));

    }



    private void filterVehicles() {


        parentFilter.filterVehicles((Organization) orgCombo.getValue(), (Fleet) fleetCombo.getValue(),
                (String) carTypeCombo.getValue());


    }


    public void populateFleetComboBoxes(User u) {
        this.currentUser = u;
        List<Organization> l;
        //if user not RTS user hide combo and select its own organization
        if (OnDiagPermissionManager.grantAdminAccess(u)) {
            l = WebManagerFacade.getInstance().getOrganizationList();
            if (l != null) {
                this.orgCombo.bindData(l);

            } else {
                LOG.error(String.format("The  list for Organizations is null"));
                return;
            }


        } else {
            Organization org = u.getMyOrganization();
            l = new ArrayList<Organization>();
            l.add(org);
            this.orgCombo.bindData(l);
            //hide org combo
            this.orgCombo.setVisible(false);
            this.orgCombo.setValue(org);
            bindFleetSelector(org);
            filterVehicles();
        }

        if (u != null) {

            String lang = u.getLanguage();
            //this.label.setValue(LanguageManager.getInstance().getValue(lang, GenericGuiName.filter.getValue()));
            this.orgCombo.setCaption(LanguageManager.getInstance().getValue(lang, Organization.class.getSimpleName()));
            this.fleetCombo.setCaption(LanguageManager.getInstance().getValue(lang, Fleet.class.getSimpleName()));
            this.carTypeCombo.setCaption(LanguageManager.getInstance().getValue(lang, "Type"));
        }

    }

    private void bindFleetSelector(Organization currentOrg) {

        List<?> l;

        if (currentOrg == null) {

            if (OnDiagPermissionManager.grantAdminAccess(currentUser)) {
                l = WebManagerFacade.getInstance().getAllFleets();
            } else {
                LOG.info("User has no permission to see all fleets : " + currentUser.getLogin());
                return;
            }


        } else {
            l = currentOrg.getFleetList();
        }

        if (l != null) {
            if (!l.isEmpty()) {
                this.fleetCombo.bindData(l);
                //get current Fleet
                Fleet f = (Fleet) this.fleetCombo.getValue();
                bindTypeSelector(f);

            } else {
                this.fleetCombo.bindData(null);
                bindTypeSelector(null);
                LOG.info(String.format("The  list for Fleets is empty"));

            }
        }


    }

    private void bindTypeSelector(Fleet f) {

        if (f != null) {

            List<?> l = getUniqueTypesFromVehicles(f.getVehicleList());
            if (l != null) {
                this.carTypeCombo.bindData(l);
            } else {
                this.carTypeCombo.bindData(null);
                LOG.error(String.format("The  list for types is null"));
            }
        } else {
            this.carTypeCombo.bindData(null);
        }
    }

    private List<String> getUniqueTypesFromVehicles(List<Vehicle> vehicleList) {
        Set<String> t = new HashSet<>();
        for (Vehicle v : vehicleList) {

            t.add(v.getVehicleType());
        }

        return new ArrayList<>(t);

    }


    @Override
    public String getMinimizedValueAsHTML() {
        return "";
    }

    @Override
    public Component getPopupComponent() {
        return this;
    }
}
