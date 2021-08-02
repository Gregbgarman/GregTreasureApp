package com.mobile.treasureapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.mobile.treasureapp.Activities.SendMessagesActivity;
import com.mobile.treasureapp.Activities.UserSchoolActivity;
import com.mobile.treasureapp.Dialogs.ConfirmSchoolDialog;
import com.mobile.treasureapp.Dialogs.LongClickDialog;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import com.mobile.treasureapp.TimeFormatter;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class GenericPostAdapter extends RecyclerView.Adapter<GenericPostAdapter.ViewHolder>  {

    public interface GenericPostAdapterInterface{
        public void ShowMenu(Bundle bundle);
    }

    private List<Post> postList;
    private Context context;
    private boolean UseNormalSize;
    private GenericPostAdapterInterface genericPostAdapterInterface;

    public GenericPostAdapter(Context context,List<Post> postList,boolean UseNormalSize,GenericPostAdapterInterface genericPostAdapterInterface){
        this.context=context;
        this.postList=postList;
        this.UseNormalSize=UseNormalSize;
        this.genericPostAdapterInterface=genericPostAdapterInterface;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view=null;
        if (UseNormalSize==true){
            view= LayoutInflater.from(context).inflate(R.layout.eachgenericpost,parent,false);
        }
        else{
            view= LayoutInflater.from(context).inflate(R.layout.eachgenericpostsmall,parent,false);
        }

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

        private TextView tvPosterName,tvItemName,tvPostTime;
        private ImageView ivPosterProfilePic,ivItemImage;
        private CardView cardView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            if (UseNormalSize==true) {
                tvItemName = itemView.findViewById(R.id.tvGPItemName);
                tvPosterName = itemView.findViewById(R.id.tvGPUsername);
                ivItemImage = itemView.findViewById(R.id.ivGPPostImage);
                ivPosterProfilePic = itemView.findViewById(R.id.ivGPProfilePic);
                tvPostTime = itemView.findViewById(R.id.tvGPPostTime);
                cardView=itemView.findViewById(R.id.GenericCardContainer);
            }
            else{
                tvItemName = itemView.findViewById(R.id.tvGPItemNameSmall);
                tvPosterName = itemView.findViewById(R.id.tvGPUsernameSmallz);
                ivItemImage = itemView.findViewById(R.id.ivGPPostImageSmall);
                ivPosterProfilePic = itemView.findViewById(R.id.ivGPProfilePicSmall);
                tvPostTime = itemView.findViewById(R.id.tvGPPostTimeSmall);
                cardView=itemView.findViewById(R.id.cardviewsmall);

            }
        }

        public void bind(Post post){


            for (String itemid: MainActivity.FavoriteItemsId){
                if (post.getObjectId().equals(itemid)){
                    cardView.setCardBackgroundColor(Color.parseColor("#A1DBE1"));
                }
            }

            tvPosterName.setText(post.GetPosterUserName());
            tvItemName.setText(post.GetItemName());
            tvPostTime.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()));
            Glide.with(context).load(post.GetPostImage().getUrl()).into(ivItemImage);
            Glide.with(context).load(post.GetPosterProfilePic().getUrl()).circleCrop().into(ivPosterProfilePic);

            if (!post.GetPosterID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {     //if not yourself

                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //show a dialog back in fragment, must pass the bundle so it can be used

                        return false;
                    }
                });

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                     //   Intent intent = new Intent(context, SendMessagesActivity.class);
                        Bundle mbundle=new Bundle();
                        mbundle.putParcelable("f",post);
                     //   intent.putExtra("thebundle",mbundle);
                     //   intent.putExtra("msg","FromPosts");

                        genericPostAdapterInterface.ShowMenu(mbundle);

                       // context.startActivity(intent);

                    }
                });
            }

        }
    }
}
