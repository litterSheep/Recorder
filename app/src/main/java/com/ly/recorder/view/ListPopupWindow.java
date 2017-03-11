package com.ly.recorder.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ly.recorder.R;
import com.ly.recorder.utils.PixelUtil;

import java.util.List;

public class ListPopupWindow extends PopupWindow {
    private ListPopupWindowAdapter mListPopupWindowAdapter;
    private Activity mContext;
    private List<String> mStringList;
    private OnPopupItemClickListener myOnItemClickListener;

    public ListPopupWindow(Activity context, List<String> list, OnPopupItemClickListener listener) {
        super(context);
        this.myOnItemClickListener = listener;
        this.mContext = context;
        this.mStringList = list;
        if (list == null)
            return;

        float titleItemHeight = PixelUtil.dp2px(context, 50f);
        int height;
        if (list.size() > 6) {
            height = (int) (titleItemHeight * 5);
        } else {
            height = (int) (titleItemHeight * list.size());
        }
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_list, null);
        this.setContentView(contentView);
        ListView lv = (ListView) contentView.findViewById(R.id.lv_more);
        if (mListPopupWindowAdapter == null) {
            mListPopupWindowAdapter = new ListPopupWindowAdapter();
        }
        lv.setAdapter(mListPopupWindowAdapter);
        this.setHeight(height);
        this.setWidth(PixelUtil.dp2px(mContext, 100f));
        this.setFocusable(false);
        this.setOutsideTouchable(true);//点击外部消失
        //防止虚拟软键盘被弹出菜单遮住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.pop_anim);
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
    }

    public void setOnItemClickListener(OnPopupItemClickListener listener) {
        this.myOnItemClickListener = listener;
    }

    public void setList(List<String> mDataList) {
        if (this.mStringList != null)
            this.mStringList.clear();
        this.mStringList = mDataList;
        if (mListPopupWindowAdapter != null) {
            mListPopupWindowAdapter.notifyDataSetChanged();
        }
    }

    public interface OnPopupItemClickListener {
        void OnItemClick(int position);
    }

    private class ListPopupWindowAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mStringList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_popu, null);
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_pop_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myOnItemClickListener.OnItemClick(position);
                    ListPopupWindow.this.dismiss();
                }
            });
            viewHolder.mTextView.setText(mStringList.get(position));
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mTextView;
    }

}
