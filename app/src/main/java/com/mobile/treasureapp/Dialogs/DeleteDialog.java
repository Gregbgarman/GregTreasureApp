package com.mobile.treasureapp.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import com.mobile.treasureapp.TimeFormatter;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class DeleteDialog extends DialogFragment {

    public interface DeleteDialogInterface{
        public void UpdateFeed();
    }

    private DeleteDialogInterface deleteDialogInterface;
    private Post PostToDelete;
    private boolean IsFavorite;
    private ImageView ivItem;
    private Button btnRemove;
    private TextView tvPostedAt,tvItemName;


    public DeleteDialog() {
        // Required empty public constructor
    }

    public DeleteDialog(Post post, Boolean IsFavorite){
        PostToDelete=post;
        this.IsFavorite=IsFavorite;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


            ivItem = view.findViewById(R.id.ivDeletedialog);
            tvItemName = view.findViewById(R.id.tvdeletedialogitemname);
            tvPostedAt = view.findViewById(R.id.tvdeletedialogpostedat);
            btnRemove=view.findViewById(R.id.btndeletedialog);
            Glide.with(getContext()).load(PostToDelete.GetPostImage().getUrl()).into(ivItem);
            tvItemName.setText(PostToDelete.GetItemName());

        if (IsFavorite==false) {
            tvPostedAt.setText("You Posted "+ TimeFormatter.getTimeDifference(PostToDelete.getCreatedAt().toString()) + " Ago");


            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure? This action can't be undone");
                    builder.setNegativeButton("No",null);
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeleteMyPost();
                            EndDialog();
                        }
                    });

                    builder.create().show();
                }
            });

        }   //end isfavorite==false


        else{
            btnRemove.setText("Remove Item");
            tvPostedAt.setText("Posted "+ TimeFormatter.getTimeDifference(PostToDelete.getCreatedAt().toString()) + " Ago");
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                    builder.setMessage("Remove Item From Favorites?");
                    builder.setNegativeButton("No",null);
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RemoveFromFavorites();
                            EndDialog();
                        }
                    });

                    builder.create().show();
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_dialog, container, false);
    }

    private void DeleteMyPost(){
        PostToDelete.deleteInBackground(new DeleteCallback() {      //posts stored on Parse
            @Override
            public void done(ParseException e) {
                deleteDialogInterface.UpdateFeed();
            }
        });


    }

    private void RemoveFromFavorites(){         //Object id's of posts on parse stored on Firebase
        MainActivity.FavoriteItemsId.remove(PostToDelete.getObjectId());
        Map<String, Object> Updates = new HashMap<>();
        Updates.put("FavoriteItemsId", MainActivity.FavoriteItemsId);

        DatabaseReference mdatabase= FirebaseDatabase.getInstance().getReference("User").child(MainActivity.CurrentUser.GetFirebaseKey());
        mdatabase.updateChildren(Updates);
        deleteDialogInterface.UpdateFeed();
    }

    private void EndDialog(){
        dismiss();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteDialog.DeleteDialogInterface){
            deleteDialogInterface=(DeleteDialog.DeleteDialogInterface)context;
        }
        else{
            throw new RuntimeException(context.toString()+" Interface error deletedialog");
        }
    }
}