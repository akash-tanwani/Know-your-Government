package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private ArrayList<Official> arrayList= new ArrayList<>();
    private OfficialAdaptor officialAdaptor;
    private RecyclerView recyclerView;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;
    private TextView loc;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loc= findViewById(R.id.curLocation);
        recyclerView=findViewById(R.id.recycler);
        officialAdaptor=new OfficialAdaptor(arrayList,this);
        recyclerView.setAdapter(officialAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // use network for location
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        String zip_code="";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            if(doNetCheck()) {
                zip_code=setLocation();
            }
            else{
                noNetworkDailog();
            }
            if (zip_code == "") {
                loc.setText("No Data For Location");
                arrayList.clear();
                officialAdaptor.notifyDataSetChanged();
            }
            else{
                OfficialDownloader officialDownloader=new OfficialDownloader(this,zip_code);
                if(doNetCheck()) {
                    new Thread(officialDownloader).start();
                    Toast.makeText(this, zip_code, Toast.LENGTH_LONG).show();
                    //call runnable
                }
                else{
                    noNetworkDailog();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {
        String zip_code="";
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                if(doNetCheck()) {
                    zip_code=setLocation();
                }
                else{
                    noNetworkDailog();
                }
                if (zip_code == "") {
                    loc.setText("No Data For Location");
                    arrayList.clear();
                    officialAdaptor.notifyDataSetChanged();
                }
                else{
                    OfficialDownloader officialDownloader=new OfficialDownloader(this,zip_code);
                    if(doNetCheck()) {
                        new Thread(officialDownloader).start();
                        Toast.makeText(this, zip_code, Toast.LENGTH_LONG).show();
                        //call runnable
                    }
                    else{
                        noNetworkDailog();
                    }
                }
                return;
            }
        }
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void noNetworkDailog() {
        loc.setText("No Data For Location");
        arrayList.clear();
        officialAdaptor.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_error);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    private String setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }

        Toast.makeText(this,String.format("%.4f, %.4f", currentLocation.getLatitude(), currentLocation.getLongitude()), Toast.LENGTH_SHORT).show();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String zip_code="";
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocation( currentLocation.getLatitude(), currentLocation.getLongitude(), 10);
            zip_code= addresses.get(0).getPostalCode();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return zip_code;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutItem:
                Intent intent1=new Intent(this,AboutActivity.class);
                startActivity(intent1);
                Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
                break;

            case R.id.SearchItem:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                if(doNetCheck()) {
                    executeDialog();
                }
                else{
                    noNetworkDailog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setIcon(R.drawable.ic_search);
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String toSearch=et.getText().toString().trim();
                if(toSearch.trim().equals("")){
                    executeDialog();
                    Toast.makeText(MainActivity.this,"Null String",Toast.LENGTH_SHORT).show();
                }
                else {
                    findData(toSearch);
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setMessage("Enter a City, State or a Zip code:");
        builder.setTitle("Search Location");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    void findData(String tosearch){
        if(doNetCheck()) {
            OfficialDownloader officialDownloader=new OfficialDownloader(this,tosearch);
            new Thread(officialDownloader).start();
        }
        else{
            noNetworkDailog();
        }
    }


    @Override
    public void onClick(View view) {
        int official_position = recyclerView.getChildAdapterPosition(view);
        Official official = arrayList.get(official_position);

        Intent intent2=new Intent(this,OfficialActivity.class);
        intent2.putExtra("official",official);
        intent2.putExtra("location",location);
        startActivity(intent2);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public void updateData(ArrayList<Official> t, String l) {
        arrayList.clear();
        arrayList.addAll(t);
        loc.setText(l);
        location=l;
        officialAdaptor.notifyDataSetChanged();

    }

    public void downloadFailed() {
        loc.setText("No Data For Location");
        arrayList.clear();
        officialAdaptor.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "location not found", Toast.LENGTH_SHORT).show();
    }
}