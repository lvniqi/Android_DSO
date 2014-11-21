package demos.dso_demo_graphview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.net.MulticastSocket;


public class MainActivity extends Activity {

    private TextView text_x1;
    public GraphView lineview;
    static  boolean temp_flag = true;
    Handler handler = new Handler();
    static double j = 0;
    //测试用
    UdpService udpservice;
    public static Handler mHandler;
    public static final int MSG_SUCCESS = 0;//获取图片成功的标识
    //WIFI控制器
    private WifiManager wifiManager;
    public static WifiManager.MulticastLock mlock;

    /*private Runnable myRunnable = new Runnable() {
        public void run() {

            handler.postDelayed(this, 10);
            double x1 = lineview.getViewportSize();
            text_x1.setText("" + x1);
            double v = 0;
            j+= 0.01;
            for (int i = 0; i < num; i++) {
                v += 0.2;

                data2[i] = new GraphView.GraphViewData(i, Math.sin(v/2+j + 2)-0.2);
                if (i == 100) {
                    data2[i] = new GraphView.GraphViewData(i, -1);
                }

            }
            series.resetData(data2);

        }
    };*/
    static final int num = 512;
    GraphViewSeries series;
    GraphView.GraphViewData[] data2 = new GraphView.GraphViewData[num];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //udp 接收
        udpservice = new UdpService(4507);
        //WIFI控制器 获取服务
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mlock = wifiManager.createMulticastLock("test");
        mlock.acquire();
        //服务程序
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MainActivity.MSG_SUCCESS:
                        Bundle bundle = new Bundle();
                        bundle = msg.getData();
                        byte [] temp_byte = bundle.getByteArray("str");
                        float[] temp_float = ByteArrayFunction.BytesToFloat(temp_byte);
                        GraphView.GraphViewData[] data = new GraphView.GraphViewData[temp_float.length];
                        if(temp_float.length != 0) {
                            for(int i=0;i<temp_float.length;i++){
                                data[i] = new GraphView.GraphViewData(i,temp_float[i]);
                            }
                            series.resetData(data);
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };
        setContentView(R.layout.activity_main);
        // init example series data
        // draw sin curve

        GraphView.GraphViewData[] data = new GraphView.GraphViewData[num];
        double v = 0;
        for (int i = 0; i < num; i++) {
            v += 0.2;
            data[i] = new GraphView.GraphViewData(i, Math.sin(v));
            data2[i] = new GraphView.GraphViewData(i, Math.sin(v/2 + 2)-2);
        }
        GraphViewSeries series1 =new GraphViewSeries(data);
        lineview = new LineGraphView(this, "");
        //series1.SetShowMaxFlag(true);
        //series1.SetShowMinFlag(true);
        series = new GraphViewSeries(null, new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(255, 255, 0), 4), data2);
        //series.SetShowMaxFlag(true);
        //series.SetShowMinFlag(true);
        //series.SetSignCurveFlag(true);
// add data
        lineview.addSeries(series1);
        lineview.addSeries(series);
        lineview.setViewPort(40, 40);
        lineview.setManualYAxisBounds(255,0);
        lineview.setScrollable(true);

// optional - activate scaling / zooming
        lineview.setScalable(true);
        lineview.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.BOTH);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(lineview);
        text_x1 = (TextView) findViewById(R.id.text_x1);
        double x1 = lineview.getViewportSize();
        text_x1.setText("" + x1);

        lineview.getGraphViewStyle().setGridColor(Color.argb(100,0,128,0));
        lineview.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
        lineview.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
        lineview.getGraphViewStyle().setNumHorizontalLabels(5);
        lineview.getGraphViewStyle().setNumVerticalLabels(5);
        lineview.getGraphViewStyle().setVerticalLabelsWidth(100);
        temp_flag = true;
        CustomLabelFormatter LabelFormatter = new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX&&temp_flag) {
                    return (int)value+"ms";
                }
                else{
                    return  (double)((int)(value*10))/10+"V";
                }
            }
        }
        ;
        lineview.setCustomLabelFormatter(LabelFormatter);
        Thread tReceived = new Thread(udpservice);
        tReceived.start();
        //myRunnable.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

