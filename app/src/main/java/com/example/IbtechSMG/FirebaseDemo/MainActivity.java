package com.example.IbtechSMG.FirebaseDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailableResult = api.isGooglePlayServicesAvailable(this);

        if (googlePlayServicesAvailableResult == ConnectionResult.SUCCESS) {
            onActivityResult(Constants.REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        } else if (api.isUserResolvableError(googlePlayServicesAvailableResult) &&
                api.showErrorDialogFragment(this, googlePlayServicesAvailableResult, Constants.REQUEST_GOOGLE_PLAY_SERVICES)) {
        } else {
            Toast.makeText(this, api.getErrorString(googlePlayServicesAvailableResult), Toast.LENGTH_LONG).show();
        }

        String firebaseInstanceToken = Utils.getFirebaseInstanceToken(getApplicationContext());
        String userName = Utils.getValueFromSharedPreferences(getApplicationContext(), "userName");
        String phoneNumber = Utils.getValueFromSharedPreferences(getApplicationContext(), "phoneNumber");

        if(userName == null || userName.isEmpty() || phoneNumber == null || phoneNumber.isEmpty()) {

            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED
                    ) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.INTERNET"}, 2);
            } else {
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        }
        else if(firebaseInstanceToken != null && !firebaseInstanceToken.isEmpty()) {

            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED
                    ) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.INTERNET"}, 1);
            } else {
                Intent i = new Intent(this, UsersActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if(grantResults.length > 0) {
                    boolean allRequestsGranted = true;
                    for(int grantResult : grantResults) {
                        if(grantResult != 1) {
                            allRequestsGranted = false;
                            break;
                        }
                    }
                    if(!allRequestsGranted) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Application Failed")
                                .setMessage("All requests must be approved.")
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        Intent i = new Intent(this, UsersActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    }
                }
            }
            case 2: {
                if(grantResults.length > 0) {
                    boolean allRequestsGranted = true;
                    for(int grantResult : grantResults) {
                        if(grantResult != 1) {
                            allRequestsGranted = false;
                            break;
                        }
                    }
                    if(!allRequestsGranted) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Application Failed")
                                .setMessage("All requests must be approved.")
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        Intent i = new Intent(this, RegisterActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    }
                }
            }
        }
    }
}
