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
 *
 * GraphViewUpdate
 * 用于更新GraphView
 * @see java.lang.Runnable
 *
 *
 */
public class GraphViewUpdate implements Runnable {
    static final int LENGTH = 250;
    static final int WAITIME = 50;
    int screen_width, screen_height;
    int j = 0;
    private int HANDLE_COUNT = 0;
    private Grid grid = new Grid(0, 0, 0, 0);
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //服务函数
    private Handler mHandler;
    //创建
    GraphViewUpdate(SurfaceHolder Holder){
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
        grid.SetWidth(width);
    }
    //设置高度
    public void SetHeight(int height) {
        screen_height = height;
        grid.SetHeight(height);
    }

    /**
     * Run
     * 运行函数
     * 用于定时更新数据
     *
     */
    @Override
    public void run() {
        while(true){
            try{
                Canvas canvas = surfaceHolder.lockCanvas(new Rect(0, 0, screen_width, screen_height));

                j++;
                double v = 0;
                int[] a = new int[LENGTH];
                for (int i = 0; i < LENGTH; i++) {
                    a[i] = (int) (100 * Math.sin((double) (v + j))) + 200;
                    v += 0.05;
                }
                grid.DrawPartBackground(0, 0, 100, screen_height, canvas);
                grid.DrawPartGrid(0,0,100,screen_height,canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                Thread.sleep(WAITIME);
            }
            catch(Exception e){}
        }
    }

    private void DrawBackground(Canvas canvas) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, screen_width, screen_height, bgPaint);
    }

    private void DrawLine(int[] data, Canvas canvas) {

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


/**
 * Grid
 * 用于绘制网格背景
 *
 * @see demos.surfaceview_demo0.BackGround
 */
class Grid extends BackGround {
    private int div = 5;
    private Paint gPaint;

    Grid(int left, int top, int width, int height) {
        super(left, top, width, height);
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.RED);
    }

    Grid(Rect rect) {
        super(rect);
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.RED);
    }

    Grid() {
        super();
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.RED);
    }

    public void SetGridColor(int color) {
        gPaint.setColor(color);
    }

    public void SetGirdPoint(Paint paint) {
        this.gPaint = paint;
    }

    public int GetGridColor() {
        return gPaint.getColor();
    }

    public Paint GeGridtPoint() {
        return gPaint;
    }

    public void SetDiv(int div) {
        this.div = div;
    }

    /**
     * 绘制外边框
     *
     * @param canvas
     */
    public void DrawRect(Canvas canvas) {
        gPaint.setStrokeWidth(5);
        gPaint.setAlpha(255);
        //外边框
        canvas.drawLine(left, top + 2, left + width, top + 2, gPaint);
        canvas.drawLine(left, top + height - 2, left + width, top + height - 2, gPaint);
        canvas.drawLine(left + 2, top, left + 2, top + height, gPaint);
        canvas.drawLine(left + width - 2, top, left + width - 2, top + height, gPaint);
    }

    public void DrawGrid(Canvas canvas) {
        gPaint.setStrokeWidth(1);
        gPaint.setAlpha(100);
        //纵向线
        float div_x = ((float) width) / div;
        for (float x = left + div_x; x < width; x += div_x) {
            canvas.drawLine(x, top, x, top + height, gPaint);
        }
        //横向线
        float div_y = ((float) height) / div;
        for (float y = top + div_y; y < height; y += div_y) {
            canvas.drawLine(left, y, left + width, y, gPaint);
        }
    }

    public void DrawPartGrid(int left, int top, int width, int height, Canvas canvas) {
        gPaint.setStrokeWidth(1);
        gPaint.setAlpha(100);
        //横向间隔
        float div_x = ((float) this.width) / div;
        //纵向间隔
        float div_y = ((float) this.height) / div;
        float temp_start_x = this.left + div_x;
        float temp_start_y = this.top + div_y;
        //输入错误检查
        if (left > this.left + this.width) {
            return;
        }
        if (width + left > this.left + this.width) {
            width = this.left + this.width - left;
        }
        if (top > this.top + this.height) {
            return;
        }
        if (top + height > this.top + this.height) {
            height = this.top + this.height;
        }
        //得到起始x
        while (temp_start_x < left) {
            temp_start_x += div_x;
        }
        //得到起始y
        while (temp_start_y < top) {
            temp_start_y += div_y;
        }
        //纵向线
        for (; temp_start_x <= left + width; temp_start_x += div_x) {
            canvas.drawLine(temp_start_x, top, temp_start_x, top + height, gPaint);
        }
        //横向线
        for (; temp_start_y <= top + height; temp_start_y += div_y) {
            canvas.drawLine(left, temp_start_y, left +width, temp_start_y, gPaint);
        }
    }
}

/**
 * Grid
 * 用于绘制背景
 */
class BackGround {
    //长宽高...
    protected int width;
    protected int height;
    protected int left;
    protected int top;
    //背景画笔
    private Paint bgPaint;

    BackGround(int left, int top, int width, int height) {
        this.width = width;
        this.height = height;
        this.left = left;
        this.top = top;
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.BLACK);
    }

    BackGround(Rect rect) {
        this.left = rect.left;
        this.top = rect.top;
        this.width = rect.right - rect.left;
        this.height = rect.bottom - rect.top;
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.BLACK);
    }

    BackGround() {
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.RED);
    }

    public void SetWidth(int width) {
        this.width = width;
    }

    public void SetHeight(int height) {
        this.height = height;
    }

    public void SetLeft(int left) {
        this.left = left;
    }

    public void SetTop(int top) {
        this.top = top;
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }

    public int GetLeft() {
        return left;
    }

    public int GetTop() {
        return top;
    }

    public void SetBgColor(int color) {
        bgPaint.setColor(color);
    }

    public void SetBgPoint(Paint paint) {
        this.bgPaint = paint;
    }

    public int GetBgColor() {
        return bgPaint.getColor();
    }

    public Paint GeBgtPoint() {
        return bgPaint;
    }

    /**
     * 绘制背景
     *
     * @param canvas     绘满整个canvas？
     * @param fullcanvas
     */
    public void DrawBackground(Canvas canvas, boolean fullcanvas) {
        if (fullcanvas) {
            canvas.drawColor(bgPaint.getColor());
        } else {
            canvas.drawRect(left, top, width + left, height + top, bgPaint);
        }
    }

    public void DrawPartBackground(int left, int top, int width, int height, Canvas canvas) {
        //输入错误检查
        if (left > this.left + this.width) {
            return;
        }
        if (width + left > this.left + this.width) {
            width = this.left + this.width - left;
        }
        if (top > this.top + this.height) {
            return;
        }
        if (top + height > this.top + this.height) {
            height = this.top + this.height;
        }
        canvas.drawRect(left, top, width + left, height + top, bgPaint);
    }
}

class Labels {
    private int height;
    private int width;
}