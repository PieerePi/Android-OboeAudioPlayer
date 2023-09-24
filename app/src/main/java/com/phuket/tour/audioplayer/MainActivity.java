package com.phuket.tour.audioplayer;

import androidx.appcompat.app.AppCompatActivity;


import com.phuket.tour.audioplayer.audiotrack.NativeMp3PlayerController;
import com.phuket.tour.audioplayer.opensles.SoundTrackController;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("songstudio");
    }
    private static String TAG = "MainActivity";

    private Button audioTrackPlayBtn;
    private Button audioTrackStopBtn;
    private Button openSLESPlayBtn;
    private Button openSLESStopBtn;
    private Button openOboeActivityBtn;
    /** 要播放的文件路径 **/
    private static String playFilePath = "131.mp3";

    public static final int UPDATE_PLAY_VOICE_PROGRESS = 730;
    public static final int AUDIO_TRACK_PLAY_DONE = 740;
    public static final int OPENSL_ES_PLAY_DONE = 750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CopyAssets(getApplicationContext(), playFilePath,
                getApplicationContext().getFilesDir().getAbsolutePath(), playFilePath);
        findView();
        bindListener();
    }

    private void findView() {
        audioTrackPlayBtn = (Button) findViewById(R.id.play_audiotrack_btn);
        audioTrackStopBtn = (Button) findViewById(R.id.stop_audiotrack_btn);
        openSLESPlayBtn = (Button) findViewById(R.id.play_opensl_es_btn);
        openSLESStopBtn = (Button) findViewById(R.id.stop_opensl_es_btn);
        openOboeActivityBtn = (Button) findViewById(R.id.open_oboe_btn);
    }

    private void bindListener() {
        audioTrackPlayBtn.setOnClickListener(audioTrackPlayBtnListener);
        audioTrackStopBtn.setOnClickListener(audioTrackStopBtnListener);
        openSLESPlayBtn.setOnClickListener(openSLESPlayBtnListener);
        openSLESStopBtn.setOnClickListener(openSLESStopBtnListener);
        openOboeActivityBtn.setOnClickListener(openOboeActivityBtnListener);
    }

    private NativeMp3PlayerController audioTrackPlayerController;
    OnClickListener audioTrackPlayBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click AudioTrack Play Btn");
            if (null == audioTrackPlayerController) {
                audioTrackPlayerController = new NativeMp3PlayerController();
                audioTrackPlayerController.setHandler(handler);
                audioTrackPlayerController.setOnCompletionListener(new SoundTrackController.OnCompletionListener() {
                    @Override
                    public void onCompletion() {
                        // 发消息随后去执行结束PLAYER，否则会卡死；Toast也是在后面做
                        handler.sendMessage(handler.obtainMessage(
                                AUDIO_TRACK_PLAY_DONE, 1, 1));
                    }
                });
                String lplayFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + playFilePath;
                audioTrackPlayerController.setAudioDataSource(lplayFilePath);
                audioTrackPlayerController.start();
            }
        }
    };

    OnClickListener audioTrackStopBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click AudioTrack Stop Btn");
            // 普通AudioTrack的停止播放
            if (null != audioTrackPlayerController) {
                audioTrackPlayerController.stop();
                audioTrackPlayerController = null;
            }
        }
    };

    private SoundTrackController openSLPlayerController;
    OnClickListener openSLESPlayBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click OpenSL ES Play Btn");
            if (null == openSLPlayerController) {
                // OpenSL ES初始化播放器
                openSLPlayerController = new SoundTrackController();
                openSLPlayerController.setOnCompletionListener(new SoundTrackController.OnCompletionListener() {
                    @Override
                    public void onCompletion() {
                        // 发消息随后去执行结束PLAYER，否则会卡死；Toast也是在后面做
                        handler.sendMessage(handler.obtainMessage(
                                OPENSL_ES_PLAY_DONE, 1, 1));
                    }
                });
                String lplayFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + playFilePath;
                openSLPlayerController.setAudioDataSource(lplayFilePath, 0.2f);
                // OpenSL ES进行播放
                openSLPlayerController.play();
            }
        }
    };

    OnClickListener openSLESStopBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Click OpenSL ES Stop Btn");
            if (null != openSLPlayerController) {
                openSLPlayerController.stop();
                openSLPlayerController = null;
            }
        }
    };

    OnClickListener openOboeActivityBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent startoboe = new Intent(MainActivity.this, OboeActivity.class);
            startActivity(startoboe);
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // 计算当前时间
            // ?msg.arg2是有没有在播放?
//            int _time = Math.max(msg.arg1, 0) / 1000;
//            int total_time = Math.max(msg.arg2, 0) / 1000;
//            float ratio = (float) _time / (float) total_time;
            if (msg.what == AUDIO_TRACK_PLAY_DONE) {
                Log.i(TAG, "AUDIO_TRACK_PLAY_DONE");
                // 普通AudioTrack的停止播放
                if (null != audioTrackPlayerController) {
                    audioTrackPlayerController.stop();
                    audioTrackPlayerController = null;
                }
                Toast.makeText(MainActivity.this, "AUDIO TRACK 播放完成", Toast.LENGTH_SHORT).show();
            } else if (msg.what == OPENSL_ES_PLAY_DONE) {
                Log.i(TAG, "OPENSL_ES_PLAY_DONE");
                if (null != openSLPlayerController) {
                    openSLPlayerController.stop();
                    openSLPlayerController = null;
                }
                Toast.makeText(MainActivity.this, "OPENSL ES 播放完成", Toast.LENGTH_SHORT).show();
            } else if (msg.what == UPDATE_PLAY_VOICE_PROGRESS){
                Log.i(TAG, "Play Progress : " + msg.arg1 / 1000 + " seconds, playing " + msg.arg2);
            }
        }
    };

    /**
     　　*
     　　* @param myContext
     　　* @param ASSETS_NAME 要复制的文件名
     　　* @param savePath 要保存的路径
     　　* @param saveName 复制后的文件名
     　　*/
    public static void CopyAssets(Context myContext, String ASSETS_NAME,
                                  String savePath, String saveName) {
        String filename = savePath + File.separator + saveName;
        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
