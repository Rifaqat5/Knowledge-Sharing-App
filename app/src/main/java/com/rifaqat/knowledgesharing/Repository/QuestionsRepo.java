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
import com.rifaqat.knowledgesharing.Adapters.QuestionAdapter;
import com.rifaqat.knowledgesharing.Models.Post;
import com.rifaqat.knowledgesharing.Models.Question;

import java.util.ArrayList;

public class QuestionsRepo {
    private Application application;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<Question> list;
    private QuestionAdapter adaptor;
    private LinearLayoutManager layoutManager;

    public QuestionsRepo(Application application) {
        this.application = application;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list=new ArrayList<>();
        adaptor=new QuestionAdapter(list, application);
        layoutManager=new LinearLayoutManager(application);
    }

    public ArrayList<Question> getList(){
        return list;
    }
    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public QuestionAdapter getAdaptor(){
        return adaptor;
    }

    public void ShowPosts(){
        database.getReference().child("Questions")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Question post=dataSnapshot.getValue(Question.class);
                            assert post != null;
                            post.setQuestionId(dataSnapshot.getKey());
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
