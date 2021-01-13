package co.luism.iot.web.common;

import co.luism.iot.web.interfaces.OnDiagCustomComponent;

/**
 * Created by luis on 17.02.15.
 */
public class OnDiagTabSheet extends com.vaadin.ui.TabSheet {

    private OnDiagCustomComponent previousSelected;

    public OnDiagCustomComponent getPreviousSelected() {
        return previousSelected;
    }

    public void setPreviousSelected(OnDiagCustomComponent previousSelected) {
        this.previousSelected = previousSelected;
    }
}
