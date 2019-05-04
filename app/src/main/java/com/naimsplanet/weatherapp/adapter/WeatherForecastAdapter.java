package com.naimsplanet.weatherapp.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naimsplanet.weatherapp.R;
import com.naimsplanet.weatherapp.common.Common;
import com.naimsplanet.weatherapp.model.WeatherForecastResult;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_date_time, text_description, text_temperature;
        ImageView image_weather;

        public MyViewHolder(View itemView) {
            super(itemView);

            image_weather = itemView.findViewById(R.id.image_weather_icon);
            text_date_time = itemView.findViewById(R.id.text_date);
            text_description = itemView.findViewById(R.id.text_description);
            text_temperature = itemView.findViewById(R.id.text_temperature);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, viewGroup, false);
        return new MyViewHolder(itemview);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        //loadimage
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(i).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.image_weather);

        holder.text_date_time.setText(new StringBuilder(Common.convertUnixTooDate(weatherForecastResult.list.get(i).dt)));
        holder.text_temperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(i).main.getTemp())).append("°C"));
        holder.text_description.setText(new StringBuilder(weatherForecastResult.list.get(i).weather.get(0).getDescription()));

    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

}
