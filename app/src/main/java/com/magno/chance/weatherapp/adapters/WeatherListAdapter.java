package com.magno.chance.weatherapp.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magno.chance.weatherapp.Constants;
import com.magno.chance.weatherapp.R;
import com.magno.chance.weatherapp.models.Forecast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder>{
    private ArrayList<Forecast> mForecasts = new ArrayList<>();
    private Context mContext;


    public WeatherListAdapter(Context context, ArrayList<Forecast> forecasts) {
        mContext = context;
        mForecasts = forecasts;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_list_item, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        holder.bindForecasts(mForecasts.get(position));
    }

    @Override
    public int getItemCount() {
        return mForecasts.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.currentTempTextView) TextView mCurrentTempTextView;
        @BindView(R.id.nameTextView) TextView mCityNameTextView;
        @BindView(R.id.iconImageView) ImageView mIconImageView;
        private Context mContext;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindForecasts(Forecast forecast) {
            String iconUrl = Constants.WEATHER_ICON_BASE_URL + forecast.getIcon() + ".png";
            String currentTemp = (forecast.getCurrentTemp() + (char) 0x00B0);
            mCityNameTextView.setText(forecast.getCityName());
            mCurrentTempTextView.setText(currentTemp);
            Picasso.with(mContext).load(iconUrl).into(mIconImageView);
        }
    }



}
