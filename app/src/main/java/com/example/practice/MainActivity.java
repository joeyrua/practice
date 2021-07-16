package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView TV = findViewById(R.id.title);
        TV.setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                startActivity(new Intent().setClass(MainActivity.this, login.class));
                MainActivity.this.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}