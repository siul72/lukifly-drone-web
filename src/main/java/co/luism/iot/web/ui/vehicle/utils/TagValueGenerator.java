package co.luism.iot.web.ui.vehicle.utils;

import co.luism.diagnostics.enterprise.SnapShotGenericValue;
import co.luism.iot.web.common.WebUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;


/**
 * Created by luis on 27.01.15.
 */
public class TagValueGenerator implements Table.ColumnGenerator {

    public TagValueGenerator() {

    }

    public Component generateCell(Table source, Object itemId,
                                  Object columnId) {


        Item i = source.getItem(itemId);

        if(i instanceof BeanItem){
            Object o = ((BeanItem) i).getBean();
            if(o instanceof SnapShotGenericValue){
                Label label = new Label();
                SnapShotGenericValue sv = (SnapShotGenericValue) o;
                Double scaledValue =  sv.getValue() * sv.getScale();
                label.setValue(WebUtils.fmt(scaledValue));
                return label;
            }
        }

        return null;
    }
}
