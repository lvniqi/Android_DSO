package demos.surfaceview_demo0;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by lvniqi on 2014/12/3.
 */
public class GraphView extends FrameLayout {
    private SeriesView seriesView;
    private GridView gridView;
    private AxisView xAxis;
    private AxisView yAxis;
    private int border;

    GraphView(Context context) {
        super(context);
        gridView = new GridView(context);//背景帧
        gridView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        xAxis = new AxisView(context);//x轴帧
        gridView.getGrid().setxAxis(xAxis);
        xAxis.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        yAxis = new AxisView(context);//y轴帧
        gridView.getGrid().setyAxis(yAxis);

        seriesView = new SeriesView(context);//波形帧
        seriesView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //设置透明
        seriesView.setZOrderOnTop(true);
        seriesView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        gridView.getSeriesViewUpdates().add(0, seriesView.getUpdate_thread());
        //FrameLayout.LayoutParams seriesFl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //int a = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
        //seriesFl.setMargins(a, 0, 0, 0);
        //seriesView.setLayoutParams(seriesFl);
        //gridView.setLayoutParams(seriesFl);

        //背景布局添加
        this.addView(gridView);
        //曲线布局添加
        this.addView(seriesView);
        this.addView(xAxis);
        this.addView(yAxis);
        this.setBackgroundColor(Color.rgb(45, 15, 0));
    }
}
