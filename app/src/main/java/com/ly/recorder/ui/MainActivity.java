package com.ly.recorder.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.adapter.HistoryAdapter;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.entity.SectionType;
import com.ly.recorder.entity.Type;
import com.ly.recorder.utils.ScreenUtil;
import com.ly.recorder.utils.TimeUtil;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;
import com.ly.recorder.view.CustomTitleBar;
import com.ly.recorder.view.ListPopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_money, et_remark;
    private TextView tv_save, tv_record_type, tv_history, tv_record_date;
    private ListView lv_history;
    private HistoryAdapter adapter;
    private List<Account> mlist;
    private AccountManager accountManager;
    private ListPopupWindow listPopupWindow;
    private int type = Constants.TYPE_OUT;
    private int typeIndex = Constants.TYPES_OUT.length - 1;//收入/支出对应的数组下标

    private KonfettiView konfettiView;
    private DatePickerDialog datePickerDialog;
    private int selectedYear, selectedMonth, selectedDay;//选择的年月日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setAdapter();

        //generateData();
    }

    private void initViews() {
        topTitleBar.setTitle_text(getString(R.string.title_record));
        topTitleBar.setShow_left_button(false);
        topTitleBar.setRight_button_imageId(R.mipmap.statistics);
        topTitleBar.setOnRightClickListener(new CustomTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                startMyActivity(StatisticsActivity.class);
            }
        });

        lv_history = (ListView) findViewById(R.id.lv_history);
        et_money = (EditText) findViewById(R.id.et_money);
        et_remark = (EditText) findViewById(R.id.et_remark);
        tv_save = (TextView) findViewById(R.id.tv_commit);
        tv_history = (TextView) findViewById(R.id.tv_history_title);
        tv_record_type = (TextView) findViewById(R.id.tv_record_type);
        tv_record_date = (TextView) findViewById(R.id.tv_record_date);
        tv_save.setOnClickListener(this);
        tv_record_type.setOnClickListener(this);
        tv_record_date.setOnClickListener(this);
        lv_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.delete_dialog_title))
                        .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                accountManager.delete(mlist.get(position));
                                mlist.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setCancelable(true)
                        .show();

                return true;
            }
        });
        tv_save.setEnabled(false);
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (et_money.length() > 0) {
                    tv_save.setEnabled(true);
                } else {
                    tv_save.setEnabled(false);
                }
            }
        });

        konfettiView = (KonfettiView) findViewById(R.id.konfettiView);
        showBoom();
    }

    private void initDatePicker() {

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        accountManager = new AccountManager();
        int startYear = accountManager.queryMinYear();

        datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                                          int month, int day) {
                        month++;//calendar中月份是从0开始，所以+1
                        if (TimeUtil.isCurrentDay(year, month, day)) {
                            tv_record_date.setText("今天");
                            selectedYear = 0;
                            selectedMonth = 0;
                            selectedDay = 0;
                        } else {
                            tv_record_date.setText(year + "-" + month + "-" + day);
                            selectedYear = year;
                            selectedMonth = month;
                            selectedDay = day;
                        }

                        tv_record_date.setTextColor(getResources().getColor(R.color.mainColor1));
                    }
                }
                , currentYear
                , currentMonth - 1//控件内部处理了，所以不做处理
                , currentDay
                , true);
        if (startYear >= currentYear) {
            startYear = currentYear - 1;
        }
        datePickerDialog.setYearRange(startYear, currentYear);
        datePickerDialog.setCloseOnSingleTapDay(false);
    }

    private void setAdapter() {
        accountManager = new AccountManager();
        mlist = accountManager.queryForRecentNum(200);
        if (mlist == null)
            mlist = new ArrayList<>();
        adapter = new HistoryAdapter(mlist, this);
        lv_history.setAdapter(adapter);
        tv_history.setText(mlist.size() == 0 ? getString(R.string.no_history) : getString(R.string.history));
    }

    private void save() {
        try {
            float money = Float.parseFloat(et_money.getText().toString());

            if (money <= 0) {
                ToastUtil.showToast(this, getString(R.string.input_error));
                return;
            }
            String remark = et_remark.getText().toString();

            Calendar ca = Calendar.getInstance();
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH) + 1;
            int day = ca.get(Calendar.DATE);
            long timeStamp = System.currentTimeMillis();

            if (selectedYear != 0 && selectedMonth != 0 && selectedDay != 0) {
                // TODO: 2017/9/6 在这里可以判断日期是否是选择的今天以后的日期
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
                timeStamp = TimeUtil.getMillisecondByFormat(year + "-" + month + "-" + day, TimeUtil.FORMAT_DATE);
            }
            Account account = new Account();
            account.setMoney(money);
            account.setRemark(remark);
            account.setYear(year);
            account.setMonth(month);
            account.setDay(day);
            account.setTime(timeStamp);
            account.setType(type);
            account.setTypeIndex(typeIndex);

            long rawId = new AccountManager().insert(account);
            if (rawId > 0) {
                ToastUtil.showToast(this, getString(R.string.save_success));
                adapter.add(account);
                et_remark.setText("");
                et_money.setText("");
            } else {
                ToastUtil.showToast(this, getString(R.string.save_fail));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(this, getString(R.string.empty_hint));
        }
    }

    /**
     * 显示烟花动画
     * Created by ly on 2017/9/6 23:31
     */
    private void showBoom() {
        konfettiView.build()
                .addColors(Color.parseColor("#FFF9D4CE"), Color.parseColor("#FFDAE7CE"), Color.parseColor("#FFDCDBEA"), Color
                        .parseColor("#FFFFD96D"))
                .setDirection(0.0, 400.0)
                .setSpeed(3f, 20f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size.SMALL)
                .setPosition(500f, ScreenUtil.getScreenWidth(this) / 2f, 0f, ScreenUtil.getScreenHeight(this) / 2f)
                .stream(300, 800L);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                hideSoftKeyboard();
                save();
                showBoom();
                showBoom();
                showBoom();
                break;
            case R.id.tv_record_type:

                hideSoftKeyboard();
                showTypePopup(v);
                break;
            case R.id.tv_record_date:
                if (datePickerDialog == null)
                    initDatePicker();
                datePickerDialog.show(getSupportFragmentManager(), "datePicker");
                break;
            default:

                break;
        }
    }

    private void showTypePopup(View view) {
        if (listPopupWindow == null) {
            List<SectionType> list = new ArrayList<>();
            for (String s : Constants.TYPES_OUT) {
                SectionType sectionType = new SectionType(s.equals(Constants.TYPES_OUT[0]), getString(R.string.type_out));
                Type type = new Type(s, Constants.TYPE_OUT);
                sectionType.t = type;
                list.add(sectionType);
            }
            for (String s : Constants.TYPES_IN) {
                SectionType sectionType = new SectionType(s.equals(Constants.TYPES_IN[0]), getString(R.string.type_in));
                Type type = new Type(s, Constants.TYPE_IN);
                sectionType.t = type;
                list.add(sectionType);
            }
            listPopupWindow = new ListPopupWindow(this, list);
            listPopupWindow.setOnItemClickListener(new ListPopupWindow.OnPopupItemClickListener() {
                @Override
                public void OnItemClick(int type, String typeName) {
                    MainActivity.this.type = type;
                    typeIndex = getIndexByName(typeName, type);
                    tv_record_type.setText(typeName);
                    int color = getResources().getColor(R.color.mainColor1);
                    if (type == Constants.TYPE_IN)
                        color = getResources().getColor(R.color.green);
                    tv_record_type.setTextColor(color);
                }

            });
        }
        //listPopupWindow.showAsDropDown(view);
        listPopupWindow.showAsDropDown(view, 0, 0);
    }

    /**
     * 根据类别和名称来获取相对应类别数组的下标
     * Created by ly on 2017/3/24 10:14
     */
    private int getIndexByName(String name, int type) {
        if (type == Constants.TYPE_IN) {
            for (int i = 0; i < Constants.TYPES_IN.length; i++) {
                if (Constants.TYPES_IN[i].equals(name)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < Constants.TYPES_OUT.length; i++) {
                if (Constants.TYPES_OUT[i].equals(name)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 仅供生成测试数据之用
     * <p>
     * Created by ly on 2017/3/15 16:10
     */
    private void generateData() {
        AccountManager accountManager = new AccountManager();
        for (int year = 2014; year < 2018; year++) {
            for (int month = 1; month < 13; month++) {
                for (int day = 1; day < 29; day++) {
                    for (int count = 0; count < 5; count++) {//每天5条
                        Random random = new Random();
                        int min = 1;
                        int max = 2;
                        int type = random.nextInt(max) % (max - min + 1) + min;

                        float money = random.nextFloat() * 50 * (year - 2013);

                        if (type == Constants.TYPE_OUT) {
                            max = Constants.TYPES_OUT.length - 1;
                        } else {
                            max = Constants.TYPES_IN.length - 1;
                            money = money * 2;//模拟收入大于支出
                        }
                        int typeIndex = random.nextInt(max) % (max - min + 1) + min;

                        Account account = new Account();
                        account.setMoney(money);
                        account.setRemark("remark:" + year + "年" + month + "月" + day + "日");
                        account.setYear(year);
                        account.setMonth(month);
                        account.setDay(day);
                        account.setTime(System.currentTimeMillis());
                        account.setType(type);
                        account.setTypeIndex(typeIndex);

                        long insert = accountManager.insert(account);

                        Logger.i("id:" + insert + "  " + year + "年" + month + "月" + day + "日" + " money:" + money);
                    }
                }
            }
        }
    }
}
