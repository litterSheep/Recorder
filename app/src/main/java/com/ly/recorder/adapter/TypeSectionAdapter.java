package com.ly.recorder.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ly.recorder.App;
import com.ly.recorder.Constants;
import com.ly.recorder.R;
import com.ly.recorder.entity.SectionType;

import java.util.List;

/**
 * Created by ly on 2017/3/23 17:25.
 */

public class TypeSectionAdapter extends BaseSectionQuickAdapter<SectionType, BaseViewHolder> {

    public TypeSectionAdapter(int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
    }

    /**
     * 加载item数据
     *
     * @param helper
     * @param item
     */
    @Override
    protected void convert(BaseViewHolder helper, SectionType item) {
        helper.setText(R.id.tv_pop_content, item.t.item);
    }

    /**
     * 加载header数据
     *
     * @param helper
     * @param item
     */
    @Override
    protected void convertHead(BaseViewHolder helper, final SectionType item) {
        TextView tv_head = helper.getView(R.id.tv_pop_content);
        tv_head.setText(item.header);
        tv_head.setTextSize(16);
        tv_head.setEnabled(false);

        if (item.t.type == Constants.TYPE_IN) {
            int green = App.getInstance().getApplicationContext().getResources().getColor(R.color.green);
            tv_head.setTextColor(green);
            helper.getView(R.id.item_vertical_line).setBackgroundColor(green);
        } else {
            int blue = App.getInstance().getApplicationContext().getResources().getColor(R.color.mainColor1);
            tv_head.setTextColor(blue);
            helper.getView(R.id.item_vertical_line).setBackgroundColor(blue);
        }
        helper.getView(R.id.ll_item_type).setEnabled(false);
    }
}
