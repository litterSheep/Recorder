package com.ly.recorder.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.adapter.HistoryAdapter;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.utils.logger.Logger;
import com.ly.recorder.view.CustomTitleBar;
import com.ly.recorder.view.ListPopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_money, et_remark;
    private TextView tv_save, tv_record_type, tv_history;
    private ListView lv_history;
    private HistoryAdapter adapter;
    private List<Account> mlist;
    private AccountManager accountManager;
    private ListPopupWindow listPopupWindow;
    private int type = Constants.TYPES.length - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setAdapter();
    }

    private void initViews() {
        topTitleBar.setTitle_text(getString(R.string.title_record));
        topTitleBar.setRight_button_text("统计");
        topTitleBar.setShow_left_button(false);
        topTitleBar.setOnRightClickListener(new CustomTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                startMyActivity(StatisticsActivity.class);
            }
        });
        topTitleBar.setOnLeftClickListener(new CustomTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftClick() {

            }
        });

        lv_history = (ListView) findViewById(R.id.lv_history);
        et_money = (EditText) findViewById(R.id.et_money);
        et_remark = (EditText) findViewById(R.id.et_remark);
        tv_save = (TextView) findViewById(R.id.tv_commit);
        tv_history = (TextView) findViewById(R.id.tv_history);
        tv_record_type = (TextView) findViewById(R.id.tv_record_type);
        tv_save.setOnClickListener(this);
        tv_record_type.setOnClickListener(this);
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
    }

    private void setAdapter() {
        accountManager = new AccountManager();
        mlist = accountManager.queryForNum(30);
        if (mlist == null)
            mlist = new ArrayList<>();
        adapter = new HistoryAdapter(mlist, this);
        lv_history.setAdapter(adapter);
        tv_history.setText(mlist.size() == 0 ? getString(R.string.no_history) : getString(R.string.history));
    }

    private void save() {
        try {
            float money = Float.parseFloat(et_money.getText().toString());

            String remark = et_remark.getText().toString();

            Calendar ca = Calendar.getInstance();
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH) + 1;
            int day = ca.get(Calendar.DATE);

            Account account = new Account();
            account.setMoney(money);
            account.setRemark(remark);
            account.setYear(year);
            account.setMonth(month);
            account.setDay(day);
            account.setTime(System.currentTimeMillis());
            account.setType(type);

            long rawId = new AccountManager().insert(account);
            if (rawId > 0) {
                ToastUtil.showToast(this, getString(R.string.save_success));
                adapter.add(account);
            } else {
                ToastUtil.showToast(this, getString(R.string.save_fail));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(this, getString(R.string.empty_hint));
        }
    }

    // ******仅供生成测试数据之用
    private void generateData() {
        AccountManager accountManager = new AccountManager();
        for (int year = 2014; year < 2017; year++) {
            for (int month = 1; month < 13; month++) {
                for (int day = 1; day < 29; day++) {
                    Random random = new Random();
                    int min = 0;
                    int max = 9;
                    int t = random.nextInt(max) % (max - min + 1) + min;

                    float money = random.nextFloat() * 50*(year-2013);

                    Account account = new Account();
                    account.setMoney(money);
                    account.setRemark("remark:" + year + "年" + month + "月" + day + "日");
                    account.setYear(year);
                    account.setMonth(month);
                    account.setDay(day);
                    account.setTime(System.currentTimeMillis());
                    account.setType(t);

                    long insert = accountManager.insert(account);

                    Logger.i("id:" + insert + "  " + year + "年" + month + "月" + day + "日" + " money:" + money);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                hideSoftKeyboard();
                save();
                generateData();
                break;
            case R.id.tv_record_type:

                showTypePopup(v);
                break;
            default:

                break;
        }
    }

    private void showTypePopup(View view) {
        if (listPopupWindow == null) {
            List<String> list = new ArrayList<>();
            for (String s : Constants.TYPES) {
                list.add(s);
            }
            listPopupWindow = new ListPopupWindow(this, list, new ListPopupWindow.OnPopupItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    type = position;
                    tv_record_type.setText(Constants.TYPES[position]);
                }
            });
        }
        //listPopupWindow.showAsDropDown(view);
        listPopupWindow.showAsDropDown(view, 0, Gravity.CENTER);
    }
}
