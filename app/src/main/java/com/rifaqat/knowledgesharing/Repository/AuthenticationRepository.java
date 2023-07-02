package com.rifaqat.knowledgesharing.Repository;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rifaqat.knowledgesharing.Models.User;

public class AuthenticationRepository {
    private Application application;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private MutableLiveData<Boolean> userLogInOrLogOutMutableLiveData;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public AuthenticationRepository(Application application) {
        this.application = application;
        firebaseUserMutableLiveData = new MutableLiveData<>();
        userLogInOrLogOutMutableLiveData = new MutableLiveData<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (auth.getCurrentUser() != null) {
            firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
        }
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLogInOrLogOutMutableLiveData() {
        return userLogInOrLogOutMutableLiveData;
    }

    //Method for creating new user or Register(sign up).
    public void register(String email, String password, String name, String department) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
                    User user = new User(name, email, password, department);
                    //get id of user that register.
                    String id = task.getResult().getUser().getUid();
                    //save data of user to database.
                    database.getReference().child("Users").child(id).setValue(user);
                    Toast.makeText(application, "User Created Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method for login already register user(sign in).
    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
                } else {
                    Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method for sign out
    public void signOut() {
        auth.signOut();
        userLogInOrLogOutMutableLiveData.postValue(true);
    }

    //Method for forget password...
    public void forgetPassword(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(application, "Check Your Email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(application, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
