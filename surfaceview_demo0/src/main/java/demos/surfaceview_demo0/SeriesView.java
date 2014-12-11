package demos.surfaceview_demo0;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lvniqi on 2014/11/18.
 */
public class SeriesView extends SurfaceView
        implements SurfaceHolder.Callback {

    Thread thread;
    SeriesViewUpdate update_thread;

    public SeriesView(Context context) {
        super(context);
        SurfaceHolder surfaceHolder;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        update_thread = new SeriesViewUpdate(surfaceHolder);
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        //update_thread.setWidth(width);
        //update_thread.setHeight(height);
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

    //得到更新进程
    public SeriesViewUpdate getUpdate_thread() {
        return update_thread;
    }
}