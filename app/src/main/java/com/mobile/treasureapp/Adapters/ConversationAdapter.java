package com.mobile.treasureapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mobile.treasureapp.Models.Message;
import com.mobile.treasureapp.R;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{

    private Context context;                        //this is the adapter used for the conversations between users
    private List<Message> messageList;
    private boolean IsMe;
    private Uri MyProfilePic;
    private ParseFile OtherPersonProfilePic;


    public ConversationAdapter(Context context, List<Message> messageList,Uri MyProfilePic,ParseFile OtherPersonProfilePic){
        this.context=context;
        this.messageList=messageList;
        this.MyProfilePic=MyProfilePic;
        this.OtherPersonProfilePic=OtherPersonProfilePic;
    }

    @Override
    public int getItemViewType(int position) {          //figuring out who the sender is, either logged in user or isn't
        Message message=messageList.get(position);

        if (message.GetSenderID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            IsMe=true;
            return 1;
        }
        else{
            IsMe=false;
            return 0;
        }

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view=null;
                                    //using correct layouts depending on who is the sender and receiver
        if (viewType==1) {
            view = LayoutInflater.from(context).inflate(R.layout.mymessagelayout, parent, false);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.otherpersonmessagelayout, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMsg;
        private ImageView ivPhoto;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            if (IsMe==true) {
                tvMsg = itemView.findViewById(R.id.tvMessageUser);
                ivPhoto = itemView.findViewById(R.id.ivMessageUserPhoto);
            }
            else{
                tvMsg = itemView.findViewById(R.id.tvotherpersonmessage);
                ivPhoto = itemView.findViewById(R.id.ivotherpersonphoto);
            }

        }

        public void bind(Message message){

            String formattedmsg="";
            String themessage= message.GetMessageContent();
            String temp="";
            boolean NeedBreak=false;
            for (int i=0;i<themessage.length();i++){
               temp=temp+themessage.charAt(i);
               formattedmsg=formattedmsg+themessage.charAt(i);          //formatting message so 18 characters in each line
                if (temp.length()%18==0){                               //before wrapping text using \n
                    NeedBreak=true;
                }

                if (NeedBreak==true && temp.charAt(i)==' '){
                    formattedmsg=formattedmsg+"\n";
                    NeedBreak=false;
                }
            }

            tvMsg.setText(formattedmsg);

            if (IsMe==true){
                Glide.with(context).load(MyProfilePic).circleCrop().into(ivPhoto);
            }
            else{
                Glide.with(context).load(OtherPersonProfilePic.getUrl()).circleCrop().into(ivPhoto);
            }

        }
    }

}