package com.magno.chance.weatherapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.magno.chance.weatherapp.R;
import com.magno.chance.weatherapp.models.Forecast;

import org.parceler.Parcels;

public class WeatherDetail extends AppCompatActivity {
    private Forecast mForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        mForecast = Parcels.unwrap(getIntent().getParcelableExtra("forecast"));

    }
}
