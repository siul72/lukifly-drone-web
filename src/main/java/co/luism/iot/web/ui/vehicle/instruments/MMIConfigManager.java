package co.luism.iot.web.ui.vehicle.instruments;

import co.luism.common.Utils;
import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luis on 28.01.15.
 */
public class MMIConfigManager {
    private static final Logger LOG = Logger.getLogger(MMIConfigManager.class);
    private static MMIConfigManager instance = null;
    private final Map<String, MMIConfig> fleetIdMMIConfigMap = new HashMap<>();

    MMIConfigManager() {
        if (!load()) {
            save();
        }
    }

    public static MMIConfigManager getInstance() {

        if(instance == null){
            instance = new MMIConfigManager();
        }

        return instance;
    }

    private MMIConfig createDefaultMMIConfig(String fleetName) {
        MMIConfig mmiConfig = new MMIConfig();
        mmiConfig.setFleetName(fleetName);
        return mmiConfig;
    }

    public void save() {

        List<Fleet> myList = WebManagerFacade.getInstance().getAllFleets();
        for (Fleet f : myList) {
            String fName = f.getName();
            fleetIdMMIConfigMap.put(fName, createDefaultMMIConfig(fName));
        }

        myList = new ArrayList(fleetIdMMIConfigMap.values());
        WebManagerFacade.getInstance().saveListToXml(MMIConfig.class, myList);
    }

    public boolean load() {
        List myList = WebManagerFacade.getInstance().loadListFromXml(MMIConfig.class);

        if (myList == null) {
            return false;
        }

        if (myList.size() <= 0) {
            return false;
        }

        fleetIdMMIConfigMap.clear();

        for (Object o : myList) {
            if (o instanceof MMIConfig) {
                String name = ((MMIConfig) o).getFleetName();
                fleetIdMMIConfigMap.put(name, (MMIConfig) o);
                LOG.info(String.format("Add fleet %s to mmi list", name));
            }
        }

        return true;
    }

    public MMIConfig getMMIConfig(String fleetName) {
        return fleetIdMMIConfigMap.get(fleetName);
    }


}
