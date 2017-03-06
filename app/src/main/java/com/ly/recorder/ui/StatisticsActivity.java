package com.ly.recorder.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2017/3/3 14:01.
 */

public class StatisticsActivity extends BaseActivity {
    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);

        initViews();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rbt_container);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_day:
                        Logger.d("checkedId:" + checkedId);
                        break;
                    case R.id.rbt_year:

                        break;
                    default:

                        break;
                }

            }
        });

        initChart();
    }

    private void initViews() {

        topTitleBar.setTitle_text(getString(R.string.title_statistics));
    }

    private void initFragment() {
        if (fragments == null) fragments = new ArrayList<>();
        fragments.add(FragmentDay.newInstance());
        fragments.add(FragmentMonth.newInstance());
        fragments.add(FragmentYear.newInstance());

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.home_fragcontent, fragments.get(0)).commit();
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

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.mainColor));//设置轴线颜色
        xAxis.setAxisLineWidth(1.5f);// 设置轴线宽度
        xAxis.setTextSize(10f);//设置轴标签字体大小
        xAxis.setDrawGridLines(false);//设置是否显示网格线

        AccountManager accountManager = new AccountManager();
        List<Account> accounts = accountManager.queryAll();
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            Account data = accounts.get(i);
            entries.add(new Entry(i, data.getMoney()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "共50元"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.mainColor));
        dataSet.setValueTextColor(Color.BLACK); // styling, ...
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setCircleColor(getResources().getColor(R.color.mainColor));
        dataSet.setCircleRadius(4.5f);
        dataSet.setLineWidth(2.5f);
        dataSet.setHighLightColor(Color.TRANSPARENT);
        dataSet.setHighlightEnabled(true);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ToastUtil.showToast(StatisticsActivity.this, e.getX() + "");
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //chart.invalidate(); // refresh

        // animate calls invalidate()...
        chart.animateX(1000); // 立即执行的动画,x轴

    }

}
