package com.rifaqat.knowledgesharing.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.CommentSampleBinding;
import com.rifaqat.knowledgesharing.Models.Comments;
import com.rifaqat.knowledgesharing.Models.User;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdaptor extends RecyclerView.Adapter<CommentAdaptor.ViewHolder> {
    ArrayList<Comments> list;
    Context context;

    public CommentAdaptor(ArrayList<Comments> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.comment_sample, parent, false);
        return new CommentAdaptor.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comments comment= list.get(position);
        String time = TimeAgo.using(comment.getCommentAt());
        holder.binding.time.setText(time);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(comment.getCommentBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        assert user != null;
                        Picasso.get().load(user.getProfile_picture())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.profileImgFriends);
                        holder.binding.commentDesc.setText(Html.fromHtml("<b>"+user.getName()+"</b> "+comment.getCommentBody()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CommentSampleBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= CommentSampleBinding.bind(itemView);
        }
    }
}
