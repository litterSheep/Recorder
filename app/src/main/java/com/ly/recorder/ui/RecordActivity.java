package com.ly.recorder.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ly.recorder.App;
import com.ly.recorder.R;
import com.ly.recorder.db.Account;
import com.ly.recorder.utils.ToastUtil;

import java.util.Calendar;

public class RecordActivity extends BaseActivity {

    private EditText et_breakfast, et_lunch, et_dinner, et_other, et_remark;
    private TextView tv_commit, tv_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initViews();
    }

    private void initViews() {

        topTitleBar.setTitle_text(getString(R.string.title_record));

        et_breakfast = (EditText) findViewById(R.id.et_breakfast);
        et_lunch = (EditText) findViewById(R.id.et_lunch);
        et_dinner = (EditText) findViewById(R.id.et_dinner);
        et_other = (EditText) findViewById(R.id.et_other);
        et_remark = (EditText) findViewById(R.id.et_remark);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_total.setVisibility(View.GONE);
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        try {
            String a = et_breakfast.getText().toString();
            String b = et_lunch.getText().toString();
            String c = et_dinner.getText().toString();
            String d = et_other.getText().toString();
            if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b) && TextUtils.isEmpty(c) && TextUtils.isEmpty(d)) {
                ToastUtil.showToast(this, getString(R.string.empty_hint));
                return;
            }
            if (TextUtils.isEmpty(a))
                a = "0";
            if (TextUtils.isEmpty(b))
                b = "0";
            if (TextUtils.isEmpty(c))
                c = "0";
            if (TextUtils.isEmpty(d))
                d = "0";

            float breakfast = Float.parseFloat(a);
            float lunch = Float.parseFloat(b);
            float dinner = Float.parseFloat(c);
            float other = Float.parseFloat(d);
            float total = breakfast + lunch + dinner + other;

            tv_total.setVisibility(View.VISIBLE);
            tv_total.setText("共计：" + total + " 元");
            String remark = et_remark.getText().toString();

            Calendar ca = Calendar.getInstance();
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH) + 1;
            int date = ca.get(Calendar.DATE);

            Account account = new Account();
            account.setBreakfast(breakfast);
            account.setLunch(lunch);
            account.setDinner(dinner);
            account.setOther(other);
            account.setTotal(total);
            account.setTime(System.currentTimeMillis());
            account.setRemark(remark);
            account.setYear(year);
            account.setMonth(month);
            account.setDate(date);

            long rawId = App.getInstance().getDaoSession().getAccountDao().insertOrReplace(account);
            if (rawId > 0) {
                ToastUtil.showToast(this, getString(R.string.save_success));
            } else {
                ToastUtil.showToast(this, getString(R.string.save_fail));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(this, getString(R.string.save_fail));
        }

    }
}
