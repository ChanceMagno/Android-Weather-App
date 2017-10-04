package com.magno.chance.weatherapp.models;

public class Forecast {
    private String mCityName;
    private String mHumidity;
    private String mPressure;
    private String mIcon;
    private String mCurrentTemp;
    private String mMaxTemp;
    private String mMinTemp;
    private String mCloudPercentage;

    public Forecast(
            String mCityName,
            String mHumidity,
            String mPressure,
            String mIcon,
            String mCurrentTemp,
            String mMaxTemp,
            String mMinTemp) {

        this.mCityName = mCityName;
        this.mHumidity = mHumidity;
        this.mPressure = mPressure;
        this.mIcon = mIcon;
        this.mCurrentTemp = mCurrentTemp;
        this.mMaxTemp = mMaxTemp;
        this.mMinTemp = mMinTemp;
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

}

