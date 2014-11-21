package demos.surfaceview_demo0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by lvniqi on 2014/11/18.
 */
public class GraphView extends SurfaceView
        implements Runnable, SurfaceHolder.Callback {

    static final long FPS = 20;
    static final long FRAME_TIME = 1000 / FPS;
    static final int BALL_R = 500;
    SurfaceHolder surfaceHolder;
    Thread thread;
    int cx = BALL_R, cy = BALL_R;
    int xx = 1, yy = 1;
    int screen_width, screen_height;

    public GraphView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void run() {

        Canvas canvas = null;
        Paint paint = new Paint();
        Paint bgPaint = new Paint();

        // Background
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.BLACK);
        // Ball
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        paint.setTextSize(100);

        long loopCount = 0;
        long waitTime = 0;
        long startTime = System.currentTimeMillis();

        while (thread != null) {
            try {
                loopCount++;
                canvas = surfaceHolder.lockCanvas();

                canvas.drawRect(0, 0, screen_width, screen_height, bgPaint);
                canvas.drawCircle(cx, cy, BALL_R, paint);
                canvas.drawText(String.valueOf(waitTime), 20, 100, paint);
                cx += xx;
                cy += yy;

                surfaceHolder.unlockCanvasAndPost(canvas);

                waitTime = (loopCount * FRAME_TIME) - (System.currentTimeMillis() - startTime);

                if (cx > screen_width - BALL_R) xx = -1;
                if (cy > screen_height - BALL_R) yy = -1;
                if (cx < BALL_R) xx = 1;
                if (cy < BALL_R) yy = 1;

                if (waitTime > 0) {
                    Thread.sleep(waitTime);
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        screen_width = width;
        screen_height = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }
}