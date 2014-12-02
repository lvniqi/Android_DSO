package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;


/**
 * Created by lvniqi on 2014/11/9.
 * <p/>
 * GraphViewSeriesUpdate
 * 用于更新GraphView
 *
 * @see java.lang.Runnable
 */
public class GraphViewSeriesUpdate implements Runnable {
    static final int LENGTH = 500;
    static final int WAITIME = 20;
    int screen_width, screen_height;
    int j = 0;
    private int HANDLE_COUNT = 0;
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //服务函数
    private Handler mHandler;

    //创建
    GraphViewSeriesUpdate(SurfaceHolder Holder) {
        surfaceHolder = Holder;

        mHandler = new Handler() {
            /**
             * 消息接收函数
             *
             * @param msg 接收其他线程的更新或者控制数据
             */
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
    public void SetWidth(int width) {
        screen_width = width;
    }

    //设置高度
    public void SetHeight(int height) {
        screen_height = height;
    }

    /**
     * Run
     * 运行函数
     * 用于定时更新数据
     */
    @Override
    public void run() {
        while (true) {
            try {
                Canvas canvas = surfaceHolder.lockCanvas(new Rect(0, 0, screen_width, screen_height));

                j++;
                double v = 0;
                int[] a = new int[LENGTH];
                for (int i = 0; i < LENGTH; i++) {
                    a[i] = (int) (100 * Math.sin((double) (v + j))) + 200;
                    v += 0.05;
                }
                clear(canvas);
                DrawLines(a, canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                Thread.sleep(WAITIME);
            } catch (Exception e) {
            }
        }
    }

    private void clear(Canvas canvas) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    private void DrawLines(int[] data, Canvas canvas) {

        Paint paint = new Paint();

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        // Background
        for (int i = 1; i < data.length; i++) {
            canvas.drawLine((i - 1), data[i - 1], i, data[i], paint);
        }
    }
}


class Labels {
    private Paint TxPaint;

    Labels() {
        TxPaint.setColor(Color.YELLOW);
        TxPaint.setTextAlign(Paint.Align.CENTER);
        TxPaint.setTextSize(10);
    }

    public void SetTxPoint(Paint paint) {
        this.TxPaint = paint;
    }

    public void DrawLabel(Canvas canvas) {
        canvas.drawColor(TxPaint.getColor());
    }
}