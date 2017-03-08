package com.ly.recorder.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.customtitlebar.CustomTitleBar;
import com.ly.recorder.App;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.adapter.HistoryAdapter;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.ToastUtil;
import com.ly.recorder.view.ListPopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_money, et_remark;
    private TextView tv_save, tv_record_type;
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
        topTitleBar.setOnRightClickLitener(new CustomTitleBar.OnRightClickLitener() {
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
        tv_record_type = (TextView) findViewById(R.id.tv_record_type);
        tv_save.setOnClickListener(this);
        tv_record_type.setOnClickListener(this);
    }

    private void setAdapter() {
        accountManager = new AccountManager();
        mlist = accountManager.queryForNum(30);
        if (mlist == null)
            mlist = new ArrayList<>();
        adapter = new HistoryAdapter(mlist, this);
        lv_history.setAdapter(adapter);
    }

    private void save() {
        try {
            float money = Float.parseFloat(et_money.getText().toString());

            String remark = et_remark.getText().toString();

            Calendar ca = Calendar.getInstance();
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH) + 1;
            int date = ca.get(Calendar.DATE);

            Account account = new Account();
            account.setMoney(money);
            account.setRemark(remark);
            account.setYear(year);
            account.setMonth(month);
            account.setDate(date);
            account.setTime(System.currentTimeMillis());
            account.setType(type);

            long rawId = App.getInstance().getDaoSession().getAccountDao().insertOrReplace(account);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                hideSoftKeyboard();
                save();
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
            listPopupWindow = new ListPopupWindow(this, list, new ListPopupWindow.OnPopuItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    type = position;
                    tv_record_type.setText(Constants.TYPES[position]);
                }
            });
        }
        //listPopupWindow.showAsDropDown(view);
        listPopupWindow.showAsDropDown(view, 50, Gravity.LEFT);
    }
}
