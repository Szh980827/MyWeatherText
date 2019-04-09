package com.example.songzhihao.myweathertext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String str = intent.getStringExtra("areaName");
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
