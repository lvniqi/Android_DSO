package demos.dso_demo1;

//android 上下文
import android.content.Context;
//android 绘图库
import android.graphics.Canvas;
//android 画点库
import android.graphics.Paint;
//android 工具 控件构造
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by lvniqi on 2014/10/25.
 */
public class YScale extends View {
    // 宽度 分数？
    private static final int WIDTH_FRACTION = 24;

    //高度和宽度
    private int width;
    private int height;

    private Paint paint;

    //构造函数
    //线程 xml文件id
    public YScale(Context context, AttributeSet attrs){
        //调用父类构造函数
        super(context,attrs);

        //Create pain
        paint = new Paint();
    }
    //重写父类方法
    //子控件 放置大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get offered dimension
        //得到总宽度
        int h = MeasureSpec.getSize(heightMeasureSpec);

        // Set wanted dimensions
        //宽度为分割大小
        setMeasuredDimension(h / WIDTH_FRACTION, h);
    }

    //VIEW大小改变函数 将大小保存
    @Override
    protected  void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        // Get actual dimensions
        width = w;
        height = h;

    }

    //绘制视图本身
    @Override
    protected  void onDraw(Canvas canvas){
        //线宽设置为2
        paint.setStrokeWidth(2);
        //更改原有原点至x = 0 y = 高度一般的地方
        canvas.translate(0,height/2);
        //绘制 左侧 y轴 标志线

        //短线
        for (int i = 0; i < height / 2; i += MainActivity.SIZE){
            //
            canvas.drawLine(width * 2 / 3, i, width, i, paint);
            canvas.drawLine(width * 2 / 3, -i, width, -i, paint);
        }
        //长线
        for (int i = 0; i < height / 2; i += MainActivity.SIZE * 5){
            canvas.drawLine(width / 3, i, width, i, paint);
            canvas.drawLine(width / 3, -i, width, -i, paint);
        }
    }

}
