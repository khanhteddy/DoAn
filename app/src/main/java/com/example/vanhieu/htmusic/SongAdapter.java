package com.example.vanhieu.htmusic;

/**
 * Created by VanHieu on 16/12/2015.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SongAdapter extends ArrayAdapter implements Filterable {

    Activity context = null;
    int textViewResourceId;
    private ArrayList<Song> songs;
    private ArrayList<Song> originalsongs;
    private LayoutInflater songInf;
    private SongsFilter filter;
    public SongAdapter(Activity context, int textViewResourceId, ArrayList<Song> theSongs) {
        super(context, textViewResourceId, theSongs);
        this.context=context;
        this.textViewResourceId=textViewResourceId;
        songs= new ArrayList<Song>();
        songs.addAll(theSongs);
        originalsongs= new ArrayList<Song>();
        originalsongs.addAll(theSongs);

        //songInf=LayoutInflater.from(c);
    }
    private class ViewHolder {
        TextView songView;
        TextView artistView;
        TextView pathView;

    }

    public SongAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new SongsFilter();
        }
        return filter;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        ViewHolder holder = null;
        holder = new ViewHolder();
        Log.v("ConvertView", String.valueOf(position));

            LayoutInflater inflater= context.getLayoutInflater();
            convertView = inflater.inflate(textViewResourceId,null);
        //get title and artist views

        final TextView songView = (TextView)convertView.findViewById(R.id.song_title);
            final TextView artistView = (TextView)convertView.findViewById(R.id.song_artist);
            final TextView pathView = (TextView)convertView.findViewById(R.id.song_path);
        //get song using position

        //set position as tag

        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        pathView.setText(currSong.getPath());
        return convertView;
    }
    private class SongsFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Song> filteredItems = new ArrayList<Song>();

                for(int i = 0, l = originalsongs.size(); i < l; i++)
                {
                    Song song = originalsongs.get(i);
                    if(song.getTitle().toString().toLowerCase().contains(constraint) ||song.getArtist().toString().toLowerCase().contains(constraint))
                        filteredItems.add(song);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = originalsongs;
                    result.count = originalsongs.size();
                }
            }
            return result;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            songs = (ArrayList<Song>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = songs.size(); i < l; i++)
                add(songs.get(i));
            notifyDataSetInvalidated();
        }
    }



}