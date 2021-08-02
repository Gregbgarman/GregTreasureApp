package com.mobile.treasureapp.TabFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.treasureapp.Adapters.GenericPostAdapter;
import com.mobile.treasureapp.Adapters.ProfilePostsAdapter;
import com.mobile.treasureapp.Fragments.HomeFragment;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class TabFragmentPosts extends Fragment {

    public interface TabFragmentPostsInterface{
        public void ShowDeleteDialog(Post post,Boolean IsFavorite);
    }
    private TabFragmentPostsInterface tabFragmentPostsInterface;

    private List<Post> MyPosts;
    private ProfilePostsAdapter MyPostsAdapter;
    private RecyclerView recyclerView;

    public TabFragmentPosts() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.rvTabMyPosts);
        ProfilePostsAdapter.ProfilePostsAdapterInterface profilePostsAdapterInterface=new ProfilePostsAdapter.ProfilePostsAdapterInterface() {
            @Override
            public void ExpandImage(Post post) {                    //handled in mainactivity
                tabFragmentPostsInterface.ShowDeleteDialog(post,false);
            }
        };

        MyPosts=new ArrayList<>();
        for (Post post: MainActivity.postList){
            if (post.GetPosterID().equals(MainActivity.CurrentUser.GetUserID())){
                MyPosts.add(post);
            }
        }

        MyPostsAdapter=new ProfilePostsAdapter(getContext(),MyPosts,profilePostsAdapterInterface);
        recyclerView.setAdapter(MyPostsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_posts, container, false);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if(context instanceof TabFragmentPosts.TabFragmentPostsInterface){
            tabFragmentPostsInterface = (TabFragmentPosts.TabFragmentPostsInterface) context;
        }

        else{
            throw new RuntimeException(context.toString()+
                    "must implement TabFragmentInterface");
        }

    }
}