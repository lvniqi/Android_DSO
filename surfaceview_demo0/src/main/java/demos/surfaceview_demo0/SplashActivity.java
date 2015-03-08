package demos.surfaceview_demo0;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static demos.surfaceview_demo0.DefinedMessages.TCP_PORT;
import static java.lang.System.exit;

public class SplashActivity extends Activity {

    private static final int FAILURE = 0; // 失败
    private static final int SUCCESS = 1; // 成功
    public static TcpService tcpService;
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
        tcpService = new TcpService(TCP_PORT);
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
                    //Bundle bundle = new Bundle();
                    //bundle.putSerializable("tcpChannel",tcpService);
                    //intent.putExtra("tcpChannel",bundle);
                    startActivity(intent);
                    finish();
                    //两个参数分别表示进入的动画,退出的动画
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Dialog dialog = new Dialog(SplashActivity.this,
                            getApplicationContext().getString(R.string.error),
                            getApplicationContext().getString(R.string.connect_timeout));
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exit(0);
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            exit(0);
                        }
                    });
                    dialog.show();
                    dialog.getButtonAccept().setText(getApplicationContext().getString(R.string.yes));
                }
            }
        }.execute(new Void[]{});
    }

    private int loadingCache() {
        //wifi扫描
        WifiOpenHelper wifiManager = new WifiOpenHelper(getApplicationContext());
        wifiManager.startScan();
        Log.i("WifiList", wifiManager.getWifiList().toString());
        tcpService.start();
        SendIp(wifiManager.getIPAddress());
        while (!tcpService.isCheck()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (tcpService.isOutTime()) {
                /*Toast.makeText(SplashActivity.getmContext(), "超时!",
                        Toast.LENGTH_SHORT).show();*/
            Log.i("Splash", "TIMEOUT FAILURE");
            return FAILURE;
        }
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
                    55554);
            socket.send(dataPacket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
