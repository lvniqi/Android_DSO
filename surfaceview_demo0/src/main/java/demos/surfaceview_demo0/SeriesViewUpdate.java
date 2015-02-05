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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
    static final int LENGTH = 2048;
    //服务函数
    private static Handler mHandler;
    CosData test;
    int yTrans = 0;
    private int HANDLE_COUNT = 0;
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //x轴注册
    private AxisView xAxis;
    //y轴注册
    private AxisView yAxis;
    //左侧位置
    private int width = 0;
    private int height = 0;
    private int movex = 0;
    private int movey = 0;
    //暂停
    private boolean showFlag = true;
    private boolean ch1Flag = false;
    private boolean ch2Flag = false;
    final boolean suspendShowFlag = (ch1Flag | ch2Flag) & (!showFlag);
    //暂停字符
    private String suspendShowString;

    //创建
    SeriesViewUpdate(SurfaceHolder Holder) {
        setSurfaceHolder(Holder);

        mHandler = new Handler() {
            /**
             * 消息接收函数
             *
             * @param msg 接收其他线程的更新或者控制数据
             */
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DefinedMessages.ADD_NEW_DATA:
                        Log.d("UDP Demo", "已经接收");
                        Bundle bundle = new Bundle();
                        bundle = msg.getData();
                        byte[] temp_byte = bundle.getByteArray("str");
                        int[] temp_float = ByteArrayFunction.BytesToInt(temp_byte);
                        test = new CosData(temp_float);
                        break;
                }
                HANDLE_COUNT++;
                super.handleMessage(msg);
            }
        };
    }

    public void setCh1Flag(boolean ch1Flag) {
        this.ch1Flag = ch1Flag;
    }

    public void setCh2Flag(boolean ch2Flag) {
        this.ch2Flag = ch2Flag;
    }

    public void setSuspendShowString(String suspendShowString) {
        this.suspendShowString = suspendShowString;
    }

    public boolean isShow() {
        return showFlag;
    }

    public void setShow(boolean showFlag) {
        this.showFlag = showFlag;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    //设置宽度
    public void setWidth(int width) {
        this.width = width;
    }

    //设置高度
    public void setHeight(int height) {
        this.height = height;
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
     *
     * @param movex
     */
    public void setMovex(int movex) {
        this.movex += movex;
        //测试显示坐标
        String temp = this.movex + "";
        if (xAxis.getLabel() != null) {
            xAxis.getLabel().remove(0);
            xAxis.getLabel().add(temp);
            xAxis.postInvalidate();
        }
    }

    /**
     * 设置Y轴移动
     *
     * @param movey
     */
    public void setMovey(int movey) {
        this.movey += movey;
        //测试显示坐标
        String temp = this.movey + "";
        if (yAxis.getLabel() != null) {
            yAxis.getLabel().remove(0);
            yAxis.getLabel().add(temp);
            yAxis.postInvalidate();
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
            Canvas canvas = null;
            try {
                canvas = null;
                canvas = surfaceHolder.lockCanvas(new Rect(0, 0, width, height));
                if (!(ch1Flag | ch2Flag)) {
                    clear(canvas);
                    DrawText(canvas, Color.RED, MainActivity.getmContext().getString(R.string.no_source_in));
                } else if (isShow()) {
                    /*yTrans++;
                    double v = 0;
                    ArrayList<Integer> a2 = new ArrayList<Integer>();
                    for (int i = 0; i < LENGTH; i++) {
                        //int temp = (int) (300 * Math.sin((double) v+1));
                        int temp = (int) (300  * Math.sin(10*v));
                        //int temp = (int) (300 * Math.sin((double) yTrans / 100 * v)) + (int) (0.2 * yTrans);
                        a2.add(temp);
                        //a2.add(100);
                        //a2.add(-100);
                        v += 0.02;
                    }*/

                    Integer[] a = test.getData().toArray(new Integer[0]);
                    clear(canvas);
                    DrawLines(a, canvas);
                } else {
                    clear(canvas);
                    DrawText(canvas, Color.BLUE, MainActivity.getmContext().getString(R.string.menu_open));
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    canvas = null;
                }
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
     *
     * @param data
     * @param canvas
     */
    private void DrawLines(Integer[] data, Canvas canvas) {
        final int FIX_LENGTH = 4;
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        //防止越界
        if (movex < width - data.length) {
            movex = width - data.length;
        } else if (movex > 0) {
            movex = 0;
        }
        //偏移
        canvas.translate(movex, movey);
        canvas.clipRect(-movex, -movey, width - movex, height - movey);
        //快速修正后
        Integer[] afterFix = FastFix(data, FIX_LENGTH);
        int startX = 0;
        int startY = height - (afterFix[0] + afterFix[afterFix.length / 2]) / 2;
        int endX = 1;
        int endY = height - (afterFix[1] + afterFix[1 + afterFix.length / 2]) / 2;
        for (int i = 1; i < afterFix.length / 2; i++) {
            int j = 1;
            for (; j < FIX_LENGTH; j++) {
                if (Math.abs(data[FIX_LENGTH * i + j] - data[FIX_LENGTH * i + j - 1]) > 50) {
                    break;
                }
            }
            //如果有峰峰值大于阀值
            if (j < FIX_LENGTH) {
                int temp_start = height - afterFix[i];
                int temp_end = height - afterFix[afterFix.length / 2 + i];
                //如果后面大于前面
                if (temp_end > temp_start) {
                    if (startY < temp_start) {
                        temp_start = startY;
                    } else if (startY > temp_end) {
                        temp_end = startY;
                    }
                    canvas.drawRect(FIX_LENGTH * startX,
                            temp_start,
                            FIX_LENGTH * endX,
                            temp_end,
                            paint);
                }
                //如果前面大于后面
                else {
                    if (startY > temp_start) {
                        temp_start = startY;
                    } else if (startY < temp_end) {
                        temp_end = startY;
                    }
                    canvas.drawRect(FIX_LENGTH * startX,
                            temp_end,
                            FIX_LENGTH * endX,
                            temp_start,
                            paint);
                }
                endY = height - afterFix[i + afterFix.length / 2];
            }
            //正常绘图
            else {
                canvas.drawLine(FIX_LENGTH * startX, startY, FIX_LENGTH * endX, endY, paint);
            }
            startX = i;
            startY = endY;
            if (i < afterFix.length - 1) {
                endX = i + 1;
                endY = height - (afterFix[i + 1] + afterFix[i + 1 + afterFix.length / 2]) / 2;
            }
        }
    }

    private void DrawText(Canvas canvas, int color, String string) {
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);
        paint.setTextSize(DensityUtil.dip2px(MainActivity.getmContext(), 50));
        paint.setAntiAlias(true);
        float[] widths = new float[string.length()];
        float toalwidth = 0;
        float height = paint.getTextSize();
        int index = 0;
        paint.getTextWidths(string, widths);
        for (int i = 0; i < string.length(); i++) {
            toalwidth += widths[i];
            if (toalwidth > width) {
                i--;
                String text = string.substring(index, i);
                canvas.drawText(text, 0, height + paint.getTextSize(), paint);
                index = i;
                height += paint.getTextSize();
                toalwidth = 0;
            }
        }
        if (toalwidth > 1) {
            String text = string.substring(index, string.length());
            canvas.drawText(text, 0, height + paint.getTextSize(), paint);
        }
        //canvas.drawText(string,0,paint.getTextSize(),paint);
    }
    /**
     * 快速绘线设置函数
     *
     * @param data
     * @param step
     * @return data_out
     */
    private Integer[] FastFix(Integer[] data, int step) {
        if (step < 2) {
            return data;
        }
        int count = data.length / step;
        Integer[] data_out = new Integer[count * 2];

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
                data_out[count + i] = data[max];
            } else {
                data_out[i] = data[max];
                data_out[count + i] = data[min];
            }
        }
        return data_out;
    }

    private Integer[] FastFix_Sample(Integer[] data, int step) {
        if (step < 2) {
            return data;
        }
        int count = data.length / step;
        Integer[] data_out = new Integer[count];

        for (int i = 0; i < count; i++) {
            data_out[i] = data[i * step];
        }
        return data_out;
    }

    private Integer GetMax(Integer[] data, int start, int end) {
        Integer temp_data = data[start];
        for (int i = start; i < end; i++) {
            if (temp_data < data[i]) {
                temp_data = data[i];
            }
        }
        return temp_data;
    }

    private Integer GetMin(Integer[] data, int start, int end) {
        Integer temp_data = data[start];
        for (int i = start; i < end; i++) {
            if (temp_data > data[i]) {
                temp_data = data[i];
            }
        }
        return temp_data;
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

class CosData implements DataInterface {
    private ArrayList<Integer> data;
    private int Lenth;

    CosData() {
        data = new ArrayList<Integer>();
    }

    CosData(int[] a) {
        data = new ArrayList<Integer>();
        for (int x : a) {
            data.add(x);
        }
    }
    @Override
    public int getX(int index) {
        return index;
    }

    @Override
    public int getY(int index) {
        return data.get(index);
    }

    @Override
    public int getLenth() {
        return Lenth;
    }

    public ArrayList<Integer> getData() {
        return data;
    }

    public void setData(ArrayList<Integer> data) {
        this.data = data;
    }
}