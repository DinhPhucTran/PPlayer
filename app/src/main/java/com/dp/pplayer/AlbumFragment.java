package com.dp.pplayer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {
    public static ListView albumListView;
    public static String ALBUM_TITLE = "com.dp.pplayer.albumTitle";
    public static String ALBUM_ARTIST = "com.dp.pplayer.albumArtist";

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        albumListView = (ListView)view.findViewById(R.id.fragmentAlbumList);
        //setListAdapter(MainActivity.albumAdapter);
        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent albumIntent = new Intent(getContext(), AlbumActivity.class);

                TextView textView = (TextView) view.findViewById(R.id.albumTitle);
                String albumTitle = textView.getText().toString();

                TextView textView2 = (TextView) view.findViewById(R.id.albumArtist);
                String albumArtist = textView2.getText().toString();

                //albumIntent.putExtra(ALBUM_ARTIST, albumArtist);
                //albumIntent.putExtra(ALBUM_TITLE, albumTitle);
                //startActivity(albumIntent);
            }
        });
        return view;
    }

    public void setListAdapter(AlbumAdapter adapter) {
        albumListView.setAdapter(adapter);
    }
}
