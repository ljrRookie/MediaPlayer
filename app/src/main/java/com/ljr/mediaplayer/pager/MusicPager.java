package com.ljr.mediaplayer.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ljr.mediaplayer.base.BasePager;
import com.ljr.mediaplayer.util.LogUtil;

/**
 * Created by LinJiaRong on 2017/6/20.
 * TODO：
 */

public class MusicPager extends BasePager {
    private final Context mContext;


    public MusicPager(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View initView() {
        LogUtil.e("MusicPager的视图被初始化了。。。");

        return null;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("MusicPager的数据被初始化了。。。");
    }
}
