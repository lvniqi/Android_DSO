package com.example.lvniqi.multimeter.Card;

import android.content.Context;
import android.widget.SeekBar;

import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.events.BusProvider;
import com.example.lvniqi.multimeter.LEDView;
import com.example.lvniqi.multimeter.R;

/**
 * Created by lvniqi on 2015-05-19.
 */
public class SigCard extends SimpleCard {
    protected String rightButtonText;
    protected int mRightButtonTextColor = -1;
    protected OnButtonPressListener onRightButtonPressedListener;
    protected boolean dividerVisible = false;
    protected boolean fullWidthDivider = false;
    protected SeekBar seekBar;
    protected LEDView ledView;
    protected int LED_TYPE;
    protected float LED_VALUE;
    public SigCard(Context context) {
        super(context);
    }
    @Override
    public int getLayout(){
        return R.layout.sig_card;
    }

    public String getRightButtonText() {
        return rightButtonText;
    }

    public void setRightButtonText(int rightButtonTextId) {
        setRightButtonText(getString(rightButtonTextId));
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
    public void setLedValue(float LED_VALUE) {
        this.LED_VALUE = LED_VALUE;
        if(ledView != null){
            ledView.setText(LED_VALUE,LED_TYPE);
        }
    }
    public void setLedAll(float value,int type) {
        this.LED_TYPE = type;
        this.LED_VALUE = value;
        if(ledView != null){
            ledView.setText(LED_VALUE,LED_TYPE);
        }
    }
    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
    }
    public SeekBar getSeekBar() {
        return seekBar;
    }
    public void setLedView(LEDView ledView) {
        this.ledView = ledView;
    }
}
