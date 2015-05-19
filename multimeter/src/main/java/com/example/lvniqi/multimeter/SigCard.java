package com.example.lvniqi.multimeter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.model.CardItemView;

/**
 * Created by lvniqi on 2015-05-19.
 */
public class SigCard extends SimpleCard {
    public SigCard(Context context) {
        super(context);
    }
    @Override
    public int getLayout(){
        return R.layout.list_sig;
    }
}
class SigCardItemView extends CardItemView<SigCard> {

    TextView mTitle;
    TextView mDescription;

    // Default constructors
    public SigCardItemView(Context context) {
        super(context);
    }

    public SigCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SigCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(SigCard card) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mLEDView = (LEDView)findViewById(R.id.ledview);
        setTitle(card.getTitle());
        this.getRootView();
        this.getRootView().setTag(viewHolder);
        viewHolder.mLEDView.start();
    }

    public void setTitle(String title){
        mTitle = (TextView)findViewById(R.id.titleTextView);
        mTitle.setText(title);
    }

    public void setDescription(String description){
        mDescription = (TextView)findViewById(R.id.descriptionTextView);
        mDescription.setText(description);
    }
    static class ViewHolder {
        LEDView mLEDView;
    }
}