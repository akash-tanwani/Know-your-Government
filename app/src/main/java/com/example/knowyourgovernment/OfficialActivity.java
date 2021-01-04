package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private TextView loc,title,name, url, email, phone,party,address;
    private TextView a,p,e,u;
    private Official official;
    private ImageButton facebook, youtube, twitter, photo, logo;
    private ConstraintLayout c;
    private String l="";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        c=findViewById(R.id.screen);
        a=findViewById(R.id.address);
        p=findViewById(R.id.phone);
        e=findViewById(R.id.email);
        u=findViewById(R.id.website);
        loc=findViewById(R.id.curLocation);
        title=findViewById(R.id.title);
        name=findViewById(R.id.name);
        party=findViewById(R.id.party);
        phone=findViewById(R.id.phoneNum);
        email=findViewById(R.id.actualemail);
        url=findViewById(R.id.url);
        address=findViewById(R.id.actAddress);
        photo=findViewById(R.id.userImage);
        logo=findViewById(R.id.urlImage);
        facebook=findViewById(R.id.facebook);
        twitter=findViewById(R.id.twitter);
        youtube=findViewById(R.id.youtube);

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
                String p=official.getParty();
                party.setText("( "+p+" )");
                setlogo(p);
                hide();
            }
        }
    }

    private void hide() {
        if (official.getAddress().equals("")) {
            address.setVisibility(View.GONE);
            a.setVisibility(View.GONE);
        }else{
            address.setText(official.getAddress());
            Linkify.addLinks(address, Linkify.ALL);
            address.setLinkTextColor(Color.parseColor("#FFFFFF"));
        }

        if (official.getPhone().equals("")) {
            phone.setVisibility(View.GONE);
            p.setVisibility(View.GONE);
        }else{
            phone.setText(official.getPhone());
            Linkify.addLinks(phone, Linkify.ALL);
            phone.setLinkTextColor(Color.parseColor("#FFFFFF"));
        }

        if (official.getEmail().equals("")) {
            email.setVisibility(View.GONE);
            e.setVisibility(View.GONE);
        }else{
            email.setText(official.getEmail());
            Linkify.addLinks(email, Linkify.ALL);
            email.setLinkTextColor(Color.parseColor("#FFFFFF"));
        }

        if (official.getUrl().equals("")) {
            url.setVisibility(View.GONE);
            u.setVisibility(View.GONE);
        }else{
            url.setText(official.getUrl());
            Linkify.addLinks(url, Linkify.ALL);
            url.setLinkTextColor(Color.parseColor("#FFFFFF"));
        }

        if (official.getFacebook().equals("")) {
            facebook.setVisibility(View.GONE);
        }
        if (official.getTwitter().equals("")) {
            twitter.setVisibility(View.GONE);
        }
        if (official.getYoutube().equals("")) {
            youtube.setVisibility(View.GONE);
        }
        String pURL=official.getPhotoURL();
        if (pURL!= "") {
            Picasso.get().load(pURL).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo); // Use this if you don't want a callback
        }
    }

    private void setlogo(String p) {
        if(p.contains("Democratic"))
        {
            c.setBackgroundColor(Color.parseColor("#0600FF"));
            logo.setImageResource(R.drawable.dem_logo);
        }
        else if(p.contains("Republic")){
            c.setBackgroundColor(Color.parseColor("#FF0000"));
            logo.setImageResource(R.drawable.rep_logo);
        }
        else{
            c.setBackgroundColor(Color.parseColor("#000000"));
            logo.setVisibility(View.GONE);
        }
    }

    public void callFacebookURL(View view) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void callTwitterURL(View view) {
        Intent intent = null;
        String name = official.getTwitter();
        try { // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e)
        { // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void callYouTubeURL(View view) {
        String name = official.getYoutube();
        Intent intent = null; try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
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

    public void downloadImage(View view) {
        if (official.getPhotoURL().trim().equals("") ){}
        else{
            Intent intent2 = new Intent(this, PhotoActivity.class);
            intent2.putExtra("official", official);
            intent2.putExtra("location", l);
            startActivity(intent2);
        }
    }
}