package demos.surfaceview_demo0;

/**
 * Created by lvniqi on 2014/11/10.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static demos.surfaceview_demo0.ByteArrayFunction.BytesToArrayInt;


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
    ReentrantReadWriteLock lockAxis = new ReentrantReadWriteLock();
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
        Ch2.setShowMax(true);
        Ch2.setShowMin(true);
        Ch2.setSignPos(4);
        //放大10倍
        Ch1.setLevel(10f);
        //下移20个单位20/256*10 = =0.78V
        Ch1.setOffset(-20);
        channelList = new ArrayList<SeriesChannel>(2);
        channelList.add(0, Ch1);
        channelList.add(1, Ch2);
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
            if (lockAxis.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    if (scalingY > 0) {
                        float realPos = startpos / height;
                        float PosValue = this.manualMaxYValue * (1 - realPos) + this.manualMinYValue * realPos;
                        manualMaxYValue = (manualMaxYValue - PosValue) / scalingY + PosValue;
                        manualMinYValue = (manualMinYValue - PosValue) / scalingY + PosValue;
                        isMove = true;
                    }
                } finally {
                    lockAxis.writeLock().unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setScalingY", e.toString());
        }
    }

    //X缩放
    public void setScalingX(float scalingX, float startpos) {
        try {
            if (lockAxis.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    if (scalingX > 0) {
                        float realPos = startpos / width;
                        float PosValue = this.manualMaxXValue * (1 - realPos) + this.manualMinXValue * realPos;
                        manualMaxXValue = (manualMaxXValue - PosValue) / scalingX + PosValue;
                        manualMinXValue = (manualMinXValue - PosValue) / scalingX + PosValue;
                        isMove = true;
                    }
                } finally {
                    lockAxis.writeLock().unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setScalingX", e.toString());
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
            if (lockAxis.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    float size = manualMaxXValue - manualMinXValue;
                    manualMaxXValue -= moveX * size / width;
                    manualMinXValue -= moveX * size / width;
                    isMove = true;
                } finally {
                    lockAxis.writeLock().unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setMoveX", e.toString());
        }
    }

    /**
     * 设置Y轴移动
     *
     * @param moveY
     */
    public void setMoveY(int moveY) {
        try {
            if (lockAxis.writeLock().tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    float size = manualMaxYValue - manualMinYValue;
                    this.manualMaxYValue += moveY * size / height;
                    this.manualMinYValue += moveY * size / height;
                    isMove = true;
                } finally {
                    lockAxis.writeLock().unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("setMoveY", e.toString());
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
            //未启动时 睡眠
            if (width == 0 || height == 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.i("Surface sleep", e.toString());
                }
            } else {
                Canvas canvas = null;
                isMove = true;
                try {
                    canvas = surfaceHolder.lockCanvas(new Rect(0, 0, width, height));
                    if (canvas != null) {
                        if (!(channelList.get(0).isShow() || channelList.get(1).isShow())) {
                            clear(canvas);
                            if (width != 0) {
                                DrawText(canvas, Color.rgb(0xff, 0x44, 0x44), MainActivity.getmContext().getString(R.string.no_source_in));
                            }
                        } else if (isShow()) {
                            clear(canvas);
                            isTranslate = false;
                            if (lockAxis.readLock().tryLock()) {
                                try {
                                    for (SeriesChannel channelX : channelList) {

                                        if (channelX.isShow() && channelX.getData() != null) {
                                            DrawLines(canvas, channelX);
                                        }
                                    }
                                } finally {
                                    lockAxis.readLock().unlock();
                                }
                            }
                        } else {
                            clear(canvas);
                            DrawText(canvas, Color.rgb(0x00, 0x99, 0xcc), MainActivity.getmContext().getString(R.string.menu_open));
                        }
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        Thread.sleep(5);
                    }
                } catch (Exception e) {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    Log.i("Surface Exception", e.toString());
                }

            }
        }
        Log.i("SeriesViewSeries", "SeriesViewSeries Destroyed");
        this.width = 0;
        this.height = 0;
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
        final int FIX_SIZE = (int) (DensityUtil.dip2px(MainActivity.getmContext(), 2) * densityX + 1);
        final float FIX_LENGTH = (FIX_SIZE / densityX);
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(chx.getCurveColor());
        paint.setStrokeWidth(DensityUtil.dip2px(MainActivity.getmContext(), 2));
        paint.setAlpha(220);
        paint.setAntiAlias(true);
        //快速修正后
        Integer[] afterFix = chx.getFastFix(FIX_SIZE);
        //防止越界
        final float lenX = FIX_LENGTH * (afterFix.length / 2 - 1);
        //偏移
        if (!isTranslate) {
            if (lenX < width) {
                Log.i("lenX < width", lenX + "");
                //非扫描模式
                if (!chx.isScan()) {
                    manualMaxXValue = lenX * densityX;
                    manualMinXValue = 0f;
                } else {
                    manualMaxXValue -= manualMinXValue;
                    manualMinXValue = 0f;
                }
            } else if (manualMaxXValue * width / (manualMaxXValue - manualMinXValue) > lenX) {
                Log.i("manualMaxXValue>lenX", manualMaxXValue * width / (manualMaxXValue - manualMinXValue) + "");
                Log.i("lenX", lenX + "");
                float temp = (manualMaxXValue - manualMinXValue);
                manualMinXValue = manualMinXValue - manualMaxXValue + lenX * temp / width;
                manualMaxXValue = lenX * temp / width;
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
        int _x0 = (int) (manualMinXValue * width / sizeX / FIX_LENGTH - 1);
        final int x0 = _x0 > 0 ? _x0 : 0;
        int _x1 = (int) ((manualMinXValue * width / sizeX + width) / FIX_LENGTH + 2);
        final int x1 = _x1 <= afterFix.length / 2 ? _x1 : afterFix.length / 2;
        float startX = x0 * FIX_LENGTH;
        float startY = height - (afterFix[x0]) * height / sizeY * chx.getLevel();
        float endX = (x0 + 1) * FIX_LENGTH;
        float endY = height - (afterFix[x0 + 1]) * height / sizeY * chx.getLevel();
        for (int i = x0 + 1; i < x1; i++) {
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
                    canvas.drawRect(startX,
                            temp_start,
                            endX,
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
                    canvas.drawRect(startX,
                            temp_end,
                            endX,
                            temp_start,
                            paint);
                }
                endY = height - afterFix[i + afterFix.length / 2] * height / sizeY * chx.getLevel();
            }
            //正常绘图
            else {
                canvas.drawLine(startX, startY, endX, endY, paint);
            }
            startX = endX;
            startY = endY;
            if (i < afterFix.length / 2 - 1) {
                endX = FIX_LENGTH * (i + 1);
                endY = height - (afterFix[i + 1]) * height / sizeY * chx.getLevel();
            }
        }
        //如果显示最大值
        if (chx.isShowMax()) {
            float temp = height - chx.getValueMax() * height / sizeY * chx.getLevel();
            float len = DensityUtil.dip2px(MainActivity.getmContext(), 15);
            for (int j = (int) (manualMinXValue * width / sizeX - 1); j < manualMinXValue * width / sizeX + width + 1; j += len) {
                canvas.drawLine(j, temp, (float) j + len / 5, temp, paint);
            }
        }
        //如果显示最小值
        if (chx.isShowMin()) {
            float temp = height - chx.getValueMin() * height / sizeY * chx.getLevel();
            float len = DensityUtil.dip2px(MainActivity.getmContext(), 15);
            for (int j = (int) (manualMinXValue * width / sizeX - 1); j < manualMinXValue * width / sizeX + width + 1; j += len) {
                canvas.drawLine(j, temp, (float) j + len / 5, temp, paint);
            }
        }
        //如果显示标志
        if (chx.isSign()) {
            final float size = DensityUtil.dip2px(MainActivity.getmContext(), 10);
            final float y_max = height - chx.getValueMin() * height / sizeY * chx.getLevel();
            final float y_min = height - chx.getValueMax() * height / sizeY * chx.getLevel();
            final float axismax = height - manualMinYValue * height / sizeY;
            final float axismin = height - manualMaxYValue * height / sizeY;
            if (y_min >= axismax - 1) {
                Path path = new Path();
                path.moveTo(manualMinXValue * width / sizeX + width / 2 - size * (chx.getSignPos() - 1), axismax - size);// 此点为多边形的起点
                path.lineTo(manualMinXValue * width / sizeX + width / 2 - size * chx.getSignPos(), axismax);
                path.lineTo(manualMinXValue * width / sizeX + width / 2 - size * (chx.getSignPos() + 1), axismax - size);
                path.close(); // 使这些点构成封闭的多边形
                canvas.drawPath(path, paint);
            } else if (y_max <= axismin + 1) {
                Path path = new Path();
                path.moveTo(manualMinXValue * width / sizeX + width / 2 - size * (chx.getSignPos() - 1), axismin + size);// 此点为多边形的起点
                path.lineTo(manualMinXValue * width / sizeX + width / 2 - size * chx.getSignPos(), axismin);
                path.lineTo(manualMinXValue * width / sizeX + width / 2 - size * (chx.getSignPos() + 1), axismin + size);
                path.close(); // 使这些点构成封闭的多边形
                canvas.drawPath(path, paint);
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
    //显示最小值
    private boolean isShowMin = false;
    //标记位置
    private boolean isSign = true;
    //标记位置
    private int signPos = 1;
    //曲线颜色
    private int curveColor;
    //偏移量
    private int offset;
    //得到数据计数值
    private int dataCount = 0;
    //快速绘线步进
    private int myStep = 0;
    //衰减值
    private float level = 1;
    //曲线最大值
    private int valueMax = 0;
    //曲线最小值
    private int valueMin = 0;
    //快速绘线后数据
    private Integer[] afterFixData = null;
    //快速绘线后数据计数值
    private int afterFixDataCount = 0;
    //原始数据
    private ArrayList<Integer> BaseData = null;
    //数据锁
    private ReadWriteLock lockData = new ReentrantReadWriteLock();

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

    public boolean isShowMin() {
        return isShowMin;
    }

    public void setShowMin(boolean isShowMin) {
        this.isShowMin = isShowMin;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean isSign) {
        this.isSign = isSign;
    }

    public int getSignPos() {
        return signPos;
    }

    public void setSignPos(int signPos) {
        this.signPos = signPos;
    }

    public ArrayList<Integer> getData() {
        return BaseData;
    }

    public void setData(ArrayList<Integer> data) {
        if (lockData.writeLock().tryLock()) {
            BaseData = data;
            dataCount++;
            lockData.writeLock().unlock();
        }
    }

    public void setData(byte[] data) {
        if (lockData.writeLock().tryLock()) {
            /*int[] dataInt = byte2int(data);
            ArrayList<Integer> temp2;
            Integer[] dataInteger = new Integer[dataInt.length];
            for (int i = 0; i < dataInt.length; i++) {
                dataInteger[i] = dataInt[i];
            }
            BaseData = new ArrayList(Arrays.asList(dataInteger));
            dataCount++;*/
            BaseData = BytesToArrayInt(data, 5000);
            dataCount++;
            lockData.writeLock().unlock();
        }
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
        lockData.readLock().lock();
        if (step < 1) {
            step = 1;
        }
        int count = data.length / step;
        Integer[] data_out = new Integer[count * 2];
        valueMax = data[0];
        valueMin = data[0];
        if (step < 2) {
            for (int i = 0; i < count; i++) {
                data_out[i] = data_out[count + i] = data[i] + getOffset();
                if (data[i] > valueMax) {
                    valueMax = data[i];
                } else if (data[i] < valueMin) {
                    valueMin = data[i];
                }
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
                if (data[max] > valueMax) {
                    valueMax = data[max];
                } else if (data[min] < valueMin) {
                    valueMin = data[min];
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
        lockData.readLock().unlock();
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

    public int getValueMax() {
        return valueMax + getOffset();
    }

    public int getValueMin() {
        return valueMin + getOffset();
    }
}