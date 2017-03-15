package com.ly.recorder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * scrollview嵌套listview
 * 通过重写onMeasure 解决滑动冲突
 *
 * @author lenovo
 */
public class NoScrollListView extends ListView {
    public boolean isMeasure;

    public NoScrollListView(Context context) {
        super(context);
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //计算itemView的宽高
        isMeasure = true;
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }

}