package com.mobile.treasureapp.TabFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class TabAdapter extends FragmentStateAdapter {


    public TabAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position==0){
            return new TabFragmentFavoriteItems();
        }
        else{
            return new TabFragmentPosts();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
