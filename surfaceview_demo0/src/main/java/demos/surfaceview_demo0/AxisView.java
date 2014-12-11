package demos.surfaceview_demo0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by lvniqi on 2014-12-07.
 */

/**
 * Axis_Label
 * 坐标轴类
 *
 * @see demos.surfaceview_demo0.AxisView
 */
class AxisView extends View {
    boolean isX;
    private ArrayList<Float> axis;
    private ArrayList<String> label;
    private Paint paint;
    private float otherSide;

    /**
     * 构造函数
     *
     * @param context
     */
    AxisView(Context context) {
        super(context);
        paint = new Paint();
        paint.setTextSize(30);
        paint.setColor(Color.WHITE);
    }

    /**
     * 得到坐标值符串
     *
     * @return label
     */
    public ArrayList<String> getLabel() {
        return label;
    }

    /**
     * 设置坐标值符串
     *
     * @param label
     */
    public void setLabel(ArrayList<String> label) {
        this.label = label;
    }

    /**
     * 得到坐标位置
     *
     * @return
     */
    public ArrayList<Float> getAxis() {
        return axis;
    }

    /**
     * 设置坐标位置
     *
     * @param axis
     */
    public void setAxis(ArrayList<Float> axis) {
        this.axis = axis;
    }

    /**
     * 得到画笔颜色
     *
     * @return paint
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * 设置画笔颜色
     *
     * @param paint
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean getisX() {
        return isX;
    }

    public void setisX(boolean isX) {
        this.isX = isX;
    }

    public float getOtherSide() {
        return otherSide;
    }

    public void setOtherSide(float otherSide) {
        this.otherSide = otherSide;
    }

    public void DrawAxis(Canvas canvas, float otherSide, boolean isX) {
        if (isX) {
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(label.get(0), axis.get(0), otherSide, paint);
            paint.setTextAlign(Paint.Align.CENTER);
            for (int i = 1; i < axis.size() - 1; i++) {

                canvas.drawText(label.get(i), axis.get(i), otherSide, paint);
            }
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(label.get(axis.size() - 1), axis.get(axis.size() - 1), otherSide, paint);
        } else {
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(label.get(0), otherSide, axis.get(0) + paint.getTextSize() / 2, paint);
            for (int i = 1; i < axis.size(); i++) {
                canvas.drawText(label.get(i), otherSide, axis.get(i), paint);
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getAxis().size() != 0 && getLabel().size() != 0) {
            DrawAxis(canvas, this.getOtherSide(), this.getisX());
        }
    }
}