package com.rifaqat.knowledgesharing.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rifaqat.knowledgesharing.Adapters.VideoAdapter;
import com.rifaqat.knowledgesharing.Repository.VideoRepo;

public class VideoViewModel extends AndroidViewModel {
    public final VideoRepo videoRepo;
    VideoAdapter adapter;
    LinearLayoutManager layoutManager;
    public VideoViewModel(@NonNull Application application) {
        super(application);
        videoRepo=new VideoRepo(application);
        adapter=videoRepo.getAdaptor();
        layoutManager=videoRepo.getLayoutManager();
    }
    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public VideoAdapter getAdaptor(){
        return adapter;
    }

    public void ShowPosts(){
        videoRepo.ShowPosts();
    }
}
