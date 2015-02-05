package demos.surfaceview_demo0;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

    public static Context getmContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
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
        actionTriggerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionTriggerType.getColorNormal() == Color.rgb(0x00, 0x99, 0xcc)) {
                    actionTriggerType.setColorNormal(Color.rgb(0xff, 0x44, 0x44));
                    actionTriggerType.showSub(true);
                    mainMenu.hideLabels(actionTriggerType);
                } else {
                    actionTriggerType.setColorNormal(Color.rgb(0x00, 0x99, 0xcc));
                    actionTriggerType.showSub(false);
                    mainMenu.showLabels(actionTriggerType);
                }
            }
        });
        actionInSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionInSource.getColorNormal() == Color.rgb(0x00, 0x99, 0xcc)) {
                    actionInSource.setColorNormal(Color.rgb(0xff, 0x44, 0x44));
                    actionInSource.showSub(true);
                    mainMenu.hideLabels(actionInSource);
                } else {
                    actionInSource.setColorNormal(Color.rgb(0x00, 0x99, 0xcc));
                    actionInSource.showSub(false);
                    mainMenu.showLabels(actionInSource);
                }
            }
        });
        actionInsource1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionInsource1.getColorNormal() == Color.rgb(0x00, 0x99, 0xcc)) {
                    actionInsource1.setColorNormal(Color.rgb(0xff, 0x44, 0x44));
                    graphView.getUpdate_thread().setCh1Flag(true);
                } else {
                    actionInsource1.setColorNormal(Color.rgb(0x00, 0x99, 0xcc));
                    graphView.getUpdate_thread().setCh1Flag(false);
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
        Thread tReceived = new Thread(udpservice);
        tReceived.start();
        //Display display = getWindowManager().getDefaultDisplay();
        //Log.i("view", "height:" + display.getHeight());
        //Log.i("view", "width:" + display.getWidth());
    }

}