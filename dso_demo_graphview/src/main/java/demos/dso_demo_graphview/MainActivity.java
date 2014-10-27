package demos.dso_demo_graphview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;


public class MainActivity extends Activity {

    private TextView text_x1;
    public GraphView lineview;

    Handler handler = new Handler();
    private Runnable myRunnable= new Runnable() {
        public void run() {

            handler.postDelayed(this, 1000);
            double x1 = lineview.getViewportSize();
            text_x1.setText(""+x1);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init example series data
        // draw sin curve
        int num = 128;
        GraphView.GraphViewData[] data = new GraphView.GraphViewData[num];
        GraphView.GraphViewData[] data2 = new GraphView.GraphViewData[num];
        double v=0;
        for (int i=0; i<num; i++) {
            v += 0.2;
            data[i] = new GraphView.GraphViewData(i, Math.sin(v));
            data2[i] = new GraphView.GraphViewData(i, Math.sin(v+2));
            if(i == 100)
            {
                data2[i] = new GraphView.GraphViewData(i,-1);
            }
        }
        lineview = new LineGraphView(this, "");
        GraphViewSeries series =new GraphViewSeries(null,new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(90, 250, 00),3),data2);
// add data
        lineview.addSeries(new GraphViewSeries(data));
        lineview.addSeries(series);
        lineview.setViewPort(40, 40);
        lineview.setManualYAxisBounds(1,-1);
        lineview.setScrollable(true);

        lineview.setVerticalLabels(new String[]{"12V", " 0V", "-12V"});
// optional - activate scaling / zooming
        lineview.setScalable(true);
        lineview.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.BOTH);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(lineview);
        text_x1 = (TextView)findViewById(R.id.text_x1);
        double x1 = lineview.getViewportSize();
        text_x1.setText(""+x1);

        lineview.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
        lineview.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
        lineview.getGraphViewStyle().setNumHorizontalLabels(4);
        lineview.getGraphViewStyle().setNumVerticalLabels(4);
        lineview.getGraphViewStyle().setVerticalLabelsWidth(100);
        myRunnable.run();

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

