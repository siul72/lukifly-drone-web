package co.luism.iot.web.ui.vehicle.charts;

import co.luism.iot.web.interfaces.OnDiagCustomComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.vaadin.addon.JFreeChartWrapper;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Created by luis on 03.12.14.
 */
public class OnDiagChart extends CustomComponent implements OnDiagCustomComponent {

    private TimeSeriesCollection dataSet = new TimeSeriesCollection();
    private String title = "Environment Data";
    private String x_axis_label = "Time";
    private String y_axis_label = "Value";
    private JFreeChart myChart;


    public OnDiagChart(String title, Collection<TimeSeries> series) {

        this.title = title;
        for(TimeSeries t : series){
            dataSet.addSeries(t);
        }

        startUp();
    }

    @Override
    public void startUp(){
        myChart = createChart(dataSet);
        buildMainLayout();
    }

    @Override
    public void buildMainLayout() {

//        // Generate the graph
//        JFreeChart chart = ChartFactory.createXYLineChart("Burn Down Chart", // Title
//                "days", // x-axis Label
//                "Esimated Effort", // y-axis Label
//                dataset, // Dataset
//                PlotOrientation.VERTICAL, // Plot Orientation
//                true, // Show Legend
//                true, // Use tooltips
//                false // Configure chart to generate URLs?
//        );

        JFreeChartWrapper wrapper = new JFreeChartWrapper(myChart);

        wrapper.setHeight("600px");
        wrapper.setWidth("800px");

        VerticalLayout v = new VerticalLayout();
        v.addComponent(wrapper);
        setCompositionRoot(v);

    }

    @Override
    public void setCaptionNames(String currentLanguage) {

    }

    @Override
    public void closeDown() {

    }

    private JFreeChart createChart(XYDataset dataSet) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,  // title
            x_axis_label,             // x-axis label
           y_axis_label,   // y-axis label
            dataSet,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd.MM.yy HH:mm:ss"));

        return chart;

    }


    public String getTitle() {
        return title;
    }
}
