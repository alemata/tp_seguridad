package com.seginf.sdviruschecker;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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
        new SDAnalyzerTask().execute(this);
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
            Thread.sleep(2000);
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
