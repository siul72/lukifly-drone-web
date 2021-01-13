package co.luism.iot.web.ui.vehicle.alarm;

import co.luism.diagnostics.common.DiagnosticsEvent;
import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.common.ReturnCode;
import co.luism.diagnostics.enterprise.DataTag;
import co.luism.diagnostics.enterprise.GenericTagValue;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.diagnostics.enterprise.Vehicle;
import co.luism.diagnostics.interfaces.IDiagnosticsEventHandler;
import co.luism.iot.web.common.FileUploader;
import co.luism.iot.web.common.PageElementId;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.server.Page;
import com.vaadin.ui.*;

import java.io.File;

/**
 * Created by luis on 02.02.15.
 */
public class ImportExportComponent extends CustomComponent implements OnDiagCustomComponent, IDiagnosticsEventHandler, OnDiagVehiclePanel {

    private VerticalLayout mainLayout;
    private ProgressBar progressBar;
    private Label progressStatus;
    private Vehicle vehicle = null;
    private Upload uploadVehicleAlarmDataZipFile;

    public ImportExportComponent(){

    }

    @Override
    public void startUp() {

        mainLayout = new VerticalLayout();
        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressStatus = new Label();
        progressStatus.setVisible(false);
        progressStatus.setId(PageElementId.UPLOAD_RESULT);
        FileUploader receiver = new FileUploader(this);
        uploadVehicleAlarmDataZipFile = new Upload("Zip Data File", receiver);
        uploadVehicleAlarmDataZipFile.addSucceededListener(receiver);

        buildMainLayout();
    }

    @Override
    public void buildMainLayout() {

        mainLayout.setHeight("200px");

        uploadVehicleAlarmDataZipFile.setSizeUndefined();
        mainLayout.addComponent(uploadVehicleAlarmDataZipFile);
        progressBar.setSizeUndefined();
        progressBar.setWidth("300px");
        mainLayout.addComponent(progressBar);
        //progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
        progressStatus.setSizeUndefined();
        mainLayout.addComponent(progressStatus);

        mainLayout.setComponentAlignment(uploadVehicleAlarmDataZipFile, Alignment.MIDDLE_CENTER);
        mainLayout.setComponentAlignment(progressBar, Alignment.MIDDLE_CENTER);
        mainLayout.setComponentAlignment(progressStatus, Alignment.MIDDLE_CENTER);

        setCompositionRoot(mainLayout);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {

        uploadVehicleAlarmDataZipFile.setCaption(LanguageManager.getInstance().getValue(currentLanguage, "ALARM_DATA_ZIP_FILE"));
        uploadVehicleAlarmDataZipFile.setButtonCaption(LanguageManager.getInstance().getValue(currentLanguage, "START_UPLOAD"));
        uploadVehicleAlarmDataZipFile.setId(PageElementId.UPLOAD_ELEMENT);
    }

    @Override
    public void closeDown() {

    }

    public void setUploadFile(File file, Object source) {
        ReturnCode ret = WebManagerFacade.getInstance().importAlarms(vehicle, this, file);


        if(ret != ReturnCode.RET_OK){
            new Notification(String.format("%s \n %s",
                    LanguageManager.getInstance().getValue("FAIL_TO_IMPORT_ALARM_DATA"),
                    LanguageManager.getInstance().getValue(ret.toString())),
                    Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
        }
        progressStatus.setValue(LanguageManager.getInstance().getValue("IMPORT_FINISH"));
        progressStatus.setVisible(true);
        progressBar.setVisible(false);
    }

    public void setStartProgress(String value){
        progressBar.setVisible(true);
        progressStatus.setVisible(false);
    }

    @Override
    public void handleDiagnosticsEvent(DiagnosticsEvent diagnosticsEvent) {

        if(diagnosticsEvent.getTagType() == EventTypeEnum.PROGRESS_IMPORT_ALARM_DATA){
            progressBar.setValue(diagnosticsEvent.getValue());
        }

        if(diagnosticsEvent.getTagType() == EventTypeEnum.PROGRESS_IMPORT_ENV_DATA){
            progressBar.setValue(diagnosticsEvent.getValue());
        }

    }

    @Override
    public void setVehicle(Vehicle v) {
        vehicle = v;
    }

    @Override
    public Vehicle getMyVehicle() {
        return this.vehicle;
    }

    @Override
    public void updateStatus() {

    }

    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {

    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {

    }
}
