package com.example.chirag.retrofitdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.example.chirag.retrofitdemo.adapter.SongAdapter;
import com.example.chirag.retrofitdemo.model.MainResponse;
import com.example.chirag.retrofitdemo.model.Song;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Song> songArrayList = new ArrayList<>();
    private Utilities utils = new Utilities();
    private RecyclerView mRvSong;
    private Intent playIntent;
    private boolean musicBound = false;
    private int pos;
    private MusicService musicSrv;

    private AppCompatSeekBar mSbMusic;
    private AppCompatTextView mTvStart, mTvEnd;
    private AppCompatImageView mIvPlay, mIvForward, mIvRewind;
    private CountDownTimer timer;
    private float updateTime;
    private static final String FORMAT = "%02d:%02d";


    private boolean paused = false, playbackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvPlay = (AppCompatImageView) findViewById(R.id.bt_play);
        mIvForward = (AppCompatImageView) findViewById(R.id.bt_forward);
        mIvRewind = (AppCompatImageView) findViewById(R.id.bt_rewind);
        mSbMusic = (AppCompatSeekBar) findViewById(R.id.sb_music);
        mTvEnd = (AppCompatTextView) findViewById(R.id.tv_end);
        mTvStart = (AppCompatTextView) findViewById(R.id.tv_start);

        mRvSong = (RecyclerView) findViewById(R.id.rv_song);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvSong.setLayoutManager(layoutManager);
        final SongAdapter songAdapter = new SongAdapter(this, songArrayList, this);
        mRvSong.setAdapter(songAdapter);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://music.sparkenproduct.in/public/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SongInterface songInterface = retrofit.create(SongInterface.class);
        final Call<MainResponse> arrayListCall = songInterface.repoContributor();

        arrayListCall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful()) {
                    MainResponse songs = response.body();
                    if (songs != null) {
                        songArrayList.addAll(songs.getData());
                        songAdapter.notifyDataSetChanged();

                    }
                }
                Log.d("test", "onSuccess " + songArrayList.size());
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Log.d("test", "onFailure: " + t.getMessage());

            }
        });
        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               musicSrv.setImage(mIvPlay);
               musicSrv.playPause();

            }
        });
        mIvForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onClick: ");

                musicSrv.nextSong();

            }


        });

        mIvRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.previousSong();
            }
        });
        mSbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicSrv.mediaplayerSeekTo();
            }
        });
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            Log.i("LOG ","");
            //pass list
            musicSrv.setList(songArrayList);
            musicBound = true;
            musicSrv.setSeekbar(mSbMusic);
            musicSrv.setTextView(mTvStart, mTvEnd);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

    }

    public void songPicked(View view) {
        musicSrv.setSongPosn(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if (playbackPaused) {
            playbackPaused = false;
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.act_list_song_rl) {
            pos = (int) v.getTag();
            musicSrv.setSongPosn(pos);
            Log.d("test", "onPos " + pos);
        }
        musicSrv.playSong();
        mIvPlay.setImageResource(R.drawable.ic_pause_black_48dp);
        musicSrv.getSeekbarPosn();
        Log.d("test", "onClick: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }
}
    

