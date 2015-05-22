package com.example.lvniqi.multimeter.Card;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dexafree.materialList.cards.internal.BaseCardItemView;
import com.example.lvniqi.multimeter.LEDView;
import com.example.lvniqi.multimeter.R;

public class LedCardItemView extends BaseCardItemView<LedCard> {
    public LedCardItemView(Context context) {
        super(context);
    }

    public LedCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LedCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(final LedCard card) {
        super.build(card);
        // Title
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(card.getTitle());
        if (card.getTitleColor() != -1) {
            title.setTextColor(card.getTitleColor());
        }
        //LEDview
        LEDView ledview = (LEDView)findViewById(R.id.led_view);
        if(ledview != null) {
            ledview.setText(card.LED_VALUE, card.LED_TYPE);
            card.setLedView(ledview);
        }
    }
}
