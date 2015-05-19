package com.example.lvniqi.multimeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

    private static final int FAILURE = 0; // 失败
    private static final int SUCCESS = 1; // 成功
    private static Context mContext;
    private TextView mVersionNameText;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.splash);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pinfo != null) {
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;

            mVersionNameText = (TextView) findViewById(R.id.version_name);
            mVersionNameText.setText(versionName);
        }
        new AsyncTask<Void, Void, Integer>() {
            private static final int SHOW_TIME_MIN = 1500;

            @Override
            protected Integer doInBackground(Void... params) {
                int result;
                long startTime = System.currentTimeMillis();
                result = loadingCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                // ... ...
                if (result == SUCCESS) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    //两个参数分别表示进入的动画,退出的动画
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }.execute(new Void[]{});
    }

    private int loadingCache() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
            return OFFLINE;
        }
        */
        return SUCCESS;
    }

    private class ToastMessageTask extends AsyncTask<String, String, String> {
        String toastMessage;

        @Override
        protected String doInBackground(String... params) {
            toastMessage = params[0];
            return toastMessage;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // 这是执行在GUI线程context
        protected void onPostExecute(String result) {
            Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
