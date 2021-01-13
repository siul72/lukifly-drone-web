package co.luism.iot.web.ui.administration;
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

import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.ui.common.OnDiagErrorMessage;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import co.luism.iot.web.common.*;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.ui
 * Created by luis on 25.09.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class BeanUpdateForm extends CustomComponent implements Button.ClickListener, OnDiagCustomComponent {

    private static final Logger LOG = Logger.getLogger(BeanUpdateForm.class);
    private final FormLayout mainLayout = new FormLayout();
    private final ProgressBar progressBar = new ProgressBar();
    private Map<Class, String[]> formColumns;
    private Table parentTable;
    private BeanContainer<Integer, Object> parentTableBeanContainer;
    private BeanFieldGroup<? extends DiagnosticsPersistent> fieldGroup;
    private BeanItem<DiagnosticsPersistent> defaultBean;
    private Class currentClass;
    private final ComboBox parentKey = new ComboBox();
    private Upload uploadTranslation;
    private Upload uploadEventData;
    private Upload uploadProcessData;
    private Upload uploadCategories;

    private final Button createButton = new Button();
    private final Button updateButton = new Button();
    private final Button removeButton = new Button();

    private final Label status = new Label();
    private final PasswordField newPassword = new PasswordField("New Password");
    private final PasswordField newPasswordVerified = new PasswordField("Verify Password");
    private final Button passwordButton = new Button("Change Password");
    private final Label feedBackLabel = new Label();

    public BeanUpdateForm(Class clazz, Table t, Map<Class, String[]> col, BeanContainer<Integer, Object> tableBean) {

        parentTableBeanContainer = tableBean;
        parentTable = t;
        formColumns = col;
        currentClass = clazz;
        startUp();

    }

    @Override
    public void startUp(){
        try {
            defaultBean = new BeanItem<>((DiagnosticsPersistent) currentClass.newInstance());
        } catch (InstantiationException e) {
            LOG.error(e);
            return;
        } catch (IllegalAccessException e) {
            LOG.error(e);
            return;
        }

        buildMainLayout();


        if (currentClass.equals(User.class)  || currentClass.equals(Configuration.class)) {
            Collection<Organization> departments = WebManagerFacade.getInstance().getOrganizationList();
            BeanItemContainer<Organization> org = new BeanItemContainer<>(Organization.class, departments);

            initComboBox(org, Organization.class.getSimpleName() , TableFieldName.TABLE_ORGANIZATION_ID);

        }

        if (currentClass.equals(DataScanCollector.class)) {
            Collection<Fleet> departments = WebManagerFacade.getInstance().getAllFleets();
            BeanItemContainer<Fleet> org = new BeanItemContainer<>(Fleet.class, departments);
            initComboBox(org, Fleet.class.getSimpleName(), TableFieldName.TABLE_FLEET_ID);

        }


        if (currentClass.equals(Fleet.class)) {
            Collection<Configuration> departments = WebManagerFacade.getInstance().getAllConfigurations();
            BeanItemContainer<Configuration> org = new BeanItemContainer<>(Configuration.class, departments);
            initComboBox(org, Configuration.class.getSimpleName(), TableFieldName.TABLE_CONFIGURATION_ID);
        }


        if (currentClass.equals(Vehicle.class)) {
            Collection<Fleet> departments = WebManagerFacade.getInstance().getAllFleets();
            BeanItemContainer<Fleet> org = new BeanItemContainer<>(Fleet.class, departments);
            initComboBox(org, Fleet.class.getSimpleName(), TableFieldName.TABLE_FLEET_ID);
        }

        if (currentClass.equals(Configuration.class)){
            DataTagUploader receiver = new DataTagUploader();
            receiver.setParent(this);
            // Create the upload with a caption and set receiver later
            uploadEventData = new Upload("Event Data File", receiver);
            uploadEventData.setId(PageElementId.ADMIN_UPLOAD_EVENT);
            uploadEventData.setButtonCaption("Start Upload");
            uploadEventData.addSucceededListener(receiver);
            mainLayout.addComponent(uploadEventData);

            uploadProcessData = new Upload("Process Data File", receiver);
            uploadProcessData.setId(PageElementId.ADMIN_UPLOAD_PROCESS_DATA);
            uploadProcessData.setButtonCaption("Start Upload");
            uploadProcessData.addSucceededListener(receiver);
            mainLayout.addComponent(uploadProcessData);

            status.setVisible(false);


        }

        if (currentClass.equals(Language.class)) {
            TranslationUploader receiver = new TranslationUploader();
            receiver.setParent(this);
            // Create the upload with a caption and set receiver later
            uploadTranslation = new Upload("Translation File", receiver);
            uploadTranslation.setButtonCaption("Start Upload");
            uploadTranslation.addSucceededListener(receiver);
            mainLayout.addComponent(uploadTranslation);
        }

        if (currentClass.equals(AlarmCategory.class)) {
            DataTagUploader receiver = new DataTagUploader();
            receiver.setParent(this);
            // Create the upload with a caption and set receiver later
            uploadCategories = new Upload("Configuration File", receiver);
            uploadCategories.setButtonCaption("Start Upload");
            uploadCategories.addSucceededListener(receiver);
            mainLayout.addComponent(uploadCategories);
        }


        initAddRemoveButtons();
        mainLayout.addComponent(status);
        status.setId(PageElementId.ADMIN_STATUS_MESSAGE);
        mainLayout.addComponent(progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        setCompositionRoot(mainLayout);

        if (currentClass.equals(User.class)) {
            initPasswordFields();
            setComponentIds();

        }

    }


    private void initComboBox(Container org, String name, String id) {

        parentKey.setId(PageElementId.UPDATE_FORM_PARENT);
        parentKey.setCaption(name);
        parentKey.setContainerDataSource(org);
        parentKey.setItemCaptionPropertyId(id);
        parentKey.setTextInputAllowed(false);
        parentKey.setNullSelectionAllowed(true);
        // Add the item that marks 'null' value
        String nullItem = "-- none --";
        parentKey.addItem(nullItem);

        // Designate it as the 'null' value marker
        parentKey.setNullSelectionItemId(nullItem);

        parentKey.setImmediate(true);

        Collection<?> itemIds = parentKey.getItemIds();
        Object o = itemIds.iterator().next();
        parentKey.setValue(o);

        mainLayout.addComponent(parentKey);

    }

    private void initEditor(Class clazz) {
        fieldGroup = new BeanFieldGroup(clazz);
        for (String fName : formColumns.get(clazz)) {
            TextField field = new TextField(fName);
            mainLayout.addComponent(field);
            fieldGroup.bind(field, fName);
        }

    }

    public void setTranslationFile(File name) {
        //find  flag field
        BeanItem b = fieldGroup.getItemDataSource();

        if (b.getBean() instanceof Language) {
            Language l = (Language) b.getBean();
            if (LanguageManager.getInstance().createLanguageFile(l, name) == true) {
                new Notification("Translation Created", Notification.Type.ASSISTIVE_NOTIFICATION)
                        .show(Page.getCurrent());

            } else {
                new Notification("Translation Not Created", Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
            }

        }


        fieldGroup.setItemDataSource(b);
    }

    public void setDataTagFile(File fileName, Object source) {

        boolean ok = false;
        String msg;

        if (source instanceof Upload) {
            if (source.equals(this.uploadEventData)) {
                LOG.debug("Object is uploadEventData ");
                msg =  "Event Data not saved to Database";
                if (!WebManagerFacade.getInstance().saveEventConfigurationToDataBase(fileName)) {
                    new Notification(msg, "", Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
                    LOG.error(msg);
                    status.setValue(msg);
                } else {
                    msg =  "Data Stored";
                    new Notification(msg, "", Notification.Type.ASSISTIVE_NOTIFICATION).show(Page.getCurrent());
                    LOG.debug(msg);
                    status.setValue(msg);
                    ok = true;
                }
            }

            if (source.equals(this.uploadProcessData)) {
                LOG.debug("Uploading Process Data....");
                BeanItem data = fieldGroup.getItemDataSource();
                if(data != null){
                    Object dataBean = data.getBean();
                    if (dataBean != null) {
                        if (dataBean instanceof Configuration) {
                            Integer configurationId = ((Configuration) dataBean).getConfigurationId();
                            msg = "Process Data not saved to Database";
                            if (!WebManagerFacade.getInstance().saveProcessConfigurationToDataBase(fileName, configurationId)) {
                                new Notification(msg, "", Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
                                LOG.error(msg);
                                status.setValue(msg);
                            } else {
                                msg =  "Data Stored";
                                new Notification(msg, "", Notification.Type.ASSISTIVE_NOTIFICATION).show(Page.getCurrent());
                                LOG.debug(msg);
                                ok = true;
                                status.setValue(msg);
                            }
                        } else {
                            displayError(OnDiagErrorMessage.CONFIG_NOT_SET_LOG, OnDiagErrorMessage.CONFIG_NOT_SET_MSG);

                        }
                    } else {
                        displayError(OnDiagErrorMessage.CONFIG_NOT_SET_LOG, OnDiagErrorMessage.CONFIG_NOT_SET_MSG);
                    }
                } else {
                    displayError(OnDiagErrorMessage.CONFIG_NOT_SET_LOG, OnDiagErrorMessage.CONFIG_NOT_SET_MSG);
                }

            }

            if (source.equals(this.uploadCategories)) {

                LOG.debug("Object is uploadCategories");

                if (!WebManagerFacade.getInstance().saveCategoriesConfigurationToDataBase(fileName)) {
                    new Notification("Category Data not saved to Database", "",
                            Notification.Type.ERROR_MESSAGE)
                            .show(Page.getCurrent());
                    LOG.error("fail to store data");
                } else {
                    new Notification("Data Stored", "", Notification.Type.ASSISTIVE_NOTIFICATION)
                            .show(Page.getCurrent());
                    LOG.debug("store data ok");
                    ok = true;
                }

            }

        }

        if (!ok) {
            msg = "Object is not uploadEventData neither uploadProcessData or fail to load data";
            displayError(msg, msg);
        }

        status.setVisible(true);
        progressBar.setVisible(false);
    }

    private void displayError(String logMessage, String guiMessage) {

        LOG.warn(logMessage);
        new Notification(guiMessage, "",Notification.Type.ERROR_MESSAGE)  .show(Page.getCurrent());
        status.setValue(guiMessage);
    }

    private void initAddRemoveButtons() {

        HorizontalLayout buttonBar = new HorizontalLayout();

        createButton.setCaption("New");
        createButton.addClickListener(this);
        createButton.setData(0);
        buttonBar.addComponent(createButton);

        updateButton.setCaption("Update");
        updateButton.addClickListener(this);
        updateButton.setData(0);
        updateButton.setVisible(false);
        buttonBar.addComponent(updateButton);

        removeButton.setCaption("Delete");
        removeButton.addClickListener(this);
        removeButton.setData(0);
        removeButton.setVisible(false);
        buttonBar.addComponent(removeButton);

        mainLayout.addComponent(buttonBar);

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        Object obj = event.getSource();

        if (obj == createButton) {
            handleCreate();
        }

        if (obj == updateButton) {
            handleUpdate();
        }

        if (obj == removeButton) {
            handleRemove();

        }

        if(obj == passwordButton) {

            DiagnosticsPersistent dataBean = fieldGroup.getItemDataSource().getBean();
            if ((dataBean instanceof User) ) {

                if (dataBean instanceof User) {
                    User u = (User) dataBean;
                    handlePasswordUpdate(u);

                }

            }


        }

    }

    private void handleRemove() {
        status.setValue("Remove Button Clicked");
        BeanItem data = fieldGroup.getItemDataSource();
        DiagnosticsPersistent u = (data.getBean() instanceof DiagnosticsPersistent) ? ((DiagnosticsPersistent) data.getBean()) : null;

        if (u == null) {
            LOG.error("bean not found");
            return;
        }

        if (!u.delete()) {
            LOG.error(String.format("unable to delete bean"));
            Notification.show("unable to delete bean", Notification.Type.ERROR_MESSAGE);
            return;

        }

        parentTableBeanContainer.removeItem(data);
        setUpdateStatus(0);
        Notification.show("Removed " + fieldGroup.getItemDataSource().getBean(), Notification.Type.TRAY_NOTIFICATION);
    }

    private void handleUpdate() {
        status.setValue("Update Button Clicked");

        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException e) {
            LOG.error("Fail to update value:" + e.getMessage());
            Notification.show("unable to update bean", Notification.Type.ERROR_MESSAGE);
            return;
        }

        Object o = updateButton.getData();

        if (o == null) {
            fieldGroup.discard();
            LOG.error("wrong updateButton status");
            Notification.show("wrong updateButton status", Notification.Type.ERROR_MESSAGE);
            return;
        }

        int status = (Integer) o;

        DiagnosticsPersistent dataBean = fieldGroup.getItemDataSource().getBean();
        if ((dataBean instanceof User) || (dataBean instanceof Configuration)) {
            Object d = parentKey.getValue();
            Organization org = (d instanceof Organization) ? (Organization) d : null;

            if (dataBean instanceof User) {
                User u = (User) dataBean;
                u.setMyOrganization(org);
                u.setOrganizationId(org.getOrganizationId());
            }

            if (dataBean instanceof Configuration) {
                Configuration u = (Configuration) dataBean;
                u.setMyOrganization(org);
                u.setOrganizationId(org.getOrganizationId());

            }
        }

        if (dataBean instanceof Fleet) {
            Object d = parentKey.getValue();
            Fleet u = (Fleet) dataBean;
            Configuration org = (d instanceof Configuration) ? (Configuration) d : null;
            if(org != null){
                if(!org.getConfigurationId().equals(u.getConfigurationId())){
                    u.setMyConfiguration(org);
                    u.setConfigurationId(org.getConfigurationId());
                }
            }
        }


        if ((dataBean instanceof Vehicle)) {
            Object d = parentKey.getValue();
            Fleet org = (d instanceof Fleet) ? (Fleet) d : null;
            Vehicle u = (Vehicle) dataBean;
            u.setMyFleet(org);
            u.setFleetId(org.getFleetId());
        }

        if ((dataBean instanceof DataScanCollector)) {
            Object d = parentKey.getValue();
            Fleet fleet = (d instanceof Fleet) ? (Fleet) d : null;
            DataScanCollector u = (DataScanCollector) dataBean;
            u.setMyFleet(fleet);
            u.setFleetId(fleet.getFleetId());
        }

        if (dataBean == null) {
            fieldGroup.discard();
            LOG.error("bean not found");
            Notification.show("bean not found", Notification.Type.ERROR_MESSAGE);
            return;
        }

        switch (status) {
            case 0:
                if (!WebManagerFacade.getInstance().updateData(dataBean, WebUtils.getSessionUserName(getSession()))) {

                    fieldGroup.discard();
                    LOG.error("unable to update bean");
                    Notification.show("unable to update bean", Notification.Type.ERROR_MESSAGE);
                } else {

                    setUpdateStatus(0);
                    parentTable.sort();

                    if (dataBean instanceof Language) {
                        Language l = (Language) dataBean;
                        LanguageManager.getInstance().setEnable(l.getName(), l.isEnabled());
                    }


                    Notification.show(String.format("%s updated", dataBean.getClass().getSimpleName()), Notification.Type.TRAY_NOTIFICATION);
                }

                break;
            case 1:
                if (!WebManagerFacade.getInstance().createData(dataBean)) {
                    fieldGroup.discard();
                    LOG.error("unable to create bean");
                    Notification.show("unable to create bean", Notification.Type.ERROR_MESSAGE);
                } else {

                    setUpdateStatus(0);
                    parentTable.setPageLength(parentTableBeanContainer.size());
                    Notification.show(String.format("New %s created", dataBean.getClass().getSimpleName()), Notification.Type.TRAY_NOTIFICATION);
                }
                break;
            default:
                return;

        }


    }

    private void handleCreate() {

        status.setValue("Create Button Clicked");

        Integer o = (Integer) createButton.getData();

        if (o == null) {
            LOG.error("createButton status error");
            return;
        }

        int status = o;

        switch (status) {
            case 0:
                addNewBean(parentTableBeanContainer.getBeanType());
                setUpdateStatus(2);
                break;
            case 1:
                setUpdateStatus(0);
                break;
            default:
                break;
        }

    }

    private void setUpdateStatus(int status) {

        switch (status) {

            case 0:
                fieldGroup.setItemDataSource(defaultBean);
                updateButton.setVisible(false);
                createButton.setVisible(true);
                removeButton.setVisible(false);
                createButton.setCaption("New");
                createButton.setData(0);
                updateButton.setData(0);
                break;

            case 1:
                updateButton.setCaption("Update");
                updateButton.setVisible(true);
                updateButton.setData(0);
                createButton.setVisible(true);
                removeButton.setVisible(true);
                createButton.setCaption("Cancel");
                createButton.setData(1);
                break;

            case 2:
                updateButton.setCaption("Save");
                updateButton.setData(1);
                updateButton.setVisible(true);
                createButton.setCaption("Cancel");
                createButton.setData(1);
                removeButton.setVisible(false);
                break;
        }


    }

    private <T> void addNewBean(Class clazz) {

        T bean = null;
        try {
            bean = (T) clazz.newInstance();
        } catch (InstantiationException e) {
            LOG.error(e);
            return;
        } catch (IllegalAccessException e) {
            LOG.error(e);
            return;
        }

        parentTableBeanContainer.addBean(bean);
        BeanItem b = new BeanItem(bean);
        fieldGroup.setItemDataSource(b);
    }

    public void updateSelectedItem(Item item) {

        BeanItem data = (BeanItem) item;

        if (data.getBean().getClass().equals(User.class)) {
            User u = (User) data.getBean();
            parentKey.setValue(u.getMyOrganization());
        }

        if (data.getBean().getClass().equals(Configuration.class)) {
            Configuration u = (Configuration) data.getBean();
            parentKey.setValue(u.getMyOrganization());
        }

        if (data.getBean().getClass().equals(Fleet.class)) {
            Fleet u = (Fleet) data.getBean();
            parentKey.setValue(u.getMyConfiguration());
        }

        if (data.getBean().getClass().equals(Vehicle.class)) {
            Vehicle u = (Vehicle) data.getBean();
            parentKey.setValue(u.getMyFleet());
        }

        fieldGroup.setItemDataSource(data);
        setUpdateStatus(1);


    }

    public void setIndeterminateProgress() {

        progressBar.setVisible(true);
        status.setVisible(false);

    }

    @Override
    public void buildMainLayout() {

        initEditor(this.currentClass);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {

    }

    @Override
    public void closeDown() {

    }

    private void initPasswordFields() {

        newPassword.addValidator(new PasswordValidator());
        newPassword.setRequired(true);
        newPassword.setValue("");
        newPassword.setNullRepresentation("");
        mainLayout.addComponent(newPassword);
        newPasswordVerified.addValidator(new PasswordValidator());
        newPasswordVerified.setRequired(true);
        newPasswordVerified.setValue("");
        newPasswordVerified.setNullRepresentation("");
        mainLayout.addComponent(newPasswordVerified);
        passwordButton.addClickListener(this);
        mainLayout.addComponent(passwordButton);
        mainLayout.addComponent(feedBackLabel);

    }

    private void setComponentIds() {
        updateButton.setId(PageElementId.USER_SETTINGS_UPDATE);
        newPassword.setId(PageElementId.USER_SETTINGS_NEW_PASSWORD);
        newPasswordVerified.setId(PageElementId.USER_SETTINGS_NEW_VERIFY_PASSWORD);
        passwordButton.setId( PageElementId.USER_SETTINGS_PASS_UPDATE);

    }

    private void handlePasswordUpdate(User myUser) {

        feedBackLabel.setValue(null);
        // Try to change the password when the button is clicked


        if(newPassword.getValue().equals(newPasswordVerified.getValue())){
            WebManagerFacade.getInstance().setPassword(myUser, newPassword.getValue());
            feedBackLabel.setValue("Password changed!");
            clearPwdFields();
            return;

        }

        feedBackLabel.setValue("Password not changed!");
        clearPwdFields();
    }

    private void clearPwdFields() {
        newPassword.setValue(null);
        newPasswordVerified.setValue(null);
    }
}
