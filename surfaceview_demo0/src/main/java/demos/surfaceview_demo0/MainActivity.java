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
    static GraphView graphView;
    static FloatingActionsMenu mainMenu;
    static FloatingActionsMenu mainMenu2;
    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mainMenu2 = new FloatingActionsMenu(mContext);
        setContentView(R.layout.activity_main);
        graphView = new GraphView(this);
        ((RelativeLayout) findViewById(R.id.SurfaceView_01)).addView(graphView);
        //((FloatingActionButton)findViewById(R.id.action_trigger1)).setVisibility(View.INVISIBLE);
        //触发方式
        final FloatingActionButton actionTriggerType = new FloatingActionButton(getBaseContext());
        final FloatingActionButton actionTrigger1 = (FloatingActionButton) findViewById(R.id.action_trigger1);
        final FloatingActionButton actionTrigger2 = (FloatingActionButton) findViewById(R.id.action_trigger2);
        final TextView textTrigger1 = (TextView) findViewById(R.id.textView_trigger1);
        final TextView textTrigger2 = (TextView) findViewById(R.id.textView_trigger2);
        actionTrigger1.setVisibility(View.GONE);
        actionTrigger2.setVisibility(View.GONE);
        textTrigger1.setVisibility(View.GONE);
        textTrigger2.setVisibility(View.GONE);
        actionTriggerType.addSubButtons(actionTrigger1);
        actionTriggerType.addSubButtons(actionTrigger2);
        actionTriggerType.addSubButtons(textTrigger1);
        actionTriggerType.addSubButtons(textTrigger2);
        actionTriggerType.setTitle(this.getString(R.string.trigger_type));
        //信号来源
        FloatingActionButton actionInSource = new FloatingActionButton(getBaseContext());
        actionInSource.setTitle(this.getString(R.string.in_source));
        mainMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        mainMenu.addButton(actionTriggerType);

        mainMenu.addButton(actionInSource);
        //actionTriggerType.setColorNormal(R.color.white_pressed);
        actionTriggerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionTriggerType.getColorNormal() == Color.rgb(0x00, 0x99, 0xcc)) {
                    actionTriggerType.setColorNormal(Color.rgb(0xff, 0x44, 0x44));
                    for (View x : actionTriggerType.getSubButtons()) {
                        x.setVisibility(View.VISIBLE);
                    }
                    mainMenu.hideLabels(actionTriggerType);
                } else {
                    actionTriggerType.setColorNormal(Color.rgb(0x00, 0x99, 0xcc));
                    for (View x : actionTriggerType.getSubButtons()) {
                        x.setVisibility(View.GONE);
                    }
                    mainMenu.showLabels(actionTriggerType);
                }
            }
        });

        //Display display = getWindowManager().getDefaultDisplay();
        //Log.i("view", "height:" + display.getHeight());
        //Log.i("view", "width:" + display.getWidth());
    }

}