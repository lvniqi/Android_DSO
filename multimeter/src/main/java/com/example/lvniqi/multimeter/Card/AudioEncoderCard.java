package com.example.lvniqi.multimeter.Card;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.events.BusProvider;
import com.example.lvniqi.multimeter.DefinedMessages;
import com.example.lvniqi.multimeter.R;

/**
 * Created by lvniqi on 2015-05-21.
 */
public class AudioEncoderCard extends SimpleCard {
    protected EditText editText;
    protected String rightButtonText;
    protected int mRightButtonTextColor = -1;
    protected OnButtonPressListener onRightButtonPressedListener;
    protected boolean dividerVisible = false;
    protected boolean fullWidthDivider = false;
    public AudioEncoderCard(final Context context){
        super(context);
    }
    @Override
    public int getLayout() {
        return R.layout.audio_encoder_card;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setText(String value) {
        if(editText != null){
            editText.setText(value);
        }
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
}
