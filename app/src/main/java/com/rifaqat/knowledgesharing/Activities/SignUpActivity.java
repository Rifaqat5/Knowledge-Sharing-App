package com.rifaqat.knowledgesharing.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.ViewModels.AuthenticationViewModel;
import com.rifaqat.knowledgesharing.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    AuthenticationViewModel viewModel;
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create an ArrayAdapter for the spinner...Spinner is used to select department from dropdown menu.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.skills_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        // Set an OnItemSelectedListener to update the department EditText
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSkill = parent.getItemAtPosition(position).toString();
                binding.department.setText(selectedSkill);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
                Toast.makeText(SignUpActivity.this, "Please Select Any Department", Toast.LENGTH_SHORT).show();
            }
        });

// Disable editing on the skill EditText
        binding.department.setEnabled(false);


        //Hide toolbar.
        Objects.requireNonNull(getSupportActionBar()).hide();
        //ViewModel Initialization ...
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(AuthenticationViewModel.class);

        //if current users list is not null then we will navigate user to sign in from here means first screen always be sign in.
        viewModel.getFirebaseUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    finish();
                }
            }
        });

        //If user is already login before and click on already account btn then he will go to sign in screen.
        binding.alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });


        //Now we are going to register user by clicking on signUp btn.
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                String name = binding.name.getText().toString();
                String department = binding.department.getText().toString();

                if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !department.isEmpty()) {
                        viewModel.register(email, password, name, department);
//                    We will navigate user to sign in activity with user register
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                } else {
                    Toast.makeText(SignUpActivity.this, "Please fill all the boxes properly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}