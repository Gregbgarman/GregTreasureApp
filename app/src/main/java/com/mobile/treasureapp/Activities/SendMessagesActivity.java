package com.mobile.treasureapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.treasureapp.Adapters.ConversationAdapter;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Message;
import com.mobile.treasureapp.Models.MostRecentMessage;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SendMessagesActivity extends AppCompatActivity {       //direct messaging activity

    private static final String TAG = "SendMessagesActivity";
    private RecyclerView RVMessages;
    private EditText etMessageContent;
    private TextView tvOtherPersonName;
    private Button btnSendMessage;
    private String OtherPersonID;
    private DatabaseReference databaseMessages;
    private List<Message> MessageList;
    private ParseFile OtherPersonProfilePic;
    private boolean OnFirstLoad;
    private ConversationAdapter conversationAdapter;
    private MostRecentMessage mostRecentMessage;

    private Post OtherPersonPost;

    private int counter;
    private MostRecentMessage MsgFromIntent;        //has data needed to create other person
    private String OtherPersonUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);
        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.VeryLightGray));

        MainActivity.InsideMessagingActivity=true;      //using this as a flag to control live querying
        mostRecentMessage=null;
        OtherPersonPost=null;
        counter=0;

        btnSendMessage=findViewById(R.id.btnSendTheMessage);
        RVMessages=findViewById(R.id.RVSendMessages);
        etMessageContent=findViewById(R.id.etTheMessage);
        tvOtherPersonName=findViewById(R.id.tvOtherPersonName);

        //different points of entry into this activity, from the list of conversations or from a Post/Other person profile
        // The other person is created from the data that is obtained from those entry ways

        if (getIntent().getStringExtra("msg").equals("FromMessages")) {         //coming from list of conversations
            Bundle bundle = getIntent().getBundleExtra("mybundle");
            MsgFromIntent = bundle.getParcelable("recentmsg");

            if (MsgFromIntent.getSenderid().equals(MainActivity.CurrentUser.GetUserID())) {
                OtherPersonID = MsgFromIntent.getReceiverId();
                OtherPersonProfilePic = MsgFromIntent.getReceiverProfilePic();
                tvOtherPersonName.setText(MsgFromIntent.getReceiverUsername());
                OtherPersonUserName=MsgFromIntent.getReceiverUsername();
            } else {
                OtherPersonID = MsgFromIntent.getSenderid();
                OtherPersonProfilePic = MsgFromIntent.getSenderProfilePic();
                tvOtherPersonName.setText(MsgFromIntent.getSenderUserName());
                OtherPersonUserName=MsgFromIntent.getSenderUserName();
            }
        }


        else {          //entering activity from a Post or other user's profile activity-this same Post used to get info to create other person

            Bundle bundle = getIntent().getBundleExtra("thebundle");
            OtherPersonPost = bundle.getParcelable("f");
            OtherPersonID = OtherPersonPost.GetPosterID();
            OtherPersonProfilePic = OtherPersonPost.GetPosterProfilePic();
            tvOtherPersonName.setText(OtherPersonPost.GetPosterUserName());
            OtherPersonUserName=OtherPersonPost.GetPosterUserName();
        }

        databaseMessages=FirebaseDatabase.getInstance().getReference("Message");        //Getting reference to messages on firebase
        MessageList=new ArrayList<>();
        OnFirstLoad=true;

        btnSendMessage.setOnClickListener(new View.OnClickListener() {      //sending a message
            @Override
            public void onClick(View v) {       //create message object here and save on backend
                Message message=new Message();
                message.SetSenderID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                message.SetReceiverID(OtherPersonID);
                message.SetMessageContent(etMessageContent.getText().toString().trim());
                message.SetCreatedAt(Calendar.getInstance().getTime());
                databaseMessages.child(databaseMessages.push().getKey()).setValue(message);
                Collections.reverse(MessageList);
                MessageList.add(message);
                Collections.reverse(MessageList);
                etMessageContent.getText().clear();
                conversationAdapter.notifyDataSetChanged();
                SetMostRecentMessage(message);

            }
        });

        QueryMessagesSentToMe();

        // Method order:  QueryMessagesSentToMe() -> QueryMyMessages() -> GetMostRecentMessage() -> SetUpLiveQueryParse()

    }

    private void QueryMyMessages(){     //querying all messages I have sent, then sorting

       databaseMessages.orderByChild("SenderID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();
                        while (iter.hasNext()) {
                            Message message=iter.next().getValue(Message.class);
                            if (message.GetReceiverID().equals(OtherPersonID)){         //adding messages where receiver is the other person
                                MessageList.add(message);                               //all relevant messages are obtained at this point
                            }
                        }

                        MessageList.sort((o1,o2) -> o1.GetCreatedAt().compareTo(o2.GetCreatedAt()));           //now sorting by time
                        Collections.reverse(MessageList);

                        if (OnFirstLoad==true) {        //when first coming into this activity
                            SetUpRecyclerView();        //set up adapter and recyclerview
                            OnFirstLoad=false;
                            RVMessages.scrollToPosition(0);
                            GetMostRecentMessage();             //run this when come in to get most recent message-may or may not need to mark it as read
                            SetUpLiveQueryParse();              //then live query will call it from now on
                        }
                        else{
                            conversationAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,"error retrieving messages I have sent");
                    }
                });

    }

    private void QueryMessagesSentToMe(){
        databaseMessages.orderByChild("ReceiverID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MessageList.clear();
                        Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();
                        while (iter.hasNext()) {
                            Message message=iter.next().getValue(Message.class);
                            if (message.GetSenderID().equals(OtherPersonID)){       //adding messages sent to me where the sender is the other person
                                MessageList.add(message);
                            }
                        }

                        QueryMyMessages();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,"Error retrieving messages sent to me");
                    }
                });
    }

    private void SetUpRecyclerView(){
        conversationAdapter=new ConversationAdapter(SendMessagesActivity.this,MessageList, MainActivity.ProfilePictureUri,OtherPersonProfilePic);
        RVMessages.setAdapter(conversationAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        RVMessages.setLayoutManager(linearLayoutManager);
    }

    private void GetMostRecentMessage(){        //getting most recent messages
                                                //Stored on Parse-messages will be stored on Parse until they are read
        counter=0;
        ParseQuery<MostRecentMessage> query=ParseQuery.getQuery(MostRecentMessage.class);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<MostRecentMessage>() {
            @Override
            public void done(List<MostRecentMessage> TheMessages, ParseException e) {
                if (TheMessages != null) {

                    for (MostRecentMessage theMostRecentMessage:TheMessages){

                        if (theMostRecentMessage.getReceiverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && theMostRecentMessage.getSenderid().equals(OtherPersonID)){
                            if (counter==0) {
                                mostRecentMessage = theMostRecentMessage;             //if I was the receiver of the last message
                                if (mostRecentMessage.getReceiverHasRead() == false) {
                                    mostRecentMessage.setReceiverHasRead(true);
                                    mostRecentMessage.saveInBackground();           //updating message as read
                                }
                                counter++;
                            }
                            else if (counter>0){
                                theMostRecentMessage.deleteInBackground();      //if other person sent multiple messages,deleting those extra ones
                            }                                                   //this will give us the true most recent message
                        }                                                       //The receiver deletes all the sender's messages except for one-the most recent msg

                        else if (theMostRecentMessage.getReceiverId().equals(OtherPersonID) && theMostRecentMessage.getSenderid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            mostRecentMessage=theMostRecentMessage;     //if I was last person to send msg
                        }
                    }
                }
            }
        });

    }

    //need to delete themostrecentmessage if it isnt null, then create a new message and post it

    private void SetMostRecentMessage(Message message){     //once you have sent message to other person in chat

        if (mostRecentMessage!=null){

            //delete message and create new one if one already existed
            mostRecentMessage.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    CreateNewMostRecentMessage(message);
                }
            });
        }
                //when there was no mostrecent message yet-create one
        else{
            CreateNewMostRecentMessage(message);
        }

    }

    private void CreateNewMostRecentMessage(Message message){               //creating and saving most recent message on Parse
        MostRecentMessage NewmostRecentMessage=new MostRecentMessage();

        NewmostRecentMessage.setReceiverHasRead(false);
        NewmostRecentMessage.setMessage(message.GetMessageContent());
        NewmostRecentMessage.setReceiverID(OtherPersonID);
        NewmostRecentMessage.setReceiverProfilePic(OtherPersonProfilePic);
        NewmostRecentMessage.setSenderid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        NewmostRecentMessage.setReceiverUsername(OtherPersonUserName);
        NewmostRecentMessage.setSenderUserName(MainActivity.CurrentUser.GetName());

        String filename=getFileName(MainActivity.ProfilePictureUri);
        try {
            InputStream inputstream = this.getContentResolver().openInputStream(MainActivity.ProfilePictureUri);
            byte buffer[] = new byte[inputstream.available()];
            inputstream.read(buffer);
            ParseFile NewImage = new ParseFile(filename, buffer);
            inputstream.close();
            NewmostRecentMessage.setSenderProfilePic(NewImage);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        NewmostRecentMessage.saveInBackground();

    }


    public void SetUpLiveQueryParse(){      //setting up so can mark messages as read right away-live chatting scenario

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<MostRecentMessage> parseQuery = ParseQuery.getQuery(MostRecentMessage.class);
        parseQuery.whereEqualTo(MostRecentMessage.KEY_RECEIVERID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        SubscriptionHandling<MostRecentMessage> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<MostRecentMessage>() {
            @Override
            public void onEvent(ParseQuery<MostRecentMessage> Allmessages, MostRecentMessage themostrecentmsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {         //point of this is to instantly mark message as read
                        if (MainActivity.InsideMessagingActivity==true) {       //Need to cut off the live querying inside main activity while in here
                            GetMostRecentMessage();                             //-If didn't do this, would be getting red notification counter in main when leave
                        }                                                       //this activity
                    }
                });
            }
        });
    }


    public String getFileName(Uri uri) {    //converting uri to string
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.InsideMessagingActivity=false;
        finish();
    }


}