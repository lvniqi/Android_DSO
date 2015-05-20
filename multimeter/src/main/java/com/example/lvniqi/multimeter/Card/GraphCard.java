package com.example.lvniqi.multimeter.Card;

import android.content.Context;

import com.dexafree.materialList.cards.SimpleCard;
import com.example.lvniqi.multimeter.DefinedMessages;
import com.example.lvniqi.multimeter.R;
import com.jjoe64.graphview.GraphView;

public class GraphCard extends SimpleCard {
    private GraphView graphView;
    public GraphCard(final Context context){
        super(context);
    }
    @Override
    public int getLayout() {
        return R.layout.graphview;
    }

    public void setGraphView(GraphView graphView) {
        this.graphView = graphView;
    }

    public GraphView getGraphView() {
        return graphView;
    }

}


