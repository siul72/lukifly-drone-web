package co.luism.iot.web.ui.administration;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis on 04.03.15.
 */
public class PagedTable extends Table {
    private static final long serialVersionUID = 6881455780158545828L;
    private List<PageChangeListener> listeners = null;
    private BeanContainer beanContainer;
    private TextField searchField = new TextField();
    private String currentFilterColumn;
    private final ComboBox filterColumnCombo = new ComboBox();
    private String[] currentVisibleColumns;
    private PagedTableContainer pagedTableContainer = new PagedTableContainer();

    public PagedTable() {
        setPageLength(25);
        initSearch();
    }



    public interface PageChangeListener {
        public void pageChanged(PagedTableChangeEvent event);
    }

    public class PagedTableChangeEvent {

        final PagedTable table;

        public PagedTableChangeEvent(PagedTable table) {
            this.table = table;
        }

        public PagedTable getTable() {
            return table;
        }

        public int getCurrentPage() {
            return table.getCurrentPage();
        }

        public int getTotalAmountOfPages() {
            return table.getTotalAmountOfPages();
        }
    }

    public HorizontalLayout createControls() {
        Label itemsPerPageLabel = new Label("Items per page:");
        final ComboBox itemsPerPageSelect = new ComboBox();

        //itemsPerPageSelect.addItem("5");
        itemsPerPageSelect.addItem("10");
        itemsPerPageSelect.addItem("25");
        itemsPerPageSelect.addItem("50");
        itemsPerPageSelect.addItem("100");
        //itemsPerPageSelect.addItem("600");
        itemsPerPageSelect.setImmediate(true);
        itemsPerPageSelect.setNullSelectionAllowed(false);
        itemsPerPageSelect.setWidth("50px");
        itemsPerPageSelect.addValueChangeListener(new ValueChangeListener() {
            private static final long serialVersionUID = -2255853716069800092L;

            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                setPageLength(Integer.valueOf(String.valueOf(event
                        .getProperty().getValue())));
            }
        });
        itemsPerPageSelect.select("25");

        filterColumnCombo.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {

                try{
                    currentFilterColumn = valueChangeEvent.getProperty().getValue().toString();
                } catch (NullPointerException ex){
                    currentFilterColumn = null;
                }

            }
        });


        Label pageLabel = new Label("Page:&nbsp;", ContentMode.HTML);
        final TextField currentPageTextField = new TextField();
        currentPageTextField.setValue(String.valueOf(getCurrentPage()));
        currentPageTextField.setConverter(Integer.class);
        final IntegerRangeValidator validator = new IntegerRangeValidator("Wrong page number", 1, getTotalAmountOfPages());
        currentPageTextField.addValidator(validator);
        Label separatorLabel = new Label("&nbsp;/&nbsp;", ContentMode.HTML);
        final Label totalPagesLabel = new Label(
                String.valueOf(getTotalAmountOfPages()), ContentMode.HTML);
        currentPageTextField.setStyleName(Reindeer.TEXTFIELD_SMALL);
        currentPageTextField.setImmediate(true);
        currentPageTextField.addValueChangeListener(new ValueChangeListener() {
            private static final long serialVersionUID = -2255853716069800092L;

            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                if (currentPageTextField.isValid()
                        && currentPageTextField.getValue() != null) {
                    int page = Integer.valueOf(String
                            .valueOf(currentPageTextField.getValue()));
                    setCurrentPage(page);
                }
            }
        });
        pageLabel.setWidth(null);
        currentPageTextField.setWidth("20px");
        separatorLabel.setWidth(null);
        totalPagesLabel.setWidth(null);

        HorizontalLayout controlBar = new HorizontalLayout();
        HorizontalLayout pageSize = new HorizontalLayout();
        HorizontalLayout pageFilter = new HorizontalLayout();
        HorizontalLayout pageManagement = new HorizontalLayout();
        final Button first = new Button("<<", new Button.ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

            public void buttonClick(Button.ClickEvent event) {
                setCurrentPage(0);
            }
        });
        final Button previous = new Button("<", new Button.ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

            public void buttonClick(Button.ClickEvent event) {
                previousPage();
            }
        });
        final Button next = new Button(">", new Button.ClickListener() {
            private static final long serialVersionUID = -1927138212640638452L;

            public void buttonClick(Button.ClickEvent event) {
                nextPage();
            }
        });
        final Button last = new Button(">>", new Button.ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

            public void buttonClick(Button.ClickEvent event) {
                setCurrentPage(getTotalAmountOfPages());
            }
        });
        first.setStyleName(Reindeer.BUTTON_LINK);
        previous.setStyleName(Reindeer.BUTTON_LINK);
        next.setStyleName(Reindeer.BUTTON_LINK);
        last.setStyleName(Reindeer.BUTTON_LINK);

        pageSize.addComponent(itemsPerPageLabel);
        pageSize.addComponent(itemsPerPageSelect);
        pageSize.setComponentAlignment(itemsPerPageLabel, Alignment.MIDDLE_LEFT);
        pageSize.setComponentAlignment(itemsPerPageSelect, Alignment.MIDDLE_LEFT);
        //pageSize.setSpacing(true);

        pageFilter.addComponent(filterColumnCombo);
        filterColumnCombo.setWidth("120px");
        pageFilter.addComponent(searchField);
        pageFilter.setComponentAlignment(filterColumnCombo, Alignment.MIDDLE_CENTER);
        pageFilter.setComponentAlignment(searchField, Alignment.MIDDLE_CENTER);
        pageFilter.setSpacing(true);

        pageManagement.addComponent(first);
        pageManagement.addComponent(previous);
        pageManagement.addComponent(pageLabel);
        pageManagement.addComponent(currentPageTextField);
        pageManagement.addComponent(separatorLabel);
        pageManagement.addComponent(totalPagesLabel);
        pageManagement.addComponent(next);
        pageManagement.addComponent(last);
        pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(currentPageTextField, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(separatorLabel,  Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(totalPagesLabel,  Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
        pageManagement.setWidth(null);
        pageManagement.setSpacing(true);

        controlBar.addComponent(pageSize);
        controlBar.addComponent(pageFilter);
        controlBar.addComponent(pageManagement);
        controlBar.setComponentAlignment(pageFilter,Alignment.MIDDLE_CENTER);
        controlBar.setWidth("100%");
        controlBar.setExpandRatio(pageFilter, 1);

        addListener(new PageChangeListener() {
            public void pageChanged(PagedTableChangeEvent event) {
                first.setEnabled(pagedTableContainer.getStartIndex() > 0);
                previous.setEnabled(pagedTableContainer.getStartIndex() > 0);
                next.setEnabled(pagedTableContainer.getStartIndex() < pagedTableContainer
                        .getRealSize() - getPageLength());
                last.setEnabled(pagedTableContainer.getStartIndex() < pagedTableContainer
                        .getRealSize() - getPageLength());
                currentPageTextField.setValue(String.valueOf(getCurrentPage()));
                totalPagesLabel.setValue(String.valueOf(getTotalAmountOfPages()));
                itemsPerPageSelect.setValue(String.valueOf(getPageLength()));
                validator.setMaxValue(getTotalAmountOfPages());
            }
        });
        return controlBar;
    }

    public void setFilterColumns(String[] filterColumns){

        filterColumnCombo.removeAllItems();
        currentFilterColumn = null;

        if(filterColumns == null){

            return;
        }

        if(filterColumns.length <=0){

            return;
        }



        for(String s : filterColumns){
            filterColumnCombo.addItem(s);
        }

        currentFilterColumn = filterColumns[0];
        filterColumnCombo.select(currentFilterColumn);

    }


    @Override
    public Container.Indexed getContainerDataSource() {
        return pagedTableContainer;
    }

    @Override
    public void setContainerDataSource(Container newDataSource) {

        if(newDataSource instanceof BeanContainer){
            this.beanContainer = (BeanContainer) newDataSource;

            if(pagedTableContainer == null){
                pagedTableContainer = new PagedTableContainer();
            }
            pagedTableContainer.setContainer((Container.Indexed)newDataSource);
            pagedTableContainer.setPageLength(getPageLength());
            super.setContainerDataSource(pagedTableContainer);
            firePagedChangedEvent();

        }


    }

    private void setPageFirstIndex(int firstIndex) {
        if (pagedTableContainer != null) {
            if (firstIndex <= 0) {
                firstIndex = 0;
            }
            if (firstIndex > pagedTableContainer.getRealSize() - 1) {
                int size = pagedTableContainer.getRealSize() - 1;
                int pages = 0;
                if (getPageLength() != 0) {
                    pages = (int) Math.floor(0.0 + size / getPageLength());
                }
                firstIndex = pages * getPageLength();
            }
            pagedTableContainer.setStartIndex(firstIndex);
            setCurrentPageFirstItemIndex(firstIndex);
            containerItemSetChange(new Container.ItemSetChangeEvent() {
                private static final long serialVersionUID = -5083660879306951876L;

                public Container getContainer() {
                    return pagedTableContainer;
                }
            });
            if (alwaysRecalculateColumnWidths) {
                for (Object columnId : pagedTableContainer.getContainerPropertyIds()) {
                    setColumnWidth(columnId, -1);
                }
            }
            firePagedChangedEvent();
        }
    }

    private void firePagedChangedEvent() {
        if (listeners != null) {
            PagedTableChangeEvent event = new PagedTableChangeEvent(this);
            for (PageChangeListener listener : listeners) {
                listener.pageChanged(event);
            }
        }
    }

    @Override
    public void setPageLength(int pageLength) {

        if(pagedTableContainer == null) {

            return;
        }

        if (pageLength >= 0 && getPageLength() != pageLength) {
            pagedTableContainer.setPageLength(pageLength);
            super.setPageLength(pageLength);
            firePagedChangedEvent();
        }
    }

    public void nextPage() {

        if(pagedTableContainer == null) {

            return;
        }

        setPageFirstIndex(pagedTableContainer.getStartIndex() + getPageLength());
    }

    public void previousPage() {

        if(pagedTableContainer == null) {

            return;
        }

        setPageFirstIndex(pagedTableContainer.getStartIndex() - getPageLength());
    }

    public int getCurrentPage() {

        if(pagedTableContainer == null) {

            return 0;
        }

        double pageLength = getPageLength();
        int page = (int) Math.floor((double) pagedTableContainer.getStartIndex()
                / pageLength) + 1;
        if (page < 1) {
            page = 1;
        }
        return page;
    }

    public void setCurrentPage(int page) {

        if(pagedTableContainer == null) {

            return;
        }

        int newIndex = (page - 1) * getPageLength();
        if (newIndex < 0) {
            newIndex = 0;
        }
        if (newIndex >= 0 && newIndex != pagedTableContainer.getStartIndex()) {
            setPageFirstIndex(newIndex);
        }
    }

    public int getTotalAmountOfPages() {
        if(pagedTableContainer == null) {

            return 0;
        }

        if(pagedTableContainer.getContainer() == null){
            return 0;
        }

        int size = pagedTableContainer.getContainer().size();
        double pageLength = getPageLength();
        int pageCount = (int) Math.ceil(size / pageLength);
        if (pageCount < 1) {
            pageCount = 1;
        }
        return pageCount;
    }

    public void addListener(PageChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<PageChangeListener>();
        }
        listeners.add(listener);
    }

    public void removeListener(PageChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<PageChangeListener>();
        }
        listeners.remove(listener);
    }

    public void setAlwaysRecalculateColumnWidths(
            boolean alwaysRecalculateColumnWidths) {
        this.alwaysRecalculateColumnWidths = alwaysRecalculateColumnWidths;
    }

    private void initSearch() {

        /*
         * We want to show a subtle prompt in the search field. We could also
         * set a caption that would be shown above the field or description to
         * be shown in a tooltip.
         */
        searchField.setInputPrompt("Search");

        /*
         * Granularity for sending events over the wire can be controlled. By
         * default simple changes like writing a text in TextField are sent to
         * server with the next Ajax call. You can set your component to be
         * immediate to send the changes to server immediately after focus
         * leaves the field. Here we choose to send the text over the wire as
         * soon as user stops writing for a moment.
         */
        searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);

        /*
         * When the event happens, we handle it in the anonymous inner class.
         * You may choose to use separate controllers (in MVC) or presenters (in
         * MVP) instead. In the end, the preferred application architecture is
         * up to you.
         */
        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {

                /* Reset the filter for the contactContainer. */
                //contactContainer.removeAllContainerFilters();
                //contactContainer.addContainerFilter(new ContactFilter(event.getText()));

                if(currentFilterColumn == null){
                    return;
                }

                beanContainer.removeAllContainerFilters();
                beanContainer.addContainerFilter(new TableFilter(event.getText(), currentFilterColumn));
                setContainerDataSource(beanContainer);
                setVisibleColumns(currentVisibleColumns);

            }
        });
    }

    @Override
    public void setVisibleColumns(Object... visibleColumns){
        if(visibleColumns instanceof String[]){
            currentVisibleColumns = (String[]) visibleColumns;
        }

        super.setVisibleColumns(visibleColumns);
    }

    /*
    * A custom filter for searching names and companies in the
    * contactContainer.
    */
    private class TableFilter implements Container.Filter {
        private String needle;
        private String searchColons;

        public TableFilter(String needle,  String columns) {
            this.needle = needle.toLowerCase();
            searchColons = columns;
        }

        public boolean passesFilter(Object itemId, Item item) {
            try{
                String haystack = item.getItemProperty(searchColons).getValue().toString();
                return haystack.contains(needle);
            } catch (NullPointerException ex){
                return false;
            }
        }

        public boolean appliesToProperty(Object id) {
            return true;
        }
    }


}
