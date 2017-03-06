package com.ly.recorder.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.customtitlebar.CustomTitleBar;
import com.ly.recorder.App;
import com.ly.recorder.R;
import com.ly.recorder.adapter.HistoryAdapter;
import com.ly.recorder.db.Account;
import com.ly.recorder.db.AccountManager;
import com.ly.recorder.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity {

    private EditText et_money, et_remark;
    private TextView tv_save;
    private ListView lv_history;
    private HistoryAdapter adapter;
    private List<Account> mlist;
    private AccountManager accountManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setAdapter();
    }

    private void initViews() {
        topTitleBar.setTitle_text(getString(R.string.title_record));
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
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                save();
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
}
