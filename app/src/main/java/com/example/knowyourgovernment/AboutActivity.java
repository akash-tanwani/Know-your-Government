package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    public void buttonClicked(View v)
    {
        String url = "https://developers.google.com/civic-information/";
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}