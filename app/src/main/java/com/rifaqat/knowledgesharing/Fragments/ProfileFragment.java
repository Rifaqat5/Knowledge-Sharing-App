package com.rifaqat.knowledgesharing.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifaqat.knowledgesharing.databinding.FragmentProfileBinding;
import com.rifaqat.knowledgesharing.Activities.SignInActivity;
import com.rifaqat.knowledgesharing.Models.User;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.ViewModels.AuthenticationViewModel;
import com.rifaqat.knowledgesharing.ViewModels.ProfileViewModel;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    AuthenticationViewModel viewModel;
    FragmentProfileBinding binding;
    ActivityResultLauncher<String> galleryLauncher;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Dialog aboutDialog;
    ProfileViewModel viewModel1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ViewModel Initialization ...
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(AuthenticationViewModel.class);
        viewModel1 = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(ProfileViewModel.class);

        database=FirebaseDatabase.getInstance();
        aboutDialog=new Dialog(getContext());

        //When a user logout then will navigate to sign in activity.
        viewModel.getUserLogInOrLogOutMutableLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                startActivity(new Intent(getContext(), SignInActivity.class));
                getActivity().finish();
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //binding initialization.
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Sign out method...
        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.signOut();
            }
        });

//        Opening Gallery to take profile picture...
        storage = FirebaseStorage.getInstance();
        binding.addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        binding.profileImg.setImageURI(result);

                        //Store taken image to FirebaseStorage.
                        final StorageReference reference = storage.getReference()
                                .child("profile_picture")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Profile Uploaded", Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("profile_picture").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                    }
                });


        //Read data from data base to profile fragment.
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user=snapshot.getValue(User.class);
                            assert user != null;
                            Picasso.get().load(user.getProfile_picture())
                                    .placeholder(R.drawable.placeholder)
                                            .into(binding.profileImg);
                            binding.name.setText(user.getName());
                            binding.email.setText(user.getEmail());
                            binding.department.setText(user.getDepartment());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        // Show friends from recycler view...and Followers RV work
        viewModel1.getLayoutManager().setReverseLayout(true);
        viewModel1.getLayoutManager().setStackFromEnd(true);
        binding.followersRv.setLayoutManager(viewModel1.getLayoutManager());
        binding.followersRv.setAdapter(viewModel1.getAdaptor());
        viewModel1.ShowFollowers();
    }
}