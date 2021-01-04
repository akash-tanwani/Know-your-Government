package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    private TextView loc,title,name;
    private Official official;
    private ImageButton photo, logo;
    private ConstraintLayout c;
    private String l="",party="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        c=findViewById(R.id.screenNew);
        loc=findViewById(R.id.curLocation);
        title=findViewById(R.id.title);
        name=findViewById(R.id.name);
        photo=findViewById(R.id.userImage);
        logo=findViewById(R.id.urlImage);


        Intent intent = getIntent();
        if(intent.hasExtra("location"))
        {
            l=intent.getStringExtra("location");
            if (l != null)
            {
                loc.setText(l);
            }
        }

        if (intent.hasExtra("official"))
        {
            official = (Official) intent.getSerializableExtra("official");
            if (official != null)
            {
                title.setText(official.getTitle());
                name.setText(official.getName());
                party=official.getParty();
                openPhoto();
                setlogo();
            }
        }
    }

    private void openPhoto() {
        String pURL=official.getPhotoURL();
        if (pURL!= "") {
            Picasso.get().load(pURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo); // Use this if you don't want a callback
        }
    }

    private void setlogo() {
        if(party.contains("Democratic"))
        {
            c.setBackgroundColor(Color.parseColor("#0600FF"));
            logo.setImageResource(R.drawable.dem_logo);
        }
        else if(party.contains("Republic")){
            c.setBackgroundColor(Color.parseColor("#FF0000"));
            logo.setImageResource(R.drawable.rep_logo);
        }
        else{
            c.setBackgroundColor(Color.parseColor("#000000"));
            logo.setVisibility(View.GONE);
        }
    }

    public void callURL(View view) {
        String url;
        if(official.getParty().contains("Democratic"))
        {
            url="https://democrats.org";
        }
        else
        {
            url="https://www.gop.com";
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}