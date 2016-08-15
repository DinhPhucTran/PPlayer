package com.dp.pplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {
    public static ListView artistListView;

    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        artistListView = (ListView)view.findViewById(R.id.fragmentArtistList);
        //setListAdapter(MainActivity.songAdapter);
        return view;
    }

    public void setListAdapter(SongAdapter adapter) {
        artistListView.setAdapter(adapter);
    }
}
