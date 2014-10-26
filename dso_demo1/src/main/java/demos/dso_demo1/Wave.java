package demos.dso_demo1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lvniqi on 2014/10/26.
 */
public class Wave extends View {
    //宽度
    private int width;
    //高度
    private int height;
    //曲线
    private Path path;
    //画笔
    private Canvas cb;
    //画笔
    private Paint paint;
    //位图
    private Bitmap bitmap;
    //位图 方格
    private Bitmap graticule;
    //主界面
    protected MainActivity main;
    //数据输入
    protected MainActivity.Data_In data_in;
    //数据保存标志
    protected boolean storage;
    //数据清除标志
    protected boolean clear;
    //步进
    protected float step;
    //步进
    protected float scale;
    protected float start;
    protected float index;
    //构造函数
    public Wave(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create path and paint

        path = new Path();
        paint = new Paint();
    }


    //窗口大小改变了
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        // 得到大小

        width = w;
        height = h;

        //放置一个位图用于跟踪存储

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        cb = new Canvas(bitmap);

        //放置一个位图用于格子线

        graticule = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);

        //制造一个画布
        Canvas canvas = new Canvas(graticule);

        // 背景为黑色
        canvas.drawColor(Color.BLACK);
        // Set up paint
        //线宽为2
        paint.setStrokeWidth(2);
        //空心线
        paint.setStyle(Paint.Style.STROKE);
        //颜色不透明 绿色
        paint.setColor(Color.argb(255, 0, 63, 0));

        //画y平行线
        for (int i = 0; i < width; i += MainActivity.SIZE)
            canvas.drawLine(i, 0, i, height, paint);
        //坐标移至左侧中点
        canvas.translate(0, height / 2);
        //画x平行线
        for (int i = 0; i < height / 2; i += MainActivity.SIZE)
        {
            canvas.drawLine(0, i, width, i, paint);
            canvas.drawLine(0, -i, width, -i, paint);
        }

        // Draw the graticule on the bitmap
        //放置位图
        cb.drawBitmap(graticule, 0, 0, null);
        //坐标移至0点
        cb.translate(0, height / 2);
    }
    private int max;


    //绘制视图本身
    @SuppressLint("DefaultLocale")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // 确定有数据

        if ((data_in == null))
        {
            canvas.drawBitmap(graticule, 0, 0, null);
            return;
        }

        // Draw the graticule on the bitmap

        if (!storage || clear)
        {
            cb.drawBitmap(graticule, 0, -height / 2, null);
            clear = false;
        }

        // Calculate x scale etc

        float xscale = (float)(2.0 / ((data_in.sample / 100000.0) * scale));
        int xstart = Math.round(start);
        int xstep = Math.round((float)1.0 / xscale);
        int xstop = Math.round(xstart + ((float)width / xscale));

        if (xstop > data_in.length)
            xstop = (int)data_in.length;

        // Calculate y scale

        if (max < 4096)
            max = 4096;

        float yscale = (float)(max / (height / 2.0));

        max = 0;

        // Draw the trace

        path.rewind();
        path.moveTo(0, 0);

        if (xscale < 1.0)
        {
            for (int i = 0; i < xstop - xstart; i += xstep)
            {
                if (max < Math.abs(data_in.data[i + xstart]))
                    max = Math.abs(data_in.data[i + xstart]);

                float x = (float)i * xscale;
                float y = -(float)data_in.data[i + xstart] / yscale;
                path.lineTo(x, y);
            }
        }

        else
        {
            for (int i = 0; i < xstop - xstart; i++)
            {
                if (max < Math.abs(data_in.data[i + xstart]))
                    max = Math.abs(data_in.data[i + xstart]);

                float x = (float)i * xscale;
                float y = -(float)data_in.data[i + xstart] / yscale;
                path.lineTo(x, y);

                // Draw points at max resolution

                if (main.timebase == 0)
                {
                    path.addRect(x - 2, y - 2, x + 2, y + 2, Path.Direction.CW);
                    path.moveTo(x, y);
                }
            }
        }

        // Green trace

        paint.setColor(Color.GREEN);
        cb.drawPath(path, paint);

        // Draw index

        if (index > 0 && index < width)
        {
            // Yellow index

            paint.setColor(Color.YELLOW);
            paint.setTextSize(height / 48);
            paint.setTextAlign(Paint.Align.LEFT);
            cb.drawLine(index, -height / 2, index, height / 2, paint);

            // Get value

            int i = Math.round(index / xscale);
            if (i + xstart < data_in.length)
            {
                float y = -data_in.data[i + xstart] / yscale;

                // Draw value

                String s = String.format("%3.2f",
                        data_in.data[i + xstart] / 32768.0);
                cb.drawText(s, index, y, paint);
            }

            paint.setTextAlign(Paint.Align.CENTER);

            // Draw time value

            if (scale < 100.0)
            {
                String s = String.format((scale < 1.0)? "%3.3f":
                                (scale < 10.0)? "%3.2f": "%3.1f",
                        (start + (index * scale)) /
                                MainActivity.SMALL_SCALE);
                cb.drawText(s, index, height / 2, paint);
            }

            else
            {
                String s = String.format("%3.3f", (start + (index * scale)) /
                        MainActivity.LARGE_SCALE);
                cb.drawText(s, index, height / 2, paint);
            }
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
