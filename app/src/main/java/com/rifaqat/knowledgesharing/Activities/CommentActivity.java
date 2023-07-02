package com.rifaqat.knowledgesharing.Activities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.R;
import com.rifaqat.knowledgesharing.databinding.ActivityCommentBinding;
import com.rifaqat.knowledgesharing.Adapters.CommentAdaptor;
import com.rifaqat.knowledgesharing.Models.Comments;
import com.rifaqat.knowledgesharing.Models.Post;
import com.rifaqat.knowledgesharing.Models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    Intent intent;
    String postId,postedBy;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CommentVM viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Remove toolbar
        Objects.requireNonNull(getSupportActionBar()).hide();
        viewModel =new CommentVM(getApplication());

        intent=getIntent();
        postId=intent.getStringExtra("postId");
        postedBy=intent.getStringExtra("postedBy");
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        //Get User Post Image and Description From Posts Node...
        database.getReference().child("Posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post=snapshot.getValue(Post.class);
                        assert post != null;
                        Picasso.get().load(post.getPostImage())
                                .placeholder(R.drawable.placeholder)
                                .into(binding.post);
                        binding.descriptionHome.setText(post.getPostDescription());
                        String time = TimeAgo.using(post.getPostedAt());
                        binding.time.setText(time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        Get user name,skill and profile from Users node.
        database.getReference()
                .child("Users")
                .child(postedBy).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        assert user != null;
                        Picasso.get().load(user.getProfile_picture())
                                .placeholder(R.drawable.placeholder)
                                .into(binding.profileImgHome);
                        binding.departmentHome.setText(user.getDepartment());
                        binding.nameHome.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        Now we are going to set click listener on post comment button to show comment on recycler view...
        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comments comment=new Comments();
                String commentBody=binding.writeComment.getText().toString();
                comment.setCommentBody(commentBody);
                comment.setCommentBy(auth.getUid());
                comment.setCommentAt(new Date().getTime());

                if(commentBody.isEmpty()){
                    Toast.makeText(CommentActivity.this, "Write Something", Toast.LENGTH_SHORT).show();
                }
                else {
                    database.getReference()
                            .child("Comments")
                            .child(postId)
                            .child("comments")
                            .push()
                            .setValue(comment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.writeComment.setText("");
                                    Toast.makeText(CommentActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

//        Call ShowComment() method from repo class through viewModel.and other required things
        viewModel.getLayoutManager().setReverseLayout(true);
        viewModel.getLayoutManager().setStackFromEnd(true);
        binding.commentRv.setLayoutManager(viewModel.getLayoutManager());
        binding.commentRv.setAdapter(viewModel.getAdaptor());
        viewModel.ShowComments();
    }




//    VIEW MODEL CLASS
public class CommentVM extends AndroidViewModel {
    public final CommentRepo commentRepo;
    CommentAdaptor adaptor;
    LinearLayoutManager layoutManager;
    public CommentVM(@NonNull Application application) {
        super(application);
        commentRepo=new CommentRepo(application);
        adaptor=commentRepo.getAdaptor();
        layoutManager=commentRepo.getLayoutManager();
    }

    public LinearLayoutManager getLayoutManager(){
        return layoutManager;
    }
    public CommentAdaptor getAdaptor(){
        return adaptor;
    }

    public void ShowComments(){
        commentRepo.ShowComments();
    }
}



//REPOSITORY CLASS
    public class CommentRepo {
        private final Application application;
        private FirebaseDatabase database;
        private ArrayList<Comments> list;
        private CommentAdaptor adaptor;
        private LinearLayoutManager layoutManager;

        public CommentRepo(Application application) {
            this.application = application;
            database = FirebaseDatabase.getInstance();
            list=new ArrayList<>();
            adaptor=new CommentAdaptor(list, application);
            layoutManager=new LinearLayoutManager(application);
        }

        public ArrayList<Comments> getList(){
            return list;
        }
        public LinearLayoutManager getLayoutManager(){
            return layoutManager;
        }
        public CommentAdaptor getAdaptor(){
            return adaptor;
        }

        public void ShowComments(){
            database.getReference()
                    .child("Comments")
                    .child(postId)
                    .child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                Comments comments=dataSnapshot.getValue(Comments.class);
                                list.add(comments);
                            }
                            adaptor.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}