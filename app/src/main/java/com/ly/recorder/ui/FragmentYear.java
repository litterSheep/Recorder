package com.ly.recorder.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ly.recorder.R;
import com.ly.recorder.db.Account;

import java.util.List;

public class FragmentYear extends Fragment {

    private List<Account> accounts;

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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void initData(List<Account> accounts) {
        this.accounts = accounts;
    }
}
