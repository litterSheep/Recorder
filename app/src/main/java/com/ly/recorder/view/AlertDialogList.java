package com.ly.recorder.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.recorder.R;
import com.ly.recorder.adapter.HistoryAdapter;
import com.ly.recorder.db.Account;
import com.ly.recorder.utils.PixelUtil;

import java.util.List;

/**
 * Created by ly on 2017/3/25 10:32.
 */

public class AlertDialogList extends AlertDialog {

    private HistoryAdapter historyAdapter;
    private TextView tv_title;

    public AlertDialogList(Context context, String title, List<Account> list) {
        super(context);

        View v = LayoutInflater.from(context).inflate(R.layout.include_list, null);

        tv_title = (TextView) v.findViewById(R.id.tv_history_title);
        ListView listView = (ListView) v.findViewById(R.id.lv_history);
        tv_title.setText(title);
        tv_title.setTextSize(PixelUtil.sp2px(context, 8));
        historyAdapter = new HistoryAdapter(list, context);
        listView.setAdapter(historyAdapter);

        //设置动画
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.dialog_style);

        setView(v);
        setCancelable(true);
        show();
    }

    public void reSetData(String title, List<Account> list) {
        historyAdapter.setList(list);
        tv_title.setText(title);

        show();
    }
}
