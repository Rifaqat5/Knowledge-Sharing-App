package com.rifaqat.knowledgesharing.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rifaqat.knowledgesharing.databinding.FragmentSearchBinding;
import com.rifaqat.knowledgesharing.Adapters.FriendsAdaptor;
import com.rifaqat.knowledgesharing.Models.User;


import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<User> list;
    FriendsAdaptor adaptor;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        adaptor = new FriendsAdaptor(list, getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding initialization.
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //RecyclerView Work...
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.friendsRv.setLayoutManager(layoutManager);
        binding.friendsRv.setAdapter(adaptor);

        // Set User data from firebase to list
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserId(dataSnapshot.getKey());
                    //Current user profile will not show in the list...
                    if (!Objects.equals(dataSnapshot.getKey(), FirebaseAuth.getInstance().getUid())) {
                        list.add(user);
                    }
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Add a TextChangedListener to the search bar to perform search as the user types
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = s.toString().toLowerCase().trim();
                searchUsersByName(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Searches for users in the list by name
     * @param searchQuery the search query entered by the user
     */
    private void searchUsersByName(String searchQuery) {
        ArrayList<User> searchList = new ArrayList<>();
        for (User user : list) {
            if (user.getName().toLowerCase().contains(searchQuery)) {
                searchList.add(user);
            }
        }
        adaptor.setList(searchList);
    }
}
