package com.mobile.treasureapp.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.treasureapp.Activities.OtherPeopleProfileActivity;
import com.mobile.treasureapp.Activities.SendMessagesActivity;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LongClickDialog extends DialogFragment implements View.OnClickListener {       //shown when clicking on a post in home feed

    private CardView cvViewprofile,cvAddtofavorites,cvMessage;
    private ImageView ivProfilePic,ivpostimage;
    private ImageView ivMessageIcon,ivProfileicon,ivfavicon;
    private TextView tvProfileMsg,tvMessagemsg,tvFavoriteMsg,tvPersonName;
    private Bundle PostFromAdapter;
    private Post post;


    public LongClickDialog() {
        // Required empty public constructor
    }

    public LongClickDialog(Bundle PostFromAdapter){
        this.PostFromAdapter=PostFromAdapter;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        post =PostFromAdapter.getParcelable("f");
        cvViewprofile=view.findViewById(R.id.flccardviewprofile);
        cvAddtofavorites=view.findViewById(R.id.flccardviewaddtofavorite);
        cvMessage=view.findViewById(R.id.flcCardviewmsg);
        ivProfilePic=view.findViewById(R.id.ivflcProfilepic);
        ivpostimage=view.findViewById(R.id.ivflcBig);
        tvFavoriteMsg=view.findViewById(R.id.tvflcAddfav);
        tvMessagemsg=view.findViewById(R.id.tvflcSendMessage);
        tvProfileMsg=view.findViewById(R.id.tvflcviewprofile);
        tvPersonName=view.findViewById(R.id.tvlcdPersonName);
        ivMessageIcon=view.findViewById(R.id.ivflcmessageicon);
        ivProfileicon=view.findViewById(R.id.ivflcprofileicon);
        ivfavicon=view.findViewById(R.id.ivflcfavoritesicon);

        Glide.with(getContext()).load(R.drawable.messageicon).into(ivMessageIcon);
        Glide.with(getContext()).load(R.drawable.randomperson).into(ivProfileicon);
        Glide.with(getContext()).load(R.drawable.starimage).into(ivfavicon);


        cvMessage.setOnClickListener(this);
       cvViewprofile.setOnClickListener(this);
       cvAddtofavorites.setOnClickListener(this);
        Glide.with(getContext()).load(post.GetPosterProfilePic().getUrl()).circleCrop().into(ivProfilePic);
        Glide.with(getContext()).load(post.GetPostImage().getUrl()).into(ivpostimage);
        tvPersonName.setText(post.GetPosterUserName());
        tvFavoriteMsg.setText("Add To Favorites");

        String firstname="";
        for (int i=0;i<post.GetPosterUserName().length();i++){              //getting first name of user who posted item
            if (post.GetPosterUserName().charAt(i)!=' '){
                firstname=firstname+post.GetPosterUserName().charAt(i);
            }
            else{
                break;
            }

        }

        tvMessagemsg.setText("Send " + firstname + " A Message");       //more personalized messages shown on screen
        tvProfileMsg.setText("View " + firstname + "'s Profile");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_long_click_dialog, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flcCardviewmsg:
                GoToMessage();
                dismiss();
                break;

            case R.id.flccardviewaddtofavorite:
                AddToFavorites();
                dismiss();
                break;

            case R.id.flccardviewprofile:
                GoToProfile();
                dismiss();
                break;
        }

    }

    private void GoToMessage(){
        Intent intent = new Intent(getContext(), SendMessagesActivity.class);
        intent.putExtra("thebundle",PostFromAdapter);       //adding bundle
        intent.putExtra("msg","FromPosts");         //adding message
        getContext().startActivity(intent);
    }

    private void GoToProfile(){
        Intent intent = new Intent(getContext(), OtherPeopleProfileActivity.class);
        intent.putExtra("thebundle",PostFromAdapter);
        getContext().startActivity(intent);
    }

    private void AddToFavorites(){
        Boolean NewItemAdded=false;
        ArrayList<String> Likeditems=new ArrayList<>();
        Likeditems.addAll(MainActivity.FavoriteItemsId);


        if (MainActivity.FavoriteItemsId.size()==0){                //no needed checking for duplicates if favorite list is empty
            MainActivity.FavoriteItemsId.add(post.getObjectId());
            NewItemAdded=true;
        }

        else{
            for (String Itemid:Likeditems){                         //Can't add duplicates-checking here to prevent it
                if (!Likeditems.contains(post.getObjectId())){
                    MainActivity.FavoriteItemsId.add(post.getObjectId().toString());
                    NewItemAdded=true;
                    break;
                }
            }
        }


        if (NewItemAdded==true){                                //updating favorite items list on firebase if new item added
            Map<String, Object> Updates = new HashMap<>();
            Updates.put("FavoriteItemsId", MainActivity.FavoriteItemsId);

            DatabaseReference mdatabase=FirebaseDatabase.getInstance().getReference("User").child(MainActivity.CurrentUser.GetFirebaseKey());
            mdatabase.updateChildren(Updates);
        }

    }

}