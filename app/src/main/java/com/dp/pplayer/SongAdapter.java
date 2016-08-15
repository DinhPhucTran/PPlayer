package com.dp.pplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DinhPhuc on 05/03/2016.
 */
public class SongAdapter extends BaseAdapter{
    private ArrayList<Song> songList;
    private LayoutInflater songInflater;

    public SongAdapter(Context context, ArrayList<Song> songs) {
        songList = songs;
        songInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout)songInflater.inflate(R.layout.song, parent, false);

        TextView title = (TextView)songLay.findViewById(R.id.songTitle);
        TextView artist = (TextView)songLay.findViewById(R.id.songArtist);

        Song currSong = songList.get(position);
        title.setText(currSong.getTitle());
        artist.setText(currSong.getArtist());
        songLay.setTag(position);

        return songLay;
    }
}
