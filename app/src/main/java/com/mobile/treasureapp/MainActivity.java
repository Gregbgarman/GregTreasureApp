package com.mobile.treasureapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ResourceLoader;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.treasureapp.Activities.LoginActivity;
import com.mobile.treasureapp.Activities.SettingsActivity;
import com.mobile.treasureapp.Activities.UserSchoolActivity;
import com.mobile.treasureapp.Activities.ViewMessagesActivity;
import com.mobile.treasureapp.Dialogs.DeleteDialog;
import com.mobile.treasureapp.Dialogs.LongClickDialog;
import com.mobile.treasureapp.Fragments.FragmentProfile;
import com.mobile.treasureapp.Fragments.HomeFragment;
import com.mobile.treasureapp.Models.MostRecentMessage;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.Models.User;
import com.mobile.treasureapp.TabFragments.TabFragmentFavoriteItems;
import com.mobile.treasureapp.TabFragments.TabFragmentPosts;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.time.ZoneOffset.UTC;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.HomeFragmentInterface,HomeFragment.ShowOptionsInMain, TabFragmentPosts.TabFragmentPostsInterface,
        TabFragmentFavoriteItems.TabFragmentFavoriteItemsInterface,DeleteDialog.DeleteDialogInterface {

    //Gotchas
    // 1.School API needs to be reactivated so often
    //2. If red dot msg notification not appearing-need to manually set color of it via code ~line 185
    //3. Must click next to msg icon for it to load messages-not on it directly



    public static Bitmap ProfilePictureBitmap;
    public static Uri ProfilePictureUri;
    public static User CurrentUser;

    public static ArrayList<String> FavoriteItemsId;

    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;

    private DatabaseReference databaseReferencePosts;
    private DatabaseReference databaseReferenceUsers;

    public static List<Post> postList;
    public static boolean InsideMessagingActivity;
    public static boolean UpdatedPictureFlag;
    private boolean StartOnProfileFragment;

    public int MyNotificationCounter;         //setting to zero inside fxns,not onCreate()
    private CardView CVNotification;
    private TextView tvNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UpdatedPictureFlag=false;
        StartOnProfileFragment=false;
        setContentView(R.layout.loadingscreen);
        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));

        databaseReferencePosts=FirebaseDatabase.getInstance().getReference("Post");
        databaseReferenceUsers=FirebaseDatabase.getInstance().getReference("User");

        LoadCurrentUser();          //Going ahead and loading user now to have it for duration of using app
        postList=new ArrayList<>();
        InsideMessagingActivity=false;

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.treasuresmall);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        SetUpLiveMessageQuery();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckForUnreadMessages();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.icSpace:
                startActivity(new Intent(MainActivity.this, ViewMessagesActivity.class));
                break;

            case android.R.id.home:

                break;

            case R.id.icSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);

        MenuItem menuItem=menu.findItem(R.id.icMessages);
        View view=MenuItemCompat.getActionView(menuItem);
        tvNotification=view.findViewById(R.id.tvInsideCV);
        CVNotification=view.findViewById(R.id.cvCounter);

        tvNotification.setVisibility(View.INVISIBLE);
        CVNotification.setVisibility(View.INVISIBLE);
       // CVNotification.setBackgroundColor(Color.parseColor("#F44336"));   weird rendering situation-may need this line on some PC's


        return true;
    }

    public void LoadProfilePic(){

        StorageReference myreference=FirebaseStorage.getInstance().getReference("ProfilePictures/" +CurrentUser.GetProfilePictureID());

        try {
            File file = File.createTempFile("tempfile", "tempfile");
            myreference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        ProfilePictureBitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                        ProfilePictureUri=Uri.fromFile(file);

                    }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<FileDownloadTask.TaskSnapshot> task) {
                    QueryPosts();
                }
            });
        }catch (Exception e){
            Log.d("MainActivity","Error fetching file");
        }

    }

    public void LoadCurrentUser(){      //changed from single event listener

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");
        usersRef.orderByChild("UserID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        CurrentUser=snapshot.getChildren().iterator().next().getValue(User.class);
                        FavoriteItemsId=CurrentUser.GetFavoriteItemsList();
                        if (FavoriteItemsId==null){
                            FavoriteItemsId=new ArrayList<>();
                        }
                        LoadProfilePic();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

    }


    public void SetUpActionBottomBars(){
        bottomNavigationView=findViewById(R.id.TheBottomNav);
        fragmentManager=getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ItemHome:
                        fragment=new HomeFragment(postList);
                        break;
                    case R.id.ItemProfile:
                    default:
                        fragment=new FragmentProfile();
                        break;

                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();

                return true;
            }
        });
        if (StartOnProfileFragment==true){
            bottomNavigationView.setSelectedItemId(R.id.ItemProfile);
            StartOnProfileFragment=false;
        }
        else{
            bottomNavigationView.setSelectedItemId(R.id.ItemHome);
        }


        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.notificationbell);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f9f9f9")));
    }


    public void QueryPosts(){
        postList.clear();
        ParseQuery<Post> query=ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_SCHOOL);
        query.whereEqualTo(Post.KEY_SCHOOL, CurrentUser.GetSchoolAttending());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                for (int i=0;i<posts.size();i++){
                    Post post=posts.get(i);
                        postList.add(post);
                }

                //query liked items here from parse then once that is done run the below code
                //with the liked items known-can turn colors blue of the items on feed
                //

                setContentView(R.layout.activity_main);
                getSupportActionBar().show();
                SetUpActionBottomBars();
                //Loading screen goes away here


            }
        });


    }

    ////////////////////////////////////
    //    interface methods
    ////////////////////////////////

    @Override
    public void RefreshFeed() {

        QueryPosts();
    }

    @Override
    public void GetOptions(Bundle bundle) {     //had to do this in main,couldnt go from home fragment

        FragmentManager fm =MainActivity.this.getSupportFragmentManager();
        LongClickDialog longClickDialog=new LongClickDialog(bundle);
        longClickDialog.show(fm,"SHOW");
    }

    @Override
    public void ShowDeleteDialog(Post post, Boolean IsFavorite) {
        FragmentManager fm =MainActivity.this.getSupportFragmentManager();
        DeleteDialog deleteDialog=new DeleteDialog(post,IsFavorite);
        deleteDialog.show(fm,"delete");
    }


    @Override
    public void UpdateFeed() {
        StartOnProfileFragment=true;
        QueryPosts();
    }

    ///////////////////////////
    // END INTERFACE METHODS
    /////////////////////////

    public void CheckForUnreadMessages(){

        MyNotificationCounter=0;
        ParseQuery<MostRecentMessage> query=ParseQuery.getQuery(MostRecentMessage.class);
        query.whereEqualTo(MostRecentMessage.KEY_RECEIVERID,FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.findInBackground(new FindCallback<MostRecentMessage>() {
            @Override
            public void done(List<MostRecentMessage> MessagesToMe, ParseException e) {
                for (MostRecentMessage mostRecentMessage:MessagesToMe){
                    if (mostRecentMessage.getReceiverHasRead()==false){
                        MyNotificationCounter++;
                    }
                }

                if (MyNotificationCounter>0){
                    CVNotification.setVisibility(View.VISIBLE);
                    tvNotification.setVisibility(View.VISIBLE);
                    tvNotification.setText(String.valueOf(MyNotificationCounter));
                }
                else{
                    CVNotification.setVisibility(View.INVISIBLE);
                    tvNotification.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void SetUpLiveMessageQuery(){
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<MostRecentMessage> parseQuery = ParseQuery.getQuery(MostRecentMessage.class);
        parseQuery.whereEqualTo(MostRecentMessage.KEY_RECEIVERID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        SubscriptionHandling<MostRecentMessage> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<MostRecentMessage>() {
            @Override
            public void onEvent(ParseQuery<MostRecentMessage> Allmessages, MostRecentMessage themostrecentmsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {         //point of this is to update notifications as messages come in

                        if (MainActivity.InsideMessagingActivity==false) {

                            if (themostrecentmsg.getReceiverHasRead() == false) {
                                MyNotificationCounter++;
                            }

                            if (MyNotificationCounter > 0) {
                                CVNotification.setVisibility(View.VISIBLE);
                                tvNotification.setVisibility(View.VISIBLE);
                                tvNotification.setText(String.valueOf(MyNotificationCounter));
                            }

                        }

                    }
                });

            }

        });


    }
}