package com.mobile.treasureapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.User;
import com.mobile.treasureapp.R;

import org.parceler.Parcels;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class UserNameActivity extends AppCompatActivity {           //screen after login screen to get user's name and profile picture

    private EditText etFirstName,etLastName;
    private Button btnSubmit;
    private ImageView ivFirstPicture;
    private Button btnGetProfilePic;
    private Boolean UserUploadedPic;
    private StorageReference storageReference;
    private String PictureID;
    private Uri PictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));
        getSupportActionBar().hide();
        UserUploadedPic=false;
        btnGetProfilePic=findViewById(R.id.btnFirstProfilePicture);
        etFirstName=findViewById(R.id.etFirstName);
        etLastName=findViewById(R.id.etLastName);
        btnSubmit=findViewById(R.id.btnCreateProfile);
        ivFirstPicture=findViewById(R.id.ivFirstPicture);
        Glide.with(this).load(R.drawable.randomprofilepic).circleCrop().into(ivFirstPicture);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();
        final String Email=getIntent().getStringExtra("UserEmail");
        final String uid=currentUser.getUid();

        btnGetProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessPhotos();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FirstName=etFirstName.getText().toString().trim();
                String LastName=etLastName.getText().toString().trim();

                if (FirstName.length()==0 || LastName.length()==0){
                    Toast.makeText(UserNameActivity.this, "Missing Information", Toast.LENGTH_SHORT).show();
                }

                if (FirstName.length()>0 && LastName.length()>0){       //fields can't be empty
                    User user=new User();
                    user.SetEmail(Email);
                    user.SetUserID(uid);
                    user.SetName(FirstName+" "+LastName);
                    if (UserUploadedPic==true){
                        SavePhotoToFirebase(user);
                    }
                    else{
                        user.SetProfilePictureID("DefaultProfilePic.png");      //the app icon will be the default image
                       GoNextActivity(user);
                    }

                }
            }
        });
    }

    public void AccessPhotos(){             //Launching photo gallery
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,321);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==321 && data!=null && data.getData()!=null){

            PictureID= UUID.randomUUID().toString();        //Photos are stored in firebase Storage and located through unique path id, created here
            PictureUri=data.getData();
            Glide.with(this).load(PictureUri).circleCrop().into(ivFirstPicture);
            UserUploadedPic=true;
        }

    }

    public void SavePhotoToFirebase(User user){         //save newly chosen image to Storage in firebase
        StorageReference reference=null;

        try {
            reference = storageReference.child("ProfilePictures/" +PictureID );
        }catch (Exception e){
            Log.d("UserNameActivity","Error saving profile picture to firebase");
        }

        reference.putFile(PictureUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        user.SetProfilePictureID(PictureID);
                        Log.i("UserNameActivity","Saved image to firebase");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {         //user will just get default picture if save fails
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        user.SetProfilePictureID("DefaultProfilePic.png");
                        Log.e("UserNameActivity","Failed to Save image to firebase");

                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {

                GoNextActivity(user);               //going to next activity to choose school
            }
        });

    }


    public void GoNextActivity(User user){
        Intent intent=new Intent(UserNameActivity.this,UserSchoolActivity.class);
        intent.putExtra("AppUser", Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please Finish Account Creation", Toast.LENGTH_SHORT).show();
    }

}