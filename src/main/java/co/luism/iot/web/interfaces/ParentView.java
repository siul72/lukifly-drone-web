package co.luism.iot.web.interfaces;/*
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

import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.enterprise.Organization;
import co.luism.iot.web.common.WebPageEnum;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.interfaces
 * Created by luis on 30.09.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public interface ParentView {

    void updateLanguageGui(String currentLanguage);
    void switchDataSource(Class aClass);
    WebPageEnum getNAME();
    void filterVehicles(Organization org, Fleet f, String type);
    void buildLayout();
    void startUp();
    void closeDown();


}
