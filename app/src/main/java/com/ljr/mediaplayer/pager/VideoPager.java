package com.ljr.mediaplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ljr.mediaplayer.R;

import com.ljr.mediaplayer.activity.SystemVideoPlayer;
import com.ljr.mediaplayer.adapter.VideoAdapter;
import com.ljr.mediaplayer.base.BasePager;
import com.ljr.mediaplayer.bean.MediaItem;
import com.ljr.mediaplayer.util.LogUtil;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Created by LinJiaRong on 2017/6/20.
 * TODO：
 */

public class VideoPager extends BasePager {
    private ListView listview;
    private TextView tv_noData;
    private ProgressBar pb_loading;
    private ArrayList<MediaItem> mMediaItems;

    public VideoPager(Context context) {
        super(context);

    }

    @Override
    public View initView() {
        LogUtil.e("本地视频的视图被初始化了。。。");
        View view = View.inflate(mContext, R.layout.video_pager, null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_noData = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem mediaItem = mMediaItems.get(position);
                //1.调起系统所有的播放-隐式意图
                /*Intent intent = new Intent();
                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video*//*");
                mContext.startActivity(intent);*/
                //2.调用自定义的播放器-显示意图
                Intent intent = new Intent(mContext,SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video*//*");
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频的数据被初始化了。。。");
        getDataFromLocal();
    }

    /**
     * 从本地的sdcard得到数据
     * 1.遍历sdcard后缀名
     * 2.从内容提供者里面获取视频
     * 3.如果是6.0的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                isGrantExternalRW((Activity) mContext);
                mMediaItems = new ArrayList<>();
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//文件名称
                        MediaStore.Video.Media.DURATION,//文件总时长
                        MediaStore.Video.Media.SIZE,//文件大小
                        MediaStore.Video.Media.DATA,//文件的绝对地址
                        MediaStore.Video.Media.ARTIST//作者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        mMediaItems.add(mediaItem);
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaItems != null && mMediaItems.size() > 0) {
                VideoAdapter videoAdapter = new VideoAdapter(mContext, mMediaItems, true);
                listview.setAdapter(videoAdapter);
                //隐藏文本
                tv_noData.setVisibility(View.GONE);
            } else {
                tv_noData.setVisibility(View.VISIBLE);
            }
            pb_loading.setVisibility(View.GONE);
        }
    };

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
            return false;
        }
        return true;
    }
}
