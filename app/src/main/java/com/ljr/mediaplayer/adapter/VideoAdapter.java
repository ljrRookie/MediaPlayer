package com.ljr.mediaplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ljr.mediaplayer.R;
import com.ljr.mediaplayer.bean.MediaItem;
import com.ljr.mediaplayer.util.Utils;

import java.util.ArrayList;

/**
 * Created by LinJiaRong on 2017/6/20.
 * TODO：
 */

public class VideoAdapter extends BaseAdapter {
    private  Context mContext;
    private final ArrayList<MediaItem> mMediaItems;
    private final boolean mIsVideo;
    private Utils mUtils;

    public VideoAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean isVideo) {
        this.mContext = context;
        this.mMediaItems = mediaItems;
        this.mIsVideo = isVideo;
        mUtils = new Utils();
    }

    @Override
    public int getCount() {
        return mMediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.item_video_pager, null);
            viewHolder = new ViewHolder();
            viewHolder.mIvIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.mTvSize = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        }else{
             viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = mMediaItems.get(position);
        viewHolder.mTvName.setText(mediaItem.getName());
        viewHolder.mTvSize.setText(Formatter.formatFileSize(mContext,mediaItem.getSize()));
        viewHolder.mTvTime.setText(mUtils.stringForTime((int) mediaItem.getDuration()));
        if(!mIsVideo){
            viewHolder.mIvIcon.setImageResource(R.drawable.music_default_bg);
        }
        return convertView;
    }
    static class ViewHolder {
        ImageView mIvIcon;
        TextView mTvName;
        TextView mTvTime;
        TextView mTvSize;
    }
}
