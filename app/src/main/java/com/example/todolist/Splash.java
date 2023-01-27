package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.animation.Animation;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 3000);

    }

}