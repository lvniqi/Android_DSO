package demos.surfaceview_demo0;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lvniqi on 2014/11/18.
 */
public class GraphView extends SurfaceView
        implements SurfaceHolder.Callback {

    Thread thread;
    GraphViewUpdate update_thread;

    public GraphView(Context context) {
        super(context);
        SurfaceHolder surfaceHolder;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        update_thread = new GraphViewUpdate(surfaceHolder);
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        update_thread.SetWidth(width);
        update_thread.SetHeight(height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(update_thread);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }
}