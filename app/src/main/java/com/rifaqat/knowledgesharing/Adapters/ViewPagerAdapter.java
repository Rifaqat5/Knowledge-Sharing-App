package com.rifaqat.knowledgesharing.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rifaqat.knowledgesharing.Fragments.AddFragment;
import com.rifaqat.knowledgesharing.Fragments.AddTextFragment;
import com.rifaqat.knowledgesharing.Fragments.DocumentFragment;
import com.rifaqat.knowledgesharing.Fragments.VideoFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new AddTextFragment();
            case 1:return new AddFragment();
            case 2:return new VideoFragment();
            default:return new DocumentFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        switch (position){
            case 0:title="Ask Question";break;
            case 1:title="Add Image";break;
            case 2:title="Add Video";break;
            case 3:title="Add Document";break;
        }
        return title;
    }
}
