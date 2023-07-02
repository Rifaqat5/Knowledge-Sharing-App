package com.rifaqat.knowledgesharing.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.ActivityMakeFriendBinding;
import com.rifaqat.knowledgesharing.Models.User;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MakeFriendActivity extends AppCompatActivity {
    ActivityMakeFriendBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String otherUserId, currentUserId, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMakeFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        state = "not_friends";
        //get user id from FriendsAdaptor which is put in extra(intent)...
        otherUserId = getIntent().getStringExtra("userId");

//        Take data from database to the views.
        database.getReference().child("Users")
                .child(otherUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            Picasso.get().load(user.getProfile_picture())
                                    .placeholder(R.drawable.placeholder)
                                    .into(binding.profileImg);
                            binding.name.setText(user.getName());
                            binding.email.setText(user.getEmail());
                            binding.department.setText(user.getDepartment());
                            //This method will keep trace of sendRequest and decline button text.
                            MaintananceOfBtnText();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //First we are going to do work on sent request so we hide and disable decline button...
        binding.decline.setVisibility(View.INVISIBLE);
        binding.decline.setEnabled(false);

//        Now we are going to set click listener on sendRequest means to do sent request work...
        binding.sentRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sentRequest.setEnabled(false);
                if (state.equals("not_friends")) {
                    SendFriendRequest();
                } else if (state.equals("request_sent")) {
                    CancelFriendRequest();
                } else if (state.equals("request_received")) {
                    AcceptFriendRequest();
                } else if (state.equals("friends")) {
                    UnFriendExistingFriend();
                }
            }
        });
    }

    private void UnFriendExistingFriend() {
        database.getReference().child("Friends")
                .child(currentUserId).child(otherUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("Friends")
                                    .child(otherUserId).child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.sentRequest.setEnabled(true);
                                                binding.sentRequest.setText("Send Request");
                                                state = "not_friends";
                                                binding.decline.setVisibility(View.INVISIBLE);
                                                binding.decline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        User user=new User();
        User user1=new User();
        user.setFriendId(currentUserId);
        user1.setFriendId(otherUserId);

        database.getReference().child("Friends").child(currentUserId).child(otherUserId)
                .setValue(user1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("Friends").child(otherUserId).child(currentUserId)
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference().child("Friend_Requests")
                                                    .child(currentUserId).child(otherUserId)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                database.getReference().child("Friend_Requests")
                                                                        .child(otherUserId).child(currentUserId)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    binding.sentRequest.setEnabled(true);
                                                                                    binding.sentRequest.setText("UnFriend");
                                                                                    state = "friends";
                                                                                    binding.decline.setVisibility(View.INVISIBLE);
                                                                                    binding.decline.setEnabled(false);
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        database.getReference().child("Friend_Requests")
                .child(currentUserId).child(otherUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("Friend_Requests")
                                    .child(otherUserId).child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.sentRequest.setEnabled(true);
                                                binding.sentRequest.setText("Send Request");
                                                state = "not_friends";
                                                binding.decline.setVisibility(View.INVISIBLE);
                                                binding.decline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceOfBtnText() {
        database.getReference().child("Friend_Requests")
                .child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(otherUserId)) {
                            String request_type = dataSnapshot.child(otherUserId).child("request_type").getValue().toString();
                            if (request_type.equals("sent")) {
                                binding.sentRequest.setText("Cancel Request");
                                state = "request_sent";
                                binding.decline.setVisibility(View.INVISIBLE);
                                binding.decline.setEnabled(false);
                            } else if (request_type.equals("received")) {
                                binding.sentRequest.setText("Accept Request");
                                state = "request_received";
                                binding.decline.setVisibility(View.VISIBLE);
                                binding.decline.setEnabled(true);

                                //Here is the work of decline friend request...
                                binding.decline.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelFriendRequest();
                                    }
                                });
                            }
                        } else {
                            database.getReference().child("Friends")
                                    .child(currentUserId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(otherUserId)) {
                                                state = "friends";
                                                binding.sentRequest.setText("UnFriend");
                                                binding.decline.setVisibility(View.INVISIBLE);
                                                binding.decline.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequest() {
        database.getReference().child("Friend_Requests")
                .child(currentUserId).child(otherUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("Friend_Requests")
                                    .child(otherUserId).child(currentUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.sentRequest.setEnabled(true);
                                                binding.sentRequest.setText("Cancel Request");
                                                state = "request_sent";
                                                binding.decline.setVisibility(View.INVISIBLE);
                                                binding.decline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}