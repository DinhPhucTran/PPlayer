package com.dp.pplayer;

/**
 * Created by DinhPhuc on 05/03/2016.
 */
public class Album {
    private long id;
    private String title;
    private String artist;
    private String coverPath;
    private int numberOfSongs;

    public Album(long albumId, String albumTitle, String albumArtist, String albumArtPath, int songs) {
        id = albumId;
        title = albumTitle;
        artist = albumArtist;
        coverPath = albumArtPath;
        numberOfSongs = songs;
    }

    public long getId() {return id;}

    public String getTitle() {return title;}

    public String getArtist() {return artist;}

    public String getCoverPath() {return coverPath;}

    public int getNumberOfSongs() {return numberOfSongs;}
}
