package com.dp.pplayer;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by DinhPhuc on 05/03/2016.
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private String albumArtPath;

    public Song(long songId, String songTitle, String songArtist, String songAlbum, long songAlbumId) {
        id = songId;
        title = songTitle;
        artist = songArtist;
        album = songAlbum;
        albumId = songAlbumId;
    }

//    public Song(long songId, long songDuration, String songTitle, String songArtist, String songAlbum) {
//        id = songId;
//        title = songTitle;
//        artist = songArtist;
//        album = songAlbum;
//        duration = songDuration;
//    }
    public long getId() {return id;}

    public String getTitle() {return title;}

    public String getArtist() {return artist;}

    public String getAlbum() {return album;}

    public long getAlbumId() {return albumId;}

    public String getAlbumArtPath() {return albumArtPath;}

    public void setAlbumArtPath(String path) {
        albumArtPath = path;
    }
    public Uri getURI() {
        return ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    }
}
