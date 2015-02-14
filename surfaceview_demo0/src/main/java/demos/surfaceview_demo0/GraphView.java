package demos.surfaceview_demo0;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by lvniqi on 2014/12/3.
 */
public class GraphView extends RelativeLayout {
    private SeriesView seriesView;
    private GridView gridView;
    private AxisView xAxis;
    private AxisView yAxis;
    private int border;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    GraphView(Context context) {
        super(context);
        //Y轴

        RelativeLayout.LayoutParams yAxis_layoutParams = new RelativeLayout.LayoutParams(
                DensityUtil.dip2px(context, 30), ViewGroup.LayoutParams.WRAP_CONTENT);
        yAxis_layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        yAxis = new AxisView(context);//y轴帧
        this.addView(yAxis, yAxis_layoutParams);
        if (MainActivity.getCurrentApiVersion() < 17) {
            yAxis.setId(SupportApi15.generateViewId());
        } else {
            yAxis.setId(generateViewId());
        }
        //x轴
        RelativeLayout.LayoutParams xAxis_layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(context, 30));
        xAxis_layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        xAxis_layoutParams.addRule(RelativeLayout.RIGHT_OF, yAxis.getId());
        xAxis = new AxisView(context);//x轴帧
        this.addView(xAxis, xAxis_layoutParams);
        if (MainActivity.getCurrentApiVersion() < 17) {
            xAxis.setId(SupportApi15.generateViewId());
        } else {
            xAxis.setId(generateViewId());
        }
        //网格帧
        RelativeLayout.LayoutParams grid_view_layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        grid_view_layoutParams.addRule(RelativeLayout.RIGHT_OF, yAxis.getId());
        grid_view_layoutParams.addRule(RelativeLayout.ABOVE, xAxis.getId());
        gridView = new GridView(context);
        gridView.getGrid().setyAxis(yAxis);
        gridView.getGrid().setxAxis(xAxis);
        //gridView.setBackgroundColor(Color.TRANSPARENT);
        this.addView(gridView, grid_view_layoutParams);
        seriesView = new SeriesView(context);//波形帧

        //seriesView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //设置透明
        seriesView.setZOrderOnTop(true);
        seriesView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        gridView.getSeriesViewUpdates().add(0, seriesView.getUpdate_thread());
        Log.i("view", "dip2px:" + DensityUtil.dip2px(context, 10));
        //曲线布局添加
        this.addView(seriesView, grid_view_layoutParams);
        //this.setBackgroundColor(Color.TRANSPARENT);
        //this.setBackgroundColor(Color.rgb(45, 15, 0));
    }

    public SeriesViewUpdate getUpdate_thread() {
        return seriesView.getUpdate_thread();
    }
}