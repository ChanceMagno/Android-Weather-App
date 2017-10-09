package com.magno.chance.weatherapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magno.chance.weatherapp.Constants;
import com.magno.chance.weatherapp.R;
import com.magno.chance.weatherapp.adapters.WeatherListAdapter;
import com.magno.chance.weatherapp.models.Forecast;
import com.magno.chance.weatherapp.service.weatherApiService;
import com.magno.chance.weatherapp.util.SimpleItemTouchHelperCallback;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;



import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private ArrayList<Forecast> mDayForecast;
    private ArrayList<String> cityCodes = new ArrayList<String>();
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mListener;
    private WeatherListAdapter mAdapter;


    @BindView(R.id.mainRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.locationFloatingActionButton) FloatingActionButton mLocationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_REF_WEATHER).child(Constants.DATABASE_REF_CITYCODES);
        mLocationButton.setOnClickListener(this);
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

        getSavedCityCodes();
    }

    public void getSavedCityCodes(){
        mDatabaseRef.addListenerForSingleValueEvent(mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cityCodes = new ArrayList<String>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        cityCodes.add(child.getValue(String.class));
                    }
                    getSavedForecasts(cityCodes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getForecastWithZip(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
                    getCoordinates();
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

    public void goToWeatherDetail(ArrayList<Forecast> mForecasts){
        Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
        intent.putExtra("forecast", Parcels.wrap(mDayForecast.get(0)));
        startActivity(intent);
    }


    public void getCoordinates(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
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

    public void getForecastWithZip(final String zipcode){
        weatherApiService.getForecastFromZip(zipcode, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    showToast(call.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                mDayForecast = weatherApiService.processResults(response);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(response.code() == 200){
                            // TODO: 10/8/17 fix city id's
//                            cityCodes.add(mDayForecast.get(0).getCityID());
//                            mDatabaseRef.setValue(cityCodes);
                            goToWeatherDetail(mDayForecast);
                        } else {
                            showToast("Unable to retrieve forecast for " + zipcode);
                        }
                    }
                });

            }

        });
    }

    public void getSavedForecasts(ArrayList<String> savedForcast){
        weatherApiService.getSavedForecasts(savedForcast, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    showToast("Failed to retrieve forcast(s)");

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mDayForecast = weatherApiService.processListResults(response); {
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(response.code() == 200) {
                                mAdapter = new WeatherListAdapter(getApplicationContext(), mDayForecast, cityCodes, mDatabaseRef);
                                mRecyclerView.setAdapter(mAdapter);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                mRecyclerView.setLayoutManager(layoutManager);
                                mRecyclerView.setHasFixedSize(true);
                                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
                                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                                touchHelper.attachToRecyclerView(mRecyclerView);

                            } else {
                                showToast("unable to retrieve forecast");
                            }
                        }
                    });

                }
            }
        });
    }

    public void getLocalForecast(String lat, String lon){
        weatherApiService.getForecastFromCoords(lat, lon, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mDayForecast = weatherApiService.processResults(response);

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(response.code() == 200){
                            // TODO: 10/8/17 fix city id saving
//                            cityCodes.add(mDayForecast.get(0).getCityID());
//                            mDatabaseRef.setValue(cityCodes);
                            goToWeatherDetail(mDayForecast);
                        } else {
                            showToast("Unable to retrieve forecast for your current location");
                        }
                    }
                });

            }
        });
    }


    @Override
    public void onClick(View view) {
        if(checkLocationPerm()){
            getCoordinates();
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
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        if(googleApiClient != null){
            googleApiClient.disconnect();
        }
        if(mListener != null){
            mDatabaseRef.removeEventListener(mListener);
        }
        super.onStop();
    }

}




