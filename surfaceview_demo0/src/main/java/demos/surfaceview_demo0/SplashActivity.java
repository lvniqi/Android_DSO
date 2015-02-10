package demos.surfaceview_demo0;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SplashActivity extends Activity {

    private static final int FAILURE = 0; // 失败
    private static final int SUCCESS = 1; // 成功

    private TextView mVersionNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            private static final int SHOW_TIME_MIN = 800;

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
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //两个参数分别表示进入的动画,退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }.execute(new Void[]{});
    }

    private int loadingCache() {
        //wifi扫描
        WifiOpenHelper wifimanager = new WifiOpenHelper(getApplicationContext());
        wifimanager.startScan();
        Log.i("WifiList", wifimanager.getWifiList().toString());
        SendIp(wifimanager.getIPAddress());
        /*if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
            return OFFLINE;
        }
        */
        return SUCCESS;
    }

    public void SendIp(String ip_address) {
        byte[] data = ip_address.getBytes();
        MulticastSocket socket;
        try {
            socket = new MulticastSocket();
            socket.setReuseAddress(true);
            socket.setTimeToLive(1);
            InetAddress address = InetAddress.getByName(ip_address.substring(0, ip_address.lastIndexOf(".") + 1) + "255");
            DatagramPacket dataPacket = new DatagramPacket(data, data.length, address,
                    4507);
            socket.send(dataPacket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}