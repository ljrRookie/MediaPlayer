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

public class NetMusicPager extends BasePager {
    private final Context mContext;


    public NetMusicPager(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View initView() {
        LogUtil.e("NetMusicPager的视图被初始化了。。。");

        return null;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("NetMusicPager的数据被初始化了。。。");
    }
}
