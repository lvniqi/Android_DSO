package demos.surfaceview_demo0;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import static demos.surfaceview_demo0.DefinedMessages.UDP_PORT;


public class MainActivity extends Activity {
    public static GraphView graphView;
    public static RelativeLayout status;
    static FloatingActionsMenu mainMenu;
    private static Context mContext;
    private static int currentApiVersion;
    private static TcpService tcpReceived;
    //信息字符
    private static TextView messageCH1;
    //测试用
    private UdpService udpservice;
    private Thread udpReceived;

    public static Context getmContext() {
        return mContext;
    }

    public static int getCurrentApiVersion() {
        return currentApiVersion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        Log.i("currentApiVersion", currentApiVersion + "");
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        setContentView(R.layout.activity_main);
        //udp 接收
        udpservice = new UdpService(UDP_PORT);
        graphView = new GraphView(this);
        ((RelativeLayout) findViewById(R.id.SurfaceView_01)).addView(graphView);
        //((FloatingActionButton)findViewById(R.id.action_trigger1)).setVisibility(View.INVISIBLE);
        //信息
        final FloatingActionButton actionMessage = new FloatingActionButton(getBaseContext());
        actionMessage.setTitle(this.getString(R.string.message));
        //测试使用数字字体
        final Typeface font = DensityUtil.createFont(mContext, "digital-7.ttf", Typeface.NORMAL);
        //消息text
        messageCH1 = (TextView) findViewById(R.id.textView1);
        messageCH1.setTypeface(font);
        ((TextView) findViewById(R.id.textView2)).setTypeface(font);
        status = (RelativeLayout) findViewById(R.id.Status);
        //status.setVisibility(View.INVISIBLE);
        //触发方式
        final FloatingActionButton actionTriggerType = new FloatingActionButton(getBaseContext());
        final FloatingActionButton actionTrigger1 = (FloatingActionButton) findViewById(R.id.action_trigger1);
        final FloatingActionButton actionTrigger2 = (FloatingActionButton) findViewById(R.id.action_trigger2);
        final TextView textTrigger1 = (TextView) findViewById(R.id.textView_trigger1);
        final TextView textTrigger2 = (TextView) findViewById(R.id.textView_trigger2);
        actionTriggerType.addSubButton(actionTrigger1);
        actionTriggerType.addSubButton(actionTrigger2);
        actionTriggerType.addSubLabel(textTrigger1);
        actionTriggerType.addSubLabel(textTrigger2);
        actionTriggerType.setTitle(this.getString(R.string.trigger_type));
        actionTriggerType.showSub(false);

        //信号来源
        final FloatingActionButton actionInSource = new FloatingActionButton(getBaseContext());
        final FloatingActionButton actionInsource1 = (FloatingActionButton) findViewById(R.id.action_insource1);
        final FloatingActionButton actionInsource2 = (FloatingActionButton) findViewById(R.id.action_insource2);
        final TextView textInsource1 = (TextView) findViewById(R.id.textView_insource1);
        final TextView textInsource2 = (TextView) findViewById(R.id.textView_insource2);
        actionInSource.addSubButton(actionInsource1);
        actionInSource.addSubButton(actionInsource2);
        actionInSource.addSubLabel(textInsource1);
        actionInSource.addSubLabel(textInsource2);
        actionInSource.setTitle(this.getString(R.string.in_source));
        actionInSource.showSub(false);
        //检测是否有虚拟按键
        if (ViewConfiguration.get(mContext).hasPermanentMenuKey()) {
            ;
        }
        //退出
        final FloatingActionButton actionExit = (FloatingActionButton) findViewById(R.id.action_exit);
        final TextView textExit = (TextView) findViewById(R.id.textView_exit);
        actionExit.setVisibility(View.INVISIBLE);
        textExit.setVisibility(View.INVISIBLE);
        //主菜单
        mainMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        mainMenu.addButton(actionMessage);
        mainMenu.addButton(actionTriggerType);
        mainMenu.addButton(actionInSource);
        //actionTriggerType.setColorNormal(R.color.white_pressed);
        final int ResRed = this.getResources().getColor(R.color.holo_red_light);
        final int ResBlue = this.getResources().getColor(R.color.holo_blue_dark);
        final int ResOrange = this.getResources().getColor(R.color.holo_orange_dark);
        actionTriggerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionTriggerType.getColorNormal() == ResBlue) {
                    actionTriggerType.setColorNormal(ResRed);
                    actionTriggerType.showSub(true);
                    mainMenu.hideLabels(actionTriggerType);
                } else {
                    actionTriggerType.setColorNormal(ResBlue);
                    actionTriggerType.showSub(false);
                    mainMenu.showLabels(actionTriggerType);
                }
            }
        });
        actionInSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionInSource.getColorNormal() == ResBlue) {
                    actionInSource.setColorNormal(ResRed);
                    actionInSource.showSub(true);
                    mainMenu.hideLabels(actionInSource);
                } else {
                    actionInSource.setColorNormal(ResBlue);
                    actionInSource.showSub(false);
                    mainMenu.showLabels(actionInSource);
                }
            }
        });
        actionInsource1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionInsource1.getColorNormal() == ResBlue) {
                    actionInsource1.setColorNormal(ResRed);
                    graphView.getUpdate_thread().getChannelList().get(0).setShow(true);
                } else {
                    actionInsource1.setColorNormal(ResBlue);
                    graphView.getUpdate_thread().getChannelList().get(0).setShow(false);
                }
            }
        });
        actionInsource2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionInsource2.getColorNormal() == ResBlue) {
                    actionInsource2.setColorNormal(ResRed);
                    graphView.getUpdate_thread().getChannelList().get(1).setShow(true);
                } else {
                    actionInsource2.setColorNormal(ResBlue);
                    graphView.getUpdate_thread().getChannelList().get(1).setShow(false);
                }
            }
        });
        actionExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionExit.getColorNormal() == ResOrange) {
                    actionExit.setColorNormal(ResRed);
                    textExit.setText(getmContext().getString(R.string.exit_chick));

                } else if (actionExit.getColorNormal() == ResRed) {
                    System.exit(0);
                }
            }
        });
        mainMenu.setOnClickListenerFunc(new FloatingActionsMenu.setOnClickListenerFunc() {
            @Override
            public void fuc() {
                if (mainMenu.isExpanded()) {
                    graphView.getUpdate_thread().setShow(false);
                    actionExit.setVisibility(View.VISIBLE);
                    textExit.setVisibility(View.VISIBLE);
                } else {
                    actionExit.setVisibility(View.INVISIBLE);
                    textExit.setVisibility(View.INVISIBLE);
                    if (actionExit.getColorNormal() == ResRed) {
                        actionExit.setColorNormal(ResOrange);
                        textExit.setText(getmContext().getString(R.string.exit));
                    }
                    graphView.getUpdate_thread().setShow(true);
                }
            }
        });
        //开启udp接收
        udpReceived = new Thread(udpservice);
        udpReceived.start();
        SnackBar snackbar = new SnackBar(MainActivity.this,
                mContext.getString(R.string.connected),
                mContext.getString(R.string.yes),
                null);
        if (19 <= currentApiVersion) {
            snackbar.setFullScreen(true);
        }
        snackbar.setBackgroundSnackBar(Color.parseColor("#e0fafafa"));
        snackbar.setDismissTimer(3000);
        snackbar.setMessageTextSize(17);
        snackbar.show();
        //接收tcp线程
        //Intent intent=getIntent();
        //Bundle bundle=intent.getBundleExtra("tcpChannel");
        tcpReceived = SplashActivity.tcpService;
        //Display display = getWindowManager().getDefaultDisplay();
        //Log.i("view", "height:" + display.getHeight());
        //Log.i("view", "width:" + display.getWidth());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //隐藏虚拟按键
        hideKey();
    }

    public void hideKey() {
        if (19 <= currentApiVersion) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    @Override
    protected void onDestroy() {
        udpReceived.interrupt();
        udpservice.close();
        //tcpService.close();
        super.onDestroy();
        Log.i("Main", "Destroy");
        tcpReceived.close();
    }

    public static class textMessageTask extends AsyncTask<String, String, String> {
        String message;

        @Override
        protected String doInBackground(String... params) {
            message = params[0];
            return message;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // 这是执行在GUI线程context
        protected void onPostExecute(String result) {
            messageCH1.setText(result);
        }
    }
}