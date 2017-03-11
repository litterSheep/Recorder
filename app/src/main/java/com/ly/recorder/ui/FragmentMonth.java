package com.ly.recorder.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.TimeUtil;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMonth extends Fragment implements OnChartValueSelectedListener {
    private AccountManager accountManager;
    private LineChart mChart;
    private int year, month;//选定的月份
    private boolean isOpenComparison = false;//是否开启与上月对比
    private int totalPrevious, totalCurrent;//上个月总花费和本月总花费
    private CheckBox cb_comparison;

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

        cb_comparison = (CheckBox) view.findViewById(R.id.cb_comparison);
        cb_comparison.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpenComparison = isChecked;
                setData(year, month);
            }
        });

        initChart(view);
        setData(year, month);

        return view;
    }

    public void initData(AccountManager accountManager, int year, int month) {
        this.accountManager = accountManager;
        this.year = year;
        this.month = month;
    }

    private List<Account> getList(int y, int m) {
        if (accountManager == null)
            accountManager = new AccountManager();
        return accountManager.queryForMonth(y, m);
    }

    private void initChart(View view) {
        mChart = (LineChart) view.findViewById(R.id.chart);
        // enable touch gestures
        mChart.setTouchEnabled(true); // 设置是否可以触摸
        // enable scaling and dragging
        mChart.setDragEnabled(true);// 是否可以拖拽
        mChart.setScaleEnabled(true);// 是否可以缩放
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.getAxisRight().setEnabled(false);//隐藏Y轴右边轴线，此时标签数字也隐藏
        mChart.getAxisLeft().setEnabled(false);//隐藏Y轴左边轴线，此时标签数字也隐藏
        //chart.setBackgroundColor(getResources().getColor(R.color.mainColor));// 设置背景
        // no description text
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setExtraOffsets(5, 10, 25, 25);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.mainColor));//设置轴线颜色
        xAxis.setAxisLineWidth(1.5f);// 设置轴线宽度
        xAxis.setTextSize(10f);//设置轴标签字体大小
        xAxis.setDrawGridLines(false);//设置是否显示网格线
        xAxis.setDrawAxisLine(false);//是否画轴线

        mChart.setOnChartValueSelectedListener(this);
    }

    public void setData(int year, int month) {
        this.year = year;
        this.month = month;

        //本月数据
        List<Entry> entries = getEntries(getList(year, month));
        //上月数据
        List<Entry> entriesLastMonth = null;
        if (isOpenComparison) {
            int lastMonth = month - 1;
            int lastMonthY = year;
            if (month == 1) {//如果本月是1月，则上一月是12月  同时年份-1
                lastMonth = 12;
                lastMonthY = year - 1;
            }
            entriesLastMonth = getEntries(getList(lastMonthY, lastMonth));
            if (entriesLastMonth == null || entriesLastMonth.size() == 0) {
                ToastUtil.showToast(getActivity(), getString(R.string.no_last_month_data));
                cb_comparison.setChecked(false);
                isOpenComparison = false;
                return;
            }
        }

        if (entries != null && entries.size() > 0 || entriesLastMonth != null && entriesLastMonth.size() > 0) {
            //一条线就是一组数据集
            LineDataSet dataSet, dataSetLastMonth;
            LineData mChartData = mChart.getData();
            if (mChartData != null && mChartData.getDataSetCount() > 0) {
                dataSet = (LineDataSet) mChartData.getDataSetByIndex(0);
                dataSet.setValues(entries);
                if (isOpenComparison) {
                    if (entriesLastMonth != null && entriesLastMonth.size() > 0) {
                        dataSetLastMonth = (LineDataSet) mChartData.getDataSetByIndex(1);
                        if (dataSetLastMonth == null) {//还未添加过数据
                            dataSetLastMonth = new LineDataSet(entriesLastMonth, "上月共花费：" + totalPrevious + "元");
                            setLineStyle(dataSetLastMonth);

                            LineData lineData = mChart.getLineData();
                            lineData.addDataSet(dataSetLastMonth);
                        } else {
                            dataSetLastMonth.setValues(entriesLastMonth);
                        }
                    } else {
                        Logger.w("entriesLastMonth 没有上月的数据...");
                    }
                } else {
                    dataSetLastMonth = (LineDataSet) mChartData.getDataSetByIndex(1);
                    if (dataSetLastMonth != null)
                        mChart.getLineData().removeDataSet(dataSetLastMonth);
                }
                mChartData.notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {

                dataSet = new LineDataSet(entries, "本月共花费：" + totalCurrent + "元"); // add entries to dataset

                setLineStyle(dataSet);

                LineData lineData;
                if (isOpenComparison && entriesLastMonth != null && entriesLastMonth.size() > 0) {
                    dataSetLastMonth = new LineDataSet(entriesLastMonth, "上月共花费：" + totalPrevious + "元");
                    setLineStyle(dataSetLastMonth);
                    lineData = new LineData(dataSet, dataSetLastMonth);
                } else {
                    lineData = new LineData(dataSet);
                }
                mChart.setData(lineData);
            }
        } else {
            mChart.clear();
        }

        mChart.setNoDataText(getString(R.string.show_no_data));
        mChart.setNoDataTextColor(getResources().getColor(R.color.gray_text));

        //chart.invalidate(); // refresh

        // animate calls invalidate()...
        mChart.animateX(1000); // 立即执行的动画,x轴
    }

    //设置曲线样式
    private void setLineStyle(LineDataSet dataSet) {
        int color;
        if (isOpenComparison) {
            color = getResources().getColor(R.color.gray_line);
        } else {
            color = getResources().getColor(R.color.mainColor);
        }

        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.BLACK); // styling, ...
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(4.5f);
        dataSet.setLineWidth(2.5f);
        dataSet.setHighLightColor(Color.TRANSPARENT);
        dataSet.setHighlightEnabled(true);
    }

    private List<Entry> getEntries(List<Account> list) {
        List<Entry> entries = new ArrayList<>();
        if (list == null || list.size() == 0)
            return entries;
        //key:日期，value:该日对应的总花费
        Map<Integer, Float> temp = new HashMap<>();
        int total = 0;
        for (Account account : list) {
            Integer day = account.getDate();
            Float money = account.getMoney();
            total += money;
            if (temp.containsKey(day)) {
                money += temp.get(day); //把这一天的都累加
            }
            temp.put(day, money);
        }

        //这个月最后一天(X轴展示的日期)
        int lastDay = TimeUtil.getLastDayOfMonth(year, month);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        if (currentYear == year && currentMonth == month) {//选定的月份就是本月
            lastDay = list.get(list.size() - 1).getDate();
        }
        for (int i = 1; i < lastDay; i++) {
            Float money = temp.get(i);
            if (money == null)
                money = 0f;//如果没有则表示这一天花费为0
            entries.add(new Entry(i, money));
        }

        if (isOpenComparison) {
            totalPrevious = total;
        } else {
            totalCurrent = total;
        }
        return entries;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
