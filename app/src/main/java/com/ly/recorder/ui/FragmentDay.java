package com.ly.recorder.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDay extends Fragment implements OnChartValueSelectedListener {
    private PieChart mChart;
    private List<Account> accounts;

    public FragmentDay() {
    }

    public static FragmentDay newInstance() {
        FragmentDay fragment = new FragmentDay();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_day, container, false);
        initChart(view);
        setData(accounts);
        return view;
    }

    private void initChart(View view) {
        mChart = (PieChart) view.findViewById(R.id.chart_day);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        //饼图与文字描述的padding
        mChart.setExtraOffsets(5, 10, 25, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(55);//中央圆孔背景透明度

        mChart.setHoleRadius(20f);//中央圆孔的大小
        mChart.setTransparentCircleRadius(20f);//中央圆孔透明圈大小

        mChart.setDrawCenterText(false);//中间的文字
        //mChart.setCenterTextTypeface(mTfLight);
        //mChart.setCenterText(getString(R.string.app_name));

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);
    }

    public void setData(List<Account> list) {
        accounts = list;
        if (mChart == null || accounts == null)
            return;

        ArrayList<PieEntry> entries = new ArrayList<>();
        float total = 0;
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

        if (entries.size() > 0) {

            PieDataSet dataSet;
            PieData pieData = mChart.getData();

            if (pieData != null && pieData.getDataSetCount() > 0) {

                dataSet = (PieDataSet) pieData.getDataSet();
                dataSet.setValues(entries);

                pieData.notifyDataChanged();
                mChart.notifyDataSetChanged();
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
                mChart.setData(data);
            }
        } else {
            mChart.clear();
        }
        mChart.highlightValues(null);
        //mChart.invalidate();

        mChart.setNoDataText(getString(R.string.show_no_data));
        mChart.setNoDataTextColor(getResources().getColor(R.color.gray_text));

        mChart.animateY(1200, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(20f);
        l.setYEntrySpace(20f);
        l.setYOffset(0f);

        // entry label styling
        //在扇形区内隐藏每个类别的描述
        mChart.setDrawEntryLabels(false);
        mChart.setEntryLabelColor(Color.GRAY);
        //mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);
    }

    public List<Account> getAccounts() {
        return accounts;
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
