package com.ly.recorder.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

public class FragmentMonth extends Fragment {

    public FragmentMonth() {
        // Required empty public constructor
    }

    public static FragmentMonth newInstance() {
        FragmentMonth fragment = new FragmentMonth();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_month, container, false);
        initChart(view);
        return view;
    }

    private void initChart(View view) {
        LineChart chart = (LineChart) view.findViewById(R.id.chart);
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
                ToastUtil.showToast(getActivity(), e.getX() + "");
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
