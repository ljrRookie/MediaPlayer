package com.ljr.mediaplayer.activity;


import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.ljr.mediaplayer.R;
import com.ljr.mediaplayer.base.BasePager;
import com.ljr.mediaplayer.pager.MusicPager;
import com.ljr.mediaplayer.pager.NetMusicPager;
import com.ljr.mediaplayer.pager.NetVideoPager;
import com.ljr.mediaplayer.pager.VideoPager;
import com.ljr.mediaplayer.replacefragment.ReplaceFragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private ArrayList<BasePager> mBasePager;
    private RadioGroup mRg_bottom_tag;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        mBasePager = new ArrayList<>();
        mBasePager.add(new VideoPager(this));
        mBasePager.add(new MusicPager(this));
        mBasePager.add(new NetVideoPager(this));
        mBasePager.add(new NetMusicPager(this));

        mRg_bottom_tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch(checkedId){
                    default:
                        mPosition = 0;
                           break;
                    case R.id.rb_audio :
                        mPosition = 1;
                        break;
                    case R.id.rb_net_video :
                        mPosition = 2;
                        break;
                    case R.id.rb_netaudio :
                        mPosition = 3;
                        break;
                }
                setFragment();
            }
        });
        //默认选中项
        mRg_bottom_tag.check(R.id.rb_video);
    }

    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        /**
         * 1.得到FragmentManger
         * 2.开启事务
         * 3.替换
         * 4.提交事务
         */
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        ft.commit();
    }

    /**
     * 根据位置得到相应的页面
     * @return
     */
    public BasePager getBasePager() {
        BasePager basePager = mBasePager.get(mPosition);
        if(basePager != null && !basePager.isInitData){
            basePager.initData();;
            basePager.isInitData = true;
        }
        return basePager;
    }
}
