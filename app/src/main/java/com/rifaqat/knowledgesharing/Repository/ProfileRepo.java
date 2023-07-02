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
import com.rifaqat.knowledgesharing.Adapters.FollowersAdapter;
import com.rifaqat.knowledgesharing.Models.User;

import java.util.ArrayList;

public class ProfileRepo {
    private Application application;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<User> list;
    private FollowersAdapter adaptor;
    private LinearLayoutManager layoutManager;

    public ProfileRepo(Application application) {
        this.application = application;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list=new ArrayList<>();
        adaptor=new FollowersAdapter(application,list);
        layoutManager=new LinearLayoutManager(application,LinearLayoutManager.HORIZONTAL,false);
    }

    public ArrayList<User> getList(){
        return list;
    }
    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public FollowersAdapter getAdaptor(){
        return adaptor;
    }

    public void ShowFollowers(){
        database.getReference().child("Friends").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    assert user != null;
                    list.add(user);
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
