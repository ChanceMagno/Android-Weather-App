package com.magno.chance.weatherapp.service;


import android.util.Log;


import com.magno.chance.weatherapp.Constants;
import com.magno.chance.weatherapp.models.Forecast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class weatherApiService {

    public static void getForecastFromZip(String location, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.API_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.YOUR_QUERY_PARAMETER, location);
        urlBuilder.addQueryParameter(Constants.API_UNITS, Constants.API_UNITS_FORMAT);
        urlBuilder.addQueryParameter(Constants.API_KEY_QUERY_PARAMETER, Constants.API_KEY);
        String url = urlBuilder.build().toString();
        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void getForecastFromCoords(String latitude, String longitude, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.API_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.API_LAT_QUERY_PARAMETER, latitude);
        urlBuilder.addQueryParameter(Constants.API_LON_QUERY_PARAMETER, longitude);
        urlBuilder.addQueryParameter(Constants.API_UNITS, Constants.API_UNITS_FORMAT);
        urlBuilder.addQueryParameter(Constants.API_KEY_QUERY_PARAMETER, Constants.API_KEY);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<Forecast> processResults(Response response) {
        ArrayList<Forecast> dayForecasts = new ArrayList<>();
        try {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONObject dayObject = new JSONObject(jsonData);
                String cityName = dayObject.getString("name");
                String humidity = dayObject.getJSONObject("main").getString("humidity");
                String pressure = dayObject.getJSONObject("main").getString("pressure");
                String icon = dayObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String currentTemp = dayObject.getJSONObject("main").getString("temp");
                String minTemp = dayObject.getJSONObject("main").getString("temp_min");
                String maxTemp = dayObject.getJSONObject("main").getString("temp_max");

                Forecast dayForecast = new Forecast(cityName, humidity, pressure, icon, currentTemp, maxTemp, minTemp);
                dayForecasts.add(dayForecast);
            }
        } catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dayForecasts;
    }






}
