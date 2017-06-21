package com.ljr.mediaplayer.base;

import android.content.Context;
import android.view.View;

/**
 * Created by LinJiaRong on 2017/6/20.
 * TODO： 基类，公共类
 */

public abstract class BasePager {
    public final Context mContext;
    public View rootView;
    public boolean isInitData;


    public BasePager(Context context) {
        this.mContext = context;
        rootView = initView();
    }

    public abstract View initView();
    public void initData(){

    }
}
