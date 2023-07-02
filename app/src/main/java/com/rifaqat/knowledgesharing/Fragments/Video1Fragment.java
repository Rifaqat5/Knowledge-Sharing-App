package com.rifaqat.knowledgesharing.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rifaqat.knowledgesharing.databinding.FragmentVideo1Binding;
import com.rifaqat.knowledgesharing.ViewModels.VideoViewModel;



public class Video1Fragment extends Fragment {

    FragmentVideo1Binding binding;
    VideoViewModel viewModel;
    public Video1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(VideoViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding initialization.
        binding = FragmentVideo1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Here we are going to show the posts which are posted by different users.
        viewModel.getLayoutManager().setReverseLayout(true);
        viewModel.getLayoutManager().setStackFromEnd(true);
        binding.videoRv.setLayoutManager(viewModel.getLayoutManager());
        binding.videoRv.setAdapter(viewModel.getAdaptor());
        viewModel.ShowPosts();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.getAdaptor().releasePlayer();
    }
}