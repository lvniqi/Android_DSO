package demos.dso_demo1;

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

        //画格子
        for (int i = 0; i < width; i += MainActivity.SIZE)
            canvas.drawLine(i, 0, i, height, paint);

        canvas.translate(0, height / 2);

        for (int i = 0; i < height / 2; i += MainActivity.SIZE)
        {
            canvas.drawLine(0, i, width, i, paint);
            canvas.drawLine(0, -i, width, -i, paint);
        }

        // Draw the graticule on the bitmap

        cb.drawBitmap(graticule, 0, 0, null);

        cb.translate(0, height / 2);
    }
}
