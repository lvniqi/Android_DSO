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
    private int border;

    GraphView(Context context) {
        super(context);
        FrameLayout graphViewSeries_layout = new FrameLayout(context);//波形帧
        graphViewSeries_layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        FrameLayout gridView_layout = new FrameLayout(context);//背景帧
        gridView_layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        gridView = new GridView(context);
        seriesView = new SeriesView(context);
        //设置透明
        seriesView.setZOrderOnTop(true);
        seriesView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        //背景布局添加
        gridView_layout.addView(gridView);
        //曲线布局添加
        graphViewSeries_layout.addView(seriesView);
        FrameLayout.LayoutParams seriesFl = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        seriesFl.setMargins(65, 0, 0, 0);
        gridView_layout.setLayoutParams(seriesFl);
        graphViewSeries_layout.setLayoutParams(seriesFl);
        //全部添加
        this.addView(gridView_layout);
        this.addView(graphViewSeries_layout);
        this.setBackgroundColor(Color.rgb(45, 15, 0));
    }

}
