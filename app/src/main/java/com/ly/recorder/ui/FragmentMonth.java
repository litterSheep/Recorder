package com.ly.recorder.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.TimeUtil;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentMonth extends Fragment implements OnChartValueSelectedListener {
    private AccountManager accountManager;
    private LineChart mLineChart;
    private PieChart mPieChart;
    private int year, month;//选定的月份
    private boolean isOpenComparison = false;//是否开启与上月对比
    private final String IS_OPEN_COMPARISON = "isOpenComparison";
    private final String TOTAL_PREVIOUS = "totalPrevious";
    private final String TOTAL_CURRENT = "totalCurrent";
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
        if (savedInstanceState != null) {
            isOpenComparison = savedInstanceState.getBoolean(IS_OPEN_COMPARISON);
            totalCurrent = savedInstanceState.getInt(TOTAL_CURRENT);
            totalPrevious = savedInstanceState.getInt(TOTAL_PREVIOUS);
        }
        View view = inflater.inflate(R.layout.fragment_fragment_month, container, false);
        cb_comparison = (CheckBox) view.findViewById(R.id.cb_comparison);
        cb_comparison.setText(getString(R.string.comparison_month));
        cb_comparison.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpenComparison = isChecked;
                setLineData();//只有折线图有对比上月
            }
        });
        initLineChart(view);
        initPieChart(view);
        setData(year, month);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_OPEN_COMPARISON, isOpenComparison);
        outState.putInt(TOTAL_PREVIOUS, totalPrevious);
        outState.putInt(TOTAL_CURRENT, totalCurrent);
        super.onSaveInstanceState(outState);
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
        xAxis.setAxisMaximum(28f);

        //mLineChart.setOnChartValueSelectedListener(this);
    }

    private void initPieChart(View view) {
        mPieChart = (PieChart) view.findViewById(R.id.pie_chart_month);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        //饼图与文字描述的padding
        mPieChart.setExtraOffsets(5, 10, 50, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);//拖动饼图 松手后滑动的灵敏度

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
    }

    public void setData(int year, int month) {
        this.year = year;
        this.month = month;
        if(mLineChart == null || mPieChart == null){
            Logger.w("mLineChart/mPieChart == null, return...");
            return;
        }
        setLineData();
        setPieData();
    }

    private void setLineData() {
        //本月数据
        List<Entry> entries = getLineEntries(getList(year, month));
        //上月数据
        List<Entry> entriesLastMonth = null;
        if (isOpenComparison) {
            int lastMonth = month - 1;
            int lastMonthY = year;
            if (month == 1) {//如果本月是1月，则上一月是12月  同时年份-1
                lastMonth = 12;
                lastMonthY = year - 1;
            }
            entriesLastMonth = getLineEntries(getList(lastMonthY, lastMonth));
            if (entriesLastMonth == null || entriesLastMonth.size() == 0) {
                ToastUtil.showToast(getActivity(), getString(R.string.no_last_month_data));
                cb_comparison.setChecked(false);
                isOpenComparison = false;
                return;
            }
        }

        if (entries != null && entries.size() > 0 || entriesLastMonth != null && entriesLastMonth.size() > 0) {
            //一条线就是一组数据集
            LineDataSet dataSetCurrentMonth, dataSetLastMonth;
            LineData mChartData = mLineChart.getData();
            if (mChartData != null && mChartData.getDataSetCount() > 0) {
                dataSetCurrentMonth = (LineDataSet) mChartData.getDataSetByIndex(0);
                //setLineStyle(dataSetCurrentMonth,getResources().getColor(R.color.mainColor));
                dataSetCurrentMonth.setValues(entries);
                if (isOpenComparison) {
                    if (entriesLastMonth != null && entriesLastMonth.size() > 0) {
                        dataSetLastMonth = (LineDataSet) mChartData.getDataSetByIndex(1);
                        if (dataSetLastMonth == null) {//还未添加过数据
                            dataSetLastMonth = new LineDataSet(entriesLastMonth, "上月共花费：" + totalPrevious + "元");
                            setLineStyle(dataSetLastMonth, getResources().getColor(R.color.gray_line));

                            LineData lineData = mLineChart.getLineData();
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
                        mLineChart.getLineData().removeDataSet(dataSetLastMonth);
                }
                mChartData.notifyDataChanged();
                mLineChart.notifyDataSetChanged();
            } else {

                dataSetCurrentMonth = new LineDataSet(entries, "本月共花费：" + totalCurrent + "元"); // add entries to dataset

                setLineStyle(dataSetCurrentMonth, getResources().getColor(R.color.mainColor));

                LineData lineData;
                if (isOpenComparison && entriesLastMonth != null && entriesLastMonth.size() > 0) {
                    dataSetLastMonth = new LineDataSet(entriesLastMonth, "上月共花费：" + totalPrevious + "元");
                    setLineStyle(dataSetLastMonth, getResources().getColor(R.color.gray_text));
                    lineData = new LineData(dataSetCurrentMonth, dataSetLastMonth);
                } else {
                    lineData = new LineData(dataSetCurrentMonth);
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
    }

    private void setPieData() {
        if (mPieChart == null)
            return;

        List<PieEntry> entries = getPieEntries(getList(year, month));

        if (entries.size() > 0) {

            PieDataSet dataSet;
            PieData pieData = mPieChart.getData();

            if (pieData != null && pieData.getDataSetCount() > 0) {

                dataSet = (PieDataSet) pieData.getDataSet();
                dataSet.setValues(entries);

                pieData.notifyDataChanged();
                mPieChart.notifyDataSetChanged();
            } else {
                dataSet = new PieDataSet(entries, "共花费" + totalCurrent + "元");

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
        l.setDrawInside(false);
        l.setYEntrySpace(3f);//描述文字之间的间隔
        l.setYOffset(8f);//描述文字marginTop

        // entry label styling
        //在扇形区内隐藏每个类别的描述
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setEntryLabelColor(Color.GRAY);
        //mPieChart.setEntryLabelTypeface(mTfRegular);
        mPieChart.setEntryLabelTextSize(12f);
    }

    //设置曲线样式
    private void setLineStyle(LineDataSet dataSet, int color) {
        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.BLACK); // styling, ...
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(2f);
        dataSet.setLineWidth(1.8f);
        dataSet.setValueTextSize(8f);
        dataSet.setHighLightColor(Color.TRANSPARENT);
        dataSet.setHighlightEnabled(false);
    }

    private List<Entry> getLineEntries(List<Account> list) {
        List<Entry> entries = new ArrayList<>();
        if (list == null || list.size() == 0)
            return entries;
        //key:日期，value:该日对应的总花费
        Map<Integer, Float> temp = new HashMap<>();
        int total = 0;
        for (Account account : list) {
            Integer day = account.getDay();
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
            lastDay = list.get(list.size() - 1).getDay();
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

    private List<PieEntry> getPieEntries(List<Account> list) {
        List<PieEntry> entries = new ArrayList<>();
        if (list == null || list.size() == 0)
            return entries;
        //key:type  value:该类型对应的总花费
        Map<Integer, Float> types = new HashMap<>();
        for (Account account : list) {
            Float money = account.getMoney();

            Integer type = account.getType();
            if (type == null)
                type = Constants.TYPES.length - 1;//设置为默认值
            if (types.containsKey(type)) {
                money += types.get(type);
            }
            types.put(type, money);
        }

        for (Integer type : types.keySet()) {
            float money = types.get(type);
            entries.add(new PieEntry(money, Constants.TYPES[type] + money + "元"));
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

    }

    @Override
    public void onNothingSelected() {

    }
}
