package com.rifaqat.knowledgesharing.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.FriendsSampleBinding;
import com.rifaqat.knowledgesharing.Activities.MakeFriendActivity;
import com.rifaqat.knowledgesharing.Models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdaptor extends RecyclerView.Adapter<FriendsAdaptor.ViewHolder> {
    ArrayList<User> list;
    Context context;

    public FriendsAdaptor(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setList(ArrayList<User> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.friends_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=list.get(position);
        holder.binding.nameFriends.setText(user.getName());
        Picasso.get().load(user.getProfile_picture())
                        .placeholder(R.drawable.placeholder)
                                .into(holder.binding.profileImgFriends);
        holder.binding.departmentFriends.setText(user.getDepartment());

        //Here we are going to set click listener on item to go to FriendDetailsFragment...
        holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId= user.getUserId();
                Intent intent=new Intent(context, MakeFriendActivity.class);
                intent.putExtra("userId",userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        FriendsSampleBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=FriendsSampleBinding.bind(itemView);
        }
    }
}
