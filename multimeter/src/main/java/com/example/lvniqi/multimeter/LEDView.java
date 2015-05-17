package com.example.lvniqi.multimeter;

/**
 * Created by lvniqi on 2015-05-17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LEDView extends LinearLayout {

    private static final String DATE_FORMAT = "%02d:%02d:%02d";
    private static final int REFRESH_DELAY = 500;
    private final Runnable mTimeRefresher = new Runnable() {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance(TimeZone
                    .getTimeZone("GMT+8"));
            final Date d = new Date();
            calendar.setTime(d);

            timeView.setText(String.format(DATE_FORMAT,
                    calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)));
            mHandler.postDelayed(this, REFRESH_DELAY);
        }
    };
    private final Handler mHandler = new Handler();
    private TextView timeView;
    private TextView bgView;

    @SuppressLint("NewApi")
    public LEDView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LEDView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LEDView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Load font from assets font folder.
     */
    public static Typeface createFont(Context context, String font, int style) {
        Typeface typeface;
        try {
            AssetManager assets = context.getAssets();
            typeface = Typeface.createFromAsset(assets, "fonts" + File.separator + font);
        } catch (RuntimeException e) {
            // createFromAsset() will throw a RuntimeException in case of error.
            Log.e("font", "Unable to create font: " + font, e);
            typeface = Typeface.defaultFromStyle(style);
        }
        return typeface;
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (this.isInEditMode()) return;
        View view = layoutInflater.inflate(R.layout.ledview, this);
        timeView = (TextView) view.findViewById(R.id.ledview_clock_time);
        bgView = (TextView) view.findViewById(R.id.ledview_clock_bg);
        AssetManager assets = context.getAssets();
        final String FONT_DIGITAL_7 = "fonts" + File.separator
                + "digital-7.ttf";
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        timeView.setTypeface(font);// 设置字体
        bgView.setTypeface(font);// 设置字体

    }

    public void start() {
        mHandler.post(mTimeRefresher);
    }

    public void stop() {
        mHandler.removeCallbacks(mTimeRefresher);
    }
}
