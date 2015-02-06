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
    CosData test;
    int yTrans = 0;
    //服务函数
    private Handler mHandler;
    private int HANDLE_COUNT = 0;
    //surfaceview lock
    private SurfaceHolder surfaceHolder;
    //继续运行？
    private boolean isContinue = true;
    //x轴注册
    private AxisView xAxis;
    //y轴注册
    private AxisView yAxis;
    //左侧位置
    private int width = 0;
    private int height = 0;
    private int moveX = 0;
    private int moveY = 0;
    //y轴缩放
    //y最大值
    private float manualMaxYValue = 256;
    //y最小值
    private float manualMinYValue = 0;
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

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
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

    public void setScalingY(float scalingY, float startpos) {
        float realPos = startpos / height;
        Log.i(startpos + "", realPos + "");
        float PosValue = this.manualMaxYValue * (1 - realPos) + this.manualMinYValue * realPos;
        manualMaxYValue = (manualMaxYValue - PosValue) / scalingY + PosValue;
        manualMinYValue = (manualMinYValue - PosValue) / scalingY + PosValue;
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
     * @param moveX
     */
    public void setMoveX(int moveX) {
        this.moveX += moveX;
        //测试显示坐标
        String temp = this.moveX + "";
        if (xAxis.getLabel() != null) {
            xAxis.getLabel().remove(0);
            xAxis.getLabel().add(temp);
            xAxis.postInvalidate();
        }
    }

    /**
     * 设置Y轴移动
     *
     * @param moveY
     */
    public void setMoveY(int moveY) {
        //this.moveY += moveY;
        float size = manualMaxYValue - manualMinYValue;
        this.manualMaxYValue += moveY * size / height;
        this.manualMinYValue += moveY * size / height;
        //测试显示坐标
        //String temp = (int)(this.moveY/(manualMaxYValue-manualMinYValue)) + "";
        if (yAxis.getLabel() != null) {
            yAxis.getLabel().remove(0);
            yAxis.getLabel().add(((int) manualMinYValue) + "");
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
        while (isContinue) {
            Canvas canvas = null;
            try {
                canvas = null;
                canvas = surfaceHolder.lockCanvas(new Rect(0, 0, width, height));
                if (!(ch1Flag | ch2Flag)) {
                    clear(canvas);
                    DrawText(canvas, Color.rgb(0xff, 0x44, 0x44), MainActivity.getmContext().getString(R.string.no_source_in));
                } else if (isShow()) {
                    clear(canvas);
                    if (test != null && (test.getData().size() > 0)) {
                        Integer[] a = test.getData().toArray(new Integer[0]);
                        DrawLines(a, canvas);
                    }
                } else {
                    clear(canvas);
                    DrawText(canvas, Color.rgb(0x00, 0x99, 0xcc), MainActivity.getmContext().getString(R.string.menu_open));
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
        final float WINDOW_SIZE = (float) 5;
        final int FIX_SIZE = (int) (DensityUtil.dip2px(MainActivity.getmContext(), (float) 2) * 1.5 / WINDOW_SIZE);
        final float FIX_LENGTH = (FIX_SIZE * WINDOW_SIZE);
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(DensityUtil.dip2px(MainActivity.getmContext(), 2));
        paint.setAntiAlias(true);
        //快速修正后
        Integer[] afterFix = FastFix(data, FIX_SIZE);
        //防止越界
        float x_edge = width - FIX_LENGTH * (afterFix.length / 2 - 1);
        if (x_edge > 0) {
            moveX = 0;
        } else if (moveX < x_edge) {
            moveX = (int) (width - FIX_LENGTH * (afterFix.length / 2 - 1));
        } else if (moveX > 0) {
            moveX = 0;
        }
        float sizeY = manualMaxYValue - manualMinYValue;
        //偏移
        canvas.translate(moveX, manualMinYValue * height / sizeY);
        canvas.clipRect(-moveX, -manualMinYValue * height / sizeY, width - moveX, height - manualMinYValue * height / sizeY);
        float startX = 0;
        float startY = height - ((afterFix[0] + afterFix[afterFix.length / 2]) / 2) * height / (manualMaxYValue - manualMinYValue);
        float endX = 1;
        float endY = height - (afterFix[1] + afterFix[1 + afterFix.length / 2]) / 2 * height / (manualMaxYValue - manualMinYValue);
        for (int i = 1; i < afterFix.length / 2; i++) {
            //如果有峰峰值大于阀值
            if (Math.abs(afterFix[i - 1] - afterFix[afterFix.length / 2 + i - 1]) > 100) {
                float temp_start = height - afterFix[i] * height / (manualMaxYValue - manualMinYValue);
                float temp_end = height - afterFix[afterFix.length / 2 + i] * height / (manualMaxYValue - manualMinYValue);
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
                endY = height - afterFix[i + afterFix.length / 2] * height / (manualMaxYValue - manualMinYValue);
            }
            //正常绘图
            else {
                canvas.drawLine(FIX_LENGTH * startX, startY, FIX_LENGTH * endX, endY, paint);
            }
            startX = i;
            startY = endY;
            if (i < afterFix.length - 1) {
                endX = i + 1;
                endY = height - (afterFix[i + 1] + afterFix[i + 1 + afterFix.length / 2]) / 2 * height / (manualMaxYValue - manualMinYValue);
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
        if (step < 1) {
            step = 1;
        }
        int count = data.length / step;
        Integer[] data_out = new Integer[count * 2];
        if (step < 2) {
            for (int i = 0; i < count; i++) {
                data_out[i] = data_out[count + i] = data[i];
            }
        } else {
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