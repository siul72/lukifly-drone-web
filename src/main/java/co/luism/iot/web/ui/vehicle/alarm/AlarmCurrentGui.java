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
package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.common.DateTimeUtils;
import co.luism.diagnostics.common.AlarmSyncStatus;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.iot.web.common.PageElementId;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class AlarmCurrentGui extends AlarmGui {

    private SnapShotAlarmTagValue alarmTagValue;

    public AlarmCurrentGui(){
        startUp();
    }

    @Override
    public void startUp(){
        this.mainIcon = new Image(null, new ThemeResource("icons/alarm_icon.png"));
        this.workshopInfoPanel = new Panel();
        this.workshopInfo = new Label();
        super.buildMainLayout();
    }

    public void setFresh(){
        //alarmTagValue.setFresh(true);
        switch (this.alarmTagValue.getAlarmSyncStatus()){
            case ALARM_SYNC_NONE:
            case ALARM_SYNC_OLD:
                resumeLayout.setStyleName("v-alarm-old");
                break;
            case ALARM_SYNC_FRESH:
                resumeLayout.setStyleName("v-alarm-new-fresh");
                break;

        }

    }

    @Override
    protected void bindData(){
        super.bindData();

        errorCode.setValue(String.format("P%s", dataTag.getTagId()));

        String details = String.format("TagName: %s TimeStamp: %s  Status: %s", dataTag.getName(),
                DateTimeUtils.getTimeStringUtc(alarmTagValue.getTimeStamp()),
                alarmTagValue.getStatus());
        detailInfo.setValue(details);

        if(alarmTagValue.isAck()){
            resumeLayout.setStyleName("v-alarm-ticked");
            detailsIcon.setStyleName("v-alarm-new-element");
            //alarmAck.setStyleName("v-alarm-new-element");
            closeAlarm.setStyleName("v-alarm-new-element");
        } else {
            if(alarmTagValue.getAlarmSyncStatus() == AlarmSyncStatus.ALARM_SYNC_FRESH){
                resumeLayout.setStyleName("v-alarm-new-fresh");
            }
        }
    }



    @Override
    void setAlarmAck(Object source) {

        //this.alarmTagValue.setAck(true);
        //this.alarmTagValue.update();
        //setVisible(false);

    }

    @Override
    void detailInfoShow(Object source, Boolean visible) {

    }

    public SnapShotAlarmTagValue getAlarmTagValue() {
        return alarmTagValue;
    }

    @Override
    public void setAlarmTagValue(Object alarmTagValue) {
        this.alarmTagValue = (SnapShotAlarmTagValue)alarmTagValue;
        this.dataTag = WebManagerFacade.getInstance().getAlarmTagById(this.alarmTagValue.getTagId());
        bindData();
        setElementIds();
    }


    private void setElementIds(){

        String elementId = String.format("-%s-%d",
                this.getClass().getSimpleName(),
                this.dataTag.getTagId());

        this.detailsIcon.setId(PageElementId.A_GUI_INFO_ICON + elementId);
        this.errorCode.setId(PageElementId.A_GUI_ERROR_CODE + elementId);
        this.shortDescription.setId(PageElementId.A_GUI_SHORT_DESC + elementId);

    }
}
