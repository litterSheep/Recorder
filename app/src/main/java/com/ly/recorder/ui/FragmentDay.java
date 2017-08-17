package com.ly.recorder.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.adapter.HistoryAdapter;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.TimeUtil;
import com.ly.recorder.utils.logger.Logger;
import com.ly.recorder.view.NoScrollListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDay extends Fragment {
    private static final int BAR_OUT = 1, BAR_IN = 0;
    private PieChart mPieChart;
    private HorizontalBarChart mBarChart;
    private List<Account> accounts;
    private float total;
    private TextView tv_history;
    private NoScrollListView listView;
    private HistoryAdapter adapter;
    private int year, month, day;//选定的年月日
    private AccountManager accountManager;
    private Typeface mTfLight;
    private ScrollView sv;
    private int flag;

    public FragmentDay() {
    }

    public static FragmentDay newInstance() {
        FragmentDay fragment = new FragmentDay();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //在配置变化(如屏幕旋转，系统字体设置变化)的时候将这个fragment保存下来
        //setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_day, container, false);
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        tv_history = (TextView) view.findViewById(R.id.tv_history_title);
        listView = (NoScrollListView) view.findViewById(R.id.lv_history);
        sv = (ScrollView) view.findViewById(R.id.sv_day);

        initBarChart(view);
        initPieChart(view);
        setData(year, month, day);

        //int a = 5 / 0;
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Logger.d("hidden........" + hidden);
        super.onHiddenChanged(hidden);
        if (hidden) {
            flag = sv.getScrollY();
        } else {
            sv.scrollTo(0, flag);
        }
    }

    public void initData(AccountManager accountManager, int year, int month, int day) {
        this.accountManager = accountManager;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private List<Account> getList(int type, int y, int m, int d) {
        if (accountManager == null)
            accountManager = new AccountManager();
        return accountManager.queryForDay(type, y, m, d);
    }

    private void initBarChart(View view) {
        mBarChart = (HorizontalBarChart) view.findViewById(R.id.chart_bar_h);
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if (e.getY() <= 0)
                    return;
                int type;
                if (e.getX() == BAR_OUT) {//收入
                    type = Constants.TYPE_OUT;
                } else {//支出
                    type = Constants.TYPE_IN;
                }
                adapter.setList(getList(type, year, month, day));
                List<PieEntry> entries = getPieEntries(type);
                setPieData(entries, type);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        // mBarChart.setHighlightEnabled(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawValueAboveBar(true);

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

    private void initPieChart(View view) {
        mPieChart = (PieChart) view.findViewById(R.id.chart_day);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        //饼图与文字描述的padding
        mPieChart.setExtraOffsets(5, 10, 25, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.85f);

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(55);//中央圆孔背景透明度

        mPieChart.setHoleRadius(20f);//中央圆孔的大小
        mPieChart.setTransparentCircleRadius(23f);//中央圆孔透明圈大小

        mPieChart.setDrawCenterText(false);//中间的文字
        //mPieChart.setCenterTextTypeface(mTfLight);
        //mPieChart.setCenterText(getString(R.string.app_name));

        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        mPieChart.setEntryLabelTypeface(mTfLight);
    }

    private void setAdapter() {
        accounts = accountManager.queryForDay(0, year, month, day);
        if (accounts == null)
            accounts = new ArrayList<>();
        if (adapter == null) {
            adapter = new HistoryAdapter(accounts, getActivity());
            listView.setAdapter(adapter);
        } else {
            adapter.setList(accounts);
        }
        if (accounts.size() == 0) {
            tv_history.setText(getString(R.string.no_day_history));
        } else {
            if (TimeUtil.isCurrentDay(year, month, day)) {
                tv_history.setText(getString(R.string.day_history));
            } else {
                tv_history.setText(year + "年" + month + "月" + day + "日 账单");
            }
        }
        sv.scrollTo(0, 0);
    }

    public void setData(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        if (mBarChart != null) {
            setBarData();
            setAdapter();
            List<PieEntry> entries = getPieEntries(Constants.TYPE_OUT);
            if (entries == null || entries.size() == 0) {
                entries = getPieEntries(Constants.TYPE_IN);
            }
            setPieData(entries, Constants.TYPE_OUT);
        } else {
            Logger.w("Chart == null, return...");
        }
    }

    private void setBarData() {

        List<BarEntry> barEntriesOut = getBarEntries(Constants.TYPE_OUT);
        List<BarEntry> barEntriesIn = getBarEntries(Constants.TYPE_IN);

        String labelOut = getString(R.string.type_out), labelIn = getString(R.string.type_in);
        if (barEntriesOut.size() > 0 || barEntriesIn.size() > 0) {
            BarDataSet barDataOut, barDataIn;

            if (mBarChart.getData() != null &&
                    mBarChart.getData().getDataSetCount() > 0) {

                barDataOut = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
                barDataOut.setValues(barEntriesOut);
                barDataOut.setLabel(labelOut);

                barDataIn = (BarDataSet) mBarChart.getData().getDataSetByIndex(1);
                barDataIn.setValues(barEntriesIn);
                barDataIn.setLabel(labelIn);

                mBarChart.getData().notifyDataChanged();
                mBarChart.notifyDataSetChanged();
            } else {
                barDataOut = new BarDataSet(barEntriesOut, labelOut);
                barDataIn = new BarDataSet(barEntriesIn, labelIn);

                barDataOut.setColors(getResources().getColor(R.color.mainColor1));
                //barDataOut.setBarShadowColor(Color.rgb(203, 203, 203));
                barDataIn.setColors(getResources().getColor(R.color.green));
                //barDataIn.setBarShadowColor(Color.rgb(203, 203, 203));
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(barDataOut);
                dataSets.add(barDataIn);

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
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(10f);
        l.setXEntrySpace(8f);//描述之间的间隔
    }

    private void setPieData(List<PieEntry> entries, int type) {

        if (entries.size() > 0) {

            PieDataSet dataSet;
            PieData pieData = mPieChart.getData();

            String label;
            if (type == Constants.TYPE_OUT) {
                label = "共支出" + total;
            } else {
                label = "共收入" + total;
            }
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
                dataSet.setSelectionShift(6f);//点击扇形后多出的部分

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
                data.setValueTextSize(16f);
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
        l.setTypeface(mTfLight);
        l.setYEntrySpace(5f);//描述文字之间的间隔
        l.setYOffset(10f);//描述文字marginTop

        // entry label styling
        //在扇形区内隐藏每个类别的描述
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setEntryLabelColor(Color.GRAY);
        //mPieChart.setEntryLabelTypeface(mTfRegular);
        mPieChart.setEntryLabelTextSize(12f);
    }

    private List<BarEntry> getBarEntries(int type) {
        List<Account> list = getList(type, year, month, day);
        List<BarEntry> entries = new ArrayList<>();
        int total = 0;
        for (Account account : list) {
            total += account.getMoney();
        }
        entries.add(new BarEntry(type == Constants.TYPE_OUT ? BAR_OUT : BAR_IN, total));

        return entries;
    }

    private List<PieEntry> getPieEntries(int type) {
        List<Account> list = getList(type, year, month, day);
        List<PieEntry> entries = new ArrayList<>();
        //key:typeIndex  value:该类型对应的总花费
        Map<Integer, Float> types = new HashMap<>();
        total = 0;
        for (Account account : list) {
            Float money = account.getMoney();
            total += money;

            Integer typeIndex = account.getTypeIndex();
            if (typeIndex == null) {
                //设置为默认值
                if (type == Constants.TYPE_OUT) {
                    typeIndex = Constants.TYPES_OUT.length - 1;
                } else {
                    typeIndex = Constants.TYPES_IN.length - 1;
                }
            }
            if (types.containsKey(typeIndex)) {
                money += types.get(typeIndex);
            }
            types.put(typeIndex, money);
        }

        for (Integer typeIndex : types.keySet()) {
            float money = types.get(typeIndex);
            String name;
            if (type == Constants.TYPE_OUT) {
                name = Constants.TYPES_OUT[typeIndex];
            } else {
                name = Constants.TYPES_IN[typeIndex];
            }
            entries.add(new PieEntry(money, name + money));
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

}
