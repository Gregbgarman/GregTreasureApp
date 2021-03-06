package com.mobile.treasureapp.TabFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class TabAdapter extends FragmentStateAdapter {          //used with viewpager in home fragment


    public TabAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {      //selecting item on viewpager
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
