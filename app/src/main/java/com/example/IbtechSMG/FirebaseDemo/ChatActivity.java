package com.example.IbtechSMG.FirebaseDemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smg on 23/09/16.
 */

public class ChatActivity extends Activity{

    private SharedPreferences prefs;

    private EditText chatMessageEditText;
    private Button sendButton;

    private String otherUserName;
    private String otherPhoneNumber;

    public static boolean ChatActivityIsOpen = false;
    public static String ChatActivityActiveUserName = "";
    public static String ChatActivityActiveUserPhoneNumber = "";

    private ChatAdapter chatAdapter;
    private ListView chatMessagesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ChatActivityIsOpen = true;

        chatMessageEditText = (EditText)findViewById(R.id.chatMessageEditText);
        sendButton = (Button)findViewById(R.id.sendButton);
        chatMessagesListView = (ListView) findViewById(R.id.chatMessagesListView);

        // ----Set autoscroll of listview when a new message arrives----//
        chatMessagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatMessagesListView.setStackFromBottom(true);

        chatAdapter = new ChatAdapter(this);
        chatMessagesListView.setAdapter(chatAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        //Kullanici listesinden bir kullanici secip bu ekrani actigimizda kullanacagimiz veriler
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {

            if(bundle.containsKey("otherUserName")) {
                otherUserName = intent.getStringExtra("otherUserName");
                otherPhoneNumber = intent.getStringExtra("otherPhoneNumber");
                ChatActivityActiveUserName = otherUserName;
                ChatActivityActiveUserPhoneNumber = otherPhoneNumber;
            } else if(bundle.containsKey("senderName")) {
                otherUserName = intent.getStringExtra("senderName");
                otherPhoneNumber = intent.getStringExtra("senderPhone");
                String messageBody = intent.getStringExtra("messageBody");

                ChatActivityActiveUserName = otherUserName;
                ChatActivityActiveUserPhoneNumber = otherPhoneNumber;
                ChatMessage chatMessage = new ChatMessage(otherUserName, otherPhoneNumber, messageBody, false, Utils.getValueFromSharedPreferences(getApplicationContext(), "userName"), Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"));
                chatAdapter.add(chatMessage);

                Utils.putChatMessage(getApplicationContext(), chatMessage);

                chatAdapter.notifyDataSetChanged();
            }

            ArrayList<ChatMessage> conversationHistory = Utils.getConversationHistory(getApplicationContext(), otherPhoneNumber);
            if(conversationHistory.size() > 0) {
                chatAdapter.addAll(conversationHistory);
                chatAdapter.notifyDataSetChanged();
            }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageBody = chatMessageEditText.getText().toString();
                Map<String, String> arguments = new HashMap<>();
                arguments.put("commandName", "sendMessage");
                arguments.put("recipientPhone", otherPhoneNumber);
                arguments.put("senderPhone", Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"));
                arguments.put("messageBody", messageBody);

                Utils.sendMessage(arguments);

                ChatMessage chatMessage = new ChatMessage(Utils.getValueFromSharedPreferences(getApplicationContext(), "userName"),
                        Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"),
                        messageBody,
                        true, otherUserName, otherPhoneNumber);

                chatMessageEditText.setText("");
                chatAdapter.add(chatMessage);
                chatAdapter.notifyDataSetChanged();

                Utils.putChatMessage(getApplicationContext(), chatMessage);
            }
        });
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String messageBody = intent.getStringExtra("messageBody");

            ChatMessage chatMessage = new ChatMessage(otherUserName, otherPhoneNumber, messageBody, false, Utils.getValueFromSharedPreferences(getApplicationContext(), "userName"), Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"));
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();

            Utils.putChatMessage(getApplicationContext(), chatMessage);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ChatActivityIsOpen = false;
        ChatActivityActiveUserPhoneNumber = otherPhoneNumber;
        ChatActivityActiveUserName = otherUserName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatActivityIsOpen = true;
        ChatActivityActiveUserPhoneNumber = otherPhoneNumber;
        ChatActivityActiveUserName = otherUserName;
    }
}