package com.example.lvniqi.multimeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by lvniqi on 2015-05-18.
 */
public class LedListAdapter extends ArrayAdapter<String> {
    private ViewHolder viewHolder;

    public LedListAdapter(final Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.led_card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mLEDView = (LEDView) convertView.findViewById(R.id.led_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //viewHolder.mLEDView.start();
        /*
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				// Do something awesome!
			}
		});
		*/

        return convertView;
    }


    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    static public class ViewHolder {
        LEDView mLEDView;
    }
}
