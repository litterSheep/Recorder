package com.ly.recorder.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.ly.recorder.R;
import com.ly.recorder.utils.logger.Logger;

import java.util.ArrayList;

/**
 * Created by ly on 2017/3/3 14:01.
 */

public class StatisticsActivity extends BaseActivity {
    private ArrayList<Fragment> fragments;
    private int currentIndex = 1;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initViews();

        initFragment();

    }

    private void initViews() {

        topTitleBar.setTitle_text(getString(R.string.title_statistics));
        topTitleBar.setRight_button_text("筛选");
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rbt_container);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                int index;
                switch (checkedId) {
                    case R.id.rbt_day:
                        index = 0;
                        Logger.d("checkedId:" + checkedId);
                        break;
                    case R.id.rbt_year:
                        index = 2;
                        break;
                    default:
                        index = 1;
                        break;
                }
                selectTab(index);
            }
        });

    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();

        if (fragments == null) fragments = new ArrayList<>();
        fragments.add(FragmentDay.newInstance());
        fragments.add(FragmentMonth.newInstance());
        fragments.add(FragmentYear.newInstance());

        fragmentManager.beginTransaction().add(R.id.contentContainer, fragments.get(currentIndex)).commit();
    }

    private void selectTab(int index) {
        if (fragments == null) {
            Logger.w("fragments == null...");
            return;
        }
        if (index != currentIndex) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragments.get(currentIndex));
            Fragment fragment = fragments.get(index);

            if (!fragment.isAdded()) {
                fragmentTransaction.add(R.id.contentContainer, fragment);
            } else {//显示之前隐藏的fragment
                fragmentTransaction.show(fragment);
            }
            fragmentTransaction.commit();
        }

        currentIndex = index;
    }

}
