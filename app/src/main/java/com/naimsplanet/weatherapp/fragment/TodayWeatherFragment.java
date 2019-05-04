package com.naimsplanet.weatherapp.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.naimsplanet.weatherapp.R;
import com.naimsplanet.weatherapp.common.Common;
import com.naimsplanet.weatherapp.model.WeatherResult;
import com.naimsplanet.weatherapp.retrofit.IOpenWeatherMap;
import com.naimsplanet.weatherapp.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    static TodayWeatherFragment instance;
    private ImageView weather_image;
    private TextView city_text, humidity_text, description_text, wind_text, sunrise_text, sunset_text, pressure_text, temperature_text, date_text, geocord_text, min_temp_text, max_temp_text;
    private LinearLayout linearLayout;
    private ProgressBar loading;
    private CompositeDisposable compositeDisposable;
    private IOpenWeatherMap mServices;


    public static TodayWeatherFragment getInstance() {
        if (instance == null)
            instance = new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServices = retrofit.create(IOpenWeatherMap.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemview = inflater.inflate(R.layout.fragment_today_weather, container, false);
        weather_image = itemview.findViewById(R.id.image_weather_icon);
        city_text = itemview.findViewById(R.id.text_city_name);
        humidity_text = itemview.findViewById(R.id.text_humidity);
        temperature_text = itemview.findViewById(R.id.text_temperature);
        wind_text = itemview.findViewById(R.id.text_wind);
        sunrise_text = itemview.findViewById(R.id.text_sunrise);
        sunset_text = itemview.findViewById(R.id.text_sunset);
        pressure_text = itemview.findViewById(R.id.text_pressure);
        date_text = itemview.findViewById(R.id.text_description1);
        geocord_text = itemview.findViewById(R.id.text_cord);
        linearLayout = itemview.findViewById(R.id.weather_panel);
        loading = itemview.findViewById(R.id.loading_progressbar);
        description_text = itemview.findViewById(R.id.text_description);


        getWeatherInformation();

        return itemview;
    }

    private void getWeatherInformation() {

        compositeDisposable.add(mServices.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        //loadimage
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(weather_image);

                        String description = weatherResult.getWeather().get(0).getDescription().toLowerCase().toString();

                        Log.d("Description->", description);

                        //setWeatherIcon(description);


                        //load information
                        city_text.setText(weatherResult.getName() + ", " + weatherResult.getSys().getCountry());
                        description_text.setText(new StringBuilder("Weather in ").append(weatherResult.getName()).append(", ").append(weatherResult.getSys().getCountry()).toString());
                        temperature_text.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        date_text.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        pressure_text.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        humidity_text.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append("%").toString());
                        sunrise_text.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        sunset_text.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        geocord_text.setText(new StringBuilder("[ ").append(weatherResult.getCoord().toString()).append("").toString());

                        //display panel
                        linearLayout.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private void setWeatherIcon(String description) {

        if (description.equals("clear sky")) {
            weather_image.setImageResource(R.drawable.clear_sky);
        } else if (description.equals("few clouds")) {
            weather_image.setImageResource(R.drawable.few_clouds);
        } else if (description.equals("scattered clouds")) {
            weather_image.setImageResource(R.drawable.scatteredcircleday);
        } else if (description.equals("broken clouds")) {
            weather_image.setImageResource(R.drawable.broken_clouds);
        } else if (description.equals("shower rain")) {
            weather_image.setImageResource(R.drawable.shower_rain);
        } else if (description.equals("rain")) {
            weather_image.setImageResource(R.drawable.rain);
        } else if (description.equals("thunderstorm")) {
            weather_image.setImageResource(R.drawable.thunderstorm);
        } else if (description.equals("snow")) {
            weather_image.setImageResource(R.drawable.snow);
        } else
            weather_image.setImageResource(R.drawable.scatteredcircleday);

    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
