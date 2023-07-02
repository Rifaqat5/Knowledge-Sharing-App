package com.rifaqat.knowledgesharing.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rifaqat.knowledgesharing.Adapters.FollowersAdapter;
import com.rifaqat.knowledgesharing.Repository.ProfileRepo;

public class ProfileViewModel extends AndroidViewModel {
    public final ProfileRepo profileRepo;
    FollowersAdapter adaptor;
    LinearLayoutManager layoutManager;
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepo=new ProfileRepo(application);
        adaptor=profileRepo.getAdaptor();
        layoutManager=profileRepo.getLayoutManager();
    }

    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public FollowersAdapter getAdaptor(){
        return adaptor;
    }

    public void ShowFollowers(){
        profileRepo.ShowFollowers();
    }
}
