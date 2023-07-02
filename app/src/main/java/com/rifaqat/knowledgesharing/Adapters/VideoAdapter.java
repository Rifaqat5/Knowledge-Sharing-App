package com.rifaqat.knowledgesharing.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.VideoSampleBinding;
import com.rifaqat.knowledgesharing.Activities.CommentVideoActivity;
import com.rifaqat.knowledgesharing.Models.User;
import com.rifaqat.knowledgesharing.Models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    ArrayList<Video> list;
    boolean testClick = false;
    private List<ViewHolder> activeViewHolders = new ArrayList<>();
    public VideoAdapter(Context context, ArrayList<Video> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.video_sample, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video model = list.get(position);

//        Show videos in recycler view from here...
        holder.bind(model.getPostVideo());
        activeViewHolders.add(holder);




        String time = TimeAgo.using(model.getPostedVideoAt());
        holder.binding.time.setText(time);
        String desc = model.getDescriptionVideo();
        if (desc.equals("")) {
            holder.binding.descriptionHome.setVisibility(View.GONE);
        } else {
            holder.binding.descriptionHome.setVisibility(View.VISIBLE);
            holder.binding.descriptionHome.setText(model.getDescriptionVideo());
        }

        //Get user data from firebase to home fragment like profile,name etc.
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(model.getPostedVideoBy())
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
                .child(model.getPostVideoId())
                .child("likes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        if (firebaseAuth.getCurrentUser() != null) {
                            String currentUserId = firebaseAuth.getUid();
                            if (snapshot.hasChild(currentUserId)) {
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
                        } else {
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
                        .child("Likes").child(model.getPostVideoId()).child("likes");

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
                .child("Likes").child(model.getPostVideoId()).child("likes");
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
                .child(model.getPostVideoId())
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
                Intent intent = new Intent(context, CommentVideoActivity.class);
                intent.putExtra("postId", model.getPostVideoId());
                intent.putExtra("postedBy", model.getPostedVideoBy());
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
        VideoSampleBinding binding;
        SimpleExoPlayer videoPlayer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = VideoSampleBinding.bind(itemView);
            videoPlayer=new SimpleExoPlayer.Builder(itemView.getContext()).build();
        }
        public void bind(String videoUrl) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            videoPlayer.setMediaItem(mediaItem);
            videoPlayer.setPlayWhenReady(false);
            videoPlayer.prepare();
            videoPlayer.seekTo(0, 0);
            binding.post.setPlayer(videoPlayer); // set the player onto the PlayerView
        }

        public void onRelease() {
            videoPlayer.release();
        }
    }

    public void releasePlayer() {
        for (ViewHolder viewHolder : activeViewHolders) {
            viewHolder.onRelease();
        }
    }
}
