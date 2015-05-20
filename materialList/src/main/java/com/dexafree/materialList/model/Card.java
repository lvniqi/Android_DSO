package com.dexafree.materialList.model;

import android.support.v7.widget.RecyclerView;

import com.dexafree.materialList.controller.IMaterialListAdapter;

import static android.support.v7.widget.RecyclerView.*;

/**
 * The Card is the base class of all Card Models.
 */
public abstract class Card {

    private Object tag;

    private Adapter adapter;
    private boolean mDismissible;

    public boolean isDismissible() {
        return mDismissible;
    }

    public Adapter getcAdapter() {
        return adapter;
    }

    public void setcAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public void setDismissible(boolean canDismiss) {
        this.mDismissible = canDismiss;
    }

    public abstract int getLayout();

    public Object getTag(){
        return tag;
    }

    public void setTag(Object tag){
        this.tag = tag;
    }

}
