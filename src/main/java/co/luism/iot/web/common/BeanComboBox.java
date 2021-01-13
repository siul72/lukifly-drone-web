package co.luism.iot.web.common;/*
  ____        _ _ _                   _____           _
 |  __ \     (_) | |                 / ____|         | |
 | |__) |__ _ _| | |_ ___  ___      | (___  _   _ ___| |_ ___ _ __ ___  ___
 |  _  // _` | | | __/ _ \/ __|      \___ \| | | / __| __/ _ \ '_ ` _ \/ __|
 | | \ \ (_| | | | ||  __/ (__       ____) | |_| \__ \ ||  __/ | | | | \__ \
 |_|  \_\__,_|_|_|\__\___|\___|     |_____/ \__, |___/\__\___|_| |_| |_|___/
                                            __/ /
 Railtec Systems GmbH                      |___/
 6052 Hergiswil

 SVN file informations:
 Subversion Revision $Rev: $
 Date $Date: $
 Commmited by $Author: $
*/


import co.luism.diagnostics.enterprise.Fleet;
import co.luism.diagnostics.enterprise.Organization;
import co.luism.diagnostics.webmanager.LanguageManager;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.common
 * Created by luis on 02.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class BeanComboBox<T> extends ComboBox {
    private static final Logger LOG = Logger.getLogger(BeanComboBox.class);

    private Class myClass;
    private T nullItem;

    public BeanComboBox(Class clazz, Property.ValueChangeListener myListener) {
        super(clazz.getSimpleName());
        this.myClass = clazz;
        addValueChangeListener(myListener);
        setTextInputAllowed(false);
        setNullSelectionAllowed(true);
        setImmediate(true);

        if (this.myClass != String.class) {
            this.setItemCaptionPropertyId("name");
        }

        //addNullItem();

    }


    public boolean bindData(List dataList) {
        boolean status = this.removeAllItems();
        if (dataList == null) {
            return status;
        }

        //dataList.add(0, nullItem);
        BeanItemContainer beanItemContainer = new BeanItemContainer(this.myClass, dataList);
        setContainerDataSource(beanItemContainer);
        return status;

    }


    private void addNullItem() {

        if (nullItem == null) {
            if (this.myClass != String.class) {

                try {
                    nullItem = (T) this.myClass.newInstance();

                } catch (InstantiationException e) {
                    LOG.error(e);
                } catch (IllegalAccessException e) {
                    LOG.error(e);
                }

                if (nullItem instanceof Organization) {
                    Organization org = (Organization) nullItem;
                    org.setName(LanguageManager.getInstance().getValue("all"));
                }

                if (nullItem instanceof Fleet) {
                    Fleet fleet = (Fleet) nullItem;
                    fleet.setName(LanguageManager.getInstance().getValue("all"));
                }


            } else {
                nullItem = (T) LanguageManager.getInstance().getValue("all");

            }
        }

        if (nullItem != null) {

            setNullSelectionItemId(nullItem);
        }

    }


}
