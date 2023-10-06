package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// this activity displays the Stopwatch for each Task
public class Stopwatch_Activity extends AppCompatActivity {
    private Chronometer chronometer;
    ImageButton btStart, btStop, button;
    private boolean isResume,isRunning,flg=false;
    Handler handler,handler2;
    long tUpdate = 0L;
    long sec=0, hrs=0, min=0, prev=0;
    int pos=0;
    String uId;
    private ProgressBar progressBar;
    int i = 0;
    String sTime;
    long k,j=0;
    boolean start_flag=false, five_flag=false, Mstart_flag=false;
    TextView display_title;
    String title;

    Intent intent=new Intent();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        display_title = findViewById(R.id.display_title);
        chronometer = findViewById(R.id.chronometer);
        btStart = findViewById(R.id.bt_start);
        btStop = findViewById(R.id.bt_stop);
        handler = new Handler();
        handler2 = new Handler();

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            prev = extra.getLong("time");
            title = extra.getString("title");
            pos = extra.getInt("pos");
            uId=extra.getString("uId");
            Log.d("ma3", "onCreate: " + prev );
            display_title.setText(title);
        }
        sTime="00:00:00";

        if(prev>0){
            flg=true;
            sec =  (prev / 1000);
            hrs = hrs+(sec / 3600);
            min = (min+(sec / 60))%60;
            sec = sec % 60;

            sTime=String.format("%02d", hrs) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
            chronometer.setText(sTime);
        }

        button =findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                five_flag=true;
                j++;
                min=min+5;
                hrs = hrs+min / 60;
                sec = sec % 60;
                min=min%60;
                sTime=String.format("%02d", hrs) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
                chronometer.setText(sTime);
            }
        });

        btStart.setOnClickListener(new View.OnClickListener() {
            boolean temp = true;
            @Override
            public void onClick(View v) {
                Mstart_flag=true;

                if (!isResume) {
                    isResume = true;
                    isRunning=true;
                    start_flag=true;
                    handler.postDelayed(runnable, 0);
                    btStop.setVisibility(View.GONE);
                    btStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                } else {
                    isRunning=false;
                    isResume = false;
                    start_flag=false;
                    handler.removeCallbacks(runnable);
                    btStop.setVisibility(View.VISIBLE);
                    btStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_func();
            }
        });
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long temp ,temp2;
            if(isRunning==true) tUpdate++;

            hrs=hrs;
            temp2=((tUpdate+min*60)/3600);
            min=min;
            temp=(tUpdate/60);

            if(flg){
                tUpdate+=sec;
                flg=false;
            }
            sec = tUpdate % 60;

            sTime = String.format("%02d", (hrs+temp2)%60) + ":" + String.format("%02d", (min+temp)%60) + ":" + String.format("%02d", sec);
            chronometer.setText(sTime);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onBackPressed() {
        if (start_flag)
            Toast.makeText(this, "Please Stop the Timer", Toast.LENGTH_SHORT).show();
        else {
            my_func();
            super.onBackPressed();
        }
    }

    public void my_func(){
        if(!isResume){
            if(Mstart_flag) {
                k = (tUpdate * 1000) + (j * 300000) + prev - prev % 60000;
                intent.putExtra("position", pos);
                intent.putExtra("uId", uId);
                intent.putExtra("time", k);
                intent.putExtra("sTime", sTime);
            }
            else if(five_flag){
                k = (tUpdate * 1000) + (j * 300000) + prev;
                intent.putExtra("position", pos);
                intent.putExtra("uId", uId);
                intent.putExtra("time", k);
                intent.putExtra("sTime", sTime);
            }
            else{
                intent.putExtra("position", pos);
                intent.putExtra("uId", uId);
                intent.putExtra("time", prev);
                intent.putExtra("sTime", sTime);
            }
            btStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

            tUpdate=0L;
            start_flag=false;
            sec=0;
            min=0;
            hrs=0;
            chronometer.setText("00:00:00");
            k=0;
            j=0;
            handler.removeCallbacks(runnable);

            setResult(RESULT_OK, intent);
            finish();
        }
    }
}