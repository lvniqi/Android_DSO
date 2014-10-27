package demos.dso_demo_graphview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;


public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init example series data
        // draw sin curve
        int num = 1024;
        GraphView.GraphViewData[] data = new GraphView.GraphViewData[num];
        GraphView.GraphViewData[] data2 = new GraphView.GraphViewData[num];
        double v=0;
        for (int i=0; i<num; i++) {
            v += 0.2;
            data[i] = new GraphView.GraphViewData(i, Math.sin(v));
            data2[i] = new GraphView.GraphViewData(i, Math.sin(v+1));
        }
        GraphView graphView = new LineGraphView(this, "GraphViewDemo");
        GraphViewSeries series =new GraphViewSeries(null,new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(90, 250, 00),3),data2);
// add data
        graphView.addSeries(new GraphViewSeries(data));
        graphView.addSeries(series);
        graphView.setViewPort(0, 150);
        graphView.setManualYAxisBounds(1,-1);
        graphView.setScrollable(true);
// optional - activate scaling / zooming
        graphView.setScalable(true);
        graphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.BOTH);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView);
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

