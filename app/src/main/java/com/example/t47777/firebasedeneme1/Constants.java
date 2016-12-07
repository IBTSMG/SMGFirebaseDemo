package com.example.t47777.firebasedeneme1;

import android.os.Environment;

/**
 * Created by smg on 22/09/16.
 */

public class Constants {
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    public static final String SENDER_ID = "YOUR FCM SENDER ID ";
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final String IMAGE_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/smgChatImages";
    public static final String CONVERSATION_FOLDER = Environment.getExternalStorageDirectory().toString() + "/smgChatConversations";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
