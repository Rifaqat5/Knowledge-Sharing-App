package com.rifaqat.knowledgesharing.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.auth.FirebaseUser;
import com.rifaqat.knowledgesharing.databinding.ActivitySignInBinding;
import com.rifaqat.knowledgesharing.ViewModels.AuthenticationViewModel;

import java.util.Objects;


public class SignInActivity extends AppCompatActivity {
    AuthenticationViewModel viewModel;
    ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Hide toolbar.
        Objects.requireNonNull(getSupportActionBar()).hide();

        //ViewModel Initialization...
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(AuthenticationViewModel.class);

        //When a user give correct information for sign in then we will navigate him/her to main activity.
        viewModel.getFirebaseUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser!= null){
                    startActivity(new Intent(SignInActivity.this,MainActivity.class));
                    finish();
                }
            }
        });


        //Now we are going to log in user.
        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    viewModel.login(email, password);
                } else {
                    Toast.makeText(SignInActivity.this, "Please fill all the boxes properly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //if user want to sign up.
        binding.createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
                finish();
            }
        });

        //        If a user forget his password
        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,ForgetActivity.class));
            }
        });
    }
}