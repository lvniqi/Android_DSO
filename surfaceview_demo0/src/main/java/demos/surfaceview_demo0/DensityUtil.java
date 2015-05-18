package demos.surfaceview_demo0;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.File;

/**
 * Created by lvniqi on 2014-12-12.
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
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
}