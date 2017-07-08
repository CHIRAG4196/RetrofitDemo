package com.example.chirag.retrofitdemo;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.chirag.retrofitdemo.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    private TextView start, end;
    private SeekBar mSbMusic;
    private int songposn;
    private MediaPlayer mediaPlayer;
    private final IBinder musicBind = new MusicBinder();
    private CountDownTimer timer;
    private static final int NOTIFY_ID = 1;
    private ArrayList<Song> songArrayList;
    float mLastPosition;
    float mCurrentPosition;
    float updateTime;
    private String songTitle = "";
    private Handler mHandler;
    private AppCompatImageView mPlayImage;
    private String BaseUrl ="http://music.sparkenproduct.in";
    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songposn = 0;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }

    public void setList(ArrayList<Song> thesongArrayList) {
        songArrayList = thesongArrayList;
    }

    public void setSeekbar(SeekBar seekbar) {
        mSbMusic = seekbar;
    }


    public void setTextView(AppCompatTextView mTvStart, AppCompatTextView mTvEnd) {
        start = mTvStart;
        end = mTvEnd;
    }

    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mPlayImage.setImageResource(R.drawable.ic_play_arrow_black_48dp);

        } else {

            mediaPlayer.start();
            songposn = 0;
            playSong();
            getSeekbarPosn();
            mPlayImage.setImageResource(R.drawable.ic_pause_black_48dp);

        }
    }

    public void setImage(AppCompatImageView mIvPlay) {
        mPlayImage = mIvPlay;
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public boolean onUnbind(Intent intent) {
        return false;
    }

    public void playSong() {

        mediaPlayer.reset();
        Song song = songArrayList.get(songposn);
        songTitle = song.getSongPath();

        try {
            mediaPlayer.setDataSource(BaseUrl+songArrayList.get(songposn).getSongPath());
            Log.d("test", "playSong: "+BaseUrl+songArrayList.get(songposn).getSongPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();


    }
    public void nextSong() {
        mediaPlayer.reset();
        if (songposn < songArrayList.size())
            songposn = songposn + 1;
        else
            songposn = 0;
        try {
            mediaPlayer.setDataSource(BaseUrl+songArrayList.get(songposn).getSongPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void previousSong() {
        mediaPlayer.reset();
        if (songposn < songArrayList.size())
            songposn = songposn - 1;
        else {
            songposn = songArrayList.size()-1;
        }
        try {
            mediaPlayer.setDataSource(BaseUrl+songArrayList.get(songposn).getSongPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float getSeekbarPosn() {
if(timer!=null)
    timer.cancel();

        timer = new CountDownTimer(mediaPlayer.getDuration(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mLastPosition = mediaPlayer.getDuration();
                mCurrentPosition = mediaPlayer.getCurrentPosition();
                updateTime = (mCurrentPosition / mLastPosition) * 100;

                mSbMusic.setProgress((int) updateTime);
                String starthms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) mCurrentPosition),
                        TimeUnit.MILLISECONDS.toSeconds((long) mCurrentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mCurrentPosition)));
                start.setText(starthms);
                String lasthms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) mLastPosition),
                        TimeUnit.MILLISECONDS.toSeconds((long) mLastPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mLastPosition)));
                end.setText(lasthms);
                Log.d("testtt", "onTick: " + updateTime);
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                mSbMusic.setSecondaryProgress(percent);
                mSbMusic.setSecondaryProgressTintList(ColorStateList.valueOf(Color.YELLOW));
            }
        });
        return mCurrentPosition;
    }

    public void setSongPosn(int songPosn) {
        songposn = songPosn;
    }

    public void mediaplayerSeekTo(){

        int playPositionInMillisecconds = (mediaPlayer.getDuration() / 100) * mSbMusic.getProgress();
        mediaPlayer.seekTo((int) playPositionInMillisecconds);
        Log.d("test", "mediaplayerSeekTo: "+(int) mCurrentPosition);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);


    }
    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mediaPlayer.stop();
        mediaPlayer.release();
        stopSelf();
    }
}
