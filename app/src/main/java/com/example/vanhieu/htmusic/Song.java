package com.example.vanhieu.htmusic;

/**
 * Created by VanHieu on 10/12/2015.
 */
public class Song {
    private long id;
    private String path;
    private String title;
    private String artist;
    public Song(long songID, String songTitle, String songArtist,String songpath) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        path=songpath;

    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getPath(){return path;}

}
