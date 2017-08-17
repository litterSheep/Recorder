package com.ly.recorder.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Collections;
import java.util.Comparator;
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
    private PieChart mPieChart;
    private int year;//选定的年份
    private float totalIn, totalOut;//总支出\总支出
    private Typeface mTfLight;
    private int selectMonth;//在曲线上选择的月份
    private TextView tv_together_hint, tv_together_month;
    private float barValue = 0;//柱状图每一项对应的值
    private boolean isSelectYear = true;//如果选择折线图的月份并长按，则选择的是月

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
            totalOut = savedInstanceState.getFloat(TOTAL_CURRENT);
            totalIn = savedInstanceState.getFloat(TOTAL_PREVIOUS);
        }
        View view = inflater.inflate(R.layout.fragment_fragment_together, container, false);

        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        tv_together_hint = (TextView) view.findViewById(R.id.tv_together_hint);

        initLineChart(view);
        initBarChart(view);
        initPieChart(view);

        setData(year);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(TOTAL_PREVIOUS, totalIn);
        outState.putFloat(TOTAL_CURRENT, totalOut);
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
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                switch ((int) e.getX()) {
                    case 1://收入
                        setPieData(Constants.TYPE_IN, e.getY());
                        break;
                    case 2://支出
                        setPieData(Constants.TYPE_OUT, e.getY());
                        break;
                    default:
                        ToastUtil.showToast(getActivity(), "该类型下无详细数据");
                        break;
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        YAxis yl = mBarChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setDrawLabels(false);//是否显示轴线数字
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        mBarChart.getAxisRight().setEnabled(false);
    }

    private void initPieChart(View view) {
        tv_together_month = (TextView) view.findViewById(R.id.tv_together_month);
        mPieChart = (PieChart) view.findViewById(R.id.pie_chart_month);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        //饼图与文字描述的padding
        mPieChart.setExtraOffsets(5, 10, 50, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.85f);//拖动饼图 松手后滑动的灵敏度

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);//中央圆孔背景透明度

        mPieChart.setHoleRadius(20f);//中央圆孔的大小
        mPieChart.setTransparentCircleRadius(23f);//中央圆孔透明圈大小

        mPieChart.setDrawCenterText(false);//中间的文字
        //mPieChart.setCenterTextTypeface(mTfLight);
        //mPieChart.setCenterText(getString(R.string.app_name));

        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        mPieChart.setOnChartValueSelectedListener(this);
        mPieChart.setEntryLabelTypeface(mTfLight);
    }

    public void setData(int year) {
        this.year = year;
        if (mLineChart == null || mBarChart == null) {
            Logger.w("mLineChart/mBarChart == null, return...");
            return;
        }
        setLineData();
        setBarData(selectMonth);
        setPieData(Constants.TYPE_OUT, totalOut);
    }

    private void setLineData() {
        //该年支出list
        List<Entry> entriesOut = getLineEntries(Constants.TYPE_OUT, getList(Constants.TYPE_OUT, year));
        //该年收入list
        List<Entry> entriesIn = getLineEntries(Constants.TYPE_IN, getList(Constants.TYPE_IN, year));
        //该年结余list
        List<Entry> entriesJieYu = getJieYuEntries(entriesOut, entriesIn);

        if (entriesOut.size() > 0 || entriesIn.size() > 0) {
            String labelOut = "共支出: " + totalOut;
            String labelIn = "共收入: " + totalIn;
            String labelJieYu = "结余: " + (totalIn - totalOut);
            //一条线就是一组数据集
            LineDataSet dataSetOut, dataSetIn, dataSetJieYu;

            LineData mChartData = mLineChart.getData();
            if (mChartData != null && mChartData.getDataSetCount() > 0) {
                dataSetOut = (LineDataSet) mChartData.getDataSetByIndex(0);
                if (dataSetOut != null && entriesOut.size() > 0) {
                    dataSetOut.setValues(entriesOut);
                    dataSetOut.setLabel(labelOut);
                }
                dataSetIn = (LineDataSet) mChartData.getDataSetByIndex(1);
                if (dataSetIn != null && entriesIn.size() > 0) {
                    dataSetIn.setValues(entriesIn);
                    dataSetIn.setLabel(labelIn);
                }
                dataSetJieYu = (LineDataSet) mChartData.getDataSetByIndex(2);
                if (dataSetJieYu != null && entriesJieYu.size() > 0) {
                    dataSetJieYu.setValues(entriesJieYu);
                    dataSetJieYu.setLabel(labelJieYu);
                }
                mChartData.notifyDataChanged();
                mLineChart.notifyDataSetChanged();

            } else {

                dataSetJieYu = new LineDataSet(entriesJieYu, labelJieYu);
                setLineStyle(dataSetJieYu, getResources().getColor(R.color.mainColor));
                LineData lineData;

                if (entriesOut.size() > 0) {
                    if (entriesIn.size() > 0) {
                        dataSetOut = new LineDataSet(entriesOut, labelOut);
                        dataSetIn = new LineDataSet(entriesIn, labelIn);
                        setLineStyle(dataSetOut, getResources().getColor(R.color.red1));
                        setLineStyle(dataSetIn, getResources().getColor(R.color.green));
                        lineData = new LineData(dataSetOut, dataSetIn, dataSetJieYu);
                    } else {
                        dataSetOut = new LineDataSet(entriesOut, labelOut);
                        setLineStyle(dataSetOut, getResources().getColor(R.color.red1));
                        lineData = new LineData(dataSetOut, dataSetJieYu);
                    }
                } else {//否则就是 entriesIn > 0
                    dataSetIn = new LineDataSet(entriesIn, labelOut);
                    setLineStyle(dataSetIn, getResources().getColor(R.color.green));
                    lineData = new LineData(dataSetIn, dataSetJieYu);
                }

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

        String text = getString(R.string.together_hint);
        if (selectMonth > 0) {
            if (TimeUtil.isCurrentMonth(year, selectMonth)) {
                text = "本月收支结余情况";
            } else {
                text = selectMonth + "月收支结余情况";
            }
        }

        tv_together_hint.setText(text);
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

    private void setPieData(int type, float totalCurrent) {
        if (mPieChart == null)
            return;

        List<PieEntry> entries = getPieEntries(type);

        if (entries.size() > 0) {

            String title = "支出";
            if (type == Constants.TYPE_IN)
                title = "收入";
            String label = "共" + title + totalCurrent;

            if (selectMonth < 1 || selectMonth > 12 || isSelectYear) {
                title = "本年" + title;
            } else {
                title = selectMonth + "月" + title;
            }
            tv_together_month.setVisibility(View.VISIBLE);
            tv_together_month.setText(title + "情况");
            mPieChart.setVisibility(View.VISIBLE);

            PieDataSet dataSet;
            PieData pieData = mPieChart.getData();

            if (pieData != null && pieData.getDataSetCount() > 0) {

                dataSet = (PieDataSet) pieData.getDataSet();
                dataSet.setValues(entries);
                dataSet.setLabel(label);

                pieData.notifyDataChanged();
                mPieChart.notifyDataSetChanged();
            } else {
                dataSet = new PieDataSet(entries, label);

                //dataSet.setDrawIcons(false);
                //dataSet.setIconsOffset(new MPPointF(0, 40));

                dataSet.setSliceSpace(2f);//扇形之间的间隙
                dataSet.setSelectionShift(5f);//点击扇形后多出的部分

                // add a lot of colors

                ArrayList<Integer> colors = new ArrayList<>();

                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);

                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                //dataSet.setSelectionShift(0f);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(14f);
                data.setValueTextColor(Color.WHITE);
                //data.setValueTypeface(mTfLight);
                // undo all highlights
                mPieChart.setData(data);
            }
        } else {
            mPieChart.clear();
            tv_together_month.setVisibility(View.GONE);
        }
        mPieChart.highlightValues(null);
        //mPieChart.invalidate();

        mPieChart.setNoDataText(getString(R.string.show_no_data));
        mPieChart.setNoDataTextColor(getResources().getColor(R.color.gray_text));

        mPieChart.animateY(1200, Easing.EasingOption.EaseInOutQuad);
        // mPieChart.spin(2000, 0, 360);

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTypeface(mTfLight);
        l.setDrawInside(false);
        l.setYEntrySpace(3f);//描述文字之间的间隔
        l.setYOffset(8f);//描述文字marginTop
        l.setFormSize(10f);

        // entry label styling
        //在扇形区内隐藏每个类别的描述
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setEntryLabelColor(Color.GRAY);
        mPieChart.setEntryLabelTypeface(mTfLight);
        mPieChart.setEntryLabelTextSize(12f);
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
        float total = 0;
        for (Account account : list) {
            int month = account.getMonth();
            float money = account.getMoney();
            total += money;
            if (temp.containsKey(month)) {
                money += temp.get(month); //把这一天的都累加
            }
            temp.put(month, money);
        }

        int startMonth;
        //这个年最后一月(X轴展示的月份)
        int lastMonth;
        startMonth = list.get(0).getMonth();
        lastMonth = list.get(list.size() - 1).getMonth();
        if (startMonth < 1 || startMonth > 12)
            startMonth = 1;
        if (lastMonth < 1 || startMonth > 12)
            lastMonth = 12;
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
        if (TimeUtil.isCurrentYear(year)) {

            if (outs.size() > 0) {
                start = (int) outs.get(0).getX();
                end = (int) outs.get(outs.size() - 1).getX();
            }
            if (ins.size() > 0) {
                start = (int) ins.get(0).getX();
                end = (int) ins.get(ins.size() - 1).getX();
            }
            if (outs.size() > 0 && ins.size() > 0) {
                //在两个数组里取最小的月份
                start = (int) (outs.get(0).getX() < ins.get(0).getX() ? outs.get(0).getX() : ins.get(0).getX());
                //取最大的月份
                end = (int) (outs.get(outs.size() - 1).getX() > ins.get(ins.size() - 1).getX() ? outs.get(outs.size() - 1).getX() : ins.get(ins.size() - 1).getX());
            }
        }
        for (int i = start; i < end + 1; i++) {//遍历这一年的每一月
            float out = 0, in = 0;//该月对应的收支
            for (int j = 0; j < outs.size(); j++) {
                Entry entry = outs.get(j);
                if (entry.getX() <= i) {//取该月前所有月份的支出总和
                    out += entry.getY();
                }
            }
            for (int j = 0; j < ins.size(); j++) {
                Entry entry = ins.get(j);
                if (entry.getX() <= i) {//取该月前所有月份的收入总和
                    in += entry.getY();
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

        barValue = 0;//每次要重置，避免方法多次调用累加
        for (Account account : list) {
            barValue += account.getMoney();
        }

        entries.add(new BarEntry(type == Constants.TYPE_OUT ? BAR_OUT : BAR_IN, barValue));

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

    private List<PieEntry> getPieEntries(int type) {
        List<Account> list;
        if (selectMonth > 0 && selectMonth < 13) {
            if (accountManager == null)
                accountManager = new AccountManager();
            if (isSelectYear) {
                list = getList(type, year);
            } else {
                list = accountManager.queryForMonth(type, year, selectMonth);
            }
        } else {
            list = getList(type, year);
        }

        List<PieEntry> entries = new ArrayList<>();
        if (list == null || list.size() == 0)
            return entries;
        //key:type  value:该类型对应的总支出
        Map<Integer, Float> types = new HashMap<>();
        for (Account account : list) {
            Float money = account.getMoney();

            Integer typeIndex = account.getTypeIndex();
            if (typeIndex == null) {
                if (type == Constants.TYPE_IN) {//设置为默认值
                    typeIndex = Constants.TYPES_IN.length - 1;
                } else {
                    typeIndex = Constants.TYPES_OUT.length - 1;
                }
            }
            if (types.containsKey(typeIndex)) {
                money += types.get(typeIndex);
            }
            types.put(typeIndex, money);
        }

        for (Integer typeIndex : types.keySet()) {
            float money = types.get(typeIndex);
            if (type == Constants.TYPE_IN) {
                entries.add(new PieEntry(money, Constants.TYPES_IN[typeIndex] + money));
            } else {
                entries.add(new PieEntry(money, Constants.TYPES_OUT[typeIndex] + money));
            }
        }

        //按类别金额从降序排列
        Collections.sort(entries, new Comparator<PieEntry>() {
            @Override
            public int compare(PieEntry o1, PieEntry o2) {
                if (o1.getValue() > o2.getValue())
                    return -1;
                return 1;
            }
        });
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
        isSelectYear = true;
        setBarData(selectMonth);
    }


    @Override
    public void onChartLongPressed(MotionEvent me) {
        isSelectYear = false;
        setBarData(selectMonth);
        setPieData(Constants.TYPE_OUT, barValue);//长按时默认展示支出详情
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
