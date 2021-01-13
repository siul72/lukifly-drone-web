package co.luism.iot.web.interfaces;

import co.luism.diagnostics.common.EventTypeEnum;
import co.luism.diagnostics.enterprise.Vehicle;

import java.util.EventObject;
import java.util.List;

/**
 * Created by luis on 11.11.14.
 */
public class CloseDownEvent extends EventObject {

    CloseDownEvent(Object source) {
            super(source);
        }

}

