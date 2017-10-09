package com.magno.chance.weatherapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.magno.chance.weatherapp.Constants;
import com.magno.chance.weatherapp.R;
import com.magno.chance.weatherapp.models.Forecast;
import com.magno.chance.weatherapp.ui.MainActivity;
import com.magno.chance.weatherapp.ui.WeatherDetail;
import com.magno.chance.weatherapp.util.ItemTouchHelperAdapter;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder> implements ItemTouchHelperAdapter, View.OnClickListener{
    private ArrayList<Forecast> mForecasts = new ArrayList<>();
    private Context mContext;
    private ArrayList<String> mCityCodes;
    private DatabaseReference mDatabaseRef;


    public WeatherListAdapter(Context context, ArrayList<Forecast> forecasts, ArrayList<String> cityCodes, DatabaseReference reference) {
        mContext = context;
        mForecasts = forecasts;
        mCityCodes = cityCodes;
        mDatabaseRef = reference;

    }


    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_list_item, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(WeatherViewHolder holder, final int position) {
        holder.bindForecasts(mForecasts.get(position));
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WeatherDetail.class);
                intent.putExtra("forecast", Parcels.wrap(mForecasts.get(position)));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mForecasts.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mForecasts, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mForecasts, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mCityCodes.remove(position);
        mForecasts.remove(position);
        mDatabaseRef.setValue(mCityCodes);
        notifyItemRemoved(position);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "here", Toast.LENGTH_LONG).show();
    }


    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.currentTempTextView) TextView mCurrentTempTextView;
        @BindView(R.id.nameTextView) TextView mCityNameTextView;
        @BindView(R.id.iconImageView) ImageView mIconImageView;
        @BindView(R.id.cardView) CardView mCardView;
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
