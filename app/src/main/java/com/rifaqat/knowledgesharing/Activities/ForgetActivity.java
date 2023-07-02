package com.rifaqat.knowledgesharing.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rifaqat.knowledgesharing.databinding.ActivityForgetBinding;
import com.rifaqat.knowledgesharing.ViewModels.AuthenticationViewModel;


import java.util.Objects;

public class ForgetActivity extends AppCompatActivity {
    AuthenticationViewModel viewModel;
    ActivityForgetBinding binding;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Hide toolbar.
        Objects.requireNonNull(getSupportActionBar()).hide();

        //ViewModel Initialization...
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(AuthenticationViewModel.class);

        //Here we will call forget password method from our ViewModel.
        binding.forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=binding.email.getText().toString();
                if(email.isEmpty()){
                    binding.email.setError("Required");
                }
                else {
                    viewModel.forgetPassword(email);
                    startActivity(new Intent(ForgetActivity.this,SignInActivity.class));
                    finish();
                }
            }
        });
    }
}