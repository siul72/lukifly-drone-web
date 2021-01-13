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

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.common
 * Created by luis on 01.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public enum GenericGuiName {



    none("empty"), Save("Save"), Update("Update"),
    Cancel("Cancel"), Language("Language"), Welcome("WelcomeTo"), OnlineDiagnostics("OnlineDiagnostics"),
    filter("filter");


    private final String value;

    private GenericGuiName(String value) {
        this.value = value;


    }

    public String getValue() {
        return value;
    }

}