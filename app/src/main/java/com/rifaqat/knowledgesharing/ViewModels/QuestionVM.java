package com.rifaqat.knowledgesharing.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rifaqat.knowledgesharing.Adapters.PostAdaptor;
import com.rifaqat.knowledgesharing.Adapters.QuestionAdapter;
import com.rifaqat.knowledgesharing.Repository.HomeRepo;
import com.rifaqat.knowledgesharing.Repository.QuestionsRepo;

public class QuestionVM extends AndroidViewModel {
    public final QuestionsRepo questionRepo;
    QuestionAdapter adaptor;
    LinearLayoutManager layoutManager;
    public QuestionVM(@NonNull Application application) {
        super(application);
        questionRepo=new QuestionsRepo(application);
        adaptor=questionRepo.getAdaptor();
        layoutManager=questionRepo.getLayoutManager();
    }

    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public QuestionAdapter getAdaptor(){
        return adaptor;
    }

    public void ShowPosts(){
        questionRepo.ShowPosts();
    }
}
