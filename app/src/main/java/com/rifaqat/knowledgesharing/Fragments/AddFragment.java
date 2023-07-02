package com.rifaqat.knowledgesharing.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.FragmentAddBinding;
import com.rifaqat.knowledgesharing.Models.Post;
import com.rifaqat.knowledgesharing.Models.User;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ActivityResultLauncher<String> galleryLauncher;
    Uri uri;
    ProgressDialog dialog;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dialog=new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set dialog contents etc.
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

//        Here we are going to get the current user details from firebase
        database.getReference().child("Users")
                .child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            assert user != null;
                            Picasso.get().load(user.getProfile_picture())
                                    .placeholder(R.drawable.placeholder)
                                    .into(binding.profileImgAdd);
                            binding.departmentAdd.setText(user.getDepartment());
                            binding.nameAdd.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//      Opening Gallery to take post picture...
        binding.uploadPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        uri=result;
                        binding.postImage.setImageURI(uri);
                        binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.my_custom_bg_2));
                        binding.postBtn.setEnabled(true);
                        binding.postImage.setVisibility(View.VISIBLE);
                        binding.description.setVisibility(View.VISIBLE);
                    }
                });
        /*Now we are going to store the file/image to storage and database when
        user click on post button...*/
        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final StorageReference reference = storage.getReference()
                        .child("post")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String currentUserId = currentUser.getUid();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                User user = snapshot.getValue(User.class);
                                                if (user != null) {
                                                    String currentUserDepartment = user.getDepartment();
                                                    Post post = new Post();
                                                    post.setPostImage(uri.toString());
                                                    post.setPostedBy(auth.getUid());
                                                    post.setPostDescription(binding.description.getText().toString());
                                                    post.setPostedAt(new Date().getTime());
                                                    post.setDepartment(currentUserDepartment);

                                                    database.getReference()
                                                            .child("Posts")
                                                            .push()
                                                            .setValue(post)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @SuppressLint("UseCompatLoadingForDrawables")
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.postBtn.setEnabled(false);
                                                                    binding.postBtn.getResources().getDrawable(R.drawable.disable_btn_bg);
                                                                    binding.postImage.setVisibility(View.GONE);
                                                                    binding.description.setVisibility(View.GONE);
                                                                    binding.description.setText("");
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle the error case if needed
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}