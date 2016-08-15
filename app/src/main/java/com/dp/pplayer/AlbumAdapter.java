package com.dp.pplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DinhPhuc on 05/03/2016.
 */
public class AlbumAdapter extends BaseAdapter {
    private ArrayList<Album> albumList;
    private LayoutInflater inflater;

    public AlbumAdapter(Context context, ArrayList<Album> albums) {
        inflater = LayoutInflater.from(context);
        albumList = albums;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return albumList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout albumLayout = (RelativeLayout)inflater.inflate(R.layout.album, parent, false);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();


        TextView title = (TextView)albumLayout.findViewById(R.id.albumTitle);
        TextView artist = (TextView)albumLayout.findViewById(R.id.albumArtist);
        TextView numberOfSongs = (TextView)albumLayout.findViewById(R.id.albumNumberOfSongs);
        ImageView albumArt = (ImageView)albumLayout.findViewById(R.id.albumImage);

        Album currAlbum = albumList.get(position);
        title.setText(currAlbum.getTitle());
        artist.setText(currAlbum.getArtist());
        numberOfSongs.setText(currAlbum.getNumberOfSongs() + "");

        Bitmap bitmap = BitmapFactory.decodeFile(currAlbum.getCoverPath(),bmOptions);
        if(bitmap!=null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
            albumArt.setImageBitmap(bitmap);
        } else {
            albumArt.setImageResource(R.drawable.default_album_art);
        }

        albumLayout.setTag(position);

        return albumLayout;
    }
}
