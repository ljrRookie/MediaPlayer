package com.ljr.mediaplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ljr.mediaplayer.R;
import com.ljr.mediaplayer.util.LogUtil;
import com.ljr.mediaplayer.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemVideoPlayer extends Activity {

    @Bind(R.id.videoview)
    VideoView mVideoview;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.iv_battery)
    ImageView mIvBattery;
    @Bind(R.id.tv_system_time)
    TextView mTvSystemTime;
    @Bind(R.id.btn_voice)
    Button mBtnVoice;
    @Bind(R.id.seekbar_voice)
    SeekBar mSeekbarVoice;
    @Bind(R.id.btn_swich_player)
    Button mBtnSwichPlayer;
    @Bind(R.id.ll_top)
    LinearLayout mLlTop;
    @Bind(R.id.tv_current_time)
    TextView mTvCurrentTime;
    @Bind(R.id.seekbar_video)
    SeekBar mSeekbarVideo;
    @Bind(R.id.tv_duration)
    TextView mTvDuration;
    @Bind(R.id.btn_exit)
    Button mBtnExit;
    @Bind(R.id.btn_video_pre)
    Button mBtnVideoPre;
    @Bind(R.id.btn_video_start_pause)
    Button mBtnVideoStartPause;
    @Bind(R.id.btn_video_next)
    Button mBtnVideoNext;
    @Bind(R.id.btn_video_siwch_screen)
    Button mBtnVideoSiwchScreen;
    @Bind(R.id.ll_bottom)
    LinearLayout mLlBottom;

    private Utils mUtils;
    //视频更新进度
    private static final int PROGRESS = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case PROGRESS :
                    int currentPosition = mVideoview.getCurrentPosition();
                    mSeekbarVideo.setProgress(currentPosition);
                    mTvCurrentTime.setText(mUtils.stringForTime(currentPosition));
                    mTvSystemTime.setText(mUtils.getSysteTime());
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
                default:
                       break;
            }
        }
    } ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        ButterKnife.bind(this);
        LogUtil.e("oncreate");
        setListener();
        mUtils = new Utils();
        Uri uri = getIntent().getData();
        if(uri!=null){
            mVideoview.setVideoURI(uri);
        }
    }

    private void setListener() {
        /**
         * 对VideoView的监听
         */
        LogUtil.e("对VideoView的监听");
        videoViewListener();
        /**
         * 对seekBar的监听
         */
        seekBarChangeListener();
    }

    private void seekBarChangeListener() {
        mSeekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
             * @param seekBar
             * @param progress
             * @param fromUser  用户改变则返回true
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mVideoview.seekTo(progress);
                }
            }

            /**
             * 当手指触碰的时候回调这个方法
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 当手指离开的时候回调这个方法
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void videoViewListener() {
        LogUtil.e("准备播放！！！");
        //播放器准备好的监听
        mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.e("播放！！！！！！！！！！！！！！！！！！");
                mVideoview.start();
                //视频总时长
                int duration = mVideoview.getDuration();
                mSeekbarVideo.setMax(duration);
                mTvDuration.setText(mUtils.stringForTime(duration));
                mHandler.sendEmptyMessage(PROGRESS);
            }
        });
        //播放出错的监听
        mVideoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtil.e("视频播放错误！！！！");
                Toast.makeText(SystemVideoPlayer.this, "视频播放错误！！！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //播放完成的监听
        mVideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(SystemVideoPlayer.this, "视频播放完成！！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.btn_voice, R.id.btn_swich_player, R.id.btn_exit
            , R.id.btn_video_pre, R.id.btn_video_start_pause
            , R.id.btn_video_next, R.id.btn_video_siwch_screen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                break;
            case R.id.btn_swich_player:
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_video_pre:
                break;
            case R.id.btn_video_start_pause:
                if(mVideoview.isPlaying()){
                    mVideoview.pause();
                    mBtnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
                }else{
                    mVideoview.start();
                    mBtnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
                }
                break;
            case R.id.btn_video_next:
                break;
            case R.id.btn_video_siwch_screen:
                break;
        }
    }
}
