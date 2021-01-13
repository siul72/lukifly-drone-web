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

import co.luism.diagnostics.enterprise.*;
import co.luism.iot.web.interfaces.ParentView;
import co.luism.iot.web.ui.header.UserEditDetailsForm;
import co.luism.iot.web.ui.common.HorizontalSplitter;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import co.luism.iot.web.common.GenericGuiName;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.common.WebManagerFacedCommandEnum;
import co.luism.iot.web.common.WebPageEnum;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PageHeader
 * co.luism.diagnostics.web.ui
 * Created by luis on 24.09.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */

public class PageHeader extends CustomComponent {

    private final ProgressBar bar = new ProgressBar();
    private final VerticalLayout mainLayout = new VerticalLayout();
    private final Map<Integer, String> mapNavigation = new HashMap<Integer, String>();
    private final Map<Integer, Class> mapDataSource = new HashMap<>();
    private final Map<Integer, WebManagerFacedCommandEnum> mapCommand = new HashMap<>();
    private final MenuBar menuBar = new MenuBar();
    private final Label headingTitle = new Label("WELCOME.....");
    private final Label userName = new Label("NONE");
    private final ComboBox language = new ComboBox();
    private final Button logoutBnt = new Button();
    private User currentUser;

    PopupView popupUser;
    private final UserEditDetailsForm userSettingsForm = new UserEditDetailsForm();
    private ParentView parentView;


    MenuBar.Command navigateTo = new MenuBar.Command() {
        public void menuSelected(MenuBar.MenuItem selectedItem) {

            // Navigate to main view
            getUI().getNavigator().navigateTo(mapNavigation.get(selectedItem.getId()));
        }
    };


    MenuBar.Command selectDataSource = new MenuBar.Command() {
        public void menuSelected(MenuBar.MenuItem selectedItem) {

            // Navigate to main view
            if (parentView != null) {
                bar.setVisible(true);
                parentView.switchDataSource(mapDataSource.get(selectedItem.getId()));
                bar.setVisible(false);
            }

        }
    };

    private MenuBar.Command sendCommand = new MenuBar.Command() {
        public void menuSelected(MenuBar.MenuItem selectedItem) {

            switch (mapCommand.get(selectedItem.getId())) {
                case FACADE_COMMAND_RELOAD_DATA_TAG:
                    bar.setVisible(true);
                    WebManagerFacade.getInstance().reloadDataTags();
                    bar.setVisible(false);
                    break;
                case FACADE_COMMAND_RESTART_WEB_MANAGER:
                    bar.setVisible(true);
                    WebManagerFacade.getInstance().restartWebManager(this.getClass());
                    bar.setVisible(false);
                break;
                case FACADE_COMMAND_RELOAD_DATA_SCANNER:
                    bar.setVisible(true);
                    WebManagerFacade.getInstance().reloadDataScanner();
                    bar.setVisible(false);
                break;
                default:
                    break;
            }

        }


    };

    Button.ClickListener logout = new Button.ClickListener() {

        @Override
        public void buttonClick(Button.ClickEvent event) {

            // "Logout" the user
            getSession().setAttribute(User.class, null);
            // Refresh this view, should redirect to login view
            getUI().getNavigator().navigateTo(OnDiagLoginView.NAME.getValue());
        }

    };


    Property.ValueChangeListener languageListener = new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            Object item = language.getValue();
            Language l = (Language) item;
            parentView.updateLanguageGui(l.getName());
            userSettingsForm.setCaptionNames(l.getName());
            setHeadingText(l.getName());
            LanguageManager.getInstance().setCurrentLanguage(l.getName());

        }
    };

    private MouseEvents.ClickListener goHome = new MouseEvents.ClickListener() {

        @Override
        public void click(MouseEvents.ClickEvent event) {
            if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {

                if (parentView.getNAME() != WebPageEnum.main) {
                    getUI().getNavigator().navigateTo(OnDiagMainView.NAME.getValue());
                }


            }
        }
    };


    public PageHeader(ParentView parent) {

        parentView = parent;
        buildMainLayout();


    }

    private void buildMainLayout() {

        HorizontalLayout h = new HorizontalLayout();
        mainLayout.addComponent(h);
        // Serve the image from the theme
        Resource res = new ThemeResource("img/logo.png");
        // Display the image without caption
        Image logo = new Image(null, res);
        logo.addClickListener(goHome);
        h.addComponent(logo);
        logo.setId("site_logo");

        VerticalLayout centerLayout = new VerticalLayout();
        h.addComponent(centerLayout);
        headingTitle.addStyleName("h1");
        headingTitle.setId(PageElementId.PAGE_TITLE);
        bar.setIndeterminate(true);
        bar.setVisible(false);
        centerLayout.addComponent(bar);
        centerLayout.addComponent(headingTitle);
        centerLayout.addComponent(menuBar);

        centerLayout.setComponentAlignment(headingTitle, Alignment.TOP_LEFT);
        centerLayout.setComponentAlignment(menuBar, Alignment.BOTTOM_LEFT);

        userName.addStyleName("h2");
        h.addComponent(userName);
        popupUser = new PopupView(userSettingsForm);
        popupUser.setWidth("64px");
        popupUser.setHeight("64px");
        popupUser.setStyleName("image-popup");
        popupUser.setId(PageElementId.USER_SETTINGS);
        h.addComponent(popupUser);
        h.addComponent(logoutBnt);


        logoutBnt.setCaption("logout");
        logoutBnt.setId(PageElementId.LOGOUT_BNT);
        logoutBnt.addClickListener(logout);

        if (parentView.getNAME() == WebPageEnum.login || parentView.getNAME() == WebPageEnum.rpass) {
            headingTitle.setVisible(false);
            menuBar.setVisible(false);
            userName.setVisible(false);
            popupUser.setVisible(false);
            logoutBnt.setVisible(false);

        }

        populateLanguageComboBox();
        h.addComponent(language);
        language.setId(PageElementId.CURRENT_LANGUAGE);

        h.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);
        h.setComponentAlignment(centerLayout, Alignment.BOTTOM_LEFT);
        h.setComponentAlignment(popupUser, Alignment.MIDDLE_CENTER);
        h.setComponentAlignment(userName, Alignment.MIDDLE_CENTER);
        h.setComponentAlignment(logoutBnt, Alignment.MIDDLE_CENTER);
        h.setComponentAlignment(language, Alignment.MIDDLE_LEFT);

        centerLayout.setMargin(true);
        userName.setWidth(100, Unit.PERCENTAGE);
        logoutBnt.setWidth(95, Unit.PERCENTAGE);
        language.setWidth(95, Unit.PERCENTAGE);

        h.setWidth(100, Unit.PERCENTAGE);
        h.setExpandRatio(centerLayout, 6);
        h.setExpandRatio(popupUser, 1);
        h.setExpandRatio(userName, 1);
        h.setExpandRatio(logoutBnt, 0.5f);
        h.setExpandRatio(language, 0.8f);

        mainLayout.setComponentAlignment(h, Alignment.MIDDLE_LEFT);

        mainLayout.addComponent(new HorizontalSplitter());
        setCompositionRoot(mainLayout);

    }

    private void populateLanguageComboBox() {

        String currentLanguage = LanguageManager.getInstance().getCurrentLanguage();
        Set<Language> languageList = LanguageManager.getInstance().getActiveLanguages();
        BeanItemContainer languageBeanItemContainer = new BeanItemContainer(Language.class, languageList);
        language.setContainerDataSource(languageBeanItemContainer);
        language.addValueChangeListener(languageListener);


        for (Language l : languageList) {

            if (l.getFlag() == null) {
                continue;
            }

            if ("".equals(l.getFlag())) {
                continue;
            }
            Resource res = new ThemeResource(l.getFlag());
            language.setItemIcon(l, res);

        }

        language.setItemCaptionPropertyId("name");
        language.setTextInputAllowed(false);
        language.setNullSelectionAllowed(false);
        language.setImmediate(true);

        for (Object o : language.getItemIds()) {
            if (o instanceof Language) {
                Language l = (Language) o;
                if (currentLanguage.equals(l.getName())) {
                    language.setValue(o);
                    break;
                }
            }
        }


    }

    private void createMenuItems() {

        WebPageEnum fromPage = parentView.getNAME();

        if (fromPage == WebPageEnum.admin) {
            MenuBar.MenuItem cnf = menuBar.addItem("User Management", null, null);
            addUserItems(cnf);
            cnf = menuBar.addItem("Fleet Management", null, null);
            addFleetItems(cnf);
            cnf = menuBar.addItem("Language Management", null, null);
            addLangItems(cnf);
            cnf = menuBar.addItem("Data Management", null, null);
            addDataItems(cnf);

        }

        if (OnDiagPermissionManager.grantAdminAccess(currentUser)) {

            if (fromPage == WebPageEnum.main) {
               MenuBar.MenuItem snacks = menuBar.addItem("Admin", null, navigateTo);
               mapNavigation.put(snacks.getId(), WebPageEnum.admin.getValue());
            }

            menuBar.setVisible(true);
        } else {
            menuBar.setVisible(false);
        }


    }

    private void addUserItems(MenuBar.MenuItem config) {

        MenuBar.MenuItem i = config.addItem("organizations", null, selectDataSource);
        mapDataSource.put(i.getId(), Organization.class);
        i = config.addItem("users", null, selectDataSource);
        mapDataSource.put(i.getId(), User.class);
        i = config.addItem("roles", null, selectDataSource);
        mapDataSource.put(i.getId(), Role.class);
        i = config.addItem("permissions", null, selectDataSource);
        mapDataSource.put(i.getId(), Permission.class);


    }

    private void addFleetItems(MenuBar.MenuItem config) {

        MenuBar.MenuItem i = config.addItem("Configurations", null, selectDataSource);
        mapDataSource.put(i.getId(), Configuration.class);
        i = config.addItem("fleets", null, selectDataSource);
        mapDataSource.put(i.getId(), Fleet.class);
        i = config.addItem("trains", null, selectDataSource);
        mapDataSource.put(i.getId(), Vehicle.class);
        i = config.addItem("restart web manager", null, sendCommand);
        mapCommand.put(i.getId(), WebManagerFacedCommandEnum.FACADE_COMMAND_RESTART_WEB_MANAGER);


    }

    private void addLangItems(MenuBar.MenuItem config) {

        MenuBar.MenuItem i = config.addItem("languages", null, selectDataSource);
        mapDataSource.put(i.getId(), Language.class);
        i = config.addItem("translations", null, selectDataSource);
        mapDataSource.put(i.getId(), Translation.class);

    }

    private void addDataItems(MenuBar.MenuItem config) {

        MenuBar.MenuItem i = config.addItem("tags", null, selectDataSource);
        mapDataSource.put(i.getId(), DataTag.class);
        i = config.addItem("Data Scan", null, selectDataSource);
        mapDataSource.put(i.getId(), DataScanCollector.class);
        i = config.addItem("Buffers", null, selectDataSource);
        mapDataSource.put(i.getId(), AlarmBuffer.class);
        i = config.addItem("Categories", null, selectDataSource);
        mapDataSource.put(i.getId(), AlarmCategory.class);
        i = config.addItem("Category Event List", null, selectDataSource);
        mapDataSource.put(i.getId(), CategorySignalMap.class);
        i = config.addItem("reload DataTags", null, sendCommand);
        mapCommand.put(i.getId(), WebManagerFacedCommandEnum.FACADE_COMMAND_RELOAD_DATA_TAG);
        i = config.addItem("reload Data Scanner ", null, sendCommand);
        mapCommand.put(i.getId(), WebManagerFacedCommandEnum.FACADE_COMMAND_RELOAD_DATA_SCANNER);

    }


    public void setUserName(User user) {
        currentUser = user;
        this.userName.setValue(user.getLogin());
        userSettingsForm.setCurrentUser(user);
        String cl = user.getLanguage();

        for (Object o : language.getItemIds()) {
            if (o instanceof Language) {
                Language l = (Language) o;
                if (cl.equals(l.getName())) {
                    language.setValue(o);
                    break;
                }
            }
        }

        setHeadingText(cl);

        createMenuItems();

    }

    private void setHeadingText(String cl) {
        //check user organization
        if (currentUser == null) {
            return;
        }

        String welcomeMessage;

            welcomeMessage = String.format("%s %s %s",
                    LanguageManager.getInstance().getValue(cl, GenericGuiName.Welcome.getValue()),
                    currentUser.getMyOrganization().getName(),
                    LanguageManager.getInstance().getValue(cl, GenericGuiName.OnlineDiagnostics.getValue())
                     );

        headingTitle.setValue(welcomeMessage);
    }

}
