package com.example.IbtechSMG.FirebaseDemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by smg on 30/09/16.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private String phoneNumber;
    private String imageUrl;

    public DownloadImageTask(ImageView bmImage, String phoneNumber) {
        this.imageView = bmImage;
        this.phoneNumber = phoneNumber;
    }

    protected Bitmap doInBackground(String... urls) {
        imageUrl = urls[0];
        Bitmap userImage = null;
        try {
            InputStream in = new java.net.URL(imageUrl).openStream();
            userImage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        } finally {
            return userImage;
        }
    }

    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        saveImageToExternalStorage(bitmap);
        Utils.putKeyValueToSharedPreferences(imageView.getContext().getApplicationContext(), phoneNumber + "_imageUrl", imageUrl);
    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        File myDir = new File(Constants.IMAGE_FOLDER);
        myDir.mkdirs();
        String fname = phoneNumber + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(imageView.getContext().getApplicationContext(), new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
