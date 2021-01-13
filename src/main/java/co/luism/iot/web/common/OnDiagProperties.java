package co.luism.iot.web.common;

import co.luism.common.Utils;
import org.apache.log4j.Logger;


import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

import java.util.Properties;

/**
 * Created by luis on 22.01.15.
 */
public class OnDiagProperties {

    private static final Logger LOG = Logger.getLogger(OnDiagProperties.class);
    private static OnDiagProperties instance;
    private Properties properties;
    public static OnDiagProperties getInstance(){
        if(instance == null){
            instance = new OnDiagProperties();
        }

        return instance;
    }
    private final String softwareVersion;
    private final Boolean optimizeMarkers;

    OnDiagProperties(){
        softwareVersion = getVersion();
        LOG.info(String.format("Version: %s", softwareVersion));
        optimizeMarkers = getOptimizer();
        LOG.info(String.format("Marker optimization: %s", optimizeMarkers));
    }

    private Boolean getOptimizer() {
        Boolean f = true;
        try {
            String v = readProp("ksoft-iot.optimize.google.markers");
            f = Boolean.parseBoolean(v);
        } catch (IOException e) {
            LOG.error("Optimizer not found " + e);
            return true;
        }

        return f;
    }


    private String getVersion()  {

        try {
            return readProp("ksoft-iot.version");
        } catch (IOException e) {
            LOG.error("Version no Found" + e);
            return "";
        }

    }

    private String readProp(String prop) throws IOException {

        if(properties == null){

            File file = Utils.createResourceFile(OnDiagProperties.class, "config/", "ksoft-iot.properties");
            if(!file.exists()){
                LOG.error(String.format("file not found %s", file.getAbsolutePath()));
                return "";
            }

            FileInputStream fileInput = new FileInputStream(file);
            properties = new Properties();
            properties.load(fileInput);
            fileInput.close();
        }

        return properties.getProperty(prop);

    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public Boolean getOptimizeMarkers() {
        return optimizeMarkers;
    }
}
