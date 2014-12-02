package demos.surfaceview_demo0;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


public class MainActivity extends Activity {
    static GraphView graphView_temp;
    static GridView gridView_temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout graphView_layout = (FrameLayout) findViewById(R.id.SurfaceView_01);
        FrameLayout grid_layout = (FrameLayout) findViewById(R.id.GridView_01);
        graphView_temp = new GraphView(this);
        gridView_temp = new GridView(this);
        graphView_temp.setZOrderOnTop(true);// 这句不能少
        graphView_temp.getHolder().setFormat(PixelFormat.TRANSPARENT);
        graphView_layout.addView(graphView_temp);
        grid_layout.addView(gridView_temp);
        gridView_temp.postInvalidate();
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
