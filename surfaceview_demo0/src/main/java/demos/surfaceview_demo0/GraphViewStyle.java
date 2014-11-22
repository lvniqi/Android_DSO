package demos.surfaceview_demo0;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ContextThemeWrapper;

/**
 * Created by lvniqi on 2014/11/23.
 */

public class GraphViewStyle {
    private int verticalLabelsColor;
    private int horizontalLabelsColor;
    private int gridColor;
    private GridStyle gridStyle = GridStyle.BOTH;
    private float textSize;
    private int verticalLabelsWidth;
    private int numVerticalLabels;
    private int numHorizontalLabels;
    private int legendWidth;
    private int legendBorder;
    private int legendSpacing;
    private int legendMarginBottom;
    private Paint.Align verticalLabelsAlign;

    public GraphViewStyle() {
        setDefaults();
    }

    public GraphViewStyle(int vLabelsColor, int hLabelsColor, int gridColor) {
        setDefaults();
        this.verticalLabelsColor = vLabelsColor;
        this.horizontalLabelsColor = hLabelsColor;
        this.gridColor = gridColor;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int c) {
        gridColor = c;
    }

    public GridStyle getGridStyle() {
        return gridStyle;
    }

    public void setGridStyle(GridStyle style) {
        gridStyle = style;
    }

    public int getHorizontalLabelsColor() {
        return horizontalLabelsColor;
    }

    public void setHorizontalLabelsColor(int c) {
        horizontalLabelsColor = c;
    }

    public int getLegendBorder() {
        return legendBorder;
    }

    public void setLegendBorder(int legendBorder) {
        this.legendBorder = legendBorder;
    }

    public int getLegendSpacing() {
        return legendSpacing;
    }

    public void setLegendSpacing(int legendSpacing) {
        this.legendSpacing = legendSpacing;
    }

    public int getLegendWidth() {
        return legendWidth;
    }

    public void setLegendWidth(int legendWidth) {
        this.legendWidth = legendWidth;
    }

    public int getLegendMarginBottom() {
        return legendMarginBottom;
    }

    public void setLegendMarginBottom(int legendMarginBottom) {
        this.legendMarginBottom = legendMarginBottom;
    }

    public int getNumHorizontalLabels() {
        return numHorizontalLabels;
    }

    /**
     * @param numHorizontalLabels 0 = auto
     */
    public void setNumHorizontalLabels(int numHorizontalLabels) {
        this.numHorizontalLabels = numHorizontalLabels;
    }

    public int getNumVerticalLabels() {
        return numVerticalLabels;
    }

    /**
     * @param numVerticalLabels 0 = auto
     */
    public void setNumVerticalLabels(int numVerticalLabels) {
        this.numVerticalLabels = numVerticalLabels;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public Paint.Align getVerticalLabelsAlign() {
        return verticalLabelsAlign;
    }

    public void setVerticalLabelsAlign(Paint.Align verticalLabelsAlign) {
        this.verticalLabelsAlign = verticalLabelsAlign;
    }

    public int getVerticalLabelsColor() {
        return verticalLabelsColor;
    }

    public void setVerticalLabelsColor(int c) {
        verticalLabelsColor = c;
    }

    public int getVerticalLabelsWidth() {
        return verticalLabelsWidth;
    }

    /**
     * @param verticalLabelsWidth 0 = auto
     */
    public void setVerticalLabelsWidth(int verticalLabelsWidth) {
        this.verticalLabelsWidth = verticalLabelsWidth;
    }

    private void setDefaults() {
        verticalLabelsColor = Color.WHITE;
        horizontalLabelsColor = Color.WHITE;
        gridColor = Color.DKGRAY;
        textSize = 30f;
        legendWidth = 120;
        legendBorder = 10;
        legendSpacing = 10;
        legendMarginBottom = 0;
        verticalLabelsAlign = Paint.Align.LEFT;
    }

    /**
     * tries to get the theme's font color and use it for labels
     *
     * @param context must be instance of ContextThemeWrapper
     */
    public void useTextColorFromTheme(Context context) {
        if (context instanceof ContextThemeWrapper) {
            TypedArray array = ((ContextThemeWrapper) context).getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
            int color = array.getColor(0, getVerticalLabelsColor());
            array.recycle();

            setVerticalLabelsColor(color);
            setHorizontalLabelsColor(color);
        }
    }

    /**
     * Definition which lines will be drawn in the background
     */
    public enum GridStyle {
        BOTH, VERTICAL, HORIZONTAL, NONE;

        public boolean drawVertical() {
            return this == BOTH || this == VERTICAL && this != NONE;
        }

        public boolean drawHorizontal() {
            return this == BOTH || this == HORIZONTAL && this != NONE;
        }
    }
}
