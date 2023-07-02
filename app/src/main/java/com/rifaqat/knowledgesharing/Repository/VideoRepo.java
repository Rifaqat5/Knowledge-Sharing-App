package com.rifaqat.knowledgesharing.Repository;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.Adapters.VideoAdapter;
import com.rifaqat.knowledgesharing.Models.Video;

import java.util.ArrayList;

public class VideoRepo {
    private Application application;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<Video> list;
    private VideoAdapter adaptor;
    private LinearLayoutManager layoutManager;

    public VideoRepo(Application application) {
        this.application = application;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list=new ArrayList<>();
        adaptor=new VideoAdapter(application,list);
        layoutManager=new LinearLayoutManager(application);
    }

    public ArrayList<Video> getList(){
        return list;
    }
    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public VideoAdapter getAdaptor(){
        return adaptor;
    }

    public void ShowPosts(){
        database.getReference().child("Videos")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Video video=dataSnapshot.getValue(Video.class);
                            assert video != null;
                            video.setPostVideoId(dataSnapshot.getKey());
                            list.add(video);
                        }
                        adaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
