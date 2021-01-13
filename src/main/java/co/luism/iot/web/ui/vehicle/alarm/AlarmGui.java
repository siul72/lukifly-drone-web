package co.luism.iot.web.ui.vehicle.alarm;


import co.luism.diagnostics.enterprise.AlarmTagDescription;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;


/**
 * Created by luis on 13.11.14.
 */
abstract class AlarmGui extends CustomComponent implements OnDiagCustomComponent {

    protected DataTag dataTag;
    protected AlarmTagDescription alarmTagDescription;
    protected final VerticalLayout mainLayout = new VerticalLayout();
    protected final HorizontalLayout resumeLayout = new HorizontalLayout();
    protected final VerticalLayout detailsLayout = new VerticalLayout();
    protected final VerticalLayout detailsContainer = new VerticalLayout();
    protected final HorizontalLayout detailsButtons = new HorizontalLayout();
    protected String currentLanguage;
    protected Image mainIcon;
    protected final Label errorCode = new Label("P000");
    protected final Label shortDescription = new Label();
    protected boolean detailsVisibleStatus = false;
    protected final Panel longDescriptionPanel = new Panel();
    protected final Label longDescription = new Label();
    protected Panel workshopInfoPanel;
    protected Label workshopInfo;
    protected final Label detailInfo = new Label();
    protected final Image detailsIcon = new Image(null, new ThemeResource("icons/alarm_info_icon.png"));
    //protected final Image alarmAck = new Image(null, new ThemeResource("icons/alarm_tick_icon.png"));
    protected final Image closeAlarm = new Image(null, new ThemeResource("icons/alarm_close.png"));
    protected final Button closeDetails = new Button();

    public AlarmGui(){

        detailsIcon.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent clickEvent) {
                detailsVisibleStatus = !detailsVisibleStatus;
                detailsLayout.setVisible(detailsVisibleStatus);
                detailInfoShow(clickEvent.getSource(), detailsVisibleStatus);
            }
        });

//        alarmAck.addClickListener(new MouseEvents.ClickListener() {
//            @Override
//            public void click(MouseEvents.ClickEvent clickEvent) {
//                resumeLayout.setStyleName("v-alarm-ticked");
//                setAlarmAck(clickEvent.getSource());
//            }
//        });

        closeAlarm.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent clickEvent) {
                mainLayout.setVisible(false);
                detailInfoShow(clickEvent.getSource(), false);
            }
        });

        closeDetails.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {


                detailsLayout.setVisible(false);

            }
        });

        this.currentLanguage = LanguageManager.getInstance().getCurrentLanguage();
        closeDetails.setCaption(LanguageManager.getInstance()
                .getValue(this.currentLanguage, "CLOSE_ALARM_DETAILS"));

    }

    @Override
    public void closeDown() {

    }

    @Override
    public void buildMainLayout() {
        setSizeFull();
        resumeLayout.addComponent(mainIcon);
        resumeLayout.addComponent(errorCode);
        errorCode.setStyleName("h1");
        errorCode.setSizeUndefined();
        resumeLayout.addComponent(shortDescription);
        shortDescription.setStyleName("h2");
        resumeLayout.addComponent(detailsIcon);
//        resumeLayout.addComponent(alarmAck);
        resumeLayout.addComponent(closeAlarm);
        resumeLayout.setMargin(true);
        resumeLayout.setStyleName("v-alarm-new");

        errorCode.setSizeUndefined();
        shortDescription.setSizeUndefined();

        resumeLayout.setComponentAlignment(errorCode, Alignment.MIDDLE_CENTER);
        resumeLayout.setComponentAlignment(shortDescription, Alignment.MIDDLE_LEFT);
        resumeLayout.setComponentAlignment(detailsIcon, Alignment.MIDDLE_CENTER);
        //resumeLayout.setComponentAlignment(alarmAck, Alignment.MIDDLE_CENTER);
        resumeLayout.setComponentAlignment(closeAlarm, Alignment.MIDDLE_CENTER);

        detailsIcon.setStyleName("v-alarm-new-element");
        //alarmAck.setStyleName("v-alarm-new-element");
        closeAlarm.setStyleName("v-alarm-new-element");


        resumeLayout.setExpandRatio(mainIcon, 0.05f);
        resumeLayout.setExpandRatio(errorCode, 0.2f);
        resumeLayout.setExpandRatio(shortDescription, 1);
        //resumeLayout.setExpandRatio(detailsIcon, 0.05f);
        //resumeLayout.setExpandRatio(alarmAck, 0.05f);
        //resumeLayout.setExpandRatio(closeAlarm, 0.05f);

        resumeLayout.setSizeFull();
        resumeLayout.setImmediate(true);
        mainLayout.addComponent(resumeLayout);


        //details
        longDescriptionPanel.setSizeFull();

        longDescriptionPanel.setContent(longDescription);
        detailsContainer.addComponent(longDescriptionPanel);
        if (workshopInfoPanel != null) {
            workshopInfoPanel.setSizeFull();
            workshopInfoPanel.setContent(workshopInfo);
            detailsContainer.addComponent(workshopInfoPanel);
        }

        detailsContainer.addComponent(detailInfo);
        detailsButtons.addComponent(closeDetails);
        detailsContainer.addComponent(detailsButtons);
        detailsLayout.addComponent(detailsContainer);

        mainLayout.addComponent(detailsLayout);
        detailsLayout.setVisible(false);

        setCompositionRoot(mainLayout);

    }

    protected void bindData() {

        this.alarmTagDescription = WebManagerFacade.getInstance()
                .getTagAlarmDescription(this.currentLanguage, dataTag.getTagId());

        setTranslatedDescription(this.alarmTagDescription);

        this.longDescriptionPanel.setCaption(LanguageManager.getInstance()
                .getValue(this.currentLanguage, "ALARM_DESCRIPTION"));

        if (this.workshopInfo != null) {
            this.workshopInfo.setCaption(LanguageManager.getInstance()
                    .getValue(this.currentLanguage, "ALARM_WORKSHOP"));
        }

    }

    private void setTranslatedDescription(AlarmTagDescription alarmTagDescription) {

        if (alarmTagDescription != null) {
            shortDescription.setValue(alarmTagDescription.getShortDescription());
            longDescription.setValue(alarmTagDescription.getLongDescription());
            if (workshopInfo != null) {
                workshopInfo.setValue(alarmTagDescription.getWorkshopDescription());
            }

        } else {
            shortDescription.setValue("NO DESCRIPTION FOUND!");
        }
    }

    @Override
    public void setCaptionNames(String currentLanguage) {
        this.currentLanguage = currentLanguage;
        this.alarmTagDescription = WebManagerFacade.getInstance().getTagAlarmDescription(this.currentLanguage,
                dataTag.getTagId());

        setTranslatedDescription(this.alarmTagDescription);

    }

    abstract void setAlarmTagValue(Object alarmTagValue);

    abstract void setAlarmAck(Object source);

    abstract void detailInfoShow(Object source, Boolean visible);


}
