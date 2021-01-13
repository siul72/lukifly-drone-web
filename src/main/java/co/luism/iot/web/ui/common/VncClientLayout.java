package co.luism.iot.web.ui.common;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

/**
 * Created by luis on 26.11.14.
 */
public class VncClientLayout extends CustomLayout {

    public VncClientLayout(){
        super("vnc_client");
    }

    public void addStatusLabel(Component c){
        addComponent(c, "status");

    }

    public void addDisplay(Component c){
        addComponent(c, "display");
    }

}
