package com.dp.pplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.session.MediaController;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by DinhPhuc on 05/03/2016.
 */
public class MusicRetriever {
    final String TAG = "PPLAYER.MusicRetriever";
    private ContentResolver mContentResolver;
    private ArrayList<Song> mSongs = new ArrayList<Song>();
    private ArrayList<Album> mAlbums = new ArrayList<Album>();

    public MusicRetriever(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public void prepare() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor songCur = mContentResolver.query(songUri, null, MediaStore.Audio.Media.IS_MUSIC + " = 1",null, MediaStore.Audio.Media.TITLE + " ASC");
        Cursor albumCur = mContentResolver.query(albumUri, null, null, null, MediaStore.Audio.Albums.ALBUM + " ASC");

        if(songCur == null || albumCur == null) {
            Log.d(TAG, "ContentResolver query failed");
        }
        if(!songCur.moveToFirst()) {
            Log.d(TAG, "Query failed (no music)");
        }

        if(songCur!=null && songCur.moveToFirst()) {
            int artistColumn = songCur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int titleColumn = songCur.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int albumColumn = songCur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn = songCur.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIdColumn = songCur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            do {
                mSongs.add(new Song(
                        songCur.getLong(idColumn),
                        songCur.getString(titleColumn),
                        songCur.getString(artistColumn),
                        songCur.getString(albumColumn),
                        songCur.getLong(albumIdColumn)
                ));
            } while (songCur.moveToNext());
        }

        if(albumCur!=null && albumCur.moveToFirst()) {
            int idColumn = albumCur.getColumnIndex(MediaStore.Audio.Albums._ID);
            int albumColumn = albumCur.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artistColumn = albumCur.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int albumArtColumn = albumCur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int songNumberColumn = albumCur.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);

            do {
                long id = albumCur.getLong(idColumn);
                String album = albumCur.getString(albumColumn);
                String artist = albumCur.getString(artistColumn);
                String albumArt = albumCur.getString(albumArtColumn);
                int numberOfSongs = albumCur.getInt(songNumberColumn);
                mAlbums.add(new Album(id, album, artist, albumArt, numberOfSongs));
            } while (albumCur.moveToNext());
        }



//        Collections.sort(mSongs, new Comparator<Song>() {
//            @Override
//            public int compare(Song lhs, Song rhs) {
//                return lhs.getTitle().compareTo(rhs.getTitle());
//            }
//        });
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public Song getSong(int index) {
        return mSongs.get(index);
    }

    public ArrayList<Song> getSongList() {
        return mSongs;
    }

}
