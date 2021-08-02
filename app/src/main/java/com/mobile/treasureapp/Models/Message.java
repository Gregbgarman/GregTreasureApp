package com.mobile.treasureapp.Models;

import java.util.Date;

public class Message {

    public String SenderID;
    public String ReceiverID;
    public String MessageContent;
    public Date CreatedAt;            //use to sort messages

    public Message(){

    }

    public String GetSenderID() {
        return SenderID;
    }

    public Date GetCreatedAt() {
        return CreatedAt;
    }

    public String GetMessageContent() {
        return MessageContent;
    }

    public String GetReceiverID() {
        return ReceiverID;
    }

    public void SetCreatedAt(Date createdAt) {
        CreatedAt = createdAt;
    }

    public void SetMessageContent(String messageContent) {
        MessageContent = messageContent;
    }

    public void SetReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public void SetSenderID(String senderID) {
        SenderID = senderID;
    }
}
