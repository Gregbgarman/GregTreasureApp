package com.mobile.treasureapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.treasureapp.Adapters.ProfilePostsAdapter;
import com.mobile.treasureapp.Dialogs.SimpleExpandDialog;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.Models.User;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OtherPeopleProfileActivity extends AppCompatActivity {

    private static final String TAG = "OtherPeopleProfileActiv";
    private TextView tvPersonName,tvStudentAt,tvSendMessagemsg,tvPersonNameandPost;
    private ImageView ivprofilepic, ivbackgroundpic,ivMessageicon;
    private Post post;
    private User user;
    private CardView cvMessage;
    private Bundle bundle;
    private List<Post> UserPosts;
    private RecyclerView recyclerView;
    private ProfilePostsAdapter profilePostsAdapter;
    private ProfilePostsAdapter.ProfilePostsAdapterInterface profilePostsAdapterInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_people_profile);
        tvPersonNameandPost=findViewById(R.id.tvoppUsernameandPosts);
        tvPersonName=findViewById(R.id.tvoppLoggedInAS);
        tvStudentAt=findViewById(R.id.tvoppStudentAt);
        ivprofilepic=findViewById(R.id.ivoppUserProfilePic);
        ivbackgroundpic=findViewById(R.id.ivoppProfileBackground);
        tvSendMessagemsg=findViewById(R.id.tvoppsendmsg);
        ivMessageicon=findViewById(R.id.ivoppmessageicon);
        cvMessage=findViewById(R.id.oppCardviewmsg);
        recyclerView=findViewById(R.id.rvOtherpeoplepost);

        profilePostsAdapterInterface=new ProfilePostsAdapter.ProfilePostsAdapterInterface() {
            @Override
            public void ExpandImage(Post post) {
                FragmentManager fragmentManager=OtherPeopleProfileActivity.this.getSupportFragmentManager();
                SimpleExpandDialog simpleExpandDialog=new SimpleExpandDialog(post);
                simpleExpandDialog.show(fragmentManager," ");
            }
        };

        cvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToMessage();

            }
        });


        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));
        Glide.with(this).load(R.drawable.messageicon).circleCrop().into(ivMessageicon);

        bundle = getIntent().getBundleExtra("thebundle");
        post = bundle.getParcelable("f");

        tvPersonName.setText(post.GetPosterUserName());
        tvStudentAt.setText(post.GetSchoolAttending());

        String firstname="";
        for (int i=0;i<post.GetPosterUserName().length();i++){          //getting users' firstname
            if (post.GetPosterUserName().charAt(i)!=' '){
                firstname=firstname+post.GetPosterUserName().charAt(i);
            }
            else{
                break;
            }

        }

        tvSendMessagemsg.setText("Send " + firstname + " A Message");   //using the user's firstname for more personalized messages
        tvPersonNameandPost.setText(firstname+"'s Posts");
        SetUpAdapter();
        LoadUser();

    }

    private void LoadUser(){         //loading user's profile and profile picture from firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");
        usersRef.orderByChild("UserID").equalTo(post.GetPosterID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user=snapshot.getChildren().iterator().next().getValue(User.class);
                        LoadProfilePic();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,"Failed to load user");
                    }
                });

    }

    private void LoadProfilePic(){          //Loading the user's profile picture from Firebase Storage

        StorageReference myreference= FirebaseStorage.getInstance().getReference("ProfilePictures/" +user.GetProfilePictureID());

        try {
            File file = File.createTempFile("tempfile", "tempfile");
            myreference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
                            Glide.with(OtherPeopleProfileActivity.this).load(bitmap).circleCrop().into(ivprofilepic);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"Failed to load profile picture");
                }
            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Glide.with(OtherPeopleProfileActivity.this).load(user.GetSchoolImageUrl()+ getResources().getString(R.string.GoogleAPIKey)).into(ivbackgroundpic);

                }
            });
        }catch (Exception e){
            Log.e(TAG,"Error fetching file");
        }

    }

    private void GoToMessage(){         //launching direct messaging activity from another user's profile

        Intent intent = new Intent(this, SendMessagesActivity.class);
        intent.putExtra("thebundle",bundle);
        intent.putExtra("msg","FromPosts");
        startActivity(intent);
    }

    private void SetUpAdapter(){        //showing all posts that the user has made
        UserPosts=new ArrayList<>();
        profilePostsAdapter=new ProfilePostsAdapter(this,UserPosts,profilePostsAdapterInterface);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(profilePostsAdapter);

        for (Post apost: MainActivity.postList){

            if (apost.GetPosterID().equals(post.GetPosterID())){
                UserPosts.add(apost);
            }
        }

        profilePostsAdapter.notifyDataSetChanged();
    }
}