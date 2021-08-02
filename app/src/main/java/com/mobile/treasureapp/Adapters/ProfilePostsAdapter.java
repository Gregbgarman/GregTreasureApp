package com.mobile.treasureapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    public interface ProfilePostsAdapterInterface{
        public void ExpandImage(Post post);
    }

    private Context context;
    private List<Post> postList;
    private ProfilePostsAdapterInterface profilePostsAdapterInterface;

    public ProfilePostsAdapter(Context context,List<Post> postList,ProfilePostsAdapterInterface profilePostsAdapterInterface){
        this.context=context;
        this.postList=postList;
        this.profilePostsAdapterInterface=profilePostsAdapterInterface;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.eachprofilepost,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Post post=postList.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivPostImage;
        private ConstraintLayout container;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivPostImage=itemView.findViewById(R.id.iveachprofilepost);
            container=itemView.findViewById(R.id.ivContainer);
        }
        public void bind(Post post){
            Glide.with(context).load(post.GetPostImage().getUrl()).into(ivPostImage);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profilePostsAdapterInterface.ExpandImage(post);
                }
            });
        }
    }
}
