package com.example.IbtechSMG.FirebaseDemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by smg on 22/09/16.
 */

public class Utils {

    private static final String TAG = "Utils";
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static String getFirebaseInstanceToken(Context appContext) {
        SharedPreferences sharedPref =  appContext.getSharedPreferences("registrationToken", Context.MODE_PRIVATE);
        String sharedPreferencesToken = sharedPref.getString("token", null);
        return sharedPreferencesToken;
    }

    public static void sendMessage(Map<String, String> arguments) {
        if (arguments != null) {

            RemoteMessage.Builder remoteMessageBuilder = new RemoteMessage.Builder(Constants.SENDER_ID + "@gcm.googleapis.com");
            String messageID = UUID.randomUUID().toString();
            remoteMessageBuilder.setMessageId(messageID);
            remoteMessageBuilder.setData(arguments);

            RemoteMessage remoteMessage = remoteMessageBuilder.build();
            FirebaseMessaging fm = FirebaseMessaging.getInstance();
            fm.send(remoteMessage);
        }
    }

    public static void sendMessageWithMessageId(Map<String, String> arguments, String messageId) {
        if (arguments != null) {

            RemoteMessage.Builder remoteMessageBuilder = new RemoteMessage.Builder(Constants.SENDER_ID + "@gcm.googleapis.com");
            remoteMessageBuilder.setMessageId(messageId);
            remoteMessageBuilder.setData(arguments);

            RemoteMessage remoteMessage = remoteMessageBuilder.build();
            FirebaseMessaging fm = FirebaseMessaging.getInstance();
            fm.send(remoteMessage);
        }
    }

    public static void putKeyValueToSharedPreferences(Context appContext, String key, String value) {
        SharedPreferences sharedPref = appContext.getSharedPreferences("registrationToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValueFromSharedPreferences(Context appContext, String key) {
        SharedPreferences sharedPref =  appContext.getSharedPreferences("registrationToken", Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);
        return value;
    }

    public static String getCurrentTime() {
        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

    public static Bitmap loadImageFromStorage(String phoneNumber)
    {
        Bitmap b = null;
        try {
            File f=new File(Constants.IMAGE_FOLDER, phoneNumber + ".jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            return b;
        }
    }

    public static void putChatMessage(Context appContext, ChatMessage chatMessage) {
        FileOutputStream fOut = null;
        OutputStreamWriter myOutWriter = null;
        try {
            File conversationDirectory = new File(Constants.CONVERSATION_FOLDER);
            if (!conversationDirectory.exists()) {
                conversationDirectory.mkdirs();
            }

            String fileName = getValueFromSharedPreferences(appContext, "phoneNumber") + "_";
            if (chatMessage.isMine) {
                fileName += chatMessage.recipientPhoneNumber + ".txt";
            } else {
                fileName += chatMessage.senderPhoneNumber + ".txt";
            }
            File conversationFile = new File(Constants.CONVERSATION_FOLDER, fileName);
            fOut = new FileOutputStream(conversationFile, true);
            myOutWriter = new OutputStreamWriter(fOut);

            String data;
            if (chatMessage.isMine) {
                data = getValueFromSharedPreferences(appContext, "userName") + "::" + getValueFromSharedPreferences(appContext, "phoneNumber") + "::" + chatMessage.body + "::" + chatMessage.time + "::" + chatMessage.recipientUserName + "::" + chatMessage.recipientPhoneNumber;
            } else {
                data = chatMessage.senderUserName + "::" + chatMessage.senderPhoneNumber + "::" + chatMessage.body + "::" + chatMessage.time + "::" + chatMessage.recipientUserName + "::" + chatMessage.recipientPhoneNumber;
            }

            myOutWriter.append(data);
            myOutWriter.append(Constants.LINE_SEPARATOR);
            myOutWriter.close();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        } finally {
            if(fOut != null) {
                try {
                    fOut.flush();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                try {
                    fOut.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public static ArrayList<ChatMessage> getConversationHistory(Context appContext, String otherPhoneNumber) {
        ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
        BufferedReader br = null;
        try {
            String fileName = getValueFromSharedPreferences(appContext, "phoneNumber") + "_" + otherPhoneNumber + ".txt";
            File conversationFile = new File(Constants.CONVERSATION_FOLDER, fileName);

            if (conversationFile.exists()) {
                br = new BufferedReader(new FileReader(conversationFile));
                String line;

                while ((line = br.readLine()) != null) {
                    if (line != null) {
                        String[] chatMessageArray = line.split("::");
                        if (chatMessageArray != null && chatMessageArray.length == 6) {
                            boolean isMine = false;
                            if (getValueFromSharedPreferences(appContext, "userName").equals(chatMessageArray[0])) {
                                isMine = true;
                            }
                            ChatMessage chatMessage = new ChatMessage(chatMessageArray[0], chatMessageArray[1], chatMessageArray[2], isMine, chatMessageArray[3], chatMessageArray[4], chatMessageArray[5]);
                            chatMessages.add(chatMessage);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage(), e);
                }
            }
            return chatMessages;
        }
    }
}
