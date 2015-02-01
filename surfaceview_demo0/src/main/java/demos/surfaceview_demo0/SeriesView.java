package demos.surfaceview_demo0;

import android.content.Context;
import android.util.AttributeSet;
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
    private int pointCount = 0;
    //开启触摸
    private boolean touchEnable = true;
    //手指0
    private int mPointerId0;
    //手指1
    private int mPointerId1;

    public SeriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        SurfaceHolder surfaceHolder;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        update_thread = new SeriesViewUpdate(surfaceHolder);
    }

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
        thread.interrupt();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        final int pointerIndex0 = event.findPointerIndex(mPointerId0);
        final int pointerIndex1 = event.findPointerIndex(mPointerId1);
        if (!touchEnable) {
            super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pointCount = 1;
                mPointerId0 = event.getPointerId(0);
                nowX = (int) event.getX();
                nowY = (int) event.getY();
                handled = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointCount++;
                if (2 == pointCount) {
                    mPointerId1 = event.getPointerId(1);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointCount--;
                // 获取离开屏幕的手指的索引
                int pointerIndexLeave = event.getActionIndex();
                int pointerIdLeave = event.getPointerId(pointerIndexLeave);
                // 离开屏幕的正是目前的有效手指，此处需要重新调整
                if (mPointerId0 == pointerIdLeave) {
                    int reIndex = pointerIndexLeave == 0 ? 1 : 0;
                    mPointerId0 = event.getPointerId(reIndex);
                    nowX = (int) event.getX(reIndex);
                    nowY = (int) event.getY(reIndex);
                }
                break;
            case MotionEvent.ACTION_UP:
                pointCount = 0;
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (1 == pointCount) {
                    int dx = (int) event.getX(pointerIndex0) - nowX;
                    int dy = (int) event.getY(pointerIndex0) - nowY;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 2) {
                        moveX(dx);
                    } else if (Math.abs(dy) > 2) {
                        moveY(dy);
                    }
                    handled = true;
                }
                nowX = (int) event.getX(pointerIndex0);
                nowY = (int) event.getY(pointerIndex0);
                break;
        }
        if (1 == pointCount) {
            update_thread.setWAITIME(0);
        } else {
            update_thread.setWAITIME(500);
        }
        return handled;
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