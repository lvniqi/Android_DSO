package com.example.lvniqi.multimeter.Card;
import android.content.Context;
import com.dexafree.materialList.cards.SimpleCard;
import com.example.lvniqi.multimeter.DefinedMessages;
import com.example.lvniqi.multimeter.LEDView;
import com.example.lvniqi.multimeter.R;

public class LedCard extends SimpleCard {
    protected LEDView ledView;
    protected int LED_TYPE;
    protected float LED_VALUE;
    public LedCard(final Context context){
        super(context);
        this.LED_TYPE = DefinedMessages.UNKNOW;
        this.LED_VALUE = 0;
    }
    public LedCard(final Context context,float LED_Value,int LED_TYPE) {
        super(context);
        this.LED_TYPE = LED_TYPE;
        this.LED_VALUE = LED_Value;
    }
    @Override
    public int getLayout() {
        return R.layout.ledcard;
    }
    public void setLedView(LEDView ledView) {
        this.ledView = ledView;
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
}


