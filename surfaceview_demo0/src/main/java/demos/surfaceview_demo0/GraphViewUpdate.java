package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;


/**
 * Created by lvniqi on 2014/11/9.
 */
public class GraphViewUpdate implements Runnable {
    static final int LENGTH = 250;
    static final int WAITIME = 500;
    int screen_width, screen_height;
    int j = 0;
    private int HANDLE_COUNT = 0;
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //服务函数
    private Handler mHandler;
    //创建
    GraphViewUpdate(SurfaceHolder Holder){
        surfaceHolder = Holder;
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DefinedMessages.ADD_NEW_DATA:
                        break;
                }
                HANDLE_COUNT++;
                super.handleMessage(msg);
            }
        };
    }

    //设置宽度
    public void SetWidth(int width){
        screen_width = width;
    }

    //设置高度
    public void SetHeight(int height){
        screen_height = height;
    }

    @Override
    public void run() {
        while(true){
            try{
                j++;
                double v = 0;
                int[] a = new int[LENGTH];
                for (int i = 0; i < LENGTH; i++) {
                    a[i] = (int) (100 * Math.sin((double) (v + j))) + 200;
                    v += 0.05;
                    v += 0.05;
                }
                DrawLine(a);
                Thread.sleep(WAITIME);
            }
            catch(Exception e){}
        }
    }

    private void DrawLine(int[] data) {
        Canvas canvas = surfaceHolder.lockCanvas(new Rect(0, 0, screen_width, screen_height));
        Paint paint = new Paint();
        Paint bgPaint = new Paint();
        Path mpath = new Path();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        // Background
        bgPaint.setColor(Color.BLACK);
        // Ball

        mpath.moveTo(0, 0);
        canvas.drawRect(0, 0, screen_width, screen_height, bgPaint);
        for (int i = 1; i < data.length; i++) {
            canvas.drawLine((i - 1), data[i - 1], i, data[i], paint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void DrawGrid() {
    }

    ;
}

