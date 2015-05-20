package com.example.lvniqi.multimeter.Card;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dexafree.materialList.cards.internal.BaseCardItemView;
import com.example.lvniqi.multimeter.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphCardItemView extends BaseCardItemView<GraphCard> {
    public GraphCardItemView(Context context) {
        super(context);
    }

    public GraphCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(final GraphCard card) {
        super.build(card);
        // Title
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(card.getTitle());
        if (card.getTitleColor() != -1) {
            title.setTextColor(card.getTitleColor());
        }
        //GraphView
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        if(graphView != null) {
            DataPoint[] temp = new DataPoint[128];
            for(int i=0;i<128;i++){
                temp[i] = new DataPoint(i,Math.sin(2.0 * Math.PI / 128.0 * i) * 128);
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(temp);
            if(graphView.getSeries().size() == 0){
                graphView.addSeries(series);
                graphView.getViewport().setMinY(-128);
                graphView.getViewport().setMaxY(128);
                graphView.getViewport().setMinX(0);
                graphView.getViewport().setMaxX(100);
                graphView.getViewport().setScalable(false);
                graphView.getViewport().setScrollable(false);
                graphView.getViewport().setYAxisBoundsStatus(Viewport.AxisBoundsStatus.FIX);
                graphView.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.FIX);
                graphView.getLegendRenderer().setVisible(false);
                card.setGraphView(graphView);
                //graphView.removeAllSeries();
            }

        }
    }
}
