
package com.ly.recorder.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.ly.recorder.R;
import com.ly.recorder.utils.logger.Logger;

public class ChartMarkerView extends MarkerView {

    public static final int CHART_MONTH = 0;
    public static final int CHART_YEAR = 1;
    private TextView tvContent;
    private String unit = "日";

    public ChartMarkerView(Context context, int layoutResource, int chartType) {
        super(context, layoutResource);

        if (chartType == CHART_YEAR) {
            unit = "月";
        }
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        Logger.d("refreshContent------------------");
        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getX(), 0, true) + unit + "，" + ce.getY() + "元");
        } else {

            tvContent.setText(Utils.formatNumber(e.getX(), 0, true) + unit + "，" + e.getY() + "元");
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
