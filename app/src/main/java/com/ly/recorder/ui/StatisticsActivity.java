package com.ly.recorder.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.PreferencesUtils;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;
import com.ly.recorder.view.CustomTitleBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ly on 2017/3/3 14:01.
 */

public class StatisticsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private ArrayList<Fragment> fragments;
    private int currentIndex = 1;
    private FragmentManager fragmentManager;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private AccountManager accountManager;
    private int currentYear, currentMonth, currentDay;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initViews();

        initFragment();

        initDatePicker();
    }

    private void initViews() {

        topTitleBar.setTitle_text(getString(R.string.title_statistics));
        topTitleBar.setRight_button_text("筛选");
        topTitleBar.setOnRightClickListener(new CustomTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                datePickerDialog.show(getSupportFragmentManager(), "datePicker");
            }
        });
        topTitleBar.setOnTitleClickListener(new CustomTitleBar.OnTitleClickListener() {
            @Override
            public void onTitleClick() {
                queryData(currentYear, currentMonth, currentDay);
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

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();

        if (fragments == null) fragments = new ArrayList<>();
        fragments.add(FragmentDay.newInstance());
        fragments.add(FragmentMonth.newInstance());
        fragments.add(FragmentYear.newInstance());

        fragmentManager.beginTransaction().add(R.id.contentContainer, fragments.get(currentIndex)).commit();

        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        accountManager = new AccountManager();

        //初始化fragment中的数据
        ((FragmentDay) fragments.get(0)).initData(accountManager.queryForDay(currentYear, currentMonth, currentDay));
        ((FragmentMonth) fragments.get(1)).initData(accountManager, currentYear, currentMonth);
        ((FragmentYear) fragments.get(2)).initData(accountManager.queryForYear(currentYear));
    }

    private void initDatePicker() {

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

    private void selectTab(int index) {
        if (fragments == null) {
            Logger.w("fragments == null...");
            return;
        }
        if (index != currentIndex) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragments.get(currentIndex));
            Fragment fragment = fragments.get(index);

            if (!fragment.isAdded()) {
                fragmentTransaction.add(R.id.contentContainer, fragment);
            } else {//显示之前隐藏的fragment
                fragmentTransaction.show(fragment);
            }
            fragmentTransaction.commit();
        }
        currentIndex = index;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        month++;//calendar中月份是从0开始，所以+1

        queryData(year, month, day);

        boolean isUnShow = PreferencesUtils.getBoolean(this, Constants.PREFRENCES_FLAG);
        if (!isUnShow) {
            ToastUtil.showToast(this, getString(R.string.click_title_hint));
            PreferencesUtils.putBoolean(this, Constants.PREFRENCES_FLAG, !isUnShow);
        }
    }

    private void queryData(int year, int month, int day) {
        topTitleBar.setRight_button_text(year + "-" + month + "-" + day);

        List<Account> accountsYear = accountManager.queryForYear(year);
        List<Account> accountsDay = accountManager.queryForDay(year, month, day);

        ((FragmentDay) fragments.get(0)).setData(accountsDay);
        ((FragmentMonth) fragments.get(1)).setData(year, month);

    }

}
