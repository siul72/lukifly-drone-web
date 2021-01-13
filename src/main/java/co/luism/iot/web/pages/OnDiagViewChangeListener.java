package co.luism.iot.web.pages;

import co.luism.diagnostics.enterprise.User;
import co.luism.iot.web.interfaces.ParentView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;

/**
 * Created by luis on 09.12.14.
 */
public class OnDiagViewChangeListener implements ViewChangeListener {

    private static final Logger LOG = Logger.getLogger(OnDiagViewChangeListener.class);
    private final static OnDiagViewChangeListener instance = new OnDiagViewChangeListener();
    private Object currentView;

    OnDiagViewChangeListener(){

    }

    public static OnDiagViewChangeListener getInstance() {
        return instance;
    }

    public boolean beforeViewChange(ViewChangeEvent event) {

        if (event.getOldView() instanceof OnDiagMainView) {
            //check new view
            if(event.getNewView() instanceof OnDiagAdministrationView){
                User u = UI.getCurrent().getSession().getAttribute(User.class);
                if(u == null){
                    return false;
                }

                if (!OnDiagPermissionManager.grantAdminAccess(u)){
                    return false;
                }
            }

            LOG.debug("before Exit OnDiagMainView");
            ((OnDiagMainView) event.getOldView()).closeDown();
        }


        // Check if a user has logged in
        boolean isLoggedIn = UI.getCurrent().getSession().getAttribute(User.class) != null;
        boolean isLoginView = event.getNewView() instanceof OnDiagLoginView;

        if (!isLoggedIn && !isLoginView) {
            // Redirect to login view always if a user has not yet
            UI.getCurrent().getNavigator().navigateTo(OnDiagLoginView.NAME.getValue());
            return false;

        } else if (isLoggedIn && isLoginView) {
            // If someone tries to access to login view while logged in,
            // then cancel
            return false;
        }

        return true;

    }

    public void afterViewChange(ViewChangeEvent event) {

        this.currentView = event.getNewView();

        if(this.currentView instanceof ParentView){
            String title = String.format("Online Diagnostics - %s", ((ParentView) event.getNewView() ).getNAME());
            UI.getCurrent().getPage().setTitle(title);
        }
    }

    public Object getCurrentView() {
        return currentView;
    }
}
