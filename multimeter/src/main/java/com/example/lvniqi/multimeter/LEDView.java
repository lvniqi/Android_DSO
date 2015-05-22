package com.example.lvniqi.multimeter;

/**
 * Created by lvniqi on 2015-05-17.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class LEDView extends LinearLayout {

    private static final String DATE_FORMAT = "%02d:%02d:%02d";
    private static final int REFRESH_DELAY = 500;
    //服务程序
    final private Handler mHandler = new Handler() {
    };
    public Handler getmHandler() {
        return mHandler;
    }

    private TextView fgView;
    private TextView bgView;

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
    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (this.isInEditMode()) return;
        View view = layoutInflater.inflate(R.layout.led_view, this);
        fgView = (TextView) view.findViewById(R.id.ledview_fg);
        bgView = (TextView) view.findViewById(R.id.ledview_bg);
        AssetManager assets = context.getAssets();
        final String FONT_DIGITAL_7 = "fonts" + File.separator
                + "digital-7.ttf";
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        fgView.setTypeface(font);// 设置字体
        bgView.setTypeface(font);// 设置字体
    }
    public  void setText(float x,int type){
        String data = "";

        switch (type){
            case DefinedMessages.AC:
            case DefinedMessages.DC:
                if(x>=1000 || x<=-1000){
                    x /= 1000;
                    data = ""+x;
                    data += "kV";
                }
                else if(x<=1 && x>=-1){
                    x*=1000;
                    data = ""+(int)x;
                    data += "mV";
                }
                else{
                    data = ""+(int)x;
                    data += " V";
                }
                break;
            case DefinedMessages.FREQ:
                if(x>=1000 || x<=-1000){
                    x /= 1000;
                    data = ""+x;
                    data += "kHz";
                }
                else if(x<=1 && x>=-1){
                    x*=1000;
                    data = ""+(int)x;
                    data += "mHz";
                }
                else{
                    data = ""+(int)x;
                    data += " Hz";
                }
                break;
            case DefinedMessages.UNKNOW:
                data = "?????";
        }
        fgView.setText(data);
        String back = createBgString(data);
        bgView.setText(back);
    }
    private String createBgString(String data){
        byte [] array = data.getBytes();
        String back = "";
        for(int i=0;i<array.length;i++){
            if(array[i] <='9'&& array[i] >= '0') {
                back += "8";
            }
            else{
                back += " ";
            }
        }
        return back;
    }
}
