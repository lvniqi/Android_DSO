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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by lvniqi on 2014/11/9.
 * <p/>
 * SeriesViewUpdate
 * 用于更新GraphView
 *
 * @see java.lang.Runnable
 */
public class SeriesViewUpdate implements Runnable {
    ArrayList<SeriesChannel> channelList;
    //surfaceView lockAxis
    Lock lockAxis = new ReentrantLock();
    Lock lockData = new ReentrantLock();
    //服务函数
    private Handler mHandler;
    //位置锁
    private int HANDLE_COUNT = 0;
    private SurfaceHolder surfaceHolder;
    //继续运行？
    private boolean isContinue = true;
    //x轴注册
    private AxisView axisX;
    //y轴注册
    private AxisView axisY;
    //左侧位置
    private int width = 0;
    private int height = 0;
    //y最大值
    private float manualMaxYValue = 256;
    //y最小值
    private float manualMinYValue = 0;
    //x最大值
    private float manualMaxXValue = 200;
    //x最小值
    private float manualMinXValue = 0;
    //暂停
    private boolean isShow = true;
    //已经进行移动
    private boolean isMove = false;
    //已经进行变换
    private boolean isTranslate = false;

    //final boolean suspendShowFlag = (Ch1.isShow() | Ch2.isShow()) & (!isShow);
    //创建
    SeriesViewUpdate(SurfaceHolder Holder) {
        setSurfaceHolder(Holder);
        SeriesChannel Ch1;
        SeriesChannel Ch2;
        Ch1 = new SeriesChannel();
        Ch2 = new SeriesChannel(MainActivity.getmContext().getResources().getColor(R.color.holo_orange_light));
        Ch2.setOffset(20);
        Ch1.setLevel(1);
        channelList = new ArrayList<SeriesChannel>();
        channelList.add(0, Ch1);
        channelList.add(1, Ch2);
        mHandler = new Handler() {
            /**
             * 消息接收函数
             *
             * @param msg 接收其他线程的更新或者控制数据
             */
            public void handleMessage(Message msg) {
                Bundle bundle;
                byte[] temp_byte;
                int[] temp_int;
                lockData.lock();
                switch (msg.what) {
                    case DefinedMessages.ADD_NEW_DATA_CH1:
                        bundle = msg.getData();
                        temp_byte = bundle.getByteArray("str");
                        temp_int = ByteArrayFunction.BytesToInt(temp_byte);
                        channelList.get(0).addData(temp_int);
                        break;
                    case DefinedMessages.ADD_NEW_DATA_CH2:
                        bundle = msg.getData();
                        temp_byte = bundle.getByteArray("str");
                        temp_int = ByteArrayFunction.BytesToInt(temp_byte);
                        channelList.get(1).addData(temp_int);
                        break;
                }
                HANDLE_COUNT++;
                super.handleMessage(msg);
                lockData.unlock();
            }
        };
    }

    public void setContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean showFlag) {
        this.isShow = showFlag;
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

    //Y缩放
    public void setScalingY(float scalingY, float startpos) {
        try {
            lockAxis.tryLock(500, TimeUnit.MILLISECONDS);
            float realPos = startpos / height;
            float PosValue = this.manualMaxYValue * (1 - realPos) + this.manualMinYValue * realPos;
            manualMaxYValue = (manualMaxYValue - PosValue) / scalingY + PosValue;
            manualMinYValue = (manualMinYValue - PosValue) / scalingY + PosValue;
            isMove = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setScalingY", e.toString());
        } finally {
            lockAxis.unlock();
        }
        lockAxis.unlock();
    }

    //X缩放
    public void setScalingX(float scalingX, float startpos) {
        try {
            lockAxis.tryLock(500, TimeUnit.MILLISECONDS);
            float realPos = startpos / width;
            float PosValue = this.manualMaxXValue * (1 - realPos) + this.manualMinXValue * realPos;
            manualMaxXValue = (manualMaxXValue - PosValue) / scalingX + PosValue;
            manualMinXValue = (manualMinXValue - PosValue) / scalingX + PosValue;
            isMove = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setScalingX", e.toString());
        } finally {
            lockAxis.unlock();
        }
    }
    //设置x轴
    public void setAxisX(AxisView axisX) {
        this.axisX = axisX;
    }

    //设置y轴
    public void setAxisY(AxisView axisY) {
        this.axisY = axisY;
    }

    /**
     * 设置x轴移动
     *
     * @param moveX
     */
    public void setMoveX(int moveX) {
        try {
            lockAxis.tryLock(500, TimeUnit.MILLISECONDS);
            float size = manualMaxXValue - manualMinXValue;
            manualMaxXValue -= moveX * size / width;
            manualMinXValue -= moveX * size / width;
            isMove = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setMoveX", e.toString());
        } finally {
            lockAxis.unlock();
        }
    }

    /**
     * 设置Y轴移动
     *
     * @param moveY
     */
    public void setMoveY(int moveY) {
        try {
            lockAxis.tryLock(500, TimeUnit.MILLISECONDS);
        //this.moveY += moveY;
        float size = manualMaxYValue - manualMinYValue;
        this.manualMaxYValue += moveY * size / height;
        this.manualMinYValue += moveY * size / height;
        isMove = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setMoveY", e.toString());
        } finally {
            lockAxis.unlock();
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
                canvas = surfaceHolder.lockCanvas(new Rect(0, 0, width, height));
                if (canvas != null) {
                    if (!(channelList.get(0).isShow() | channelList.get(1).isShow())) {
                        clear(canvas);
                        if (width != 0) {
                            DrawText(canvas, Color.rgb(0xff, 0x44, 0x44), MainActivity.getmContext().getString(R.string.no_source_in));
                        }
                    } else if (isShow()) {
                        clear(canvas);
                        isTranslate = false;
                        if (lockAxis.tryLock()) {
                            try {
                                for (SeriesChannel channelX : channelList) {

                                    if (channelX.isShow() && channelX.getData() != null && (channelX.getData().size() > 0)) {
                                        DrawLines(canvas, channelX);
                                    }
                                }
                            } finally {
                                lockAxis.unlock();
                            }
                        }
                    } else {
                        clear(canvas);
                        DrawText(canvas, Color.rgb(0x00, 0x99, 0xcc), MainActivity.getmContext().getString(R.string.menu_open));
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } catch (Exception e) {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                Log.i("Surface Exception", e.toString());
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
     * @param chx
     * @param canvas
     */
    private void DrawLines(Canvas canvas, SeriesChannel chx) {
        final float densityX = (manualMaxXValue - manualMinXValue) / width;
        final int FIX_SIZE = (int) (DensityUtil.dip2px(MainActivity.getmContext(), 3) * densityX + 1);
        final float FIX_LENGTH = (FIX_SIZE / densityX);
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(chx.getCurveColor());
        paint.setStrokeWidth(DensityUtil.dip2px(MainActivity.getmContext(), 2));
        paint.setAlpha(220);
        paint.setAntiAlias(true);
        lockData.lock();
        //快速修正后
        Integer[] afterFix = chx.getFastFix(FIX_SIZE);
        lockData.unlock();
        //防止越界
        float x_lenth = FIX_LENGTH * (afterFix.length / 2);
        //偏移
        if (!isTranslate) {
            if (x_lenth < width) {
                Log.i("x_lenth < width", x_lenth + "");
                manualMaxXValue -= manualMinXValue;
                manualMinXValue = 0f;
            } else if (manualMaxXValue * width / (manualMaxXValue - manualMinXValue) > x_lenth) {
                Log.i("manualMaxXValue>x_lenth", manualMaxXValue * width / (manualMaxXValue - manualMinXValue) + "");
                Log.i("x_lenth", x_lenth + "");
                float temp = (manualMaxXValue - manualMinXValue);
                manualMinXValue = manualMinXValue - manualMaxXValue + x_lenth * temp / width;
                manualMaxXValue = x_lenth * temp / width;
            } else if (manualMinXValue < 0) {
                Log.i("manualMinXValue < 0", manualMinXValue + "");
                manualMaxXValue -= manualMinXValue;
                manualMinXValue = 0f;
            }
        }
        final float sizeX = manualMaxXValue - manualMinXValue;
        final float sizeY = manualMaxYValue - manualMinYValue;
        if (!isTranslate && isMove) {
            //测试显示坐标
            if (axisX != null && axisX.getLabel() != null) {
                int lenth = axisX.getLabel().size();
                float stepX = sizeX / (lenth - 1);
                ArrayList<String> tempLabel = new ArrayList<String>();
                for (int i = 0; i < lenth; i++) {
                    tempLabel.add((int) (manualMinXValue + i * stepX) + "");
                }
                axisX.setLabel(tempLabel);
                axisX.postInvalidate();
            }
            //测试显示坐标
            if (axisY != null && axisY.getLabel() != null) {
                int lenth = axisY.getLabel().size();
                float stepY = sizeY / (lenth - 1);
                ArrayList<String> tempLabel = new ArrayList<String>();
                for (int i = 0; i < lenth; i++) {
                    tempLabel.add((int) (manualMaxYValue - i * stepY) + "");
                }
                axisY.setLabel(tempLabel);
                axisY.postInvalidate();
            }
            isMove = false;
        }
        //偏移
        if (!isTranslate) {
            canvas.translate(-manualMinXValue * width / sizeX, manualMinYValue * height / sizeY);
            canvas.clipRect(manualMinXValue * width / sizeX, -manualMinYValue * height / sizeY, width + manualMinXValue * width / sizeX, height - manualMinYValue * height / sizeY);
            isTranslate = true;
        }
        float startX = 0;
        float startY = height - (afterFix[0]) * height / sizeY * chx.getLevel();
        float endX = 1;
        float endY = height - (afterFix[1]) * height / sizeY * chx.getLevel();
        for (int i = 1; i < afterFix.length / 2; i++) {
            //如果有峰峰值大于阀值
            if (Math.abs(afterFix[i - 1] - afterFix[afterFix.length / 2 + i - 1]) > 100) {
                float temp_start = height - afterFix[i] * height / sizeY * chx.getLevel();
                float temp_end = height - afterFix[afterFix.length / 2 + i] * height / sizeY * chx.getLevel();
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
                endY = height - afterFix[i + afterFix.length / 2] * height / sizeY * chx.getLevel();
            }
            //正常绘图
            else {
                canvas.drawLine(FIX_LENGTH * startX, startY, FIX_LENGTH * endX, endY, paint);
            }
            startX = i;
            startY = endY;
            if (i < afterFix.length / 2 - 1) {
                endX = i + 1;
                endY = height - (afterFix[i + 1]) * height / sizeY * chx.getLevel();
            }
        }
    }
    private void DrawText(Canvas canvas, int color, String string) {
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);
        paint.setTextSize(DensityUtil.dip2px(MainActivity.getmContext(), 50));
        //paint.setAntiAlias(true);
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

    public ArrayList<SeriesChannel> getChannelList() {
        return channelList;
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


class SeriesChannel {
    //暂停
    private boolean isShow = false;
    //扫描模式
    private boolean isScan = false;
    //显示最大值
    private boolean isShowMax = false;
    private int curveColor;
    private int offset;
    private int dataCount = 0;
    private int myStep = 0;
    private float level = 1;
    private Integer[] afterFixData = null;
    private int afterFixDataCount = 0;
    private ArrayList<Integer> BaseData = null;

    SeriesChannel() {
        this(MainActivity.getmContext().getResources().getColor(R.color.holo_blue_light));
    }

    SeriesChannel(int color) {
        curveColor = color;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isshow) {
        this.isShow = isshow;
    }

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean isScan) {
        this.isScan = isScan;
    }

    public boolean isShowMax() {
        return isShowMax;
    }

    public void setShowMax(boolean isShowMax) {
        this.isShowMax = isShowMax;
    }

    public void addData(int[] data) {
        BaseData = new ArrayList<Integer>();
        for (int x : data) {
            BaseData.add(x);
        }
        dataCount++;
    }

    public ArrayList<Integer> getData() {
        return BaseData;
    }

    public int getCurveColor() {
        return curveColor;
    }

    public void setCurveColor(int curveColor) {
        this.curveColor = curveColor;
    }

    public Integer[] getFastFix(int step) {
        if (myStep != step || afterFixDataCount != dataCount) {
            afterFixDataCount = dataCount;
            myStep = step;
            afterFixData = FastFix(BaseData.toArray(new Integer[0]), step);
            return afterFixData;
        } else {
            return afterFixData;
        }
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
                data_out[i] = data_out[count + i] = data[i] + getOffset();
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
                    data_out[i] = data[min] + getOffset();
                    data_out[count + i] = data[max] + getOffset();
                } else {
                    data_out[i] = data[max] + getOffset();
                    data_out[count + i] = data[min] + getOffset();
                }
            }
        }
        return data_out;
    }

    private int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }
}