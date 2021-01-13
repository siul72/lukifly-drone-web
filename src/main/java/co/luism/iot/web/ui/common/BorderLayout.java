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

 SVN file informations:
 Subversion Revision $Rev: $
 Date $Date: $
 Commmited by $Author: $
*/

import com.vaadin.ui.*;

import java.util.Iterator;

/**
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.ui
 * Created by luis on 13.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
@SuppressWarnings("serial")
public class BorderLayout extends VerticalLayout {




    public enum Constraint {
        NORTH, WEST, CENTER, EAST, SOUTH
    }

    public static final String DEFAULT_MINIMUM_HEIGHT = "50px";

    private VerticalLayout mainLayout;
    private HorizontalLayout centerLayout;

    private String minimumNorthHeight = DEFAULT_MINIMUM_HEIGHT;
    private String minimumSouthHeight = DEFAULT_MINIMUM_HEIGHT;
    private String minimumWestWidth = DEFAULT_MINIMUM_HEIGHT;
    private String minimumEastWidth = DEFAULT_MINIMUM_HEIGHT;

    protected Component north = new Label("NORTH");
    protected Component west = new Label("WEST");
    protected Component center = new Label("CENTER");
    protected Component east = new Label("EAST");
    protected Component south = new Label("SOUTH");

    /**
     * Create a layout structure that mimics the traditional
     * {@link java.awt.BorderLayout}.
     */
    public BorderLayout() {
        mainLayout = new VerticalLayout();
        super.addComponent(mainLayout);

        centerLayout = new HorizontalLayout();
        centerLayout.addComponent(west);
        centerLayout.addComponent(center);
        centerLayout.addComponent(east);
        centerLayout.setSizeFull();

        mainLayout.addComponent(north);
        mainLayout.setComponentAlignment(north, Alignment.MIDDLE_CENTER);
        mainLayout.addComponent(centerLayout);
        mainLayout.setComponentAlignment(centerLayout, Alignment.MIDDLE_CENTER);
        mainLayout.addComponent(south);
        mainLayout.setComponentAlignment(south, Alignment.MIDDLE_CENTER);
        mainLayout.setExpandRatio(centerLayout, 1);

        //set main layout
        mainLayout.setSizeFull();
        setWidth("100%");
        setHeight("100%");
    }

    @Override
    public void setWidth(String width) {
        if (mainLayout == null) {
            return;
        }
        mainLayout.setWidth(width);
        centerLayout.setExpandRatio(center, 1);

    }

    @Override
    public void setHeight(String height) {
        mainLayout.setHeight(height);
        west.setHeight("100%");
        center.setHeight("100%");
        east.setHeight("100%");
        centerLayout.setExpandRatio(center, 1);

    }

    @Override
    public void setSizeFull() {
        super.setSizeFull();
        mainLayout.setSizeFull();
        centerLayout.setExpandRatio(center, 1);

    }

    @Override
    public void setMargin(boolean margin) {
        mainLayout.setMargin(margin);

    }

    @Override
    public void setSpacing(boolean spacing) {
        mainLayout.setSpacing(spacing);
        centerLayout.setSpacing(spacing);

    }

    @Override
    public boolean isSpacing() {
        return (mainLayout.isSpacing() && centerLayout.isSpacing());
    }

    @Override
    public void removeComponent(Component c) {
        replaceComponent(c, new Label(""));
    }

    /**
     * Add component into borderlayout
     *
     * @param c          component to be added into layout
     * @param constraint place of the component (have to be on of BorderLayout.NORTH,
     *                   BorderLayout.WEST, BorderLayout.CENTER, BorderLayout.EAST, or
     *                   BorderLayout.SOUTH
     */
    public void addComponent(Component c, Constraint constraint) {
        if (constraint == Constraint.NORTH) {
            mainLayout.replaceComponent(north, c);
            north = c;
            if (north.getHeight() < 0
                    || north.getHeightUnits() == Unit.PERCENTAGE) {
                north.setHeight(minimumNorthHeight);
            }
        } else if (constraint == Constraint.WEST) {
            centerLayout.replaceComponent(west, c);
            west = c;
            if (west.getWidth() < 0 || west.getWidthUnits() == Unit.PERCENTAGE) {
                west.setWidth(minimumWestWidth);
            }
        } else if (constraint == Constraint.CENTER) {
            centerLayout.replaceComponent(center, c);
            center = c;
            center.setHeight(centerLayout.getHeight(),
                    centerLayout.getHeightUnits());
            center.setWidth("100%");
            centerLayout.setExpandRatio(center, 1);
        } else if (constraint == Constraint.EAST) {
            centerLayout.replaceComponent(east, c);
            east = c;
            if (east.getWidth() < 0 || east.getWidthUnits() == Unit.PERCENTAGE) {
                east.setWidth(minimumEastWidth);
            }
        } else if (constraint == Constraint.SOUTH) {
            mainLayout.replaceComponent(south, c);
            south = c;
            if (south.getHeight() < 0
                    || south.getHeightUnits() == Unit.PERCENTAGE) {
                south.setHeight(minimumSouthHeight);
            }
        } else {
            throw new IllegalArgumentException(
                    "Invalid BorderLayout constraint.");
        }

        centerLayout.setExpandRatio(center, 1);

    }

    @Override
    public void addComponent(Component c) {
        throw new IllegalArgumentException(
                "Component constraint have to be specified");
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        if (oldComponent == north) {
            mainLayout.replaceComponent(north, newComponent);
            north = newComponent;
        } else if (oldComponent == west) {
            centerLayout.replaceComponent(west, newComponent);
            west = newComponent;
        } else if (oldComponent == center) {
            centerLayout.replaceComponent(center, newComponent);
            centerLayout.setExpandRatio(newComponent, 1);
            center = newComponent;
        } else if (oldComponent == east) {
            centerLayout.replaceComponent(east, newComponent);
            east = newComponent;
        } else if (oldComponent == south) {
            mainLayout.replaceComponent(south, newComponent);
            south = newComponent;
        }
        centerLayout.setExpandRatio(center, 1);

    }

    /**
     * Set minimum height of the component in the BorderLayout.NORTH
     *
     * @param minimumNorthHeight
     */
    public void setMinimumNorthHeight(String minimumNorthHeight) {
        this.minimumNorthHeight = minimumNorthHeight;
    }

    /**
     * Get minimum height of the component in the BorderLayout.NORTH
     */
    public String getMinimumNorthHeight() {
        return minimumNorthHeight;
    }

    /**
     * Set minimum height of the component in the BorderLayout.SOUTH
     *
     * @param minimumSouthHeight
     */
    public void setMinimumSouthHeight(String minimumSouthHeight) {
        this.minimumSouthHeight = minimumSouthHeight;
    }

    /**
     * Get minimum height of the component in the BorderLayout.SOUTH
     */
    public String getMinimumSouthHeight() {
        return minimumSouthHeight;
    }

    /**
     * Set minimum height of the component in the BorderLayout.WEST
     *
     * @param minimumWestWidth
     */
    public void setMinimumWestWidth(String minimumWestWidth) {
        this.minimumWestWidth = minimumWestWidth;
    }

    /**
     * Get minimum height of the component in the BorderLayout.WEST
     */
    public String getMinimumWestWidth() {
        return minimumWestWidth;
    }

    /**
     * Set minimum height of the component in the BorderLayout.EAST
     *
     * @param minimumEastWidth
     */
    public void setMinimumEastWidth(String minimumEastWidth) {
        this.minimumEastWidth = minimumEastWidth;
    }

    /**
     * Get minimum height of the component in the BorderLayout.EAST
     */
    public String getMinimumEastWidth() {
        return minimumEastWidth;
    }

    /**
     * Return component from specific position
     *
     * @param position
     * @return
     */
    public Component getComponent(Constraint position) {
        if (position == Constraint.NORTH) {
            return north;
        } else if (position == Constraint.WEST) {
            return west;
        } else if (position == Constraint.CENTER) {
            return center;
        } else if (position == Constraint.EAST) {
            return east;
        } else if (position == Constraint.SOUTH) {
            return south;
        } else {
            throw new IllegalArgumentException(
                    "Invalid BorderLayout constraint.");
        }
    }

    public BorderLayoutIterator<Component> getBorderLayoutComponentIterator() {
        return new BorderLayoutIterator<Component>(
                mainLayout.iterator(),
                centerLayout.iterator());
    }

    /**
     * Iterate through the components of the borderlayout
     * <p/>
     * <p/>
     * N/S/E/W locations??
     *
     * @param <Component>
     */
    @SuppressWarnings("hiding")
    private class BorderLayoutIterator<Component> implements
            Iterator<Component> {

        Iterator<Component> mainLayoutIter;
        Iterator<Component> centerLayoutIter;

        BorderLayoutIterator(Iterator<Component> mainLayoutIter,
                             Iterator<Component> centerLayoutIter) {
            this.mainLayoutIter = mainLayoutIter;
            this.centerLayoutIter = centerLayoutIter;
        }

        public boolean hasNext() {
            return (mainLayoutIter.hasNext() || centerLayoutIter.hasNext());
        }

        public Component next() {
            if (mainLayoutIter.hasNext()) {
                return mainLayoutIter.next();
            } else {
                return centerLayoutIter.next();
            }
        }

        public void remove() {
            if (mainLayoutIter.hasNext()) {
                mainLayoutIter.remove();
            } else {
                centerLayoutIter.remove();
            }
        }

    }

    public void expand() {
        centerLayout.setExpandRatio(center, 1);
    }

    public Component getNorth() {
        return north;
    }

    public Component getWest() {
        return west;
    }

    public Component getCenter() {
        return center;
    }

    public Component getEast() {
        return east;
    }

    public Component getSouth() {
        return south;
    }

    public void setNSEWVisible(boolean b) {
        north.setVisible(b);
        south.setVisible(b);
        east.setVisible(b);
        west.setVisible(b);
    }
}
