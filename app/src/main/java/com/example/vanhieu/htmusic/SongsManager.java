package com.example.vanhieu.htmusic;

/**
 * Created by VanHieu on 16/12/2015.
 */
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SongsManager {
    // Đường dẫn SD Card
    private Context context;
    private ArrayList<Song> songList= new ArrayList<Song>();


    // Hàm dựng
    public SongsManager(Context context){
        this.context=context;
    }

    /**
     * Hàm đọc tất cả tập tin mp3 trên sdcard
     * và lưu trữ chúng trong ArrayList
     * */
    public ArrayList<Song> getPlayList(){
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return songList;
    }

    /**
     * Class để lọc những tập tin có đuôi .mp3
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thispath = musicCursor.getString(pathColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist,thispath));

            }
            while (musicCursor.moveToNext());
        }
    }
}

