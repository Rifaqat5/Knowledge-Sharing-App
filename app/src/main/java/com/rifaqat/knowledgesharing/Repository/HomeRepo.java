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
import com.rifaqat.knowledgesharing.Adapters.PostAdaptor;
import com.rifaqat.knowledgesharing.Models.Post;

import java.util.ArrayList;

public class HomeRepo {
    private Application application;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<Post> list;
    private PostAdaptor adaptor;
    private LinearLayoutManager layoutManager;

    public HomeRepo(Application application) {
        this.application = application;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        adaptor = new PostAdaptor(application, list);
        layoutManager = new LinearLayoutManager(application);
    }

    public ArrayList<Post> getList() {
        return list;
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public PostAdaptor getAdaptor() {
        return adaptor;
    }

    public void ShowPosts(){
        database.getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Post post=dataSnapshot.getValue(Post.class);
                            assert post != null;
                            post.setPostId(dataSnapshot.getKey());
                            list.add(post);
                        }
                        adaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }







}
