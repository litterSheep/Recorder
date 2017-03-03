package com.ly.recorder.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2017/3/3 14:01.
 */

public class StatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);

        initViews();

        initChart();
    }

    private void initViews() {

        topTitleBar.setTitle_text(getString(R.string.title_statistics));
    }

    private void initChart() {
        LineChart chart = (LineChart) findViewById(R.id.chart);
        // enable touch gestures
        chart.setTouchEnabled(true); // 设置是否可以触摸
        // enable scaling and dragging
        chart.setDragEnabled(true);// 是否可以拖拽
        chart.setScaleEnabled(true);// 是否可以缩放
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.getAxisRight().setEnabled(false);//隐藏Y轴右边轴线，此时标签数字也隐藏
        //chart.setBackgroundColor(getResources().getColor(R.color.mainColor));// 设置背景
        // enable / disable grid background

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//显示轴线内部INSIDE_CHART
        yAxis.setAxisLineColor(getResources().getColor(R.color.mainColor));//设置轴线颜色：
        yAxis.setAxisLineWidth(1.5f);// 设置轴线宽度
        yAxis.setTextSize(8f);//设置y轴标签字体大小
        yAxis.setDrawGridLines(false);//设置是否显示网格线

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.mainColor));
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);

        AccountManager accountManager = new AccountManager();
        List<Account> accounts = accountManager.queryAll();
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            Account data = accounts.get(i);
            entries.add(new Entry(i, data.getDinner()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "共50元"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.mainColor));
        dataSet.setValueTextColor(Color.BLACK); // styling, ...
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setCircleColor(getResources().getColor(R.color.mainColor));
        dataSet.setCircleRadius(4.5f);
        dataSet.setLineWidth(2.5f);
        dataSet.setHighLightColor(getResources().getColor(R.color.mainColor));
        dataSet.setHighlightEnabled(true);//点击小点的交叉线

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        //chart.invalidate(); // refresh

        // animate calls invalidate()...
        chart.animateX(1000); // 立即执行的动画,x轴

    }

}
