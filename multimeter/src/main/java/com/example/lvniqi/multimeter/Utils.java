package com.example.lvniqi.multimeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.File;

/**
 * Created by lvniqi on 2015-05-18.
 */
public class Utils
{
    private static int sTheme;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_WARNNING = 1;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity
     * of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_WARNNING:
                activity.setTheme(R.style.nLiveoDrawer_warnning);
                break;
            case THEME_DEFAULT:
                activity.setTheme(R.style.nLiveoDrawer);
                break;
        }
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