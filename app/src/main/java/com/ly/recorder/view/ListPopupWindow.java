package com.ly.recorder.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ly.recorder.R;
import com.ly.recorder.adapter.TypeSectionAdapter;
import com.ly.recorder.entity.SectionType;
import com.ly.recorder.entity.Type;
import com.ly.recorder.utils.PixelUtil;

import java.util.List;

public class ListPopupWindow extends PopupWindow {
    RecyclerView recyclerview;
    TypeSectionAdapter typeAdapter;
    private Activity mContext;
    private List<SectionType> mList;
    private OnPopupItemClickListener onItemClickListener;

    public ListPopupWindow(Activity context, List<SectionType> list) {
        super(context);
        this.mContext = context;
        this.mList = list;
        if (list == null)
            return;

        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_list, null);
        this.setContentView(contentView);
        recyclerview = (RecyclerView) contentView.findViewById(R.id.rv_type);
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        typeAdapter = new TypeSectionAdapter(R.layout.item_popu, R.layout.item_popu_header, mList);
        typeAdapter.openLoadAnimation();
        typeAdapter.isFirstOnly(false);//每个item是否只显示一次动画
        typeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (onItemClickListener != null) {
                    Type t = mList.get(position).t;
                    onItemClickListener.OnItemClick(t.type, t.item);
                }
                dismiss();
            }
        });
        recyclerview.setAdapter(typeAdapter);

        this.setHeight(PixelUtil.dp2px(mContext, 160));
        this.setWidth(PixelUtil.dp2px(mContext, 250));
        this.setFocusable(false);
        this.setOutsideTouchable(true);//点击外部消失
        //防止虚拟软键盘被弹出菜单遮住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.pop_anim);
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
    }

    public void setList(List<SectionType> mDataList) {
        if (this.mList != null)
            this.mList.clear();
        this.mList = mDataList;
        if (typeAdapter != null) {
            typeAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnPopupItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnPopupItemClickListener {
        void OnItemClick(int type, String typeName);
    }


}
