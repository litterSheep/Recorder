package com.ly.recorder.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ly.recorder.R;

public class FragmentYear extends Fragment {

    public FragmentYear() {
    }

    public static FragmentYear newInstance() {
        FragmentYear fragment = new FragmentYear();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_year, container, false);
        return view;
    }

}
