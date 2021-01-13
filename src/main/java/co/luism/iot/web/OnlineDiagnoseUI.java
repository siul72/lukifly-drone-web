package co.luism.iot.web;


import co.luism.diagnostics.enterprise.User;
import co.luism.iot.web.pages.OnDiagAdministrationView;
import co.luism.iot.web.pages.OnDiagLoginView;
import co.luism.iot.web.pages.OnDiagMainView;
import co.luism.iot.web.pages.OnDiagViewChangeListener;
import com.google.gwt.json.client.JSONException;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.*;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@Push
@Theme("mytheme")
public class OnlineDiagnoseUI extends UI {

    private static final Logger LOG = Logger.getLogger(OnlineDiagnoseUI.class);


    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = OnlineDiagnoseUI.class,
            widgetset = "co.luism.diagnostics.web.AppWidgetSet")
    public static class Servlet extends VaadinServlet {



    }

    public OnlineDiagnoseUI(){

    }

    private void initViews(){

        if(getNavigator() == null){
            new Navigator(this, this);
            getNavigator().addView(OnDiagLoginView.NAME.getValue(), OnDiagLoginView.class);
            getNavigator().addView(OnDiagMainView.NAME.getValue(), OnDiagMainView.class);
            getNavigator().addView(OnDiagAdministrationView.NAME.getValue(), OnDiagAdministrationView.class);
            getNavigator().addViewChangeListener(OnDiagViewChangeListener.getInstance());
            addDetachListener(WebSessionManager.getInstance().addDetachListener(this));
        }


    }

    @Override
    protected void init(VaadinRequest request) {

        DeploymentConfiguration conf = getSession().getConfiguration();
        // Heartbeat interval in seconds
        int heartbeatInterval = conf.getHeartbeatInterval();
        WrappedSession session = getSession().getSession();
        int sessionTimeout = session.getMaxInactiveInterval();
        LOG.info(String.format("Start Session with hb=%d s and session timeout=%d s", heartbeatInterval,
                sessionTimeout));

        initViews();

        JavaScript.getCurrent().addFunction("aboutToClose", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments)
                    throws JSONException {
                LOG.debug("Got aboutToClose callback!!");
                terminateSession();
            }
        });

        Page.getCurrent().getJavaScript().execute("window.onbeforeunload = function (e) { var e = e || window.event; sessionStorage.setItem('is_reloaded', true); aboutToClose(); return; };");

    }

    public void terminateSession(){
//        if(getSession().getAttribute(User.class) != null){
//            //getSession().close();
//            getSession().setAttribute(User.class, null);
//            getNavigator().navigateTo(OnDiagLoginView.NAME.getValue());
//
//
//        }

        UI.getCurrent().detach();
        //UI.getCurrent().getConnectorTracker().cleanConnectorMap();
        //OnDiagViewChangeListener.getInstance().getCurrentView();

    }

    public void closeDown() {

        LOG.info("session as expire - LOG out user");
        //getUI().getNavigator().navigateTo(OnDiagLoginView.NAME.getValue());

        if(getSession().getAttribute(User.class) != null){
            getSession().setAttribute(User.class, null);
            //getSession().close();
            if(getUI().getPushConnection().isConnected()){
                getUI().getPushConnection().disconnect();
            }
        }

        UI.getCurrent().getNavigator().navigateTo(OnDiagLoginView.NAME.getValue());



    }



}