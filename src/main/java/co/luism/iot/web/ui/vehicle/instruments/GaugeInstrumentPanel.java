package co.luism.iot.web.ui.vehicle.instruments;

import co.luism.datacollector.DataCollectorDataScanner;
import co.luism.diagnostics.enterprise.*;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.interfaces.OnDiagVehiclePanel;
import co.luism.iot.web.ui.vehicle.instruments.gauge.Gauge;
import co.luism.iot.web.ui.vehicle.instruments.gauge.GaugeConfig;
import co.luism.diagnostics.webmanager.LanguageManager;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class GaugeInstrumentPanel extends CustomComponent implements OnDiagCustomComponent, OnDiagVehiclePanel {

    private static final Logger LOG = Logger.getLogger(GaugeInstrumentPanel.class);
    private VerticalLayout mainLayout = new VerticalLayout();
    private Vehicle myVehicle;
    private Map<Integer, Gauge> processGaugeMap = new HashMap<>();
    private Map<Integer, Gauge> tagIdGaugeMap = new HashMap<>();
    private Label warningLabel = new Label();

    public GaugeInstrumentPanel() {
       startUp();
    }

    @Override
    public void startUp() {

        buildMainLayout();

    }

    @Override
    public void closeDown() {

        myVehicle = null;
    }

    @Override
    public void buildMainLayout() {

        setSizeFull();
        mainLayout.setSizeFull();
        setCompositionRoot(mainLayout);

    }

    @Override
    public void setCaptionNames(String language) {

        for(Gauge g : processGaugeMap.values()){
            g.setTitle(language);
        }
    }

    private void buildInstrumentsPanel(Vehicle vehicle) {

        if(vehicle == null){
            return;
        }

        if(this.myVehicle != null){

            if(this.myVehicle.getFleetId().equals(vehicle.getFleetId())){
                return;
            }

        }

        this.myVehicle = vehicle;

        MMIConfig mmiConfig = MMIConfigManager.getInstance().getMMIConfig(myVehicle.getMyFleet().getName());

        mainLayout.removeAllComponents();

        if(mmiConfig == null){
            warningLabel.setValue(String.format("\u2639 no mmi config for Fleet %s ☹", myVehicle.getMyFleet().getName()));
            warningLabel.setSizeUndefined();
            mainLayout.addComponent(warningLabel);
            mainLayout.setComponentAlignment(warningLabel, Alignment.MIDDLE_CENTER);

            LOG.error(String.format("could not found the mmi configuration for fleet Name %d", myVehicle.getMyFleet().getName()));
            return;
        }

        for(int line=0; line < mmiConfig.getPageLines(); line++){
            HorizontalLayout lineLayout = new HorizontalLayout();
            // common part: create layout
            lineLayout.setSizeFull();
            lineLayout.setImmediate(true);
            lineLayout.setWidth("-1px");
            lineLayout.setHeight("-1px");
            lineLayout.setMargin(false);

            mainLayout.addComponent(lineLayout);
            mainLayout.setComponentAlignment(lineLayout, Alignment.MIDDLE_CENTER);

            for(int column=0; column < mmiConfig.getPageColumns(); column++){

                GaugeConfig gf = mmiConfig.getGaugeConfig(column, line);
                if(gf == null){
                    LOG.warn(String.format("Not found GaugeConfig for line=%d, column=%d",
                            line, column));
                    continue;
                }
                DataTag tag = WebManagerFacade.getInstance().getTagByProcessId(gf.getProcessId());
                Integer tagId = (tag==null) ? null : tag.getTagId();
                Gauge g = new Gauge(gf, tagId);
                g.setImmediate(false);
                g.setWidth("-1px");
                g.setHeight("-1px");
                lineLayout.addComponent(g);
                processGaugeMap.put(gf.getProcessId(), g);

                if(tagId != null){
                    tagIdGaugeMap.put(tagId, g);
                }

            }

            mainLayout.addComponent(lineLayout);

        }

        setCaptionNames(LanguageManager.getInstance().getCurrentLanguage());

    }


    private void setAlarmValue(Integer p, long v) {

        Gauge g = processGaugeMap.get(p);
        if(g == null){
            return;
        }

        g.setAlarm(v > 0);



    }

    private void setProcessValue(Integer p, long v) {

        Gauge g = processGaugeMap.get(p);
        if(g == null){
            LOG.debug(String.format("no process match for %s - not configured", p));
            return;
        }

        g.setValue(v);
    }

    public void setVehicle(Vehicle vehicle) {

        buildInstrumentsPanel(vehicle);
        clearInstrumentData();
        loadProcessData(vehicle);

    }

    private void loadProcessData(Vehicle vehicle) {

        if(vehicle == null){
            return;
        }

        Collection<SnapShotGenericValue> mySnapShotList = DataCollectorDataScanner.getInstance().getValuesForVehicle(vehicle);

        if(mySnapShotList == null){
            LOG.error("vehicle as mySnapShotList null");
            return;
        }

        for(SnapShotGenericValue snapShotGenericValue : mySnapShotList){

            Gauge g = tagIdGaugeMap.get(snapShotGenericValue.getTagId());
            if(g== null){
                continue;
            }

            g.setValue((long)(snapShotGenericValue.getValue() * snapShotGenericValue.getScale()));
        }


    }

    @Override
    public Vehicle getMyVehicle() {
        return myVehicle;
    }

    @Override
    public void updateStatus() {

    }

    @Override
    public void setProcessValue(DataTag tag, GenericTagValue genericTagValue) {

        if (tag == null || this.myVehicle == null) {
            LOG.warn("DataTag or myVehicle is null");
            return;
        }

        if (tag.getName() == null) {
            LOG.error("DataTag name or getSnapShotValue is null");
            return;
        }


        if (genericTagValue == null) {
            LOG.error(String.format("no valid snapshot value for tag %s", tag.getName()));
            return;
        }

        if (tag.getProcess() == null) {
            LOG.error(String.format("process value not found for tag %s", tag.getName()));
            return;
        }

        Integer p = tag.getProcess();

        if (p <= 0) {
            LOG.error(String.format("process not found for tag %s", tag.getName()));
            return;
        }

        setProcessValue(p, genericTagValue.getValue());


    }

    @Override
    public void setAlarmValue(DataTag tag, SnapShotAlarmTagValue snapShotAlarmTagValue) {

        if (tag == null || this.myVehicle == null) {
            LOG.warn("DataTag or myVehicle is null");
            return;
        }

        if (tag.getName() == null) {
            LOG.error("DataTag name or getSnapShotValue is null");
            return;
        }


        if (snapShotAlarmTagValue == null) {
            LOG.error(String.format("no valid snapshot value for tag %s", tag.getName()));
            return;
        }

        if (tag.getProcess() == null) {
            LOG.error(String.format("process value not found for tag %s", tag.getName()));
            return;
        }

        Integer p = tag.getProcess();

        if (p <= 0) {
            LOG.error(String.format("process not found for tag %s", tag.getName()));
            return;
        }

        setProcessValue(p, snapShotAlarmTagValue.getValue());

    }

    private void clearInstrumentData() {


        for(Gauge g : processGaugeMap.values()){
            g.setAlarm(false);
            g.setValue(g.getMinValue());
        }

    }

    public int getGaugeCurrentValue(String gaugeName){

        for(Gauge g : processGaugeMap.values()){
            if(g.getName().equals(gaugeName)){
                return g.getCurrentValue();
            }
        }

        LOG.error("Gauge Not Found!");
        return 0;
    }


}
