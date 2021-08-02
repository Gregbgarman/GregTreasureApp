package com.mobile.treasureapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("MostRecentMessage")
public class MostRecentMessage extends ParseObject {


public static final String KEY_SENDERID="SenderID",KEY_SENDERUSERNAME="SenderUserName",KEY_MESSAGE="MessageContent",
        KEY_RECEIVERID="ReceiverID",KEY_RECEIVERUSERNAME="ReceiverUsername",KEY_SENDERPROFILEPIC="SenderProfilePic",
        KEY_RECEIVERPROFILEPIC="ReceiverProfilePic",KEY_RECEIVERHASREAD="ReceiverHasRead";


        public void setSenderid(String senderid){
            put(KEY_SENDERID,senderid);
        }
        public String getSenderid(){
            return getString(KEY_SENDERID);
        }

        public void setSenderUserName(String username){
            put(KEY_SENDERUSERNAME,username);
        }

        public String getSenderUserName(){
            return getString(KEY_SENDERUSERNAME);
        }

        public void setMessage(String message){
            put(KEY_MESSAGE,message);
        }
        public String getMessage(){
            return getString(KEY_MESSAGE);
        }

        public void setReceiverID(String receiverID){
            put (KEY_RECEIVERID,receiverID);
        }

        public String getReceiverId(){
            return getString(KEY_RECEIVERID);
        }

        public void setReceiverUsername(String receiverUsername){
            put(KEY_RECEIVERUSERNAME,receiverUsername);
        }

        public String getReceiverUsername(){
            return getString(KEY_RECEIVERUSERNAME);
        }

        public void setReceiverProfilePic(ParseFile parseFile){
            put(KEY_RECEIVERPROFILEPIC,parseFile);
        }
        public ParseFile getReceiverProfilePic(){
            return getParseFile(KEY_RECEIVERPROFILEPIC);
        }

        public void setSenderProfilePic(ParseFile profilePic){
            put(KEY_SENDERPROFILEPIC,profilePic);
        }

        public ParseFile getSenderProfilePic(){
            return getParseFile(KEY_SENDERPROFILEPIC);
        }

        public void setReceiverHasRead(Boolean bool){
            put(KEY_RECEIVERHASREAD,bool);
        }

        public boolean getReceiverHasRead(){
            return getBoolean(KEY_RECEIVERHASREAD);
        }



}
