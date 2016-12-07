package com.example.t47777.firebasedeneme1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by smg on 22/09/16.
 */

public class RegisterActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    EditText phoneEditText;
    Button registerButton;
    EditText userEditText;
    ProgressBar progressBar;
    private String messageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Register"));

        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        userEditText = (EditText) findViewById(R.id.userEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        userEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        registerButton.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);

        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            phoneEditText.setText(phoneNumber, TextView.BufferType.NORMAL);
        }

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                userEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                registerButton.setEnabled(false);
                String userName = userEditText.getText().toString();
                String phoneNumber = phoneEditText.getText().toString();
                if(!userName.isEmpty() && !phoneNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Registering..", Toast.LENGTH_SHORT).show();

                    Map<String, String> arguments = new HashMap<>();
                    arguments.put("commandName", "registerToken");
                    arguments.put("userName", userName);
                    arguments.put("phoneNumber", phoneNumber);
                    messageId = UUID.randomUUID().toString();
                    Utils.sendMessageWithMessageId(arguments, messageId);
                } else {
                    Toast.makeText(getApplicationContext(), "User Name and Phone Number must be filled.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        String phoneNumber = tMgr.getLine1Number();
                        if(phoneNumber != null && !phoneNumber.isEmpty()) {
                            phoneEditText.setText(phoneNumber, TextView.BufferType.NORMAL);
                        }
                    } catch (Exception e){ }
                }
                return;
            }
        }
    }


    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String returnCode = intent.getStringExtra("returnCode");
            String returnMessage = intent.getStringExtra("returnMessage");
            String incomingMessageId = intent.getStringExtra("incomingMessageId");

            if(messageId.equals(incomingMessageId)){

                userEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                registerButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);

                if(returnCode.equals("1")) {
                    Utils.putKeyValueToSharedPreferences(getApplicationContext(), "userName", userEditText.getText().toString());
                    Utils.putKeyValueToSharedPreferences(getApplicationContext(), "phoneNumber", phoneEditText.getText().toString());
                    Intent usersIntent = new Intent(RegisterActivity.this, UsersActivity.class);
                    usersIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    usersIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(usersIntent);
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Registration Failed")
                            .setMessage(returnMessage)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }
    };
}
