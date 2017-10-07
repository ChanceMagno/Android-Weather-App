package com.magno.chance.weatherapp.ui;

import android.content.SharedPreferences;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.magno.chance.weatherapp.Constants;
import com.magno.chance.weatherapp.R;
import com.magno.chance.weatherapp.models.Forecast;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import butterknife.BindBitmap;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDetail extends AppCompatActivity {
    private Forecast mForecast;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @BindView(R.id.cityNameTextView) TextView mCityNameTextView;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.currentTempTextView) TextView mCurrentTempTextView;
    @BindView(R.id.pressureTextView) TextView mPressureTextView;
    @BindView(R.id.humidityTextView5) TextView mHumidityTextView;
    @BindView(R.id.descriptionsTextView) TextView mDescriptionTextView;
    @BindView(R.id.lowTempTextView) TextView mLowTempTextView;
    @BindView(R.id.highTempTextView) TextView mHighTempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        ButterKnife.bind(this);

        mForecast = Parcels.unwrap(getIntent().getParcelableExtra("forecast"));

        setPageContent();

    }



    public void setPageContent(){

        mCurrentTempTextView.setText((mForecast.getCurrentTemp() + (char) 0x00B0));
        mDescriptionTextView.setText(mForecast.getDescription());
        mCityNameTextView.setText(mForecast.getCityName());
        mHumidityTextView.setText(mForecast.getHumidity());
        mPressureTextView.setText(mForecast.getPressure());
        mHighTempTextView.setText(mForecast.getMaxTemp() + (char) 0x00B0);
        mLowTempTextView.setText(mForecast.getMinTemp() + (char) 0x00B0);
        Picasso.with(this).load(getIcon(mForecast.getIcon())).into(mIconImageView);
    }



    public String getIcon(String icon){
        if(icon.contains("01")){
            return Constants.WEATHER_PNG_CLEAR_SKY;
        } else if(icon.contains("02")){
            return Constants.WEATHER_PNG_FEW_CLOUDS;
        } else if (icon.contains("03")){
            return Constants.WEATHER_PNG_SCATTERED_CLOUDS;
        } else if (icon.contains("04")){
            return Constants.WEATHER_PNG_BROKEN_CLOUDS;
        } else if (icon.contains("09")){
            return Constants.WEATHER_PNG_SHOWERS_RAIN;
        } else if (icon.contains("10")) {
            return Constants.WEATHER_PNG_RAIN;
        } else if (icon.contains("11")){
            return Constants.WEATHER_PNG_THUNDER;
        } else if (icon.contains("13")){
            return Constants.WEATHER_PNG_SNOW;
        } else if (icon.contains("50")){
            return Constants.WEATHER_PNG_MIST;
        }
        return Constants.WEATHER_UNKNOWN;
    }

}
