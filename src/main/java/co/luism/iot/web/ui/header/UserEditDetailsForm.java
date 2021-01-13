package co.luism.iot.web.ui.header;
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


import co.luism.diagnostics.common.ReturnCode;
import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.enterprise.Language;
import co.luism.diagnostics.enterprise.Organization;
import co.luism.diagnostics.enterprise.User;

import co.luism.iot.web.common.GenericGuiName;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.common.WebUtils;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.ui.common.HorizontalSplitter;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.ui
 * Created by luis on 01.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class UserEditDetailsForm extends CustomComponent implements PopupView.Content, OnDiagCustomComponent, Button.ClickListener {

    private static final Logger log = Logger.getLogger(UserEditDetailsForm.class);
    private User currentUser;
    private String currentLanguage;
    private final FormLayout mainLayout = new FormLayout();
    private final BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<>(User.class);
    private final String[]  formColumns = new String[] {"firstName", "lastName"};
    private final ComboBox language = new ComboBox();
    private final Button updateButton = new Button("Update");

    public UserEditDetailsForm(){
        startUp();
    }

    @Override
    public void startUp() {

        buildMainLayout();

    }

    @Override
    public void closeDown() {

    }

    @Override
    public void buildMainLayout() {

        initEditor();
        initComboBox();
        mainLayout.setMargin(true);
        setCompositionRoot(mainLayout);
        setComponentIds();

    }

    private void setComponentIds() {

        language.setId(PageElementId.USER_SETTINGS_LANG);
        updateButton.setId(PageElementId.USER_SETTINGS_UPDATE);


    }

    private void initEditor() {

        for(String fName : formColumns){
            TextField field = new TextField(fName);
            mainLayout.addComponent(field);
            //field.setWidth("100%");
            fieldGroup.bind(field, fName);
//
        }

    }




    private void initComboBox() {

        Collection<Language> languages =  LanguageManager.getInstance().getAllLanguages();
        BeanItemContainer<Language> org = new BeanItemContainer<>(Language.class, languages);
        language.setContainerDataSource(org);

        language.setItemCaptionPropertyId("name");
        language.setTextInputAllowed(false);
        language.setNullSelectionAllowed(false);
        language.setImmediate(true);

        mainLayout.addComponent(language);

        updateButton.addClickListener(this);
        mainLayout.addComponent(updateButton);
        mainLayout.addComponent(new HorizontalSplitter());

    }


    @Override
    public void buttonClick(Button.ClickEvent event) {
        Object obj = event.getSource();


        if (obj.equals(updateButton)) {
            handleUserUpdate();
        }


    }

    private void handleUserUpdate() {

        String cLang = this.currentLanguage;

        try {
            fieldGroup.commit();
        } catch (FieldGroup.CommitException e) {
            log.error("Fail to update value:" + e);
            Notification.show(LanguageManager.getInstance().getValue(cLang, "unable to update"), Notification.Type.ERROR_MESSAGE);
            return;
        }

        User dataBean = fieldGroup.getItemDataSource().getBean();

        if(dataBean == null) {
            fieldGroup.discard();
            log.error("not found");
            Notification.show(LanguageManager.getInstance().getValue(cLang, "not found"), Notification.Type.ERROR_MESSAGE);
            return;
        }

        Object o = language.getValue();
        Language l = null;
        if(o instanceof Language){
            l = (Language) o;
        }

        if(l != null){
            dataBean.setLanguage(l.getName());
        }

        dataBean.setUpdateBy(WebUtils.getSessionUserName(getSession()));


        if(!dataBean.update()){

            fieldGroup.discard();
            log.error("unable to update bean");
            Notification.show(LanguageManager.getInstance().getValue(cLang, "unable to update"), Notification.Type.ERROR_MESSAGE);
        } else {

            Notification.show(LanguageManager.getInstance().getValue(cLang,"User Updated"), Notification.Type.TRAY_NOTIFICATION);
        }

    }



    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        this.currentLanguage = currentUser.getLanguage();
        fieldGroup.setItemDataSource(this.currentUser);

        for(Object o :language.getItemIds() ){
            if(o instanceof Language){
                Language l = (Language) o;

                if(currentUser.getLanguage().equals(l.getName())){
                    language.setValue(o);
                    break;
                }
            }
        }
    }

    @Override
    public Component getPopupComponent() {
        return this;
    }

    @Override
    public String getMinimizedValueAsHTML() {
        return "";   // show nothing
    }

    @Override
    public void setCaptionNames(String currentLanguage) {

        this.currentLanguage = currentLanguage;

        updateButton.setCaption(LanguageManager.getInstance().getValue(currentLanguage, GenericGuiName.Update.getValue()));

        for(String s : formColumns){
            Field f = fieldGroup.getField(s);
            f.setCaption(LanguageManager.getInstance().getValue(currentLanguage, s));
        }

        language.setCaption(LanguageManager.getInstance().getValue(currentLanguage, GenericGuiName.Language.getValue()));

    }


}
