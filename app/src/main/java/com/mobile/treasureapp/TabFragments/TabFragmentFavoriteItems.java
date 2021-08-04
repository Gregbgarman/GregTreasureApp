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
import com.mobile.treasureapp.Adapters.ProfilePostsAdapter;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;


public class TabFragmentFavoriteItems extends Fragment {

    public interface TabFragmentFavoriteItemsInterface{
        public void ShowDeleteDialog(Post post,Boolean IsFavorite);
    }
    private TabFragmentFavoriteItemsInterface tabFragmentFavoriteItemsInterface;

    private RecyclerView recyclerView;
    private List<Post> FavoriteItemsList;
    private ProfilePostsAdapter FavoriteItemsAdapter;

    public TabFragmentFavoriteItems() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.rvTabFavoriteItems);
        ProfilePostsAdapter.ProfilePostsAdapterInterface profilePostsAdapterInterface=new ProfilePostsAdapter.ProfilePostsAdapterInterface() {
            @Override
            public void ExpandImage(Post post) {
                tabFragmentFavoriteItemsInterface.ShowDeleteDialog(post,true);      //code runs back in mainactivity
            }
        };


        FavoriteItemsList=new ArrayList<>();
        for (Post post:MainActivity.postList){                          //adding favorite items based on the post ID of the item
            for (int i=0;i<MainActivity.FavoriteItemsId.size();i++){
                String Itemid=MainActivity.FavoriteItemsId.get(i);
                if (post.getObjectId().equals(Itemid)){
                    FavoriteItemsList.add(post);
                }
            }
        }

        FavoriteItemsAdapter=new ProfilePostsAdapter(getContext(),FavoriteItemsList,profilePostsAdapterInterface);
        recyclerView.setAdapter(FavoriteItemsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_favorite_items, container, false);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if(context instanceof TabFragmentFavoriteItems.TabFragmentFavoriteItemsInterface){
            tabFragmentFavoriteItemsInterface= (TabFragmentFavoriteItems.TabFragmentFavoriteItemsInterface) context;
        }

        else{
            throw new RuntimeException(context.toString()+
                    "must implement TabFavFragmentInterface");
        }

    }
}