package com.ly.recorder;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ly.recorder.db.Account;
import com.ly.recorder.db.greendao.AccountDao;
import com.ly.recorder.utils.ToastUtil;

import java.util.Calendar;
import java.util.Date;

public class RecordActivity extends AppCompatActivity {

    private EditText et_breakfast,et_lunch,et_dinner,et_other,et_remark;
    private TextView tv_commit,tv_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initViews();
    }

    private void initViews() {

        et_breakfast = (EditText) findViewById(R.id.et_breakfast);
        et_lunch = (EditText) findViewById(R.id.et_lunch);
        et_dinner = (EditText) findViewById(R.id.et_dinner);
        et_other = (EditText) findViewById(R.id.et_other);
        et_remark = (EditText) findViewById(R.id.et_remark);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_total = (TextView) findViewById(R.id.tv_total);

        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void commit(){
        float breakfast = Float.parseFloat(et_breakfast.getText().toString());
        float lunch = Float.parseFloat(et_lunch.getText().toString());
        float dinner = Float.parseFloat(et_dinner.getText().toString());
        float other = Float.parseFloat(et_other.getText().toString());
        float total  = breakfast+lunch+dinner+other;

        tv_total.setText("共计："+total +" 元");
        String remark = et_remark.getText().toString();

        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        Account account = new Account();
        account.setBreakfast(breakfast);
        account.setLunch(lunch);
        account.setDinner(dinner);
        account.setOther(other);
        account.setTime(System.currentTimeMillis());
        account.setRemark(remark);
        account.setYear(year);
        account.setMonth(month);

        long rawId = App.getInstance().getDaoSession().getAccountDao().insertOrReplace(account);
        if(rawId > 0){
            ToastUtil.showToast(this,getString(R.string.save_success));
        }
    }
}
