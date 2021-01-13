package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.common.DateTimeUtils;
import co.luism.datacollector.AlarmEnvironmentDataRequester;
import co.luism.diagnostics.common.EventEnvDataStatus;
import co.luism.diagnostics.common.GetEnvStatusEvent;
import co.luism.diagnostics.enterprise.AlarmValueHistoryInfo;
import co.luism.diagnostics.interfaces.IGetEnvStatusEventHandler;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.ui.common.HorizontalSplitter;
import co.luism.iot.web.ui.vehicle.utils.AlarmUtils;
import co.luism.iot.web.ui.vehicle.charts.OnDiagChart;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by luis on 24.11.14.
 */
public class AlarmHistoryGui extends AlarmGui implements IGetEnvStatusEventHandler {

    private static final Logger LOG = Logger.getLogger(AlarmHistoryGui.class);
    private VerticalLayout chartContainer = new VerticalLayout();
    private AlarmValueHistoryInfo alarmValueHistoryInfo;
    private List<AlarmEnvironmentDataRequester> alarmEnvironmentDataRequesterList = new ArrayList<>();

    private EventEnvDataStatus eventEnvDataStatus = EventEnvDataStatus.GET_NONE;
    private final Label getEnvStatus = new Label();
    private final Button getEnvDataChart = new Button(LanguageManager.getInstance().getValue("en",
            "GET_ALARM_ENV_DATA"));

    private List<OnDiagChart> myChartList;

    private final Button closeChart = new Button(LanguageManager.getInstance().getValue("en",
            "CLOSE_CHARTS"));

    public AlarmHistoryGui() {

        startUp();


    }

    @Override
    public void startUp(){

        getEnvDataChart.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    checkShowChart();
                }
            });
        closeChart.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                detailsContainer.setVisible(true);
                chartContainer.setVisible(false);
            }
        });

        this.mainIcon = new Image(null, new ThemeResource("icons/alarm_history_item.png"));
        detailsContainer.addComponent(getEnvStatus);
        detailsButtons.addComponent(getEnvDataChart);
        super.buildMainLayout();

    }

    @Override
    public void closeDown() {

        for (AlarmEnvironmentDataRequester req : this.alarmEnvironmentDataRequesterList) {
            req.removeEnvStatusListener(this);
        }

        super.closeDown();

    }

    private void checkShowChart() {
        if (eventEnvDataStatus == EventEnvDataStatus.GET_NONE ||
                eventEnvDataStatus == EventEnvDataStatus.GET_END) {

            if (myChartList != null) {
                chartContainer.setVisible(true);
                detailsContainer.setVisible(false);
                return;
            }

            if (alarmValueHistoryInfo != null) {
                addCharts(AlarmUtils.createChartList(alarmValueHistoryInfo.getId()));
            }
        }
    }

    private void addCharts(List<OnDiagChart> chartList) {

        if (chartList == null) {
            Notification.show(LanguageManager.getInstance().getValue("ALARM_ENV_DATA_NOT_AVAILABLE"), Notification.Type.HUMANIZED_MESSAGE);
            return;
        }

        if (chartList.isEmpty()) {
            Notification.show(LanguageManager.getInstance().getValue("ALARM_ENV_DATA_NOT_AVAILABLE"), Notification.Type.HUMANIZED_MESSAGE);
            return;
        }

        myChartList = chartList;

        for (OnDiagChart chart : chartList) {
            chartContainer.addComponent(chart);
            chart.setId(PageElementId.A_GUI_HISTORY_ENV_DATA_CHART + chart.getTitle());
            chartContainer.addComponent(new HorizontalSplitter());
        }

        chartContainer.addComponent(closeChart);
        detailsLayout.addComponent(chartContainer);

        detailsContainer.setVisible(false);

    }



    @Override
    public void setAlarmTagValue(Object alarmTagValue) {

        this.alarmValueHistoryInfo = (AlarmValueHistoryInfo) alarmTagValue;
        Collection myCollection = WebManagerFacade.getInstance()
                .getAlarmEnvironmentDataRequesterList(this.alarmValueHistoryInfo.getId());

        if (myCollection != null) {
            this.alarmEnvironmentDataRequesterList.addAll(myCollection);

            for (AlarmEnvironmentDataRequester req : this.alarmEnvironmentDataRequesterList) {
                req.addEnvStatusListener(this);
            }

        }

        this.dataTag = WebManagerFacade.getInstance().getAlarmTagById(alarmValueHistoryInfo.getTagId());
        bindData();
    }


    @Override
    protected void bindData() {
        super.bindData();

        errorCode.setValue(String.format("E%s:%s",
                this.alarmValueHistoryInfo.getId(),
                this.alarmValueHistoryInfo.getEventIndex()));

        String info = String.format("TagName:%s, StartTS:%s, EndTS:%s T=%d, Count=%d, Status=%d",
                this.dataTag.getName(),
                DateTimeUtils.getTimeStringUtc(this.alarmValueHistoryInfo.getStartTimeStamp()),
                DateTimeUtils.getTimeStringUtc(this.alarmValueHistoryInfo.getEndTimeStamp()),
                this.alarmValueHistoryInfo.getDuration(),
                this.alarmValueHistoryInfo.getNumberOfEvents(),
                this.alarmValueHistoryInfo.getStatusInfo());

        if (this.alarmValueHistoryInfo.getMyTag() != null) {
            String st = (this.alarmValueHistoryInfo.getMyTag().isPreData()) ? "on" : "off";
            info = info.concat(String.format(" Pre=%s", st));
            st = (this.alarmValueHistoryInfo.getMyTag().isPostData()) ? "on" : "off";
            info = info.concat(String.format(" Post=%s", st));
            //set id for test automation
            setElementIds();
        }

        this.detailInfo.setValue(info);
        paintEventStatus();

    }

    private void paintEventStatus() {

        List<EventEnvDataStatus> tempStatusList = new ArrayList<>();

        for (AlarmEnvironmentDataRequester req : this.alarmEnvironmentDataRequesterList) {
            tempStatusList.add(req.getEnvDataStatus());
        }

        eventEnvDataStatus = calculateEventDataStatus(tempStatusList);

        switch (eventEnvDataStatus) {

            case GET_START:
                resumeLayout.setStyleName("v-alarm-env-start");
                resumeLayout.addStyleName("v-blink");
                break;
            case GET_NEXT:
                resumeLayout.setStyleName("v-alarm-env-next");
                resumeLayout.addStyleName("v-blink");
                break;
            case GET_END:
                resumeLayout.setStyleName("v-alarm-env-end");
                resumeLayout.removeStyleName("v-blink");
                break;

            default:
                resumeLayout.setStyleName("v-alarm-env-none");
                break;
        }

        getEnvStatus.setValue(String.format("EnvDataStatus=%s", eventEnvDataStatus.toString()));


    }

    private EventEnvDataStatus calculateEventDataStatus(List<EventEnvDataStatus> tempStatusList) {

        //if any is start then start
        for (EventEnvDataStatus e : tempStatusList) {
            if (e.equals(EventEnvDataStatus.GET_START)) {
                return e;
            }
        }

        for (EventEnvDataStatus e : tempStatusList) {
            if (e.equals(EventEnvDataStatus.GET_NEXT)) {
                return e;
            }
        }

        //find if all are end
        EventEnvDataStatus temp = EventEnvDataStatus.GET_NONE;
        for (EventEnvDataStatus e : tempStatusList) {
            temp = e;
        }

        return temp;
    }

    @Override
    void setAlarmAck(Object source) {

        //this.alarmValueHistoryInfo.setAck(true);
        //this.alarmValueHistoryInfo.update();
        //setVisible(false);

    }

    @Override
    void detailInfoShow(Object source, Boolean visible) {

    }

    @Override
    public void handleEnvStatus(GetEnvStatusEvent getEnvStatusEvent) {

        if(this.alarmValueHistoryInfo.getId() != getEnvStatusEvent.getHistoryId()){
            LOG.debug(String.format("this is not for me %d:%d", this.alarmValueHistoryInfo.getId(),
                    getEnvStatusEvent.getHistoryId()));
            return;
        }
        LOG.debug("rcv new status event " + getEnvStatusEvent.getStatus().toString());
        paintEventStatus();
    }

    public AlarmValueHistoryInfo getAlarmValueHistoryInfo() {
        return alarmValueHistoryInfo;
    }

    private void setElementIds(){

        String elementId = String.format("-%s-%d-%d",
                this.getClass().getSimpleName(),
                this.alarmValueHistoryInfo.getEventIndex(),
                this.alarmValueHistoryInfo.getMyTag().getSourceTagId());

        this.detailsIcon.setId(PageElementId.A_GUI_INFO_ICON + elementId);
        this.errorCode.setId(PageElementId.A_GUI_ERROR_CODE + elementId);
        this.getEnvDataChart.setId(PageElementId.A_GUI_GET_ENV_DATA_BNT + elementId);
        this.closeChart.setId(PageElementId.A_GUI_CLOSE_CHART_BNT + elementId);
        this.shortDescription.setId(PageElementId.A_GUI_SHORT_DESC + elementId);

    }
}
