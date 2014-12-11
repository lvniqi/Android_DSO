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
 * SeriesViewUpdate
 * 用于更新GraphView
 *
 * @see java.lang.Runnable
 */
public class SeriesViewUpdate implements Runnable {
    static final int LENGTH = 500;
    static final int WAITIME = 20;
    static private boolean Sizechanged;
    int j = 0;
    private int HANDLE_COUNT = 0;
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //服务函数
    private Handler mHandler;
    //左侧留白
    private int left = 0;
    //底部留白
    private int yBorder = 0;
    private int top = 0;
    private int width = 0;
    private int height = 0;
    //创建
    SeriesViewUpdate(SurfaceHolder Holder) {
        surfaceHolder = Holder;
        Sizechanged = false;
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
    public void setWidth(int width) {
        this.width = width;
    }

    //设置高度
    public void setHeight(int height) {
        this.height = height;
    }

    //设置左侧宽度
    public void setLeft(int left) {
        if (this.left != left) {
            this.left = left;
            Sizechanged = true;
        }
    }

    //设置顶部
    public void setTop(int top) {
        if (this.top != top) {
            this.top = top;
            Sizechanged = true;
        }
    }

    public void setyBorder(int yBorder) {
        if (this.yBorder != yBorder) {
            this.yBorder = yBorder;
            Sizechanged = true;
        }
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
                Canvas canvas;
                if (!Sizechanged) {
                    canvas = surfaceHolder.lockCanvas(new Rect(left, 0, width - left, height - yBorder));
                } else {
                    canvas = surfaceHolder.lockCanvas();
                }
                j++;
                double v = 0;
                int[] a = new int[LENGTH];
                for (int i = 0; i < LENGTH; i++) {
                    a[i] = (int) (100 * Math.sin((double) (v + 0.1 * j))) + (int) (0.05 * j);
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

    /**
     * 清除画布上的所有东西
     *
     * @param canvas
     */
    private void clear(Canvas canvas) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    /**
     * 绘制曲线
     * @param data
     * @param canvas
     */
    private void DrawLines(int[] data, Canvas canvas) {

        Paint paint = new Paint();

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        //偏移
        canvas.translate(left, top);
        //快速修正后
        int[] afterFix = FastFix(data, 4);
        /*for (int i = 1; i < data.length; i++) {
            canvas.drawLine((i - 1), data[i - 1], i, data[i], paint);
        }*/

        for (int i = 1; i < afterFix.length; i++) {
            canvas.drawLine(4 * (i - 1), afterFix[i - 1], 4 * i, afterFix[i], paint);
        }

    }

    private int[] FastFix(int[] data, int step) {
        if (step < 2) {
            return data;
        }
        int count = data.length / step;
        int[] data_out = new int[count];

        for (int i = 0; i < count; i++) {
            int max = i * step, min = i * step;
            for (int j = i * step; j < i * step + step; j++) {
                if (data[j] > data[max]) {
                    max = j;
                } else if (data[j] < data[min]) {
                    min = j;
                }
            }
            if (max > min) {
                data_out[i] = data[min];
                //data_out[i/step*2+1] = data[max];
            } else {
                data_out[i] = data[max];
                //data_out[i/step*2+1] = data[min];
            }
        }
        return data_out;
    }
}

