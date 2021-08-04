package com.mobile.treasureapp.Dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import com.mobile.treasureapp.TimeFormatter;

import org.jetbrains.annotations.NotNull;


public class SimpleExpandDialog extends DialogFragment {        //used when clicking on another user post while on their profile
                                                                //simply shows the item and when it was posted
   private Post ClickedPost;
   private TextView tvPostDate,tvItemname;
   private ImageView imageView;

    public SimpleExpandDialog() {
        // Required empty public constructor
    }

    public SimpleExpandDialog(Post ClickedPost) {
        this.ClickedPost=ClickedPost;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPostDate=view.findViewById(R.id.tvDialogPostDate);
        tvItemname=view.findViewById(R.id.tvExpandedNameItem);
        imageView=view.findViewById(R.id.ivExpandDialog);

        tvItemname.setText(ClickedPost.GetItemName());
        tvPostDate.setText("Posted "+TimeFormatter.getTimeDifference(ClickedPost.getCreatedAt().toString())+" ago");
        Glide.with(getContext()).load(ClickedPost.GetPostImage().getUrl()).into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_expand_dialog, container, false);
    }
}