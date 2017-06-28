package com.example.chirag.retrofitdemo.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chirag.retrofitdemo.R;
import com.example.chirag.retrofitdemo.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chirag on 28-Jun-17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Song> songArrayList;

    public SongAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongAdapter.ViewHolder holder, int position) {
        Song song = songArrayList.get(position);
        holder.mTvSongPath.setText(song.getSongPath());
        holder.mTvCreatedAt.setText(song.getCreatedAt());
        holder.mTvUpdatedAt.setText(song.getUpdatedAt());
        Picasso.with(context).load(song.getImages()).into(holder.mIvSongImage);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView mTvSongPath,mTvCreatedAt,mTvUpdatedAt;
        private CircleImageView mIvSongImage;
        public ViewHolder(View itemView) {
            super(itemView);

            mTvSongPath = (AppCompatTextView) itemView.findViewById(R.id.tv_song_path);
            mTvCreatedAt = (AppCompatTextView) itemView.findViewById(R.id.tv_created_at);
            mTvUpdatedAt = (AppCompatTextView) itemView.findViewById(R.id.tv_updated_at);
            mIvSongImage = (CircleImageView) itemView.findViewById(R.id.iv_song_image);
        }
    }
}
