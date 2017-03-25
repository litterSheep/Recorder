package com.ly.recorder.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.PreferencesUtils;
import com.ly.recorder.utils.TimeUtil;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;
import com.ly.recorder.view.ChartMarkerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 综合收支页
 * Created by ly on 2017/3/25 14:30
 */
public class FragmentTogether extends Fragment implements OnChartValueSelectedListener, OnChartGestureListener {

    private static final int BAR_OUT = 2, BAR_IN = 1, BAR_JIEYU = 0;
    private final String TOTAL_PREVIOUS = "totalIn";
    private final String TOTAL_CURRENT = "totalOut";
    private AccountManager accountManager;
    private LineChart mLineChart;
    private HorizontalBarChart mBarChart;
    private int year;//选定的年份
    private int totalIn, totalOut;//总支出\总支出
    private Typeface mTfLight;
    private int selectMonth;//在曲线上选择的月份

    public FragmentTogether() {
        // Required empty public constructor
    }

    public static FragmentTogether newInstance() {
        FragmentTogether fragment = new FragmentTogether();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            totalOut = savedInstanceState.getInt(TOTAL_CURRENT);
            totalIn = savedInstanceState.getInt(TOTAL_PREVIOUS);
        }
        View view = inflater.inflate(R.layout.fragment_fragment_together, container, false);

        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        initLineChart(view);
        initBarChart(view);
        setData(year);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TOTAL_PREVIOUS, totalIn);
        outState.putInt(TOTAL_CURRENT, totalOut);
        super.onSaveInstanceState(outState);
    }

    public void initData(AccountManager accountManager, int year) {
        this.accountManager = accountManager;
        this.year = year;
    }

    private List<Account> getList(int type, int y) {
        if (accountManager == null)
            accountManager = new AccountManager();
        return accountManager.queryForYear(type, y);
    }

    private void initLineChart(View view) {
        mLineChart = (LineChart) view.findViewById(R.id.line_chart_month);
        // enable touch gestures
        mLineChart.setTouchEnabled(true); // 设置是否可以触摸
        // enable scaling and dragging
        mLineChart.setDragEnabled(true);// 是否可以拖拽
        mLineChart.setScaleEnabled(true);// 是否可以缩放
        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);
        mLineChart.getAxisRight().setEnabled(false);//隐藏Y轴右边轴线，此时标签数字也隐藏
        mLineChart.getAxisLeft().setEnabled(false);//隐藏Y轴左边轴线，此时标签数字也隐藏
        //chart.setBackgroundColor(getResources().getColor(R.color.mainColor));// 设置背景
        // no description text
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setExtraOffsets(5, 10, 25, 25);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.mainColor));//设置轴线颜色
        xAxis.setAxisLineWidth(1.5f);// 设置轴线宽度
        xAxis.setTextSize(10f);//设置轴标签字体大小
        xAxis.setDrawGridLines(false);//设置是否显示网格线
        xAxis.setDrawAxisLine(false);//是否画轴线
        xAxis.setAxisMinimum(1f);
        xAxis.setTypeface(mTfLight);
        xAxis.setAxisMaximum(12f);

        mLineChart.setOnChartValueSelectedListener(this);
        mLineChart.setOnChartGestureListener(this);

        ChartMarkerView mv = new ChartMarkerView(getActivity(), R.layout.custom_marker_view, ChartMarkerView.CHART_YEAR);
        mv.setChartView(mLineChart); // For bounds control
        mLineChart.setMarker(mv); // Set the marker to the chart
    }

    private void initBarChart(View view) {
        mBarChart = (HorizontalBarChart) view.findViewById(R.id.chart_bar_h);
        // mBarChart.setHighlightEnabled(false);
        mBarChart.setDrawBarShadow(false);

        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawValueAboveBar(true);//值画在bar外面

        mBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mBarChart.setDrawBarShadow(true);

        mBarChart.setDrawGridBackground(false);

        mBarChart.getXAxis().setEnabled(false);

        YAxis yl = mBarChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setDrawLabels(false);//是否显示轴线数字
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        mBarChart.getAxisRight().setEnabled(false);
    }

    public void setData(int year) {
        this.year = year;
        if (mLineChart == null || mBarChart == null) {
            Logger.w("mLineChart/mBarChart == null, return...");
            return;
        }
        setLineData();
        setBarData(selectMonth);
    }

    private void setLineData() {
        //该年支出list
        List<Entry> entriesOut = getLineEntries(Constants.TYPE_OUT, getList(Constants.TYPE_OUT, year));
        //该年收入list
        List<Entry> entriesIn = getLineEntries(Constants.TYPE_IN, getList(Constants.TYPE_IN, year));
        //该年结余list
        List<Entry> entriesJieYu = getJieYuEntries(entriesOut, entriesIn);

        if (entriesOut.size() > 0 && entriesIn.size() > 0 && entriesJieYu.size() > 0) {
            String labelOut = "共支出: " + totalOut;
            String labelIn = "共收入: " + totalIn;
            String labelJieYu = "结余: " + (totalIn - totalOut);
            //一条线就是一组数据集
            LineDataSet dataSetOut, dataSetIn, dataSetJieYu;

            LineData mChartData = mLineChart.getData();
            if (mChartData != null && mChartData.getDataSetCount() > 0) {
                dataSetOut = (LineDataSet) mChartData.getDataSetByIndex(0);
                dataSetOut.setValues(entriesOut);
                dataSetOut.setLabel(labelOut);

                dataSetIn = (LineDataSet) mChartData.getDataSetByIndex(1);
                dataSetIn.setValues(entriesIn);
                dataSetIn.setLabel(labelIn);

                dataSetJieYu = (LineDataSet) mChartData.getDataSetByIndex(2);
                dataSetJieYu.setValues(entriesJieYu);
                dataSetJieYu.setLabel(labelJieYu);


                mChartData.notifyDataChanged();
                mLineChart.notifyDataSetChanged();
            } else {

                dataSetOut = new LineDataSet(entriesOut, labelOut);
                dataSetIn = new LineDataSet(entriesIn, labelIn);
                dataSetJieYu = new LineDataSet(entriesJieYu, labelJieYu);

                setLineStyle(dataSetOut, getResources().getColor(R.color.red1));
                setLineStyle(dataSetIn, getResources().getColor(R.color.green));
                setLineStyle(dataSetJieYu, getResources().getColor(R.color.mainColor));

                LineData lineData = new LineData(dataSetOut, dataSetIn, dataSetJieYu);
                mLineChart.setData(lineData);
            }
        } else {
            mLineChart.clear();
        }

        mLineChart.setNoDataText(getString(R.string.show_no_data));
        mLineChart.setNoDataTextColor(getResources().getColor(R.color.gray_text));

        //chart.invalidate(); // refresh

        // animate calls invalidate()...
        mLineChart.animateX(1200); // 立即执行的动画,x轴

        Legend legend = mLineChart.getLegend();
        legend.setXEntrySpace(10f);
        legend.setTypeface(mTfLight);
    }

    /**
     * 收支结余视图
     *
     * @param selectMonth 如果月份合法（1-12）则显示每月的情况，否则显示一年的数据
     *                    Created by ly on 2017/3/25 15:22
     */
    private void setBarData(int selectMonth) {

        List<BarEntry> barEntriesOut = getBarEntries(Constants.TYPE_OUT, selectMonth);
        List<BarEntry> barEntriesIn = getBarEntries(Constants.TYPE_IN, selectMonth);
        List<BarEntry> barEntriesJieYu = getJieYuBarEntries(barEntriesOut, barEntriesIn);

        String labelOut = getString(R.string.type_out), labelIn = getString(R.string.type_in), labelJieYu = getString(R.string.jieyu);
        if (barEntriesOut.size() > 0 && barEntriesIn.size() > 0 && barEntriesJieYu.size() > 0) {
            BarDataSet barDataOut, barDataIn, barDataJieYu;

            if (mBarChart.getData() != null &&
                    mBarChart.getData().getDataSetCount() > 0) {

                barDataOut = (BarDataSet) mBarChart.getData().getDataSetByIndex(BAR_JIEYU);
                barDataOut.setValues(barEntriesOut);
                barDataOut.setLabel(labelOut);

                barDataIn = (BarDataSet) mBarChart.getData().getDataSetByIndex(BAR_IN);
                barDataIn.setValues(barEntriesIn);
                barDataIn.setLabel(labelIn);

                barDataJieYu = (BarDataSet) mBarChart.getData().getDataSetByIndex(BAR_OUT);
                barDataJieYu.setValues(barEntriesJieYu);
                barDataJieYu.setLabel(labelJieYu);

                mBarChart.getData().notifyDataChanged();
                mBarChart.notifyDataSetChanged();
            } else {
                barDataOut = new BarDataSet(barEntriesOut, labelOut);
                barDataIn = new BarDataSet(barEntriesIn, labelIn);
                barDataJieYu = new BarDataSet(barEntriesJieYu, labelJieYu);

                barDataOut.setColors(getResources().getColor(R.color.red1));
                barDataIn.setColors(getResources().getColor(R.color.green));
                barDataJieYu.setColors(getResources().getColor(R.color.mainColor));

                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(barDataOut);
                dataSets.add(barDataIn);
                dataSets.add(barDataJieYu);

                BarData data = new BarData(dataSets);
                data.setValueTextSize(10f);
                data.setValueTypeface(mTfLight);
                data.setBarWidth(0.8f);
                mBarChart.setData(data);
            }
        } else {
            mBarChart.clear();
        }

        mBarChart.setFitBars(true);
        mBarChart.animateY(1200);
        mBarChart.setNoDataText(getString(R.string.show_no_data));
        //描述栏
        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(10f);
        l.setXEntrySpace(8f);//描述之间的间隔
    }

    //设置曲线样式
    private void setLineStyle(LineDataSet dataSet, int color) {
        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.BLACK); // styling, ...
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(2.5f);
        dataSet.setLineWidth(1.8f);
        dataSet.setValueTypeface(mTfLight);
        dataSet.setValueTextSize(8f);
        dataSet.setHighLightColor(getResources().getColor(R.color.mainColor1));
        dataSet.setHighlightEnabled(true);
    }

    private List<Entry> getLineEntries(int type, List<Account> list) {
        List<Entry> entries = new ArrayList<>();
        if (list == null || list.size() == 0)
            return entries;
        //key:月，value:该月对应的总支出/收入
        Map<Integer, Float> temp = new HashMap<>();
        int total = 0;
        for (Account account : list) {
            Integer month = account.getMonth();
            Float money = account.getMoney();
            total += money;
            if (temp.containsKey(month)) {
                money += temp.get(month); //把这一天的都累加
            }
            temp.put(month, money);
        }

        int startMonth = 1;
        //这个年最后一月(X轴展示的月份)
        int lastMonth = 12;
        if (TimeUtil.isCurrentYear(year)) {//选定的年份就是本年
            lastMonth = list.get(list.size() - 1).getMonth();
            startMonth = list.get(0).getMonth();
        }
        for (int i = startMonth; i < lastMonth + 1; i++) {
            Float money = temp.get(i);
            if (money == null)
                money = 0f;//如果没有则表示这一天支出/收入为0
            entries.add(new Entry(i, money));
        }

        if (type == Constants.TYPE_IN) {
            totalIn = 0;
            totalIn = total;
        } else {
            totalOut = 0;
            totalOut = total;
        }
        return entries;
    }

    private List<Entry> getJieYuEntries(List<Entry> outs, List<Entry> ins) {
        List<Entry> list = new ArrayList<>();
        if (outs == null || ins == null)
            return list;
        int start = 1;
        int end = 12;
        if (TimeUtil.isCurrentYear(year) && outs.size() > 0 && ins.size() > 0) {
            //在两个数组里取最小的月份
            start = (int) (outs.get(0).getX() < ins.get(0).getX() ? outs.get(0).getX() : ins.get(0).getX());
            //取最大的月份
            end = (int) (outs.get(outs.size() - 1).getX() > ins.get(ins.size() - 1).getX() ? outs.get(outs.size() - 1).getX() : ins.get(ins.size() - 1).getX());
        }
        for (int i = start; i < end + 1; i++) {//遍历这一年的每一月
            float out = 0, in = 0;//该月对应的收支
            for (Entry entry : outs) {
                int e = (int) entry.getX();
                if (e == i) {//月份相等才取值
                    out = entry.getY();
                    break;
                }
            }
            for (Entry entry : ins) {
                int e = (int) entry.getX();
                if (e == i) {//月份相等才取值
                    in = entry.getY();
                    break;
                }
            }
            list.add(new Entry(i, in - out));
        }
        return list;
    }

    private List<BarEntry> getBarEntries(int type, int selectMonth) {
        List<Account> list;
        if (selectMonth > 0 && selectMonth < 13) {
            if (accountManager == null)
                accountManager = new AccountManager();
            list = accountManager.queryForMonth(type, year, selectMonth);
        } else {
            list = getList(type, year);
        }
        List<BarEntry> entries = new ArrayList<>();
        float total = 0;
        for (Account account : list) {
            total += account.getMoney();
        }
        entries.add(new BarEntry(type == Constants.TYPE_OUT ? BAR_OUT : BAR_IN, total));

        return entries;
    }

    private List<BarEntry> getJieYuBarEntries(List<BarEntry> outs, List<BarEntry> ins) {
        List<BarEntry> entries = new ArrayList<>();
        float total = 0;
        if (outs.size() > 0 && ins.size() > 0) {
            total = ins.get(0).getY() - outs.get(0).getY();
        }
        if (total < 0)//柱状图不能显示负数
            total = 0;
        entries.add(new BarEntry(BAR_JIEYU, total));

        return entries;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        selectMonth = (int) e.getX();
        boolean isShow = PreferencesUtils.getBoolean(getActivity(), Constants.PREFERENCES_FLAG_BAR);
        if (!isShow) {
            ToastUtil.showToast(getActivity(), getString(R.string.long_click_show_dialog));
            PreferencesUtils.putBoolean(getActivity(), Constants.PREFERENCES_FLAG_BAR, !isShow);
        }
    }

    @Override
    public void onNothingSelected() {
        Logger.i("onNothingSelected");
        selectMonth = 0;
        setBarData(selectMonth);
    }


    @Override
    public void onChartLongPressed(MotionEvent me) {

        setBarData(selectMonth);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Logger.i("Gesture START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Logger.i("Gesture END, lastGesture: " + lastPerformedGesture);

//        // un-highlight values after the gesture is finished and no single-tap
//        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
//            mLineChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Logger.i("Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Logger.i("Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Logger.i("Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Logger.i("Scale / Zoom ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Logger.i("Translate / Move dX: " + dX + ", dY: " + dY);
    }

}
