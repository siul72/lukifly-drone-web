package co.luism.iot.web.ui.vehicle.utils;

import co.luism.common.DateTimeUtils;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * Created by luis on 27.01.15.
 */
public class TimeStampGenerator implements Table.ColumnGenerator {

    public TimeStampGenerator() {

    }

    public Component generateCell(Table source, Object itemId,
                                  Object columnId) {
        // Get the object stored in the cell as a property
        Property prop = source.getItem(itemId).getItemProperty(columnId);
        if (prop.getType().equals(Integer.class)) {
            Label label = new Label();
            Integer v = (Integer) prop.getValue();
            label.setValue(DateTimeUtils.getTimeStringUtc(v));
            label.addStyleName("column-type-value");
            label.addStyleName("column-" + (String) columnId);
            return label;
        }
        return null;
    }
}
