package co.luism.iot.web.ui.common;

import com.vaadin.ui.Component;

/**
 * Created by luis on 07.11.14.
 */
public class OnDiagPageTab<T> {

    private Component component;
    private T tab;


    public OnDiagPageTab(Component tabMainInstrumentPanel, T name) {
        this.component = tabMainInstrumentPanel;
        this.tab = name;

    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public T getTab() {
        return tab;
    }

    public void setTab(T tab) {
        this.tab = tab;
    }

}
