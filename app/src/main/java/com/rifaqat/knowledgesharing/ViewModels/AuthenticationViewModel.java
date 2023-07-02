package com.rifaqat.knowledgesharing.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.rifaqat.knowledgesharing.Repository.AuthenticationRepository;

public class AuthenticationViewModel extends AndroidViewModel {
    private final AuthenticationRepository authenticationRepository;
    private final MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private final MutableLiveData<Boolean> userLogInOrLogOutMutableLiveData;

    public AuthenticationViewModel(@NonNull Application application) {
        super(application);
        authenticationRepository=new AuthenticationRepository(application);
        firebaseUserMutableLiveData=authenticationRepository.getFirebaseUserMutableLiveData();
        userLogInOrLogOutMutableLiveData=authenticationRepository.getUserLogInOrLogOutMutableLiveData();
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLogInOrLogOutMutableLiveData() {
        return userLogInOrLogOutMutableLiveData;
    }

    public void register(String email, String password,String name,String department){
        authenticationRepository.register(email,password,name,department);
    }

    public void login(String email,String password){
        authenticationRepository.login(email,password);
    }

    public void signOut(){
        authenticationRepository.signOut();
    }

    public void forgetPassword(String email){
        authenticationRepository.forgetPassword(email);
    }
}
