package com.example.vanhieu.htmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlayListActivity extends AppCompatActivity {

    private ArrayList<Song> songList;
    private ListView songView;
    private EditText search;
    private SearchView search1;
    private SongAdapter songAdt;
    private ArrayList<Song> listtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        songView = (ListView) findViewById(R.id.song_list);

        songView.setTextFilterEnabled(true);
        search1 = (SearchView) findViewById(R.id.search1);
        search1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                songAdt.getFilter().filter(newText);
                return false;
            }
        });

//        search = (EditText) findViewById(R.id.search);
       initList();
        listtitle = new ArrayList<Song>();
        listtitle = getSong();







        // gán sự kiện cho mỗi bài hát được chọn
        songView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // lấy vị trí bài hát
                int songIndex=0;
                TextView v = (TextView) view.findViewById(R.id.song_path);
                String p = v.getText().toString();
                for(int i=0;i<listtitle.size();i++)
                {
                    if (listtitle.get(i).getPath().toString().equals(p)) {
                        songIndex = i;
                        break;
                    }
                }
               // Toast.makeText(getApplicationContext(),listtitle.get(1).getTitle().toString(),Toast.LENGTH_LONG).show();

               Intent in = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                // Gửi vị trí bài hát đến PlayerActivity
               in.putExtra("songIndex", songIndex);
              setResult(100, in);
                // Đóng PlayListView
               finish();
            }
        });
        registerForContextMenu(songView);
//        search.addTextChangedListener(new TextWatcher() {
//                                          @Override
//                                          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                                          }
//
//                                          @Override
//                                          public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//                                              songAdt.getFilter().filter(s.toString());
//
//                                          }
//
//                                          @Override
//                                          public void afterTextChanged(Editable s) {
//
//
//                                              }
//
//
//                                      }
//
//        );


        }
//    public void searchItem(String textosearch)
//    {
//
//     for (Song Item:listtitle)
//     {
//         if(!Item.getTitle().toString().contains(textosearch))
//         {
//             songList.remove(Item);
//
//         }
//         songAdt.notifyDataSetChanged();
//     }
//
//
//
//    }

    public void initList()
    {
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        songAdt = new SongAdapter(this,R.layout.song, songList);
        songView.setAdapter(songAdt);



    }

//int id;
    int idd;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater()
                .inflate(R.menu.my_context_menu, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int currentposition = info.position;
        int idd = v.getId();
        //idd = Integer.toString(id);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
      final int currentposition = info.position;
       // String id = Integer.toString(currentposition);
       // View view = findViewById(currentposition);
       // TextView textview = (TextView)view.findViewById(R.id.song_path);
        //String p = textview.getText().toString();
        switch (item.getItemId()) {
            case R.id.chiase: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("audio/mp3");
                intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+songList.get(currentposition).getPath()));
                // intent.putExtra(Intent.EXTRA_TEXT, mShareMessage);
                startActivity(Intent.createChooser(intent, "Chia sẽ"));
            }
            break;
            case R.id.xoa: {


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayListActivity.this);
                alertDialogBuilder.setMessage("Bán có muốn xóa bài này!");
                alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TextView v = (TextView)view.findViewById(R.id.song_path);
                        String Tiltle =songList.get(currentposition).getTitle();
                        String t = songList.get(currentposition).getPath();
                        Toast.makeText(PlayListActivity.this, t, Toast.LENGTH_LONG).show();
                        File file = new File(t);
                        songList.remove(songList.get(currentposition));

                        songAdt = new SongAdapter(PlayListActivity.this,R.layout.song, songList);
                        songView.setAdapter(songAdt);
                        getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "TITLE='" + Tiltle + "'", null);
                        listtitle = getSong();

                        file.delete();

                      //  initList();


                    }
                });
                alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //không làm gì
                    }
                });
                alertDialogBuilder.show();
                return true;





            }
        }
        return true;
    };


    public void getSongList() {
        //retrieve song info
        songList = new ArrayList<Song>();
        ContentResolver musicResolver = getContentResolver();
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
    public ArrayList<Song> getSong() {
        //retrieve song info
        ArrayList<Song> lists = new ArrayList<Song>();
        ContentResolver musicResolver = getContentResolver();
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
                lists.add(new Song(thisId, thisTitle, thisArtist,thispath));

            }
            while (musicCursor.moveToNext());

        }
        Collections.sort(lists, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return lists;
    }
}
