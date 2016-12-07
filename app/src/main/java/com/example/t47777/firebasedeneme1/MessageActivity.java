package com.example.t47777.firebasedeneme1;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smg on 03/10/16.
 */

public class MessageActivity extends Activity {

    private static final String TAG = "MessageActivity";
    private String otherUserName = "";
    private String otherPhoneNumber = "";
    private EditText messageEditText;
    private Button sendMessageButton;
    private TextView messageLabel;
    private TextView senderNameLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
        if (remoteInput != null) {

            Intent intent = getIntent();
            String inputText = remoteInput.getCharSequence(Constants.KEY_TEXT_REPLY).toString();
            otherUserName = intent.getStringExtra("senderName");
            otherPhoneNumber = intent.getStringExtra("senderPhone");
            int notificationId = intent.getIntExtra("notificationId", 0);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(otherPhoneNumber, notificationId);

            Map<String, String> arguments = new HashMap<>();
            arguments.put("commandName", "sendMessage");
            arguments.put("recipientPhone", otherPhoneNumber);
            arguments.put("senderPhone", Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"));
            arguments.put("messageBody", inputText);

            Utils.sendMessage(arguments);
            finish();
            return;
        }

        senderNameLabel = (TextView) findViewById(R.id.senderNameLabel);
        messageLabel = (TextView) findViewById(R.id.messageLabel);
        messageEditText = (EditText) findViewById(R.id.messageText);
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            otherUserName = intent.getStringExtra("senderName");
            otherPhoneNumber = intent.getStringExtra("senderPhone");
            int notificationId = intent.getIntExtra("notificationId", 0);
            String senderMessage = intent.getStringExtra("senderMessage");

            senderNameLabel.setText(otherUserName + " says:");
            messageLabel.setText(senderMessage);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(otherPhoneNumber, notificationId);
        }


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageBody = messageEditText.getText().toString();
                Map<String, String> arguments = new HashMap<>();
                arguments.put("commandName", "sendMessage");
                arguments.put("recipientPhone", otherPhoneNumber);
                arguments.put("senderPhone", Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"));
                arguments.put("messageBody", messageBody);

                Utils.sendMessage(arguments);

                messageEditText.setText("");
                finish();
            }
        });
    }
}
