package com.rifaqat.knowledgesharing.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.R;

import com.rifaqat.knowledgesharing.databinding.PostsSampleBinding;
import com.rifaqat.knowledgesharing.Activities.CommentActivity;
import com.rifaqat.knowledgesharing.Models.Post;
import com.rifaqat.knowledgesharing.Models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> {
    Context context;
    ArrayList<Post> list;
    boolean testClick = false;

    public PostAdaptor(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.posts_sample, parent, false);
        return new ViewHolder(listItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post model = list.get(position);
        Picasso.get().load(model.getPostImage())
                .placeholder(R.drawable.placeholder2)
                .into(holder.binding.post);
        String time = TimeAgo.using(model.getPostedAt());
        holder.binding.time.setText(time);
        String desc = model.getPostDescription();
        if (desc.equals("")) {
            holder.binding.descriptionHome.setVisibility(View.GONE);
        } else {
            holder.binding.descriptionHome.setVisibility(View.VISIBLE);
            holder.binding.descriptionHome.setText(model.getPostDescription());
        }

        //Get user data from firebase to home fragment like profile,name etc.
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(model.getPostedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        holder.binding.nameHome.setText(user.getName());
                        holder.binding.departmentHome.setText(user.getDepartment());
                        Picasso.get().load(user.getProfile_picture())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.profileImgHome);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Like work...
        FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(model.getPostId())
                .child("likes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        if (firebaseAuth.getCurrentUser() != null) {
                            String currentUserId = firebaseAuth.getUid();
                            if(snapshot.hasChild(currentUserId)) {
                                // User has already liked the post
                                int likeCount = (int) snapshot.getChildrenCount();
                                holder.binding.like.setText(likeCount + "");
                                holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_1, 0, 0, 0);
                            } else {
                                // User has not liked the post yet
                                int likeCount = (int) snapshot.getChildrenCount();
                                holder.binding.like.setText(likeCount + "");
                                holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                            }
                        }
                        else {
                            Toast.makeText(context, "Waiting for user authentication", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Likes").child(model.getPostId()).child("likes");

                likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))) {
                            likesRef.child(FirebaseAuth.getInstance().getUid()).removeValue();
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                        } else {
                            likesRef.child(FirebaseAuth.getInstance().getUid()).setValue(true);
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_1, 0, 0, 0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

// Add listener to update like count in real-time
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(model.getPostId()).child("likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likeCount = (int) snapshot.getChildrenCount();
                holder.binding.like.setText(likeCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        //Here we are going to click on comment text to go to comment Activity but first we will show number of comments.
        FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(model.getPostId())
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int commentsCount = (int) snapshot.getChildrenCount();
                        holder.binding.comments.setText(commentsCount + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PostsSampleBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostsSampleBinding.bind(itemView);
        }
    }

}
