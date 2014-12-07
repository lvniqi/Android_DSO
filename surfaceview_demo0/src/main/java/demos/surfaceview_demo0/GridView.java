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
    private Grid mygrid;

    GridView(Context context) {
        super(context);
        mygrid = new Grid();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mygrid.setTop(100);
        mygrid.setLeft(100);
        mygrid.setWidth(getWidth() - 200);
        mygrid.setHeight(getHeight() - 200);
        mygrid.setBorder(50);
        mygrid.DrawRect(canvas);
        mygrid.DrawGrid(canvas);
        Paint paint = new Paint();
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            temp.add(i + "ms");
        }
        mygrid.getxAxis().setLabel(temp);
        mygrid.getxAxis().setisX(true);
        mygrid.getxAxis().setOtherSide(mygrid.GetTop() + mygrid.GetHeight() + mygrid.getxAxis().getPaint().getTextSize());
        //mygrid.getxAxis().postInvalidate();
        mygrid.getyAxis().setLabel(temp);
        mygrid.getyAxis().DrawAxis(canvas, mygrid.GetLeft(), false);
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
    private int border = 10;

    Grid(int left, int top, int width, int height) {
        super(left, top, width, height);
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.WHITE);
    }

    Grid(Rect rect) {
        super(rect);
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.WHITE);
    }

    Grid() {
        super();
        gPaint = new Paint();
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setColor(Color.WHITE);
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

    public void setGridColor(int color) {
        gPaint.setColor(color);
    }

    public void setGirdPoint(Paint paint) {
        this.gPaint = paint;
    }

    public int GetGridColor() {
        return gPaint.getColor();
    }

    public Paint GeGridtPoint() {
        return gPaint;
    }

    public void setDiv(int div) {
        this.div = div;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
        this.setHeight(this.GetHeight() - border);
    }

    /**
     * 绘制外边框
     *
     * @param canvas
     */
    public void DrawRect(Canvas canvas) {
        gPaint.setStrokeWidth(2);
        gPaint.setAlpha(255);
        //外边框
        canvas.drawLine(left, top, left + width, top, gPaint);
        canvas.drawLine(left, top + height, left + width, top + height, gPaint);
        canvas.drawLine(left, top, left, top + height, gPaint);
        canvas.drawLine(left + width, top, left + width, top + height, gPaint);
    }

    public void DrawXlabel(Canvas canvas) {
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
        ArrayList<Float> xList = new ArrayList<Float>();
        ArrayList<Float> yList = new ArrayList<Float>();
        float div_x = ((float) width) / div;
        xList.add((float) left);
        for (float x = left + div_x; x < left + width; x += div_x) {
            xList.add(x);
            canvas.drawLine(x, top, x, top + height, gPaint);
        }
        xList.add((float) (left + width));
        //横向线
        yList.add((float) top);
        float div_y = ((float) height) / div;
        for (float y = top + div_y; y < top + height; y += div_y) {
            yList.add(y);
            canvas.drawLine(left, y, left + width, y, gPaint);
        }
        yList.add((float) (top + height));
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
        this.left = 0;
        this.top = 0;
        this.width = 0;
        this.height = 0;
        bgPaint = new Paint();
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.WHITE);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setTop(int top) {
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

    public void setBgColor(int color) {
        bgPaint.setColor(color);
    }

    public void setBgPoint(Paint paint) {
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
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
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
