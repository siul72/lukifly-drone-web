package co.luism.iot.web.pages;/*
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

import co.luism.diagnostics.enterprise.Permission;
import co.luism.diagnostics.enterprise.Role;
import co.luism.diagnostics.enterprise.User;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web
 * Created by luis on 03.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class OnDiagPermissionManager {

    public static boolean grantAdminAccess(User u){

        if(u == null){
            return false;
        }

        String object = "all";
        String permission = "all";

        Role r = u.getMyRole();
        for(Permission p : r.getPermissionList()){
            if(p.getObject().equals(object) && p.getPermission().equals(permission)){
                return true;
            }
        }

        return false;
    }
}
