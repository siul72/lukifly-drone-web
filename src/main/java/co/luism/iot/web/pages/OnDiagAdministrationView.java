package co.luism.iot.web.pages;
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

import co.luism.diagnostics.common.DiagnosticsPersistent;
import co.luism.diagnostics.enterprise.*;
import co.luism.iot.web.common.WebPageEnum;
import co.luism.iot.web.interfaces.ParentView;
import co.luism.iot.web.ui.administration.BeanUpdateForm;
import co.luism.iot.web.ui.administration.PagedTable;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web
 * Created by luis on 23.09.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */

/**
 * Administration Page Class
 * <img src="doc-files/admin_page_javadoc.png" alt="Example of the Administration GUI"/>
 */

public class OnDiagAdministrationView extends CustomComponent implements View, ParentView {

    private static final Logger LOG = Logger.getLogger(OnDiagAdministrationView.class);
    public static final WebPageEnum NAME = WebPageEnum.admin;
    private static final Map<Class, String> ID = new HashMap<Class, String>();
    private static final Map<Class, String[]> VISIBLE_COLUMNS = new HashMap<Class, String[]>();
    private static final Map<Class, String[]> SEARCH_COLUMNS = new HashMap<>();
    private  static PagedTable dataTable;
    private  static PageHeader headerLayout;
    private  static VerticalLayout editorLayout;
    private static BeanUpdateForm dataForm = null;
    private static BeanContainer<Integer, Object> dataElements = null;

    public OnDiagAdministrationView() {

    }

    @Override
    public void startUp() {
       headerLayout = new PageHeader(this);
       editorLayout = new VerticalLayout();
       dataTable = new PagedTable();
       initTableColumns();


    }

    @Override
    public void closeDown() {

    }

    @Override
    public void buildLayout() {

        //create a vertical layout with
        VerticalLayout mainLayout = new VerticalLayout();
        setCompositionRoot(mainLayout);

        mainLayout.addComponent(headerLayout);

        /* Root of the user interface component tree is set */
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        mainLayout.addComponent(splitPanel);
        mainLayout.setExpandRatio(splitPanel, 1);
        splitPanel.setSizeFull();
        splitPanel.setHeight(680, Unit.PIXELS);

        /* Build the component tree */
        VerticalLayout leftLayout = new VerticalLayout();

        splitPanel.setFirstComponent(leftLayout);
        splitPanel.setSecondComponent(editorLayout);
        splitPanel.setSplitPosition(60, Unit.PERCENTAGE);

        leftLayout.setMargin(true);
        editorLayout.setMargin(true);

        HorizontalLayout bottomLeftLayout = new HorizontalLayout();
        leftLayout.addComponent(bottomLeftLayout);

        leftLayout.addComponent(dataTable.createControls());
        leftLayout.addComponent(dataTable);

        leftLayout.setExpandRatio(dataTable, 1);
        dataTable.setSizeFull();

    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        User u = getSession().getAttribute(User.class);

        if(u == null){
            return;
        }

        startUp();
        initLayout(User.class);

        dataTable.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = dataTable.getValue();

                if (contactId != null) {

                    updateSelectedItem(dataTable.getItem(contactId));


                }

            }
        });

        headerLayout.setUserName(u);
        buildLayout();


    }

    private void initLayout(Class clazz) {

        dataElements = new BeanContainer<Integer, Object>(clazz);
        dataElements.setBeanIdProperty(ID.get(clazz));

        //dataTable.setContainerDataSource(dataElements);
        //dataTable.setPageLength(dataElements.size());

        //dataTable.setVisibleColumns(VISIBLE_COLUMNS.get(clazz));
        dataTable.setSelectable(true);
        dataTable.setImmediate(true);

        populateTable(clazz);

        BeanUpdateForm oldForm = null;
        boolean isToReplace = false;
        if (dataForm != null) {

            isToReplace = true;
            oldForm = dataForm;
        }

        dataForm = new BeanUpdateForm(clazz, dataTable, VISIBLE_COLUMNS, dataElements);

        if (isToReplace) {
            editorLayout.replaceComponent(oldForm, dataForm);
        } else {

            editorLayout.addComponent(dataForm);

        }

        editorLayout.setExpandRatio(dataForm, 1);
        dataForm.setSizeFull();

    }


    private static void initTableColumns(){

        VISIBLE_COLUMNS.put(User.class, new String[]{"userId", "login", "firstName", "lastName",
                "email", "language", "roleId"});

        SEARCH_COLUMNS.put(User.class, new String[]{"login"});

        VISIBLE_COLUMNS.put(Organization.class, new String[]{"organizationId", "name", "addressStreet",
                "addressPostCode", "email", "enabled"});
        VISIBLE_COLUMNS.put(Configuration.class, new String[]{"configurationId","organizationId", "projectCode", "version", "hardware" , "enabled"});
        VISIBLE_COLUMNS.put(Fleet.class, new String[]{"fleetId", "name", "configurationId", "enabled", "icon", "mapPointer"});
        VISIBLE_COLUMNS.put(Vehicle.class, new String[]{"vehicleId", "vehicleType", "vehicleNumber",
                "smsNumber", "protocolVersion",
                "timeZone", "countryCode", "daylightSavingTime", "enabled"});

        VISIBLE_COLUMNS.put(DataScanCollector.class, new String[]{"dataScanCollectorId","enabled", "pullTime",
                "fleetId"});

        VISIBLE_COLUMNS.put(DataTag.class, new String[]{"tagId","configurationId", "dataScanCollectorId","type", "sourceTagId", "name", "process",
                "engUnits", "valueType", "incrementDeadBand",
                "decrementDeadBand", "enabled"});
        SEARCH_COLUMNS.put(DataTag.class, new String[]{"tagId","configurationId", "dataScanCollectorId","type", "sourceTagId", "name", "process"});
        VISIBLE_COLUMNS.put(Role.class, new String[]{"roleId", "name", "enabled"});
        VISIBLE_COLUMNS.put(Permission.class, new String[]{"name", "object", "permission", "roleId"});
        VISIBLE_COLUMNS.put(Language.class, new String[]{"name", "flag", "enabled"});
        VISIBLE_COLUMNS.put(Translation.class, new String[]{"languageId", "textId", "translation"});
        VISIBLE_COLUMNS.put(AlarmBuffer.class, new String[]{"bufferId", "configurationId","numberOfSamples" ,"sampleSeconds", "sampleMilliSeconds"});
        VISIBLE_COLUMNS.put(AlarmCategory.class, new String[]{"categoryId", "categoryIndex", "name", "bufferId"});
        VISIBLE_COLUMNS.put(CategorySignalMap.class, new String[]{"id", "categoryId", "signalId", "position", "signalSize"});

        ID.put(User.class, "userId");
        ID.put(Organization.class, "organizationId");
        ID.put(Configuration.class, "configurationId");
        ID.put(Fleet.class, "fleetId");
        ID.put(Vehicle.class, "vehicleId");
        ID.put(DataScanCollector.class, "dataScanCollectorId");
        ID.put(DataTag.class, "tagId");
        ID.put(Role.class, "roleId");
        ID.put(Permission.class, "permissionId");
        ID.put(Language.class, "languageId");
        ID.put(Translation.class, "translationId");
        ID.put(AlarmBuffer.class, "bufferId");
        ID.put(AlarmCategory.class, "categoryId");
        ID.put(CategorySignalMap.class, "id");


    }

    private void updateSelectedItem(Item item) {
        dataForm.updateSelectedItem(item);
    }


    private <T extends DiagnosticsPersistent> void populateTable(Class clazz) {

        List<T> myDataList = null;

        if (clazz.equals(User.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllUsers();
        }

        if (clazz.equals(Organization.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getOrganizationList();
        }

        if (clazz.equals(Fleet.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllFleets();
        }

        if (clazz.equals(Vehicle.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllVehicles();
        }

        if (clazz.equals(DataTag.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllTags();
        }
        //roles
        if (clazz.equals(Role.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllRoles();
        }
        //permissions
        if (clazz.equals(Permission.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllPermissions();
        }
        //language
        if (clazz.equals(Language.class)) {
            myDataList = (List<T>) LanguageManager.getInstance().getAllLanguageList();
        }

        //translations
        if (clazz.equals(Translation.class)) {
            myDataList = (List<T>) LanguageManager.getInstance().getAllTranslationList();
        }


        if (clazz.equals(AlarmBuffer.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllBuffers();
        }

        if (clazz.equals(AlarmCategory.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllCategories();
        }

        if (clazz.equals(CategorySignalMap.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllMapCategoryEvents();
        }

        if (clazz.equals(Configuration.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllConfigurations();
        }

        if (clazz.equals(DataScanCollector.class)) {
            myDataList = (List<T>) WebManagerFacade.getInstance().getAllDataScanCollectors();
        }


        if (myDataList == null) {
            LOG.error("list is null");
            return;
        }
        dataElements.addAll(myDataList);
        dataTable.setContainerDataSource(dataElements);
        dataTable.setVisibleColumns(VISIBLE_COLUMNS.get(clazz));
        dataTable.setFilterColumns(SEARCH_COLUMNS.get(clazz));
        //dataTable.setPageLength(myDataList.size());
        dataTable.sort();
    }

    @Override
    public void updateLanguageGui(String currentLanguage) {

    }

    public void switchDataSource(Class aClass) {

        //reassign data source
        initLayout(aClass);

    }

    @Override
    public WebPageEnum getNAME() {
        return NAME;
    }

    @Override
    public void filterVehicles(Organization org, Fleet f, String type) {

    }

}
