package com.mobile.treasureapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.treasureapp.Activities.LoginActivity;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.R;
import com.mobile.treasureapp.TabFragments.TabAdapter;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.mobile.treasureapp.MainActivity.UpdatedPictureFlag;


public class FragmentProfile extends Fragment {

    public static final String TAG="Profile Fragment";
    private ImageView ivProfilePic,ivBackgroundPic;
    private TextView tvUserName,tvStudentAt;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabAdapter tabAdapter;


    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tabLayout=view.findViewById(R.id.Thetablayout);
        viewPager=view.findViewById(R.id.TheViewPager);
        tvUserName=view.findViewById(R.id.tvoppLoggedInAS);
        ivBackgroundPic=view.findViewById(R.id.ivoppProfileBackground);
        ivProfilePic=view.findViewById(R.id.ivoppUserProfilePic);
        tvStudentAt=view.findViewById(R.id.tvoppStudentAt);
        Glide.with(getContext()).load(MainActivity.CurrentUser.GetSchoolImageUrl()).into(ivBackgroundPic);

        tvUserName.setText(MainActivity.CurrentUser.GetName());
        tvStudentAt.setText("Student at " + MainActivity.CurrentUser.GetSchoolAttending());

        Glide.with(getActivity()).load(MainActivity.ProfilePictureBitmap).circleCrop().into(ivProfilePic);
        //Glide.with(getActivity()).load(MainActivity.BackGroundPhotoUri).into(ivBackgroundPic);
        SetUpViewPager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.UpdatedPictureFlag==true){
            Glide.with(getActivity()).load(MainActivity.ProfilePictureBitmap).circleCrop().into(ivProfilePic);
            UpdatedPictureFlag=false;
        }

    }

    public void SetUpViewPager(){
        FragmentManager fragmentManager=getChildFragmentManager();
        tabAdapter=new TabAdapter(fragmentManager,getLifecycle());
        viewPager.setAdapter(tabAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Favorite Items"));
        tabLayout.addTab(tabLayout.newTab().setText("My Posts"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));

            }
        });





    }

}