package co.luism.iot.web.ui.vehicle.vnc;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * Created by luis on 25.11.14.
 */
public class VncState extends JavaScriptComponentState {
    public String domId = null;
    public Integer actionType = 0;
    public Integer port = 0;
    public Integer status = 0;


}
