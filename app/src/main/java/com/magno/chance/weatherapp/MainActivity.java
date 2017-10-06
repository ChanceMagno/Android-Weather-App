package com.magno.chance.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.magno.chance.weatherapp.models.Forecast;
import com.magno.chance.weatherapp.service.weatherApiService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private Button mButton;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    public ArrayList<Forecast> mDayForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

    }

    public boolean checkLocationPerm(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Please enable location permissions in settings to use this feature", Toast.LENGTH_LONG).show();
                return false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_COARSE_LOCATION);
                return false;
            }
        } else {
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getForecastWithCoords();
                } else {
                    showToast("please enable location services");
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(MainActivity.class.getSimpleName(), "Connected to Google Play Services!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("connection", "suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showToast("Failed to connect to google play services");
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public void getForecastWithCoords(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                getLocalForecast(String.valueOf(lat), String.valueOf(lng));
                            } else {
                                showToast("Unable to retrieve current location");
                            }
                        }
                    });
        }
    }

    public void getLocalForecast(String lat, String lon){
        weatherApiService.getForecastFromCoords(lat, lon, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mDayForecast = weatherApiService.processResults(response);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(mDayForecast.get(0).getMaxTemp());
                    }
                });

            }
        });
    }


    @Override
    public void onClick(View view) {
        if(checkLocationPerm()){
            getForecastWithCoords();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(googleApiClient != null){
            googleApiClient.disconnect();
        }
        super.onStop();
    }

}




