package com.seginf.sdviruschecker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static MainActivity ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;
    }

    public void starCheck(View view) {
        Intent alarmIntent = new Intent(this, SDProcessorReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);

        showCheckView();
    }

    private void showCheckView() {
        setContentView(R.layout.activity_main);
        TextView textV1 = (TextView) findViewById(R.id.checkingText);
        textV1.setText("Starting check...");
        View checkBtn = findViewById(R.id.checkBtn);
        if (checkBtn != null) {
            checkBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void updateTheTextView(final String t) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.checkingText);
                textV1.setText(t);
            }
        });
    }

    public static MainActivity getInstace() {
        return ins;
    }

    public void showFinished(){
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                setContentView(R.layout.activity_finish);

                final TextView textNextScan = (TextView) findViewById(R.id.nextScan);
                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        textNextScan.setText("Next scan in: " + millisUntilFinished / 1000 % 60);
                    }

                    public void onFinish() {
                        textNextScan.setText("done!");
                    }
                }.start();
            }
        });
    }

    public void showStartChecking() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                showCheckView();
            }
        });
    }
}
