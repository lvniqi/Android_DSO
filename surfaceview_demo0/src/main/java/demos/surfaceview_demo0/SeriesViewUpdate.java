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

import java.util.ArrayList;


/**
 * Created by lvniqi on 2014/11/9.
 * <p/>
 * SeriesViewUpdate
 * 用于更新GraphView
 *
 * @see java.lang.Runnable
 */
public class SeriesViewUpdate implements Runnable {
    static final int LENGTH = 1000;
    static final int WAITIME = 5;
    int j = 0;
    private int HANDLE_COUNT = 0;
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //x轴注册
    private AxisView xAxis;
    //y轴注册
    private AxisView yAxis;
    //服务函数
    private Handler mHandler;
    //左侧位置
    private int left = 0;
    private int top = 0;
    private int width = 0;
    private int height = 0;
    private int movex = 0;
    private int movey = 0;
    private boolean Sizechanged;
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

    //设置x轴
    public void setxAxis(AxisView xAxis) {
        this.xAxis = xAxis;
    }

    //设置y轴
    public void setyAxis(AxisView yAxis) {
        this.yAxis = yAxis;
    }

    /**
     * 设置x轴移动
     * @param movex
     */
    public void setMovex(int movex) {
        this.movex += movex;
    }

    /**
     * 设置Y轴移动
     *
     * @param movey
     */
    public void setMovey(int movey) {
        this.movey += movey;
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
                    canvas = surfaceHolder.lockCanvas(new Rect(left, top, width + left, top + height));
                } else {
                    canvas = surfaceHolder.lockCanvas();
                }
                j++;
                double v = 0;
                int[] a = new int[LENGTH];
                for (int i = 0; i < LENGTH; i++) {
                    a[i] = (int) (100 * Math.sin((double) (v + 0.05 * j))) + (int) (0.2*j);
                    v += 0.05;
                }
                clear(canvas);
                DrawLines(a, canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                /* 测试x轴设置可用
                if(xAxis != null){
                    ArrayList<String> temp = new ArrayList<String>();
                    for(int z = 0;z<xAxis.getAxis().size();z++) {
                        temp.add(j+z+"");
                    }
                    xAxis.setLabel(temp);
                    xAxis.postInvalidate();
                }*/
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
        canvas.translate(left + movex, top + movey);
        canvas.clipRect(-movex, -movey, width - movex, height -movey);
        //快速修正后
        int[] afterFix = FastFix(data, 4);


        for (int i = 1; i < afterFix.length; i++) {
            int startX = i - 1;
            int startY = height - afterFix[i - 1];
            int endX = i;
            int endY = height - afterFix[i];
            canvas.drawLine(4 * startX, startY, 4 * endX, endY, paint);
        }
    }

    /**
     * 快速绘线设置函数
     *
     * @param data
     * @param step
     * @return data_out
     */
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
            } else {
                data_out[i] = data[max];
            }
        }
        return data_out;
    }

    /**
     * 设置x轴坐标
     *
     * @param temp
     * @return
     */
    public boolean setXlabel(ArrayList<String> temp) {
        if (xAxis != null && xAxis.getAxis().size() == temp.size()) {
            xAxis.setLabel(temp);
            xAxis.postInvalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置y轴坐标
     *
     * @param temp
     * @return
     */
    public boolean setYlabel(ArrayList<String> temp) {
        if (yAxis != null && yAxis.getAxis().size() == temp.size()) {
            yAxis.setLabel(temp);
            yAxis.postInvalidate();
            return true;
        } else {
            return false;
        }
    }
}

