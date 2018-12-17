package com.seanschlaefli.whatsfordinner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader implements Runnable {

    public static final String TAG = "ImageDownloader";

    private String URL;

    public ImageDownloader(String url) {
        URL = url;
    }

    @Override
    public void run() {
        Bitmap image = getBitmapFromURL(URL);
        if (image != null) {
            Log.d(TAG, "successfully downloaded image");
            // save image to file

        } else {

        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
