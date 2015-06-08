package com.example.lvniqi.multimeter.Card;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.internal.BaseCardItemView;
import com.example.lvniqi.multimeter.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphCardItemView extends BaseCardItemView<GraphCard> {
    private final static int DIVIDER_MARGIN_DP = 16;

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

        // Right Button - Text
        final TextView rightText = (TextView) findViewById(com.dexafree.materialList.R.id.right_text_button);

        int rightColor = card.getRightButtonTextColor();

        if (rightColor != -1) {
            rightText.setTextColor(rightColor);
        }

        rightText.setText(card.getRightButtonText().toUpperCase());
        if (card.getRightButtonTextColor() > -1) {
            rightText.setTextColor(card.getRightButtonTextColor());
        }
        rightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OnButtonPressListener listener = card.getOnRightButtonPressedListener();
                if (listener != null) {
                    listener.onButtonPressedListener(rightText, card);
                }
            }
        });
        // Divider
        int visibility = card.isDividerVisible() ? VISIBLE : INVISIBLE;

        View divider = findViewById(R.id.cardDivider);

        divider.setVisibility(visibility);

        // After setting the visibility, we prepare the divider params according to the preferences
        if (card.isDividerVisible()) {

            // If the divider has to be from side to side, the margin will be 0
            if (card.isFullWidthDivider()) {
                ((ViewGroup.MarginLayoutParams) divider.getLayoutParams()).setMargins(0, 0, 0, 0);
            } else {
                int dividerMarginPx = (int) dpToPx(DIVIDER_MARGIN_DP);
                // Set the margin
                ((ViewGroup.MarginLayoutParams) divider.getLayoutParams()).setMargins(
                        dividerMarginPx,
                        0,
                        dividerMarginPx,
                        0
                );
            }
        }
        //GraphView
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        if (graphView != null) {
            DataPoint[] temp = new DataPoint[0];
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(temp);
            series.setColor(Color.RED);
            if (graphView.getSeries().size() == 0) {
                graphView.addSeries(series);

                graphView.getGridLabelRenderer().setGridColor(Color.WHITE);
                graphView.getGridLabelRenderer().setHighlightZeroLines(false);
                graphView.getGridLabelRenderer()
                        .setHorizontalLabelsColor(getResources().
                                getColor(R.color.white));
                graphView.getGridLabelRenderer()
                        .setVerticalLabelsColor(getResources().
                                getColor(R.color.white));
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setYAxisBoundsManual(true);
                graphView.getViewport().setMinY(-32767);
                graphView.getViewport().setMaxY(32767);
                graphView.getViewport().setMinX(0);
                graphView.getViewport().setMaxX(150);
                graphView.getViewport().setScalable(false);
                graphView.getViewport().setScrollable(false);
                graphView.getLegendRenderer().setVisible(false);
                card.setGraphView(graphView);
                //graphView.removeAllSeries();
            }

        }
    }
}
