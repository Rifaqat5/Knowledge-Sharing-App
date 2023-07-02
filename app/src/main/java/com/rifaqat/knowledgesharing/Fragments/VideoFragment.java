package com.rifaqat.knowledgesharing.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rifaqat.knowledgesharing.databinding.FragmentVideoBinding;
import com.rifaqat.knowledgesharing.Models.User;
import com.rifaqat.knowledgesharing.Models.Video;
import com.rifaqat.knowledgesharing.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

public class VideoFragment extends Fragment {

    FragmentVideoBinding binding;
    Uri videoUri;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog dialog;
    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaController=new MediaController(getContext());
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("Videos");
        dialog=new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        //        Here we are going to get the current user details from firebase
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
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

        binding.postImage.setMediaController(mediaController);
        binding.postImage.start();

        binding.uploadPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        /*Now we are going to store the video to storage and database when
        user click on post button...*/
        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessVideoUploading();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode== Activity.RESULT_OK){
            assert data != null;
            videoUri=data.getData();
            binding.postImage.setVideoURI(videoUri);
            binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.my_custom_bg_2));
            binding.postBtn.setEnabled(true);
            binding.postImage.setVisibility(View.VISIBLE);
            binding.description.setVisibility(View.VISIBLE);
        }
    }

    private void ProcessVideoUploading() {
        dialog.show();
        StorageReference reference=storageReference.child("videos")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(new Date().getTime()+"");
        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Video video=new Video();
                        video.setPostVideo(uri.toString());
                        video.setPostedVideoBy(FirebaseAuth.getInstance().getUid());
                        video.setDescriptionVideo(binding.description.getText().toString());
                        video.setPostedVideoAt(new Date().getTime());
                        databaseReference
                                .push()
                                .setValue(video).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float per=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                dialog.setMessage("uploading : "+(int)per+"%");
            }
        });
    }
}