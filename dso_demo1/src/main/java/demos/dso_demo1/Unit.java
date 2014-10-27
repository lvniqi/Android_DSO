package demos.dso_demo1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lvniqi on 2014/10/27.
 */
public class Unit extends View{
    protected float scale;
    //宽度
    private int width;
    //高度
    private int height;
    //画笔
    private Paint paint;
    //初始化
    public Unit(Context context, AttributeSet attrs){
        super(context, attrs);

        // Create paint

        paint = new Paint();

        // Set initial scale

        scale = 1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        // Get dinemsions

        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas){
        paint.setStrokeWidth(2);

        // Draw half a tick

        canvas.drawLine(width, 0, width, height / 2, paint);

        // Set up paint

        paint.setAntiAlias(true);
        paint.setTextSize(height * 2 / 3);
        paint.setTextAlign(Paint.Align.CENTER);

        // Draw half of the units

        if (scale < 100.0)
            canvas.drawText("ms", width, height - (height / 8), paint);

        else
            canvas.drawText("sec", width, height - (height / 8), paint);
    }
}
