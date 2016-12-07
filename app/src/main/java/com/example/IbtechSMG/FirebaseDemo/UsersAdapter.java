package com.example.IbtechSMG.FirebaseDemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smg on 26/09/16.
 */

public class UsersAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    ArrayList<User> userList;

    public UsersAdapter(Activity activity) {
        userList = new ArrayList<User>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = (User) userList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.user_list_single, null);

        TextView userNameEditText = (TextView) vi.findViewById(R.id.userName);
        TextView phoneNumberEditText = (TextView) vi.findViewById(R.id.phoneNumber);
        ImageView imageView = (ImageView) vi.findViewById(R.id.image);
        userNameEditText.setText(user.getUserName());
        phoneNumberEditText.setText(user.getPhoneNumber());

        String imageUrlFromSharedPreferences = Utils.getValueFromSharedPreferences(vi.getContext().getApplicationContext(), user.getPhoneNumber() + "_imageUrl");
        if(imageUrlFromSharedPreferences != null)
        {
            if(!imageUrlFromSharedPreferences.equals(user.getImage())) {
                DownloadImageTask downloadImageTask = new DownloadImageTask(imageView, user.getPhoneNumber());
                downloadImageTask.execute(user.getImage());
            } else {
                if (user.getImage() != null) {
                    Bitmap bitmap = Utils.loadImageFromStorage(user.getPhoneNumber());
                    imageView.setImageBitmap(bitmap);
                }
            }
        } else {
            if (user.getImage() != null) {
                DownloadImageTask downloadImageTask = new DownloadImageTask(imageView, user.getPhoneNumber());
                downloadImageTask.execute(user.getImage());
            }
        }
        return vi;
    }

    public void add(User object) {
        userList.add(object);
    }

    public void clear() {
        userList.clear();
    }

    public void addAll(List<User> otherUserList) {
        userList.addAll(otherUserList);
    }
}
