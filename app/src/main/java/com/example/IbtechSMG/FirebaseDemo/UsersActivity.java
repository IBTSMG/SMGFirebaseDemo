package com.example.IbtechSMG.FirebaseDemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smg on 22/09/16.
 */

public class UsersActivity extends Activity {

    private static final String TAG = "UsersActivity";

    ListView usersListView;
    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("UserList"));

        usersListView = (ListView) findViewById(R.id.usersListView);
        usersAdapter = new UsersAdapter(this);
        usersListView.setAdapter(usersAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent chat = new Intent(UsersActivity.this, ChatActivity.class);
                chat.putExtra("otherPhoneNumber", ((User)usersAdapter.getItem(position)).getPhoneNumber());
                chat.putExtra("otherUserName", ((User)usersAdapter.getItem(position)).getUserName());

                startActivity(chat);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            String userListAsJson = intent.getStringExtra("userList");
            fillUserList(userListAsJson);
        }
        else {
            Map<String, String> arguments = new HashMap<>();
            arguments.put("commandName", "getUsers");
            Utils.sendMessage(arguments);
            Log.d(TAG, "UsersActivity.sendMessage");
        }
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String userListAsJson = intent.getStringExtra("userList");
            fillUserList(userListAsJson);
        }
    };

    private List<User> getUserListFromJsonString(String userListString) {
        List<User> userList = new ArrayList<User>();
        try {
            JSONObject jsonRootObject = new JSONObject(userListString);
            JSONArray jsonArray = jsonRootObject.optJSONArray("users");

            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String userName = jsonObject.optString("userName").toString() == "null" ? null : jsonObject.optString("userName").toString();
                String phoneNumber = jsonObject.optString("phoneNumber").toString() == "null" ? null : jsonObject.optString("phoneNumber").toString();
                String image = jsonObject.optString("photoUrl").toString() == "null" ? null : jsonObject.optString("photoUrl").toString();

                User user = new User(userName, phoneNumber, image);

                if(!phoneNumber.equals(Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber"))) {
                    userList.add(user);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        } finally {
            return userList;
        }
    }

    private void fillUserList(String userListAsJson) {
        usersAdapter.clear();
        usersAdapter.notifyDataSetChanged();

        List<User> userList = getUserListFromJsonString(userListAsJson);

        usersAdapter.addAll(userList);
        usersAdapter.notifyDataSetChanged();
    }
}
