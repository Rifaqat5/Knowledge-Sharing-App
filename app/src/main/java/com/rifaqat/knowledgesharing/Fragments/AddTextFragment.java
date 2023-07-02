package com.rifaqat.knowledgesharing.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rifaqat.knowledgesharing.Models.Question;
import com.rifaqat.knowledgesharing.Models.User;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.FragmentAddTextBinding;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

public class AddTextFragment extends Fragment {

    FragmentAddTextBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ProgressDialog dialog;

    public AddTextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dialog=new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //binding initialization.
        binding = FragmentAddTextBinding.inflate(inflater, container, false);
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

//        By Default postButton will be disabled
        binding.postBtn.setEnabled(false);
        binding.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String description=binding.description.getText().toString();
                if(!description.isEmpty()){
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.my_custom_bg_2));
                    binding.postBtn.setEnabled(true);
                }
                else {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.my_custom_bg));
                    binding.postBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        Now we are going to set click listener on postBtn to post question
        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                // Create a new Post object
                Question question = new Question();
                question.setQuestionDescription(binding.description.getText().toString());
                question.setQuestionBy(auth.getUid());
                question.setQuestionAt(new Date().getTime());

                // Get a new reference for the post node in Firebase
                DatabaseReference postRef = database.getReference().child("Questions").push();

                // Set the value of the new post node
                postRef.setValue(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Clear the description field and disable the post button
                        binding.description.setText("");
                        binding.postBtn.setEnabled(false);
                        binding.postBtn.setBackgroundResource(R.drawable.my_custom_bg);

                        dialog.dismiss();
                        Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}