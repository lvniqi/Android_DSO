package demos.surfaceview_demo0;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvniqi on 2014/11/22.
 */

/**
 * a graphview series.
 * holds the data, description and styles
 */
public class GraphViewSeries {
    final String description;
    final GraphViewSeriesStyle style;
    private final List<GraphView> graphViews = new ArrayList<GraphView>();
    public float series_max;
    public float series_min;
    GraphViewDataInterface[] values;
    private boolean show_max = false;
    private boolean show_min = false;
    private boolean sign_curve = false;

    /**
     * create a series with predefined values
     *
     * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     */
    public GraphViewSeries(GraphViewDataInterface[] values) {
        description = null;
        style = new GraphViewSeriesStyle();
        this.values = values;
        InitMaxMin(values);
        checkValueOrder();
    }

    /**
     * create a series with predefined options
     *
     * @param description the name of the series
     * @param style       custom style. can be null for default styles
     * @param values      the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     */
    public GraphViewSeries(String description, GraphViewSeriesStyle style, GraphViewDataInterface[] values) {
        super();
        this.description = description;
        if (style == null) {
            style = new GraphViewSeriesStyle();
        }
        this.style = style;
        this.values = values;
        //init max and min
        InitMaxMin(values);
        checkValueOrder();
    }

    //show_max?
    public boolean GetShowMaxFlag() {
        return show_max;
    }

    //show_max!
    public void SetShowMaxFlag(boolean flag) {
        show_max = flag;
        for (GraphView g : graphViews) {
            //g.redrawAll();
        }
    }

    //show_min?
    public boolean GetShowMinFlag() {
        return show_min;
    }

    //show_min!
    public void SetShowMinFlag(boolean flag) {
        show_min = flag;
        for (GraphView g : graphViews) {
            //g.redrawAll();
        }
    }

    //sign curve?
    public boolean GetSignCurveFlag() {
        return sign_curve;
    }

    //sign curve!
    public void SetSignCurveFlag(boolean flag) {
        sign_curve = flag;
        for (GraphView g : graphViews) {
            //g.redrawAll();
        }
    }

    public float GetSeriesMax() {
        return series_max;
    }

    public float GetSeriesMin() {
        return series_min;
    }

    //get maxmum and minmum
    public void CmpMaxMin(GraphViewDataInterface value) {
        if (value.getY() > series_max) {
            series_max = (float) value.getY();
        }
        if (value.getY() < series_min) {
            series_min = (float) value.getY();
        }
    }

    // init maxmum and minmum
    public void InitMaxMin(GraphViewDataInterface[] values) {
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                series_max = (float) values[0].getY();
                series_min = (float) values[0].getY();
            } else {
                CmpMaxMin(values[i]);
            }
        }
    }

    /**
     * this graphview will be redrawn if data changes
     *
     * @param graphView
     */
    public void addGraphView(GraphView graphView) {
        this.graphViews.add(graphView);
    }

    /**
     * add one data to current data
     * the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     *
     * @param value       the new data to append
     * @param scrollToEnd true => graphview will scroll to the end (maxX)
     * @deprecated please use {@link #appendData(GraphViewDataInterface, boolean, int)} to avoid memory overflow
     */
    @Deprecated
    public void appendData(GraphViewDataInterface value, boolean scrollToEnd) {
        if (values.length > 0 && value.getX() < values[values.length - 1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
        GraphViewDataInterface[] newValues = new GraphViewDataInterface[values.length + 1];
        int offset = values.length;
        System.arraycopy(values, 0, newValues, 0, offset);

        newValues[values.length] = value;
        values = newValues;
        //update max min
        CmpMaxMin(value);
        for (GraphView g : graphViews) {
            if (scrollToEnd) {
                //g.scrollToEnd();
            }
        }
    }

    /**
     * add one data to current data
     * the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     *
     * @param value        the new data to append. Important: the new value must be higher then the last value (x).
     * @param scrollToEnd  true => graphview will scroll to the end (maxX)
     * @param maxDataCount if max data count is reached, the oldest data value will be lost
     */
    public void appendData(GraphViewDataInterface value, boolean scrollToEnd, int maxDataCount) {
        if (values.length > 0 && value.getX() < values[values.length - 1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
        synchronized (values) {
            int curDataCount = values.length;
            GraphViewDataInterface[] newValues;
            if (curDataCount < maxDataCount) {
                // enough space
                newValues = new GraphViewDataInterface[curDataCount + 1];
                System.arraycopy(values, 0, newValues, 0, curDataCount);
                // append new data
                newValues[curDataCount] = value;
            } else {
                // we have to trim one data
                newValues = new GraphViewDataInterface[maxDataCount];
                System.arraycopy(values, 1, newValues, 0, curDataCount - 1);
                // append new data
                newValues[maxDataCount - 1] = value;
            }
            values = newValues;
        }
        //update max min
        CmpMaxMin(value);
        // update linked graph views
        for (GraphView g : graphViews) {
            if (scrollToEnd) {
                //g.scrollToEnd();
            }
        }
    }

    /**
     * @return series styles. never null
     */
    public GraphViewSeriesStyle getStyle() {
        return style;
    }

    /**
     * you should use {@link GraphView#removeSeries(GraphViewSeries)}
     *
     * @param graphView
     */
    public void removeGraphView(GraphView graphView) {
        graphViews.remove(graphView);
    }

    /**
     * clears the current data and set the new.
     * <p/>
     * redraws the graphview(s)
     *
     * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     */
    public void resetData(GraphViewDataInterface[] values) {
        this.values = values;
        //update max min
        InitMaxMin(values);
        checkValueOrder();
        for (GraphView g : graphViews) {
            //g.redrawAll();
        }
    }

    private void checkValueOrder() {
        if (values.length > 0) {
            double lx = values[0].getX();
            for (int i = 1; i < values.length; i++) {
                if (lx > values[i].getX()) {
                    throw new IllegalArgumentException("The order of the values is not correct. X-Values have to be ordered ASC. First the lowest x value and at least the highest x value.");
                }
                lx = values[i].getX();
            }
        }
    }

    /**
     * graph series style: color and line_width
     */
    static public class GraphViewSeriesStyle {
        public int color = 0xff0077cc;
        public int line_width = 3;
        private ValueDependentColor valueDependentColor;

        public GraphViewSeriesStyle() {
            super();
        }

        public GraphViewSeriesStyle(int color, int line_width) {
            super();
            this.color = color;
            this.line_width = line_width;
        }

        public ValueDependentColor getValueDependentColor() {
            return valueDependentColor;
        }

        /**
         * the color depends on the value of the data.
         * only possible in BarGraphView
         *
         * @param valueDependentColor
         */
        public void setValueDependentColor(ValueDependentColor valueDependentColor) {
            this.valueDependentColor = valueDependentColor;
        }
    }
}
