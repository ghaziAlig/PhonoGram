package com.ghazi.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AnimationDrawable diskAnimation;
    private MediaPlayer mediaPlayer;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button playButton;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(dateFormat.format(new Date(currentPos)));

                rightTime.setText(dateFormat.format(new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    public void setUpUI() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music_1);

        ImageView rotatingDisk = findViewById(R.id.animID);
        rotatingDisk.setBackgroundResource(R.drawable.disk_anim);
        diskAnimation = (AnimationDrawable) rotatingDisk.getBackground();


        leftTime = findViewById(R.id.leftTime);
        rightTime = findViewById(R.id.rightTime);
        seekBar = findViewById(R.id.seekBar);
        Button prevButton = findViewById(R.id.prevButton);
        playButton = findViewById(R.id.playButton);
        Button nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevButton:
                backMusic();
                break;

            case R.id.playButton:
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                }else {
                    startMusic();
                }

                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }

    }

    public void pauseMusic() {
        if (mediaPlayer != null){
            mediaPlayer.pause();
            diskAnimation.stop();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }

    }

    public void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            diskAnimation.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }

    }

    public void backMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            diskAnimation.start();
        }
    }

    public void nextMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
            diskAnimation.stop();
        }
    }

    public void updateThread() {

        thread = new Thread() {
            @Override
            public void run() {


                try {
                    while (mediaPlayer.isPlaying() && mediaPlayer !=null) {
                        thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));
                                rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));

                            }
                        });
                    }

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer.isPlaying() && mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}