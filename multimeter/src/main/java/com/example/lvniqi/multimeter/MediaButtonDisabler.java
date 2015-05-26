package com.example.lvniqi.multimeter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by lvniqi on 2015-05-25.
 */ //屏蔽线控
class MediaButtonDisabler extends BroadcastReceiver {

    private static final String TAG = "MediaButtonDisabler";

    private static final BroadcastReceiver INSTANCE = new MediaButtonDisabler();

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(1000);
        context.registerReceiver(INSTANCE, filter);
    }

    public static void unregister(Context context) {
        context.unregisterReceiver(INSTANCE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intercepted media button.");
        abortBroadcast();

        Log.v(TAG, " call MediaButtonBroadcastReceiver");
    }
}
