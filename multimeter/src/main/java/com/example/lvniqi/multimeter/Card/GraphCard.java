package com.example.lvniqi.multimeter.Card;

import android.content.Context;

import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.events.BusProvider;
import com.example.lvniqi.multimeter.DefinedMessages;
import com.example.lvniqi.multimeter.R;
import com.jjoe64.graphview.GraphView;

public class GraphCard extends SimpleCard {
    private GraphView graphView;
    private boolean isShowGraphView=false;
    protected String rightButtonText;
    protected int mRightButtonTextColor = -1;
    protected OnButtonPressListener onRightButtonPressedListener;
    protected boolean dividerVisible = false;
    protected boolean fullWidthDivider = false;
    public GraphCard(final Context context){
        super(context);
    }
    @Override
    public int getLayout() {
        return R.layout.graphview;
    }

    public boolean isShowGraphView() {
        return isShowGraphView;
    }

    public void setShowGraphView(boolean isShowGraphView) {
        this.isShowGraphView = isShowGraphView;
    }

    public void setGraphView(GraphView graphView) {
        this.graphView = graphView;
    }

    public GraphView getGraphView() {
        if(isShowGraphView){
            return graphView;
        }
        else{
            return null;
        }
    }
    public String getRightButtonText() {
        return rightButtonText;
    }
    public void setRightButtonText(String rightButtonText) {
        this.rightButtonText = rightButtonText;
        BusProvider.dataSetChanged();
    }

    public int getRightButtonTextColor() {
        return mRightButtonTextColor;
    }

    public void setRightButtonTextColor(int color) {
        this.mRightButtonTextColor = color;
        BusProvider.dataSetChanged();
    }

    public void setRightButtonTextColorRes(int colorId) {
        setRightButtonTextColor(getResources().getColor(colorId));
    }


    public boolean isDividerVisible() {
        return dividerVisible;
    }

    public boolean isFullWidthDivider() {
        return fullWidthDivider;
    }

    public void setFullWidthDivider(boolean fullWidthDivider) {
        this.fullWidthDivider = fullWidthDivider;
        BusProvider.dataSetChanged();
    }

    public void setDividerVisible(boolean visible) {
        this.dividerVisible = visible;
        BusProvider.dataSetChanged();
    }


    public OnButtonPressListener getOnRightButtonPressedListener() {
        return onRightButtonPressedListener;
    }

    public void setOnRightButtonPressedListener(OnButtonPressListener onRightButtonPressedListener) {
        this.onRightButtonPressedListener = onRightButtonPressedListener;

    }
}


