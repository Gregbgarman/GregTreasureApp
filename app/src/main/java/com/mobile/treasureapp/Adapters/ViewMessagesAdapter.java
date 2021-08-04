package com.mobile.treasureapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mobile.treasureapp.Activities.SendMessagesActivity;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.MostRecentMessage;
import com.mobile.treasureapp.R;
import com.mobile.treasureapp.TimeFormatter;
import java.util.List;

public class ViewMessagesAdapter extends RecyclerView.Adapter<ViewMessagesAdapter.ViewHolder> {

                                        //adapter used to display list of conversations user has had with other users

    private Context context;
    private List<MostRecentMessage> mostRecentMessageList;

    public ViewMessagesAdapter(Context context, List<MostRecentMessage> mostRecentMessageList ){
        this.context=context;
        this.mostRecentMessageList=mostRecentMessageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.viewmessagesadapterlayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewMessagesAdapter.ViewHolder holder, int position) {
        holder.bind(mostRecentMessageList.get(position));
    }

    @Override
    public int getItemCount() {
        return mostRecentMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfilePic,ivRedDot;
        private TextView tvRecentMsg,tvTimeSent,tvPersonName;
        private ConstraintLayout container;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            ivProfilePic=itemView.findViewById(R.id.ivVMProfilePic);
            ivRedDot=itemView.findViewById(R.id.ivRedDot);
            tvRecentMsg=itemView.findViewById(R.id.tvVMmessage);
            tvTimeSent=itemView.findViewById(R.id.tvVMTimeAt);
            tvPersonName=itemView.findViewById(R.id.tvVMPersonName);
            container=itemView.findViewById(R.id.VMContainer);

        }
        public void bind(MostRecentMessage mostRecentMessage){

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SendMessagesActivity.class);
                    Bundle mbundle=new Bundle();
                    mbundle.putParcelable("recentmsg",mostRecentMessage);
                    intent.putExtra("mybundle",mbundle);
                    intent.putExtra("msg","FromMessages");
                    context.startActivity(intent);

                }
            });


            if (mostRecentMessage.getSenderid().equals(MainActivity.CurrentUser.GetUserID())){
                tvPersonName.setText(mostRecentMessage.getReceiverUsername());
                Glide.with(context).load(mostRecentMessage.getReceiverProfilePic().getUrl()).circleCrop().into(ivProfilePic);
            }
            else{
                tvPersonName.setText(mostRecentMessage.getSenderUserName());
                Glide.with(context).load(mostRecentMessage.getSenderProfilePic().getUrl()).circleCrop().into(ivProfilePic);
            }

            Glide.with(context).load(R.drawable.reddot).circleCrop().into(ivRedDot);
            if (mostRecentMessage.getReceiverId().equals(MainActivity.CurrentUser.GetUserID()) && mostRecentMessage.getReceiverHasRead()==false){
                ivRedDot.setVisibility(View.VISIBLE);
            }
            else{
                ivRedDot.setVisibility(View.INVISIBLE);
            }

            tvRecentMsg.setText(mostRecentMessage.getMessage());
            tvTimeSent.setText(TimeFormatter.getTimeDifference(mostRecentMessage.getCreatedAt().toString()));


        }
    }
}
