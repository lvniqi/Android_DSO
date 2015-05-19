package com.example.lvniqi.multimeter.Card;
import android.content.Context;
import com.dexafree.materialList.cards.SimpleCard;
import com.example.lvniqi.multimeter.R;

public class LedCard extends SimpleCard {
    public LedCard(final Context context) {
        super(context);
    }
    @Override
    public int getLayout() {
        return R.layout.ledcard;
    }
}


