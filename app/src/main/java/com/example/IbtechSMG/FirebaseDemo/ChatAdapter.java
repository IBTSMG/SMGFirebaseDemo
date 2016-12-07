package com.example.IbtechSMG.FirebaseDemo;

/**
 * Created by smg on 25/09/16.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<ChatMessage> chatMessageList;

    public ChatAdapter(Activity activity) {
        chatMessageList = new ArrayList<ChatMessage>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chat_bubble, null);

        TextView messageText = (TextView) vi.findViewById(R.id.messageText);
        messageText.setText(message.body);
        TextView currentTime = (TextView) vi.findViewById(R.id.currentTime);
        currentTime.setText(message.time);

        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi.findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        if (message.isMine) {
            layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.RIGHT);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.LEFT);
        }
        messageText.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }

    public void addAll(ArrayList<ChatMessage> chatMessages) {
        chatMessageList.clear();
        chatMessageList.addAll(chatMessages);
    }
}
