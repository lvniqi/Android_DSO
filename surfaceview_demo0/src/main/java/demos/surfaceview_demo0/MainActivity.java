package demos.surfaceview_demo0;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


public class MainActivity extends Activity {
    public static GraphView graphView;
    static FloatingActionsMenu mainMenu;
    private static Context mContext;
    //测试用
    UdpService udpservice;
    private Thread tReceived;

    public static Context getmContext() {
        return mContext;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        setContentView(R.layout.activity_main);
        //udp 接收
        udpservice = new UdpService(4507);
        graphView = new GraphView(this);
        ((RelativeLayout) findViewById(R.id.SurfaceView_01)).addView(graphView);
        //((FloatingActionButton)findViewById(R.id.action_trigger1)).setVisibility(View.INVISIBLE);
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
        //主菜单
        mainMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        mainMenu.addButton(actionTriggerType);

        mainMenu.addButton(actionInSource);
        //actionTriggerType.setColorNormal(R.color.white_pressed);
        final int ResRed = this.getResources().getColor(R.color.holo_red_light);
        final int ResBlue = this.getResources().getColor(R.color.holo_blue_dark);
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
        mainMenu.setOnClickListenerFunc(new FloatingActionsMenu.setOnClickListenerFunc() {
            @Override
            public void fuc() {
                if (mainMenu.isExpanded()) {
                    graphView.getUpdate_thread().setShow(false);
                } else {
                    graphView.getUpdate_thread().setShow(true);
                }
            }
        });
        tReceived = new Thread(udpservice);
        //Display display = getWindowManager().getDefaultDisplay();
        //Log.i("view", "height:" + display.getHeight());
        //Log.i("view", "width:" + display.getWidth());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启udp接收
        udpservice.isRun = true;
        if (!tReceived.isAlive()) {
            tReceived.start();
        }
        //隐藏虚拟按键
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.i("currentapiVersion", currentapiVersion + "");
        if (19 <= currentapiVersion) {
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
        //udpservice.isRun = false;
        super.onDestroy();
        Log.i("Main", "Destroy");
    }
}