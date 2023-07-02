package com.rifaqat.knowledgesharing.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.Models.User;
import com.rifaqat.knowledgesharing.R;

import com.rifaqat.knowledgesharing.ViewModels.HomeViewModel;
import com.rifaqat.knowledgesharing.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    HomeViewModel viewModel;
    FirebaseAuth auth ;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(HomeViewModel.class);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //binding initialization.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = auth.getCurrentUser();

        //        Here we are going to get the current user details from firebase
        if (currentUser != null){
            String userId = currentUser.getUid();
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                assert user != null;
                                Picasso.get().load(user.getProfile_picture())
                                        .placeholder(R.drawable.placeholder)
                                        .into(binding.profileImg);
                                binding.department.setText(user.getDepartment());
                                binding.name.setText(user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

//        Now we are going to click on seeProfile button and navigate user to FragmentProfile.
        binding.seeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Create an instance of the target fragment
                ProfileFragment targetFragment = new ProfileFragment();

                // Obtain the FragmentManager
                FragmentManager fragmentManager = getFragmentManager();

                // Start a fragment transaction
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the target fragment
                fragmentTransaction.replace(R.id.container, targetFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        //if someone want to ask question
        binding.askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTextFragment targetFragment = new AddTextFragment();

                // Obtain the FragmentManager
                FragmentManager fragmentManager = getFragmentManager();

                // Start a fragment transaction
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the target fragment
                fragmentTransaction.replace(R.id.container, targetFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });


//        if someone want to see questions list
        binding.viewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowQuestionsFragment targetFragment = new ShowQuestionsFragment();

                // Obtain the FragmentManager
                FragmentManager fragmentManager = getFragmentManager();

                // Start a fragment transaction
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the target fragment
                fragmentTransaction.replace(R.id.container, targetFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        Toast.makeText(getContext(), "Please wait data is loading....", Toast.LENGTH_SHORT).show();
        // Here we are going to show the posts which are posted by different users.
        viewModel.getLayoutManager().setReverseLayout(true);
        viewModel.getLayoutManager().setStackFromEnd(true);
        binding.postRv.setLayoutManager(viewModel.getLayoutManager());
        binding.postRv.setAdapter(viewModel.getAdaptor());
        viewModel.ShowPosts();
    }
}