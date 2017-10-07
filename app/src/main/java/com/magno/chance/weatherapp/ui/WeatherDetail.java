package com.magno.chance.weatherapp.ui;

import android.content.SharedPreferences;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.magno.chance.weatherapp.Constants;
import com.magno.chance.weatherapp.R;
import com.magno.chance.weatherapp.models.Forecast;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDetail extends AppCompatActivity {
    private Forecast mForecast;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @BindView(R.id.cityNameTextView) TextView mCityNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        ButterKnife.bind(this);



        mForecast = Parcels.unwrap(getIntent().getParcelableExtra("forecast"));

        setPageContent();

    }


    public void setPageContent(){
        mCityNameTextView.setText(mForecast.getCityName());
    }

}
