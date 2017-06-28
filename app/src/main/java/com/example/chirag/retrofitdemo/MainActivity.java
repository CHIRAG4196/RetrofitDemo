package com.example.chirag.retrofitdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.chirag.retrofitdemo.adapter.SongAdapter;
import com.example.chirag.retrofitdemo.model.MainResponse;
import com.example.chirag.retrofitdemo.model.Song;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Song> songArrayList = new ArrayList<>();
    private RecyclerView mRvSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRvSong = (RecyclerView) findViewById(R.id.rv_song);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRvSong.setLayoutManager(layoutManager);
        final SongAdapter songAdapter = new SongAdapter(this,songArrayList);
        mRvSong.setAdapter(songAdapter);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://music.sparkenproduct.in/public/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SongInterface songInterface = retrofit.create(SongInterface.class);
        final Call<MainResponse> arrayListCall = songInterface.repoContributor();

        arrayListCall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if(response.isSuccessful()) {
                    MainResponse songs = response.body();
                    if(songs!=null)
                    {
                        songArrayList.addAll(songs.getData());
                        songAdapter.notifyDataSetChanged();

                    }
                }
                Log.d("test", "onSuccess "+songArrayList.size());
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Log.d("test", "onFailure: " + t.getMessage());

            }
        });
    }
}
