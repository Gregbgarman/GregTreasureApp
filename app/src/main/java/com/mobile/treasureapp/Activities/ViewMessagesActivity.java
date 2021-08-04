package com.mobile.treasureapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ResourceLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.treasureapp.Adapters.ViewMessagesAdapter;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.MostRecentMessage;
import com.mobile.treasureapp.Models.User;
import com.mobile.treasureapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewMessagesActivity extends AppCompatActivity {       //shows list of recent conversations user has had

    private RecyclerView recyclerView;
    private ViewMessagesAdapter viewMessagesAdapter;
    private List<MostRecentMessage> recentMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.VeryLightGray));
        setContentView(R.layout.activity_view_messages);
        getSupportActionBar().hide();
        recyclerView=findViewById(R.id.rvMessageFeed);

    }

    @Override
    protected void onResume() {
        super.onResume();
        recentMessageList=new ArrayList<>();
        viewMessagesAdapter=new ViewMessagesAdapter(this,recentMessageList);
        recyclerView.setAdapter(viewMessagesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QueryRecentMessages();
    }

    private void QueryRecentMessages(){         //The goal is to get the most recent message sent in a live chat between two people
                                                //and display it in a textview via the adapter.
        ParseQuery<MostRecentMessage> query=ParseQuery.getQuery(MostRecentMessage.class);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<MostRecentMessage>() {
            @Override
            public void done(List<MostRecentMessage> AllMostRecentMsgs, ParseException e) {

                for (MostRecentMessage mostRecentMessage:AllMostRecentMsgs) {       //need to watch out for multiple messages from a sender

                    if (mostRecentMessage.getReceiverId().equals(MainActivity.CurrentUser.GetUserID())) {
                                if (IsADuplicate(mostRecentMessage)==false){
                                    recentMessageList.add(mostRecentMessage);
                                }

                    }

                    else if (mostRecentMessage.getSenderid().equals(MainActivity.CurrentUser.GetUserID()))  {
                        if (IsADuplicate(mostRecentMessage)==false){
                            recentMessageList.add(mostRecentMessage);
                        }

                    }
                }
                viewMessagesAdapter.notifyDataSetChanged();
            }
        });


    }

    private boolean IsADuplicate(MostRecentMessage mostRecentMessage){      //4 cases

        for (MostRecentMessage mostRecentMessageinList:recentMessageList){

            if (mostRecentMessageinList.getSenderid().equals(MainActivity.CurrentUser.GetUserID()) && mostRecentMessage.getSenderid().equals(MainActivity.CurrentUser.GetUserID() )){

                if (mostRecentMessageinList.getReceiverId().equals(mostRecentMessage.getReceiverId())){
                    return true;
                }
            }

            if (mostRecentMessageinList.getReceiverId().equals(MainActivity.CurrentUser.GetUserID()) && mostRecentMessage.getReceiverId().equals(MainActivity.CurrentUser.GetUserID() )){

                if (mostRecentMessageinList.getSenderid().equals(mostRecentMessage.getSenderid())){
                    return true;
                }
            }

            if (mostRecentMessageinList.getSenderid().equals(MainActivity.CurrentUser.GetUserID()) && mostRecentMessage.getReceiverId().equals(MainActivity.CurrentUser.GetUserID())){

                if (mostRecentMessageinList.getReceiverId().equals(mostRecentMessage.getSenderid())){
                    return true;
                }

            }

            if (mostRecentMessageinList.getReceiverId().equals(MainActivity.CurrentUser.GetUserID()) && mostRecentMessage.getSenderid().equals(MainActivity.CurrentUser.GetUserID())){

                if (mostRecentMessageinList.getSenderid().equals(mostRecentMessage.getReceiverId())){
                    return true;
                }

            }

        }

        return false;
    }

}