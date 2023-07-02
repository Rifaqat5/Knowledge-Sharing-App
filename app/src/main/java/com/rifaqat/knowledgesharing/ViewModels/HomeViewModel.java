package com.rifaqat.knowledgesharing.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rifaqat.knowledgesharing.Adapters.PostAdaptor;
import com.rifaqat.knowledgesharing.Repository.HomeRepo;

public class HomeViewModel extends AndroidViewModel {
    public final HomeRepo homeRepo;
    PostAdaptor adaptor;
    LinearLayoutManager layoutManager;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        homeRepo=new HomeRepo(application);
        adaptor=homeRepo.getAdaptor();
        layoutManager=homeRepo.getLayoutManager();
    }

    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public PostAdaptor getAdaptor(){
        return adaptor;
    }

    public void ShowPosts(){
        homeRepo.ShowPosts();
    }
}
