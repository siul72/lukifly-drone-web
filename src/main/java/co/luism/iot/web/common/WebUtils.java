package co.luism.iot.web.common;/*
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


import co.luism.common.Utils;
import co.luism.diagnostics.enterprise.User;
import co.luism.diagnostics.webmanager.WebManagerFacade;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.common
 * Created by luis on 08.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class WebUtils {

    public static String getSessionUserName(VaadinSession session){
        String name = "";
        User u = session.getAttribute(User.class);
        if(u != null){
            name = u.getLogin();
        }

        return name;
    }

    public static Boolean themeResourceExist(String r) {

        UI ui = UI.getCurrent();
        InputStream s = ui.getSession().getService().getThemeResourceAsStream(ui, ui.getTheme(), r);

        if (s != null) {
            return true;
        }

        return false;
    }

    public static int getUsedMemory(){
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();


        long KILOBYTE = 1024L;
        long MEGABYTE = KILOBYTE * 1024L;
        memory = memory / KILOBYTE;
        return (int) memory;
    }

    public static int getCpuUsage(){
//        Sigar sigar=new Sigar();
//        int cpu=0;
//        try {
//            final ProcCpu procCPU=sigar.getProcCpu(sigar.getPid());
//            cpu=(int)Double.parseDouble(CpuPerc.format(procCPU.getPercent()).replace("%",""));
//        }
//        catch (  final Exception e) {
//        }
//
//        return cpu;

        return 0;
    }

    public static void readProp(){
        try {

            File file = Utils.createResourceFile(WebUtils.class, "config/", "ksoft-iot.properties");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                String value = properties.getProperty(key);
                System.out.println(key + ": " + value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fmt(double d)
    {
        if(d == (long) d){
            return String.format("%d",(long)d);
        } else {
            return String.format("%s",d);
        }

    }

    public static String fmt2(double d)
    {
        if(d == (long) d) {
            return String.format("%d", (long)d);
        } else {
            return String.format("%.2f",d);
        }

    }




}
