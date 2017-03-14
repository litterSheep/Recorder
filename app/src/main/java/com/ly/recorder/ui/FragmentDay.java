package com.ly.recorder.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
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
import com.ly.recorder.utils.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDay extends Fragment implements OnChartValueSelectedListener {
    private PieChart mPieChart;
    private List<Account> accounts;
    private float total;

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
        initPieChart(view);
        return view;
    }

    private void initPieChart(View view) {
        mPieChart = (PieChart) view.findViewById(R.id.chart_day);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        //饼图与文字描述的padding
        mPieChart.setExtraOffsets(5, 10, 25, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);

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

        // add a selection listener
        mPieChart.setOnChartValueSelectedListener(this);

        setData(accounts);
    }

    public void setData(List<Account> list) {
        accounts = list;
        if (mPieChart == null || accounts == null){
            Logger.w("mPieChart/accounts == null, return...");
            return;
        }

        List<PieEntry> entries = getPieEntries();

        if (entries.size() > 0) {

            PieDataSet dataSet;
            PieData pieData = mPieChart.getData();

            if (pieData != null && pieData.getDataSetCount() > 0) {

                dataSet = (PieDataSet) pieData.getDataSet();
                dataSet.setValues(entries);

                pieData.notifyDataChanged();
                mPieChart.notifyDataSetChanged();
            } else {
                dataSet = new PieDataSet(entries, "共花费" + total + "元");

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
        l.setYEntrySpace(5f);//描述文字之间的间隔
        l.setYOffset(10f);//描述文字marginTop

        // entry label styling
        //在扇形区内隐藏每个类别的描述
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setEntryLabelColor(Color.GRAY);
        //mPieChart.setEntryLabelTypeface(mTfRegular);
        mPieChart.setEntryLabelTextSize(12f);
    }

    private List<PieEntry> getPieEntries() {
        List<PieEntry> entries = new ArrayList<>();
        //key:type  value:该类型对应的总花费
        Map<Integer, Float> types = new HashMap<>();
        for (Account account : accounts) {
            Float money = account.getMoney();
            total += money;

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

    public void initData(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
