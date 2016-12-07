package com.example.t47777.firebasedeneme1;

/**
 * Created by smg on 25/09/16.
 */

public class ChatMessage {

    public String body, senderUserName, senderPhoneNumber;
    public boolean isMine;
    public String time;
    public String recipientUserName;
    public String recipientPhoneNumber;

    public ChatMessage(String senderUserName, String senderPhoneNumber, String body, boolean isMine, String recipientUserName, String recipientPhoneNumber) {
        this.body = body;
        this.isMine = isMine;
        this.senderUserName = senderUserName;
        this.senderPhoneNumber = senderPhoneNumber;
        this.time = Utils.getCurrentTime();
        this.recipientUserName = recipientUserName;
        this.recipientPhoneNumber = recipientPhoneNumber;
    }

    public ChatMessage(String senderUserName, String senderPhoneNumber, String body, boolean isMine, String time, String recipientUserName, String recipientPhoneNumber) {
        this.body = body;
        this.isMine = isMine;
        this.senderUserName = senderUserName;
        this.senderPhoneNumber = senderPhoneNumber;
        this.time = time;
        this.recipientUserName = recipientUserName;
        this.recipientPhoneNumber = recipientPhoneNumber;
    }
}
