package demos.surfaceview_demo0;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by lvniqi on 2014/11/18.
 */
public class SeriesView extends SurfaceView
        implements SurfaceHolder.Callback {

    Thread thread;
    SeriesViewUpdate update_thread;
    private int nowX = 0;
    private int nowY = 0;
    //触摸模式
    private int touchMode = 0;
    //开启触摸
    private boolean touchEnable = true;
    public SeriesView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchEnable) {
            super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchMode = 1;
                nowX = (int) event.getX();
                nowY = (int) event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                touchMode += 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchMode -= 1;
                break;
            case MotionEvent.ACTION_UP:
                touchMode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (1 == touchMode) {
                    int dx = (int) event.getX() - nowX;
                    int dy = (int) event.getY() - nowY;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 2) {
                        moveX(dx);
                    } else if (Math.abs(dy) > 2) {
                        moveY(dy);
                    }
                    nowX = (int) event.getX();
                    nowY = (int) event.getY();
                }
                break;
        }
        if (1 == touchMode) {
            update_thread.setWAITIME(0);
        } else {
            update_thread.setWAITIME(500);
        }
        return true;
    }

    private void moveX(int x) {
        update_thread.setMovex(x);
        /*ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            temp.add(x + "x");
        }
        update_thread.setXlabel(temp);*/
    }

    private void moveY(int y) {
        update_thread.setMovey(y);
        /*ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            temp.add(y + "y");
        }
        update_thread.setYlabel(temp);*/
    }

    //得到更新进程
    public SeriesViewUpdate getUpdate_thread() {
        return update_thread;
    }
}