package com.rifaqat.knowledgesharing.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.ActivityMainBinding;
import com.rifaqat.knowledgesharing.Fragments.AddMainFragment;
import com.rifaqat.knowledgesharing.Fragments.Document1Fragment;
import com.rifaqat.knowledgesharing.Fragments.HomeFragment;
import com.rifaqat.knowledgesharing.Fragments.ProfileFragment;
import com.rifaqat.knowledgesharing.Fragments.SearchFragment;
import com.rifaqat.knowledgesharing.Fragments.Video1Fragment;


import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Remove toolbar
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Replace Fragment on Bottom Navigation.
        //By default will be home...
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,new HomeFragment());
        transaction.commit();
        //noinspection deprecation
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.home:{
                        transaction.replace(R.id.container,new HomeFragment());
                        binding.add.setVisibility(View.VISIBLE);
                    }
                    break;

                    case R.id.document:{
                        transaction.replace(R.id.container,new Document1Fragment());
                        binding.add.setVisibility(View.VISIBLE);
                    }
                    break;

                    case R.id.video:{
                        transaction.replace(R.id.container,new Video1Fragment());
                        binding.add.setVisibility(View.VISIBLE);
                    }
                    break;

                    case R.id.search:{
                        transaction.replace(R.id.container,new SearchFragment());
                        binding.add.setVisibility(View.GONE);
                    }
                    break;

                    case R.id.profile:{
                        transaction.replace(R.id.container,new ProfileFragment());
                        binding.add.setVisibility(View.GONE);
                    }
                    break;
                }
                transaction.commit();
                return true;
            }
        });

//        Replace add post fragment on add button.
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container,new AddMainFragment());
                transaction.commit();
                binding.add.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the app
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}