package com.rifaqat.knowledgesharing.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.DocumentSampleBinding;
import com.rifaqat.knowledgesharing.Activities.viewpdf;
import com.rifaqat.knowledgesharing.Models.Document;
import com.rifaqat.knowledgesharing.Models.User;
import com.squareup.picasso.Picasso;


public class DocumentAdapter extends FirebaseRecyclerAdapter<Document,DocumentAdapter.ViewHolder> {
    Context context;
    public DocumentAdapter(@NonNull FirebaseRecyclerOptions<Document> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Document model) {
        String time = TimeAgo.using(model.getPostedVideoAt());
        holder.binding.time.setText(time);
        String desc = model.getTitle();
        if (desc.equals("")) {
            holder.binding.descriptionHome.setVisibility(View.GONE);
        } else {
            holder.binding.descriptionHome.setVisibility(View.VISIBLE);
            holder.binding.descriptionHome.setText(model.getTitle());
        }

        //Get user data from firebase to home fragment like profile,name etc.
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(model.getPostedDocumentBy())
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

        //Here we are opening our document in another activity through intent.
        holder.binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.binding.post.getContext(),viewpdf.class);
                intent.putExtra("filename",model.getTitle());
                intent.putExtra("fileurl",model.getPostDocument());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.binding.post.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.document_sample,parent,false);
        return  new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DocumentSampleBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DocumentSampleBinding.bind(itemView);
        }
    }
}
