package co.luism.iot.web.pages;

import co.luism.diagnostics.common.ReturnCode;
import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.enterprise.Organization;
import co.luism.diagnostics.enterprise.User;
import co.luism.diagnostics.interfaces.IWebManagerFacade;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.common.PasswordRecovery;
import co.luism.iot.web.common.PasswordValidator;
import co.luism.iot.web.common.WebPageEnum;
import co.luism.iot.web.interfaces.ParentView;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.apache.log4j.Logger;

/**
 * Login Page Class
 * <img src="doc-files/login_page.png" alt="Example of the Login GUI"/>
 */

@SuppressWarnings("serial")
@Theme("mytheme")
@PreserveOnRefresh
@Title("Online Diagnostics - Admin")
public class OnDiagLoginView extends CustomComponent implements View, Button.ClickListener, ParentView {

    private static final Logger LOG = Logger.getLogger(OnDiagLoginView.class);
    public static final WebPageEnum NAME = WebPageEnum.login;
    private TextField user;
    private PasswordField passwordField;
    private Button loginButton;
    private VerticalLayout fieldsLayout;
    private PasswordRecovery passwordRecovery;

    public OnDiagLoginView() {

    }

    @Override
    public void startUp() {

        user = new TextField();
        passwordField = new PasswordField();
        loginButton = new Button();
        passwordRecovery = new PasswordRecovery();

        buildLayout();


    }

    @Override
    public void closeDown() {

    }

    @Override
    public void enter(ViewChangeEvent event) {
        startUp();
        // focus the username field when user arrives to the login view
        user.focus();
    }

    @Override
    public void updateLanguageGui(String currentLanguage) {
        LanguageManager.getInstance().setCurrentLanguage(currentLanguage);

        String fCaption;
        if(passwordRecovery.isEnabled()){
            fCaption = LanguageManager.getInstance().getValue("RESET_PASS_INSTRUCTIONS");
        } else {
            fCaption = LanguageManager.getInstance().getValue("PLEASE_LOGIN_TEXT");
        }
        fieldsLayout.setCaption(fCaption);
        user.setCaption(LanguageManager.getInstance().getValue("USER_NAME_LABEL"));
        user.setInputPrompt(LanguageManager.getInstance().getValue("USER_NAME_PROMPT"));
        passwordField.setCaption(LanguageManager.getInstance().getValue("USER_PASSWORD_LABEL"));
        loginButton.setCaption(LanguageManager.getInstance().getValue("LOGIN_BUTTON"));

    }

    @Override
    public void switchDataSource(Class aClass) {

    }

    @Override
    public WebPageEnum getNAME() {
        return NAME;
    }

    @Override
    public void filterVehicles(Organization org, Fleet f, String type) {

    }

    @Override
    public void buildLayout() {
        setSizeFull();

        // Create the user input field
        user.setId(PageElementId.USER_NAME_TEXT_FIELD);
        user.setWidth("300px");
        user.setRequired(true);

        // Create login button
        loginButton.setId(PageElementId.LOGIN_BNT);
        loginButton.addClickListener(this);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        // Create the pass filed
        passwordField.setId(PageElementId.USER_PASSWORD_TEXTFIELD);

        passwordField.setWidth("300px");
        passwordField.addValidator(new PasswordValidator());
        passwordField.setRequired(true);
        passwordField.setValue("");
        passwordField.setNullRepresentation("");

        passwordField.addFocusListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FieldEvents.FocusEvent event) {
                loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
            }
        });

        // Add both to a panel
        fieldsLayout = new VerticalLayout(user, passwordField, loginButton);
        fieldsLayout.setSpacing(true);
        fieldsLayout.setMargin(new MarginInfo(true, true, true, false));
        fieldsLayout.setSizeUndefined();

        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout();
        viewLayout.setSizeFull();
        CustomComponent header = new PageHeader(this);
        updateLanguageGui(LanguageManager.getInstance().getCurrentLanguage());

        viewLayout.addComponent(header);
        viewLayout.addComponent(fieldsLayout);
        viewLayout.setComponentAlignment(fieldsLayout, Alignment.MIDDLE_CENTER);
        viewLayout.setExpandRatio(fieldsLayout, 1);
        setCompositionRoot(viewLayout);
    }


    @Override
    public void buttonClick(ClickEvent event) {

        Button cur = event.getButton();

        switch (cur.getId()) {
            case PageElementId.LOGIN_BNT:

                //
                // Validate the fields using the navigator. By using validors for the
                // fields we reduce the amount of queries we have to use to the database
                // for wrongly entered passwords
                //
                if (!user.isValid() || !passwordField.isValid()) {
                    Notification.show(LanguageManager.getInstance().getValue("WRONG_PASSWORD_TEXT"), Notification.Type.HUMANIZED_MESSAGE);
                    return;
                }

                String username = user.getValue();
                String password = this.passwordField.getValue();
                IWebManagerFacade myFacade = WebManagerFacade.getInstance();

                boolean isValid = (myFacade.validateUser(username, password) == ReturnCode.RET_OK) ? true : false;

                if (isValid) {

                    User myUser = WebManagerFacade.getInstance().getUser(username);
                    if (myUser == null) {
                        LOG.error(String.format("The current user login %s was not found in the system", username));
                        Notification.show(LanguageManager.getInstance().getValue("User Not Found!"), Notification.Type.HUMANIZED_MESSAGE);
                        return;
                    }
                    getSession().setAttribute(User.class, myUser);
                    // Navigate to main view
                    String goTo;
                    if (OnDiagPermissionManager.grantAdminAccess(myUser)) {
                        goTo = OnDiagAdministrationView.NAME.getValue();
                    } else {
                        goTo = OnDiagMainView.NAME.getValue();
                    }

                    getUI().getNavigator().navigateTo(goTo);
                    LOG.debug("go to: " + goTo);

                } else {

                    Notification.show(LanguageManager.getInstance().getValue("WRONG_PASSWORD_TEXT"), Notification.Type.HUMANIZED_MESSAGE);
                    // Wrong passwordField clear the passwordField field and refocuses it
                    this.passwordField.setValue(null);
                    this.passwordField.focus();



                }
                break;

        }




    }


}