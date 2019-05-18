package com.example.vanhieu.htmusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends AppCompatActivity implements  OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private SensorManager mSensorManager;
    private ShakeDetector mSensorListener;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private ImageView hinh;
    private Animation animFade;
    // Media Player
    private  MediaPlayer mp;
    // Handler để cập nhật UI timer, progress bar,...
    private Handler mHandler = new Handler();;
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 ms
    private int seekBackwardTime = 5000; // 5000 ms
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<Song> songsList = new ArrayList< Song>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MusicPlayerActivity.class));
        setContentView(R.layout.activity_musicplayer);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        hinh = (ImageView) findViewById(R.id.img);

        // Mediaplayer
        mp = new MediaPlayer();
        songManager = new SongsManager(MusicPlayerActivity.this);
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // quan trọng
        mp.setOnCompletionListener(this); // quan trọng

        // Lấy danh sách bài hát từ thẻ nhớ
        songsList = songManager.getPlayList();

        // Mặc định chơi bài hát đầu tiên trong danh sách
        if(songsList.size() > 0) {
            playSong(0);
            xoayvong(true);
        }


        /**
         * Sự kiện nút Play
         * chơi bài hát và chuyển đến nút Pause
         * tạm dừng một bài hát và chuyển thành nút play
         * */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeDetector();
        mSensorListener.setOnShakeListener(new ShakeDetector.OnShakeListener(){
            public void onShake() {
                btnNext.callOnClick();

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // kiểm tra có đang chơi bài nào không
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        xoayvong(false);

                        // Thay đổi đến nút pause
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                }else{

                    // trở lại chơi bài hát
                    if(mp!=null && songsList.size()>0){
                        mp.start();
                        xoayvong(true);

                        // chuyển nút nhấn đến nút pause.
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }
            }
        }


        );

        /**
         * Sự kiện cho nút Forward
         * chuyển bài hát đến giây kế tiếp nào đó
         * */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // lấy vị trí bài hát hiện tại
                int currentPosition = mp.getCurrentPosition();
                // kiểm tra nếu thờ gian seekForward nhỏ hơn thời lượng bài hát
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // chuyển bài hát đến vị trí đó
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // chuyển đến vị trí kết thúc bài hát
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        /**
         * Sự kiện cho nút Backward
         * Chuyển lùi bài hát đến vị trí nào đó
         * */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // lấy vị trí bài hát hiện hành
                int currentPosition = mp.getCurrentPosition();
                // nếu thời gian seekBackward >=0
                if(currentPosition - seekBackwardTime >= 0){
                    // chuyển lùi bài hát đến vị trí đó.
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // chuyển về vị trí đầu
                    mp.seekTo(0);
                }
            }
        });

        /**
         * Sự kiện cho nút Next
         * Chơi bài hát kế tiếp với vị trí currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // nếu bài hát hiện tại không là bài cuối cùng trong danh sách
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // chơi bài đầu tiên nếu bài hiện tại là bài cuối cùng
                    if(songsList.size()>0) {
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
            }
        });

        /**
         * Sự kiện cho nút Back
         * Chơi bài hát trước đó với vị trí currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // chơi bài cuối cùng
                    if(songsList.size()>0) {
                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                    }
                }

            }
        });

        /**
         * Sự kiện cho nút Repeat
         * Bật cờ Repeat
         * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    // gán cờ Repeat = true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // gán cờ Shuffle = false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        /**
         * Sự kiện cho nút Shuffle
         * Bật cờ Shuffle
         * */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // gán cờ Shuffle = true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // gán cờ shuffle = false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

        /**
         * Gán sự kiện cho nút Play List
         * Khởi động giao diện hiển thị danh sách bài hát
         * */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });

    }

    /**
     * Nhận bài hát được chọn từ danh sách
     * và chơi bài hát đó
     * */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
           // Toast.makeText(getApplicationContext(),"resume",Toast.LENGTH_LONG).show();
            songManager = new SongsManager(MusicPlayerActivity.this);
            songsList = songManager.getPlayList();

            currentSongIndex = data.getExtras().getInt("songIndex");
            // chơi bài hát được chọn
            playSong(currentSongIndex);
            xoayvong(true);
        }

    }

    /**
     * Hàm đế chơi một bài hát
     * @param songIndex - thứ tự bài hát trong danh sách (tính từ 0)
     * */
    public void  playSong(int songIndex){
        // Chơi bài hát
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).getPath());
            mp.prepare();
            mp.start();
            // Hiển thị tiêu đề bài hát
            String songTitle = songsList.get(songIndex).getTitle();
            songTitleLabel.setText(songTitle);

            // Thay đổi button play thành nút pause
            btnPlay.setImageResource(R.drawable.btn_pause);

            // gán giá trị progress bar
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // cập nhật progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật timer trên seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // hiển thị tổng thời gian bài hát
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Hiển thị lượng thời gian bài hát đã chơi
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Cập nhật cho progress
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Chạy luồng này sau mỗi 100 ms
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * Khi người dùng bắt đầu di chuyển thanh trượt
     * */
    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        mHandler.removeCallbacks(mUpdateTimeTask);
        super.onBackPressed();  // optional depending on your needs
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // gỡ bỏ Handler từ việc cập nhật progress
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * Khi người dùng di chuyển thanh trượt
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // di chuyển đến giây nào đó
        mp.seekTo(currentPosition);

        // cập nhật timer cho progress
        updateProgressBar();
    }

    /**
     * khi bài hát vừa chơi xong
     * nếu repeat = true chơi bài hát đó một lần nữa
     * nếu shuffle = true chơi ngẫu nhiên bài hát
     * */
    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // kiểm tra repeat = true hoặc false
        if(isRepeat){
            // repeat = true chơi bài hát lần nữa
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle = true, chơi bài ngẫu nhiên
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // repeat = false && shuffle = false - chơi bài kế tiếp
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // chơi bài đầu tiên
                if(songsList.size()>0) {
                    playSong(0);
                    currentSongIndex = 0;
                }
            }
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
    }
    public void xoayvong (boolean a)
    {
        Animation animaFade = AnimationUtils.loadAnimation(this, R.anim.rotate);
        hinh.setAnimation(animaFade);
        if(a) {

        hinh.startAnimation(animaFade);
    }
        else
        {

            hinh.clearAnimation();
        }



    }

}