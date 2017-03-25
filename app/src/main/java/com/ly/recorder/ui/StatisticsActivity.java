package com.ly.recorder.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.RadioGroup;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.PreferencesUtils;
import com.ly.recorder.utils.TimeUtil;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.view.CustomTitleBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ly on 2017/3/3 14:01.
 */

public class StatisticsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private final String YEAR = "year", MONTH = "month", DAY = "day";
    private final String CURRENT_INDEX = "currentIndex";
    private int currentIndex = 1;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private AccountManager accountManager;
    private int currentYear, currentMonth, currentDay;//当前年月日
    private int selectedYear, selectedMonth, selectedDay;//筛选选择的年月日
    private List<Fragment> fragments;
    private FragmentDay fragmentDay;
    private FragmentMonth fragmentMonth;
    private FragmentTogether fragmentTogether;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initViews();
        if (savedInstanceState != null) {
            selectedYear = savedInstanceState.getInt(YEAR);
            selectedMonth = savedInstanceState.getInt(MONTH);
            selectedDay = savedInstanceState.getInt(DAY);
            currentIndex = savedInstanceState.getInt(CURRENT_INDEX);
            if (selectedYear != 0)
                topTitleBar.setRight_button_text(selectedYear + "-" + selectedMonth + "-" + selectedDay);

            fragmentDay = (FragmentDay) getFragmentManager().findFragmentByTag(FragmentDay.class.getName());
            if (fragmentDay == null)
                fragmentDay = FragmentDay.newInstance();
            fragmentMonth = (FragmentMonth) getFragmentManager().findFragmentByTag(FragmentMonth.class.getName());
            if (fragmentMonth == null)
                fragmentMonth = FragmentMonth.newInstance();
            fragmentTogether = (FragmentTogether) getFragmentManager().findFragmentByTag(FragmentTogether.class.getName());
            if (fragmentTogether == null)
                fragmentTogether = FragmentTogether.newInstance();
        } else {
            fragmentDay = FragmentDay.newInstance();
            fragmentMonth = FragmentMonth.newInstance();
            fragmentTogether = FragmentTogether.newInstance();


//            getFragmentManager().beginTransaction()
//                    .add(R.id.contentContainer, fragmentDay, FragmentDay.class.getName())
//                    .add(R.id.contentContainer, fragmentMonth, FragmentMonth.class.getName())
//                    .add(R.id.contentContainer, fragmentTogether, FragmentTogether.class.getName())
//                    .commit();
        }
        initDatePicker();

        initFragment();
        selectTab(currentIndex);
    }

    private void initViews() {

        topTitleBar.setTitle_text(getString(R.string.title_statistics));
        topTitleBar.setRight_button_imageId(R.mipmap.date);
        topTitleBar.setOnRightClickListener(new CustomTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                datePickerDialog.show(getSupportFragmentManager(), "datePicker");
            }
        });
        topTitleBar.setOnTitleClickListener(new CustomTitleBar.OnTitleClickListener() {
            @Override
            public void onTitleClick() {
                refreshData(currentYear, currentMonth, currentDay);
            }
        });
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rbt_container);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                int index;
                switch (checkedId) {
                    case R.id.rbt_day:
                        index = 0;
                        break;
                    case R.id.rbt_year:
                        index = 2;
                        break;
                    default:
                        index = 1;
                        break;
                }
                selectTab(index);
            }
        });

    }

    private void initDatePicker() {

        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        accountManager = new AccountManager();
        int startYear = accountManager.queryMinYear();

        datePickerDialog = DatePickerDialog.newInstance(this
                , currentYear
                , currentMonth - 1//控件内部处理了，所以不做处理
                , currentDay
                , true);
        if (startYear >= currentYear) {
            startYear = currentYear - 1;
        }
        datePickerDialog.setYearRange(startYear, currentYear);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.setOnDateSetListener(this);

        //时间选择dialog
//        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this
//                , calendar.get(Calendar.HOUR_OF_DAY)
//                ,calendar.get(Calendar.MINUTE)
//                , false, false);
    }

    private void initFragment() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        } else {
            fragments.clear();
        }

        fragments.add(fragmentDay);
        fragments.add(fragmentMonth);
        fragments.add(fragmentTogether);

        //初始化fragment中的数据
        if (selectedYear == 0) {
            fragmentDay.initData(accountManager, currentYear, currentMonth, currentDay);
            fragmentMonth.initData(accountManager, currentYear, currentMonth);
            fragmentTogether.initData(accountManager, currentYear);
        } else {
            fragmentDay.initData(accountManager, selectedYear, selectedMonth, selectedDay);
            fragmentMonth.initData(accountManager, selectedYear, selectedMonth);
            fragmentTogether.initData(accountManager, selectedYear);
        }
    }

    private void selectTab(int index) {

        Fragment fragment = fragments.get(index);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        for (Fragment fragment1 : fragments) {
            if (fragment1.isAdded() && !fragment1.equals(fragment)) {
                ft.hide(fragment1);
            }
        }
        if (!fragment.isAdded()) {
            if (fragment instanceof FragmentDay) {
                ft.add(R.id.contentContainer, fragment, FragmentDay.class.getName());
            } else if (fragment instanceof FragmentMonth) {
                ft.add(R.id.contentContainer, fragment, FragmentMonth.class.getName());
            } else {
                ft.add(R.id.contentContainer, fragment, FragmentTogether.class.getName());
            }
        } else {//显示之前隐藏的fragment
            ft.show(fragment);
        }
        ft.commit();

        currentIndex = index;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        selectedYear = year;
        selectedMonth = month + 1;//calendar中月份是从0开始，所以+1
        selectedDay = day;

        refreshData(selectedYear, selectedMonth, selectedDay);

        boolean isShow = PreferencesUtils.getBoolean(this, Constants.PREFERENCES_FLAG);
        if (!isShow) {
            ToastUtil.showToast(this, getString(R.string.click_title_hint));
            PreferencesUtils.putBoolean(this, Constants.PREFERENCES_FLAG, !isShow);
        }
    }

    private void refreshData(int year, int month, int day) {
        topTitleBar.setNullRightButtonImage();
        String title;
        if (TimeUtil.isCurrentDay(year, month, day)) {
            title = "今天";
        } else {
            title = year + "-" + month + "-" + day;
        }
        topTitleBar.setRight_button_text(title);

        fragmentDay.setData(year, month, day);
        fragmentMonth.setData(year, month);
        fragmentTogether.setData(year);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(YEAR, selectedYear);
        outState.putInt(MONTH, selectedMonth);
        outState.putInt(DAY, selectedDay);
        outState.putInt(CURRENT_INDEX, currentIndex);

        super.onSaveInstanceState(outState);
    }
}
