package co.luism.iot.web.ui.common;

/*
  ____        _ _ _                   _____           _
 |  __ \     (_) | |                 / ____|         | |
 | |__) |__ _ _| | |_ ___  ___      | (___  _   _ ___| |_ ___ _ __ ___  ___
 |  _  // _` | | | __/ _ \/ __|      \___ \| | | / __| __/ _ \ '_ ` _ \/ __|
 | | \ \ (_| | | | ||  __/ (__       ____) | |_| \__ \ ||  __/ | | | | \__ \
 |_|  \_\__,_|_|_|\__\___|\___|     |_____/ \__, |___/\__\___|_| |_| |_|___/
                                            __/ /
 Railtec Systems GmbH                      |___/
 6052 Hergiswil

 SVN file information:
 Subversion Revision $Rev: $
 Date $Date: $
 Committed by $Author: $
*/

import co.luism.diagnostics.enterprise.AlarmValueHistoryInfo;
import co.luism.diagnostics.enterprise.SnapShotAlarmTagValue;
import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import co.luism.iot.web.ui.vehicle.alarm.AlarmCurrentGui;
import co.luism.iot.web.ui.vehicle.alarm.AlarmHistoryGui;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class PagingComponent<T> extends CustomComponent implements OnDiagCustomComponent, Button.ClickListener {

    private final Class clazz;
    private float pageSize;
    private Integer currentPage;
    private static final Logger LOG = Logger.getLogger(PagingComponent.class);
    private List<T> componentList = new ArrayList<>();
    VerticalLayout container = new VerticalLayout();
    HorizontalLayout navigator = new HorizontalLayout();
    private int numberOfPages = 0;


    public PagingComponent(Class clazz) {
        this.clazz = clazz;
        startUp();
    }

    @Override
    public void startUp() {
        buildMainLayout();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void addItem(Integer id, Object dataObject) {

        T myComponent = findComponent(id);

        //check if exist
        if (myComponent == null) {
            Object o;
            try {
                o = this.clazz.newInstance();
            } catch (InstantiationException e) {
                LOG.error(e);
                return;
            } catch (IllegalAccessException e) {
                LOG.error(e);
                return;
            }

            if (o instanceof AlarmCurrentGui && dataObject instanceof SnapShotAlarmTagValue) {

                ((AlarmCurrentGui) o).setAlarmTagValue(dataObject);

            }

            if (o instanceof AlarmHistoryGui && dataObject instanceof AlarmValueHistoryInfo) {
                ((AlarmHistoryGui) o).setAlarmTagValue(dataObject);

            }

            this.componentList.add(0, (T) o);

        } else {
            if (myComponent instanceof AlarmCurrentGui) {
                AlarmCurrentGui AlarmCurrentGui = (AlarmCurrentGui) myComponent;
                AlarmCurrentGui.setFresh();
            }
            return;
        }

        //get it back
        //myComponent = findComponent(id);
        refreshInsertPage();
    }

    private T findComponent(Integer id) {

        for (T component : this.componentList) {
            if (component instanceof AlarmHistoryGui) {
                if (id.equals(((AlarmHistoryGui) component).getAlarmValueHistoryInfo().getId())) {
                    return component;
                }
            }

            if (component instanceof AlarmCurrentGui) {
                if (id.equals(((AlarmCurrentGui) component).getAlarmTagValue().getTagId())) {
                    return component;
                }
            }
        }

        return null;
    }

    private void refreshInsertPage() {
        currentPage = -1;
        placePage(0);
    }


    public void remove(Integer id) {

        T myComponent = findComponent(id);

        if (this.componentList.contains(myComponent)) {
            this.componentList.remove(myComponent);
        }

        Integer cIx = this.container.getComponentIndex((CustomComponent) myComponent);
        if (cIx >= 0) {
            this.container.removeComponent((CustomComponent) myComponent);
        }

        refreshRemovePage();

    }

    private void refreshRemovePage() {

        double x = this.componentList.size() / pageSize;
        int newNumberOfPages = (int) Math.ceil(x);

        if (newNumberOfPages != numberOfPages) {

            currentPage = -1;
            placePage(0);


        }

    }

    public void addAll(List<?> eListOfDataObjects) {

        for (Object dataObjectValue : eListOfDataObjects) {
            Object newComponent;
            try {
                newComponent = this.clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error(e);
                continue;
            }

            if (newComponent instanceof AlarmCurrentGui && dataObjectValue instanceof SnapShotAlarmTagValue) {
                ((AlarmCurrentGui) newComponent).setAlarmTagValue(dataObjectValue);
                this.componentList.add((T) newComponent);
                continue;
            }

            if (newComponent instanceof AlarmHistoryGui && dataObjectValue instanceof AlarmValueHistoryInfo) {
                ((AlarmHistoryGui) newComponent).setAlarmTagValue(dataObjectValue);

                this.componentList.add((T) newComponent);
                continue;
            }

        }
    }


    public void clear() {
        this.componentList.clear();
        this.container.removeAllComponents();
        currentPage = -1;
        numberOfPages = 0;
    }

    public void showFirstPage() {
        placePage(0);
    }

    private void placePage(Integer i) {

        if (i.equals(currentPage)) {
            return;
        }

        this.container.removeAllComponents();

        if (this.componentList.size() < pageSize) {
            for (T v : componentList) {
                if (v instanceof CustomComponent) {
                    this.container.addComponent((CustomComponent) v);
                    ((CustomComponent) v).setImmediate(true);
                }
            }
            return;
        }

        int finalIndex;

        if ((i + 1) * pageSize > this.componentList.size()) {
            finalIndex = this.componentList.size();
        } else {
            finalIndex = (i + 1) * (int) pageSize;
        }

        for (int index = i * (int) pageSize; index < finalIndex; index++) {

            T c = this.componentList.get(index);
            if (c instanceof CustomComponent) {
                this.container.addComponent((CustomComponent) c);
                ((CustomComponent) c).setImmediate(true);
            } else {
                LOG.debug("is not Custom Component");
            }

        }

        buildNavigator(i);

    }

    private void buildNavigator(Integer activePage) {
        //numberOfPages = this.componentList.size() / pageSize;
        double x = this.componentList.size() / pageSize;
        numberOfPages = (int) Math.ceil(x);

        if (navigator.getComponentCount() == numberOfPages && currentPage >= 0) {

            this.navigator.getComponent(currentPage).removeStyleName("page-active");
            this.navigator.getComponent(activePage).addStyleName("page-active");
            this.currentPage = activePage;
            this.container.addComponent(navigator);
            return;
        }

        //place go to begin
        navigator.removeAllComponents();
        //Image navImage = new Image(null, new ThemeResource("icons/paging_icon.png"));
        //navigator.addComponent(navImage);
        for (Integer i = 0; i < numberOfPages; i++) {

            Button navPage = new Button(String.format("%d", i + 1));
            navPage.setStyleName("paging effect7");
            if (i.equals(activePage)) {
                navPage.addStyleName("page-active");
                this.currentPage = activePage;
            }

            navPage.setData(i);
            navPage.addClickListener(this);
            navigator.addComponent(navPage);
        }

        this.container.addComponent(navigator);
    }


    @Override
    public void buildMainLayout() {

        setSizeFull();
        navigator.setHeight("100%");
        navigator.setWidthUndefined();
        navigator.setImmediate(true);
        this.container.setImmediate(true);
        setCompositionRoot(this.container);
    }

    @Override
    public void setCaptionNames(String currentLanguage) {

        for (T alarmGui : this.componentList) {

            if (alarmGui instanceof OnDiagCustomComponent) {
                ((OnDiagCustomComponent) alarmGui).setCaptionNames(currentLanguage);
            }


        }
    }

    @Override
    public void closeDown() {

    }


    @Override
    public void buttonClick(Button.ClickEvent clickEvent) {

        //LOG.debug("page clicked");
        //place page index
        Object o = clickEvent.getSource();
        if (o instanceof Button) {
            Object data = ((Button) o).getData();
            if (data instanceof Integer) {
                //LOG.debug("clicked page " + data);
                placePage((Integer) data);
            }
        }

    }


    public void updateStatus() {
        for (T c : this.componentList) {
            if (c instanceof AlarmCurrentGui) {
                ((AlarmCurrentGui) c).setFresh();
            }
        }
    }
}
