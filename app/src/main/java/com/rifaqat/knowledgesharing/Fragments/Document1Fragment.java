package com.rifaqat.knowledgesharing.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.rifaqat.knowledgesharing.databinding.FragmentDocument1Binding;
import com.rifaqat.knowledgesharing.Adapters.DocumentAdapter;
import com.rifaqat.knowledgesharing.Models.Document;

public class Document1Fragment extends Fragment {
    FragmentDocument1Binding binding;
    DocumentAdapter adapter;


    public Document1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDocument1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        binding.documentsRv.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Document> options = new FirebaseRecyclerOptions.Builder<Document>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Documents"), Document.class)
                .build();

        adapter = new DocumentAdapter(options);
        binding.documentsRv.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}