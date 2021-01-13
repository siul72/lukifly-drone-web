package co.luism.iot.web.ui.vehicle.vnc;


import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JavaScript({"vnc.js", "vnc_connector.js"})
public class VncComponent extends AbstractJavaScriptComponent {

    public interface StatusChangeListener extends Serializable {
        void statusChange();
    }

    List<StatusChangeListener> listeners = new ArrayList<StatusChangeListener>();

    public VncComponent() {
        getState().domId = "display";
        //getState().actionType = 1;
        //getState().port = 5900;
        setWidth("1024px");
        setHeight("768px");


        addFunction("onError", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments)
                    throws JSONException {
                getState().status = 3;
                for (StatusChangeListener listener : listeners) {
                    listener.statusChange();
                }

            }
        });

    }

    public void addStatusChangeListener(StatusChangeListener listener) {
        listeners.add(listener);
    }

    public void reset() {
        getState().actionType = 0;

    }

    public void start() {

        getState().actionType = 1;

    }

    public void stop() {

        getState().actionType = 2;

    }

    public void setPort(Integer port) {
        getState().port = port;

    }

    @Override
    protected VncState getState() {
        return (VncState) super.getState();
    }

    public void setStatus(Integer value) {
        getState().status = value;
    }

    public Integer getStatus() {
        return getState().status;
    }


}


