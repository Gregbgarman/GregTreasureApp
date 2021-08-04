package com.mobile.treasureapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.treasureapp.Fragments.FragmentProfile;
import com.mobile.treasureapp.Fragments.HomeFragment;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Button btnLogut,btnChangeProfilePic;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri PictureUri;
    private ImageView ivProfilePic;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));
        btnLogut=findViewById(R.id.btnLogoutUser);
        btnChangeProfilePic=findViewById(R.id.btnChangeProfilePic);
        ivProfilePic=findViewById(R.id.ivSettingsprofilepic);
        tvName=findViewById(R.id.tvSettingsName);

        tvName.setText(MainActivity.CurrentUser.GetName());
        Glide.with(this).load(MainActivity.ProfilePictureBitmap).circleCrop().into(ivProfilePic);

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        btnLogut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });

        btnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessPhotos();
            }
        });


    }

    public void AccessPhotos(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==123 && data!=null && data.getData()!=null){
            PictureUri=data.getData();

            //convert uri to bitmap

            try {
                MainActivity.ProfilePictureBitmap = MediaStore.Images.Media.getBitmap(SettingsActivity.this.getContentResolver(),PictureUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Glide.with(this).load(PictureUri).circleCrop().into(ivProfilePic);
            SavePhoto();
        }

    }

    public void SavePhoto(){                //saving photo in firebase as new profile picture
        StorageReference reference=null;
                                            //profile pictures are located by their path in Storage in firebase
        try {
            reference = storageReference.child("ProfilePictures/" +MainActivity.CurrentUser.GetProfilePictureID());
        }catch (Exception e){
            Log.e(TAG,"Error saving profile picture to firebase");
        }

        reference.putFile(PictureUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SettingsActivity.this, "Saved Image", Toast.LENGTH_SHORT).show();
                        MainActivity.UpdatedPictureFlag=true;
                        LoadProfilePic();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Failed to Save New Image", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void LoadProfilePic(){       //not straightforward but this is needed so app doesnt crash when making post-
                                        //after saving photo to firebase, must instantly load it and have it because
                                        //there is a different format used and when saving to Parse, this format is recognized

        StorageReference myreference=FirebaseStorage.getInstance().getReference("ProfilePictures/" +MainActivity.CurrentUser.GetProfilePictureID());

        try {
            File file = File.createTempFile("tempfile", "tempfile");
            myreference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            MainActivity.ProfilePictureBitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                            MainActivity.ProfilePictureUri=Uri.fromFile(file);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"failed to reload prof pic after saving it");
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Log.d(TAG,"Success reloading new prof pic from firebase");
                }
            });
        }catch (Exception e){
            Log.e(TAG,"Error reloading newly saved image from firebase");
        }

    }

}