package com.ljr.mediaplayer.replacefragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljr.mediaplayer.base.BasePager;

/**
 * Created by LinJiaRong on 2017/6/20.
 * TODO：
 */

public class ReplaceFragment extends Fragment {
    private BasePager currPager;

    public ReplaceFragment(BasePager basePager) {
        this.currPager = basePager;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootView;
    }
}
