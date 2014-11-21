package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;


/**
 * Created by lvniqi on 2014/11/9.
 */
public class GraphViewUpdate implements Runnable {
    static final long FPS = 24;
    static final long FRAME_TIME = 1000 / FPS;
    static final int BALL_R = 50;
    int cx = BALL_R+150, cy = BALL_R;
    int xx = 30, yy = 30;
    int screen_width, screen_height;
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

        Canvas canvas = null;
        Paint paint = new Paint();
        Paint bgPaint = new Paint();

        // Background
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.BLACK);
        // Ball
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        paint.setTextSize(200);

        long loopCount = 0;
        long waitTime = 0;
        long startTime = System.currentTimeMillis();

        while(true){
            try{
                loopCount++;
                canvas = surfaceHolder.lockCanvas(new Rect(0, 0, screen_width, screen_height));
                canvas.drawRect(0, 0, screen_width, screen_height, bgPaint);
                canvas.drawCircle(cx, cy, BALL_R, paint);
                canvas.drawText(String.valueOf(waitTime), 20, 200, paint);
                cx += xx;
                cy += yy;

                surfaceHolder.unlockCanvasAndPost(canvas);

                waitTime = (loopCount * FRAME_TIME) - (System.currentTimeMillis() - startTime);

                if (cx>screen_width-BALL_R) xx = -xx;
                if (cy>screen_height-BALL_R) yy = -yy;
                if (cx<BALL_R) xx = Math.abs(xx);
                if (cy<BALL_R) yy = Math.abs(yy);

                if( waitTime > 0 ){
                    Thread.sleep(waitTime);
                }
            }
            catch(Exception e){}
        }
    }

    private void DrawLine(int[] data) {
        Canvas canvas = null;
        Paint paint = new Paint();
        Paint bgPaint = new Paint();

        // Background
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.BLACK);
        // Ball
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas = surfaceHolder.lockCanvas(new Rect(0, 0, screen_width, screen_height));
        canvas.drawRect(0, 0, screen_width, screen_height, bgPaint);
        for (int i = 0; i < data.length - 1; i++) {
            canvas.drawLine(i, data[i], i + 1, data[i + 1], paint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}

