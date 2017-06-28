package com.example.chirag.retrofitdemo;

import com.example.chirag.retrofitdemo.model.MainResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by chirag on 28-Jun-17.
 */

public interface SongInterface {

    @GET("song/all")
    Call<MainResponse> repoContributor();


}
