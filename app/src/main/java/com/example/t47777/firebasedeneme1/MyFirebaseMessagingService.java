package com.example.t47777.firebasedeneme1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by smg on 01/08/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private static int chatNotificationId = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String from = remoteMessage.getFrom();
        Log.d(TAG, "From: " + from);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();

            String commandName = "";

            Iterator it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();

                String key = pair.getKey();
                String value = pair.getValue();

                if(key.equals("commandName")) {
                    commandName = value;
                    break;
                }
            }

            switch (commandName) {
                case "registerTokenResponse":
                    String returnCode = data.get("returnCode");
                    String returnMessage = data.get("returnMessage");
                    String incomingMessageId = data.get("incoming_message_id");

                    Intent registerTokenResponse = new Intent("Register");
                    registerTokenResponse.putExtra("returnCode", returnCode);
                    registerTokenResponse.putExtra("returnMessage", returnMessage);
                    registerTokenResponse.putExtra("incomingMessageId", incomingMessageId);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registerTokenResponse);

                    break;

                case "getMessage":
                    String senderPhone = data.get("senderPhone");
                    String senderUserName = data.get("senderName");
                    String messageBody = data.get("messageBody");
                    String title = data.get("title");
                    ChatMessage chatMessage = new ChatMessage(senderUserName, senderPhone, messageBody, false, Utils.getCurrentTime(), Utils.getValueFromSharedPreferences(getApplicationContext(), "userName"), Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"));
                    Utils.putChatMessage(getApplicationContext(), chatMessage);

                    if(ChatActivity.ChatActivityIsOpen && ChatActivity.ChatActivityActiveUserPhoneNumber.equals(senderPhone)) {
                        Intent messageReceived = new Intent("Msg");
                        messageReceived.putExtra("messageBody", messageBody);
                        messageReceived.putExtra("senderPhone", senderPhone);
                        messageReceived.putExtra("senderName", senderUserName);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageReceived);
                    }
                    else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        showNotificationForAndroidNVersion(senderPhone, senderUserName, messageBody, title);
                    } else {
                        showNotificationForAndroidMAndLowerVersions(senderPhone, senderUserName, messageBody, title);
                    }

                    break;

                case "getUsersResponse":
                    String userListAsString = data.get("userList");

                    Intent userListReceived = new Intent("UserList");
                    userListReceived.putExtra("userList", userListAsString);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(userListReceived);

                    break;

            }

            Log.d(TAG, "Message data payload: " + data);
        }
    }

    public void showNotificationForAndroidNVersion(String senderPhone, String senderUserName, String messageBody, String title) {
        Notification.Builder notificationBuilder = null;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = -1;
        ArrayList<String> messageBodiesList;
        boolean notified = false;


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.text, messageBody);
        Bitmap b = Utils.loadImageFromStorage(senderPhone);
        if(b == null) {
            remoteViews.setImageViewResource(R.id.image, R.drawable.userimage);
        } else {
            remoteViews.setImageViewBitmap(R.id.image, Utils.loadImageFromStorage(senderPhone));
        }

        StatusBarNotification[] statusBarNotifications = notificationManager.getActiveNotifications();

        if(statusBarNotifications != null) {
            for(StatusBarNotification statusBarNotification : statusBarNotifications) {
                if(senderPhone.equals(statusBarNotification.getTag())) {
                    notificationId = statusBarNotification.getId();

                    Notification currentNotification = statusBarNotification.getNotification();
                    messageBodiesList = currentNotification.extras.getStringArrayList("messageBodiesList");
                    notified = true;

                    messageBodiesList.add(messageBody);

                    Bundle extras = new Bundle();
                    extras.putStringArrayList("messageBodiesList", messageBodiesList);

                    notificationBuilder = new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.chaticon32x32)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setExtras(extras);

                    if(b == null) {
                        notificationBuilder.setLargeIcon(Icon.createWithResource(getApplicationContext(), R.drawable.userimage));
                    } else {
                        notificationBuilder.setLargeIcon(b);
                    }

                    Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle("Me");
                    messagingStyle.setConversationTitle(messageBodiesList.size() + " new messages with " + senderUserName);

                    for(String messageBodyElement: messageBodiesList) {
                        messagingStyle.addMessage(messageBodyElement, System.currentTimeMillis(), senderUserName + ": ");
                    }

                    notificationBuilder.setStyle(messagingStyle);

                    break;
                }
            }
        }

        if(!notified) {
            Bundle extras = new Bundle();
            messageBodiesList = new ArrayList<String>();
            messageBodiesList.add(messageBody);
            extras.putStringArrayList("messageBodiesList", messageBodiesList);

            notificationBuilder = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.chaticon32x32)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setCustomContentView(remoteViews)
                    .setStyle(new Notification.DecoratedCustomViewStyle())
                    .setExtras(extras);
        }

        Intent messageActivity = new Intent(this, MessageActivity.class);
        messageActivity.putExtra("senderName", senderUserName);
        messageActivity.putExtra("senderPhone", senderPhone);
        if(notificationId != -1) {
            messageActivity.putExtra("notificationId", notificationId);
        } else {
            notificationId = chatNotificationId;
            messageActivity.putExtra("notificationId", chatNotificationId);
            chatNotificationId++;
        }
        messageActivity.putExtra("senderMessage", messageBody);

        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 1, messageActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder(Constants.KEY_TEXT_REPLY)
                .setLabel("Reply")
                .build();

        Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(getApplicationContext(), R.drawable.reply), "Reply", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        notificationBuilder.addAction(action);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(pm.isInteractive()) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationBuilder.setVibrate(new long[0]);
        }

        Notification notification = notificationBuilder.build();
        notificationManager.notify(senderPhone, notificationId, notification);
    }

    public void showNotificationForAndroidMAndLowerVersions(String senderPhone, String senderUserName, String messageBody, String title) {
        Intent messageActivity = new Intent(this, MessageActivity.class);
        messageActivity.putExtra("senderName", senderUserName);
        messageActivity.putExtra("senderPhone", senderPhone);
        messageActivity.putExtra("notificationId", chatNotificationId);
        messageActivity.putExtra("senderMessage", messageBody);

        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 1, messageActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap b = Utils.loadImageFromStorage(senderPhone);

        android.support.v4.app.NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.chaticon32x32)
                .setLargeIcon(b)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(replyPendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .addAction(R.drawable.reply, "Reply", replyPendingIntent);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(pm.isInteractive()) {
            notificationCompatBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationCompatBuilder.setVibrate(new long[0]);
        }
        Notification notification = notificationCompatBuilder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(senderPhone, chatNotificationId, notification);
        chatNotificationId++;
    }
}