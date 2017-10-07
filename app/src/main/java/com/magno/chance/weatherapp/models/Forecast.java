package com.magno.chance.weatherapp.models;

import org.parceler.Parcel;

@Parcel
public class Forecast {
    private String mCityName;
    private String mHumidity;
    private String mPressure;
    private String mIcon;
    private String mCurrentTemp;
    private String mMaxTemp;
    private String mMinTemp;
    private String mDescription;

    public Forecast() {};

    public Forecast(
            String mCityName,
            String mHumidity,
            String mPressure,
            String mIcon,
            String mCurrentTemp,
            String mMaxTemp,
            String mMinTemp,
            String mDescription) {

        this.mCityName = mCityName;
        this.mHumidity = mHumidity;
        this.mPressure = mPressure;
        this.mIcon = mIcon;
        this.mCurrentTemp = mCurrentTemp;
        this.mMaxTemp = mMaxTemp;
        this.mMinTemp = mMinTemp;
        this.mDescription = mDescription;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public String getPressure() {
        return mPressure;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getCurrentTemp() {
        return mCurrentTemp;
    }

    public String getMaxTemp() {
        return mMaxTemp;
    }

    public String getMinTemp() {
        return mMinTemp;
    }

    public String getDescription(){ return mDescription; }

}

