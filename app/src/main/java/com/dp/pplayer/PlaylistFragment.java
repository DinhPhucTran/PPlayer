package com.dp.pplayer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {
    public static ListView songListView;

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        songListView = (ListView)view.findViewById(R.id.fragmentPlaylist);
//        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent playIntent = new Intent(getContext(), MusicService.class);
//                playIntent.setAction(Integer.toString(position));
//                view.getContext().startService(playIntent);
//            }
//        });
        songListView.setAdapter(MainActivity.songAdapter);
        return view;
    }

}
