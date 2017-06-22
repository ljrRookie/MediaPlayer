package com.ljr.mediaplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ljr.mediaplayer.R;
import com.ljr.mediaplayer.bean.MediaItem;
import com.ljr.mediaplayer.util.LogUtil;
import com.ljr.mediaplayer.util.Utils;
import com.ljr.mediaplayer.view.MyVideoView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemVideoPlayer extends Activity {

    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 001;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN = 002;

    @Bind(R.id.videoview)
    MyVideoView mVideoview;
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
    @Bind(R.id.media_controller)
    RelativeLayout mMediaController;

    private Utils mUtils;
    private Uri mUri;
    /**
     * 定义手势识别器
     */
    private GestureDetector mDetector;
    /**
     * 监听电量变化的广播
     */
    private MyReceiver mReceiver;
    /**
     * 传入进来的视频列表
     */
    private ArrayList<MediaItem> mMediaItems;
    /**
     * 要播放的列表中的具体位置
     */
    private int mPosition;
    /**
     * 是否显示控制面板
     */
    private boolean isShowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;
    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;
    /**
     * 屏幕的高
     */
    private int screenHeight = 0;
    /**
     * 真实视频的宽
     */
    private int videoWidth;
    /**
     * 真实视频的高
     */
    private int videoHeight;
    /**
     * 调用声音
     */
    private AudioManager am;

    /**
     * 当前的音量
     */
    private int mCurrentVoice;

    /**
     * 0~15
     * 最大音量
     */
    private int mMaxVoice;
    /**
     * 是否是静音
     */
    private boolean isMute = false;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;
    //视频更新进度
    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIACONTROLLER = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    int currentPosition = mVideoview.getCurrentPosition();
                    mSeekbarVideo.setProgress(currentPosition);
                    mTvCurrentTime.setText(mUtils.stringForTime(currentPosition));
                    mTvSystemTime.setText(mUtils.getSysteTime());
                    mHandler.removeMessages(PROGRESS);
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
                default:
                    break;
            }
        }
    };
    private float mStartY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        ButterKnife.bind(this);
        mUtils = new Utils();
        initData();
        setListener();
        getMediaData();
        setMediaData();

    }

    private void setMediaData() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            MediaItem mediaItem = mMediaItems.get(mPosition);
            mTvName.setText(mediaItem.getName());
            mVideoview.setVideoPath(mediaItem.getData());
        } else if (mUri != null) {
            mTvName.setText(mUri.toString());
            mVideoview.setVideoURI(mUri);
        } else {
            Toast.makeText(this, "没有数据哦！！", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }


    public void getMediaData() {
        //得到播放地址
        mUri = getIntent().getData();
        mMediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        mPosition = getIntent().getIntExtra("position", 0);
    }

    private void initData() {
        //注册电量广播
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量发生变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, intentFilter);
        //实例化手势识别器，并且重写双击，点击，长按
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenAndDefault();
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    hideMediaController();
                    mHandler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    showMediaController();
                    mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }

        });
        //得到屏幕的宽高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        mCurrentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        mMaxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //设置音量
        mSeekbarVoice.setMax(mMaxVoice);
        mSeekbarVoice.setProgress(mCurrentVoice);

    }

    private void setFullScreenAndDefault() {
        if (isFullScreen) {
            setVideoType(DEFAULT_SCREEN);
        } else {
            setVideoType(FULL_SCREEN);
        }
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        mMediaController.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        mMediaController.setVisibility(View.GONE);
        isShowMediaController = false;
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
                if (fromUser) {
                    mVideoview.seekTo(progress);
                }
            }

            /**
             * 当手指触碰的时候回调这个方法
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            /**
             * 当手指离开的时候回调这个方法
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }
        });
        mSeekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
    }

    private void videoViewListener() {
        //播放器准备好的监听
        mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
               videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                mVideoview.start();
                //视频总时长
                int duration = mVideoview.getDuration();
                mSeekbarVideo.setMax(duration);
                mTvDuration.setText(mUtils.stringForTime(duration));
                hideMediaController();
                setVideoType(DEFAULT_SCREEN);
                mHandler.sendEmptyMessage(PROGRESS);
            }


        });
        //播放出错的监听
        mVideoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mHandler.sendEmptyMessage(PROGRESS);
                Toast.makeText(SystemVideoPlayer.this, "视频播放错误！！！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //播放完成的监听
        mVideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextVideo();
            }
        });
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case FULL_SCREEN://全屏
                mVideoview.setVideoSize(screenWidth, screenHeight);
                mBtnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;
                //算法
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                mVideoview.setVideoSize(width, height);
                //2.设置按钮的状态--全屏
                mBtnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            mPosition++;
            if (mPosition < mMediaItems.size()) {
                MediaItem mediaItem = mMediaItems.get(mPosition);
                mTvName.setText(mediaItem.getName());
                mVideoview.setVideoPath(mediaItem.getData());
                setButtonState();
            }
        } else if (mUri != null) {
            setButtonState();
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            //播放上一个视频
            mPosition--;
            if (mPosition >= 0) {
                MediaItem mediaItem = mMediaItems.get(mPosition);
                mTvName.setText(mediaItem.getName());
                mVideoview.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        } else if (mUri != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }

    @OnClick({R.id.btn_voice, R.id.btn_swich_player, R.id.btn_exit
            , R.id.btn_video_pre, R.id.btn_video_start_pause
            , R.id.btn_video_next, R.id.btn_video_siwch_screen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                if(isMute){
                    mBtnVoice.setBackgroundResource(R.drawable.mute);
                    updataVoice(0,isMute);
                }else{
                    mBtnVoice.setBackgroundResource(R.drawable.voice_normal);
                    updataVoice(mCurrentVoice,isMute);
                }
                isMute = !isMute;

                break;
            case R.id.btn_swich_player:
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_video_pre:
                playPreVideo();
                break;
            case R.id.btn_video_start_pause:
                startAndPause();
                break;
            case R.id.btn_video_next:
                playNextVideo();
                break;
            case R.id.btn_video_siwch_screen:
                setFullScreenAndDefault();
                break;
        }
        mHandler.removeMessages(HIDE_MEDIACONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件传递给手势识别器
        mDetector.onTouchEvent(event);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN :
                //1.记录按下的值
                mStartY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight,screenWidth);
                mHandler.removeMessages(HIDE_MEDIACONTROLLER);
                   break;
            case MotionEvent.ACTION_MOVE :
                //记录移动值
                float endY = event.getY();
                float distanceY = mStartY - endY;
                //改变的音量 = （滑动屏幕的距离：总距离）*音量最大值
                float delta = (distanceY/touchRang)*mMaxVoice;
                //最终音量 = 原来+改变
                int voice = (int) Math.min(Math.max(mVol+delta,0),mMaxVoice);
                if(delta != 0){
                    isMute = false;
                    mBtnVoice.setBackgroundResource(R.drawable.voice_normal);
                    updataVoice(voice,isMute);
                }
                break;
            case MotionEvent.ACTION_UP :
                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 监听物理键，实现声音的调节大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            mCurrentVoice--;
            updataVoice(mCurrentVoice,false);
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            mCurrentVoice++;
            updataVoice(mCurrentVoice,false);
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 播放和暂停视频
     */
    private void startAndPause() {
        if (mVideoview.isPlaying()) {
            mVideoview.pause();
            mBtnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            mVideoview.start();
            mBtnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 电量广播
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
    }

    /**
     * 监听电量变化，改变电量图标
     *
     * @param level
     */
    private void setBattery(int level) {
        if (level <= 0) {
            mIvBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            mIvBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            mIvBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            mIvBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            mIvBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            mIvBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            mIvBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            mIvBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * 动态改变按钮状态
     */
    private void setButtonState() {
        if (mMediaItems != null && mMediaItems.size() > 0) {
            if (mMediaItems.size() == 1) {
                setEnable(false);
            } else if (mMediaItems.size() == 2) {
                if (mPosition == 0) {
                    mBtnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtnVideoPre.setEnabled(false);
                    mBtnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    mBtnVideoNext.setEnabled(true);
                } else if (mPosition == mMediaItems.size() - 1) {
                    mBtnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    mBtnVideoPre.setEnabled(true);
                    mBtnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtnVideoNext.setEnabled(false);
                }
            } else {
                if (mPosition == 0) {
                    mBtnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtnVideoPre.setEnabled(false);
                } else if (mPosition == mMediaItems.size() - 1) {
                    mBtnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtnVideoNext.setEnabled(false);
                } else {
                    setEnable(true);
                }
            }
        } else if (mUri != null) {
            setEnable(false);
        }
    }

    /**
     * 动态改变按钮是否可点击
     *
     * @param isEnable
     */
    private void setEnable(boolean isEnable) {
        if (isEnable) {
            mBtnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            mBtnVideoPre.setEnabled(true);
            mBtnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            mBtnVideoNext.setEnabled(true);
        } else {
            mBtnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            mBtnVideoPre.setEnabled(false);
            mBtnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            mBtnVideoNext.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    /**
     * 音量进度条的监听
     */
    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if(progress >0 ){
                    isMute = false;
                    mBtnVoice.setBackgroundResource(R.drawable.voice_normal);
                    updataVoice(mCurrentVoice,isMute);
                }else{
                    isMute = true;
                    mBtnVoice.setBackgroundResource(R.drawable.mute);
                    updataVoice(0,isMute);
                }
                updataVoice(progress,isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
        }
    }

    private void updataVoice(int progress, boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            mSeekbarVoice.setProgress(0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            mSeekbarVoice.setProgress(progress);
            mCurrentVoice = progress;
        }
    }
}
