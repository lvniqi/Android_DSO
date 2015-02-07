package demos.surfaceview_demo0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by lvniqi on 2014/12/2.
 */
public class GridView extends View {
    private static int getwidth_flag = 0;
    private Grid mygrid;
    private ArrayList<SeriesViewUpdate> seriesViewUpdates;

    GridView(Context context) {
        super(context);
        mygrid = new Grid();
        seriesViewUpdates = new ArrayList<SeriesViewUpdate>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mygrid.setWidth(getWidth());
        mygrid.setHeight(getHeight());
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            temp.add(i + "ms");
        }
        //初始化x轴字符
        if (mygrid.getxAxis().getLabel() == null) {
            mygrid.getxAxis().setLabel(temp);
            mygrid.getxAxis().setisX(true);
        }
        if (mygrid.getyAxis().getLabel() == null) {
            mygrid.getyAxis().setLabel(temp);
            mygrid.getyAxis().setisX(false);
        }
        for (SeriesViewUpdate x : seriesViewUpdates) {
            x.setWidth(mygrid.getWidth());
            x.setHeight(mygrid.getHeight());
            x.setAxisX(mygrid.getxAxis());
            x.setAxisY(mygrid.getyAxis());
        }
        mygrid.DrawRect(canvas);
        mygrid.DrawGrid(canvas);
    }


    public ArrayList<SeriesViewUpdate> getSeriesViewUpdates() {
        return seriesViewUpdates;
    }


    public Grid getGrid() {
        return mygrid;
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
    private AxisView xAxis;
    private AxisView yAxis;

    Grid(int left, int top, int width, int height) {
        super(left, top, width, height);
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.argb(0x33, 0x99, 0x99, 0x99));
    }

    Grid(Rect rect) {
        super(rect);
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.argb(0x33, 0x99, 0x99, 0x99));
    }

    Grid() {
        super();
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.argb(0x33, 0x99, 0x99, 0x99));
    }

    public AxisView getxAxis() {
        return xAxis;
    }

    public void setxAxis(AxisView xAxis) {
        this.xAxis = xAxis;
    }

    public AxisView getyAxis() {
        return yAxis;
    }

    public void setyAxis(AxisView yAxis) {
        this.yAxis = yAxis;
    }

    public void setGirdPoint(Paint paint) {
        this.gPaint = paint;
    }

    public int getGridColor() {
        return gPaint.getColor();
    }

    public void setGridColor(int color) {
        gPaint.setColor(color);
    }

    public Paint GeGridtPoint() {
        return gPaint;
    }

    public void setDiv(int div) {
        this.div = div;
    }


    /**
     * 绘制外边框
     *
     * @param canvas
     */
    public void DrawRect(Canvas canvas) {
        float paintWidth = DensityUtil.dip2px(MainActivity.getmContext(), (float) 1.5);
        gPaint.setStrokeWidth(paintWidth);
        gPaint.setAlpha(255);
        //外边框
        canvas.drawLine(0, paintWidth / 2, width, paintWidth / 2, gPaint);
        canvas.drawLine(paintWidth / 2, 0, paintWidth / 2, height, gPaint);
        canvas.drawLine(width - paintWidth / 2, 0, width - paintWidth / 2, height, gPaint);
        canvas.drawLine(0, height - paintWidth / 2, width, height - paintWidth / 2, gPaint);

    }

    public void DrawGrid(Canvas canvas) {
        float paintWidth = DensityUtil.dip2px(MainActivity.getmContext(), (float) 0.8);
        gPaint.setStrokeWidth(paintWidth);
        gPaint.setAlpha(100);
        //纵向线
        ArrayList<Float> xList = new ArrayList<Float>();
        ArrayList<Float> yList = new ArrayList<Float>();
        float div_x = ((float) width) / div;
        xList.add((float) 0);
        for (float x = div_x; x < width; x += div_x) {
            xList.add(x);
            canvas.drawLine(x, 0, x, height, gPaint);
        }
        xList.add((float) width);
        //横向线
        yList.add((float) 0);
        float div_y = ((float) height) / div;
        for (float y = div_y; y < height; y += div_y) {
            yList.add(y);
            canvas.drawLine(0, y, width, y, gPaint);
        }
        yList.add((float) (height));
        xAxis.setAxis(xList);
        yAxis.setAxis(yList);
    }

    public void DrawPartGrid(int left, int top, int width, int height, Canvas canvas) {
        gPaint.setStrokeWidth(1);
        gPaint.setAlpha(100);
        //横向间隔
        float div_x = ((float) this.width) / div;
        //纵向间隔
        float div_y = ((float) this.height) / div;
        float temp_start_x = div_x;
        float temp_start_y = div_y;
        //输入错误检查
        if (left > this.width) {
            return;
        }
        if (width + left > this.width) {
            width = this.width - left;
        }
        if (top > this.height) {
            return;
        }
        if (top + height > this.height) {
            height = this.height;
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
            canvas.drawLine(left, temp_start_y, left + width, temp_start_y, gPaint);
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
    //背景画笔
    private Paint bgPaint;

    BackGround(int left, int top, int width, int height) {
        this.width = width;
        this.height = height;
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.BLACK);
    }

    BackGround(Rect rect) {
        this.width = rect.right - rect.left;
        this.height = rect.bottom - rect.top;
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.BLACK);
    }

    BackGround() {
        this.width = 0;
        this.height = 0;
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.WHITE);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBgPoint(Paint paint) {
        this.bgPaint = paint;
    }

    public int getBgColor() {
        return bgPaint.getColor();
    }

    public void setBgColor(int color) {
        bgPaint.setColor(color);
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
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        } else {
            canvas.drawRect(0, 0, width, height, bgPaint);
        }
    }

    public void DrawPartBackground(int left, int top, int width, int height, Canvas canvas) {
        //输入错误检查
        if (left > this.width) {
            return;
        }
        if (width + left > this.width) {
            width = this.width - left;
        }
        if (top > this.height) {
            return;
        }
        if (top + height > this.height) {
            height = this.height;
        }
        canvas.drawRect(left, top, width + left, height + top, bgPaint);
    }
}
