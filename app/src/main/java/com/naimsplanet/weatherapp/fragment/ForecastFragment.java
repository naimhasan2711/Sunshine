package com.naimsplanet.weatherapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naimsplanet.weatherapp.R;
import com.naimsplanet.weatherapp.adapter.WeatherForecastAdapter;
import com.naimsplanet.weatherapp.common.Common;
import com.naimsplanet.weatherapp.model.WeatherForecastResult;
import com.naimsplanet.weatherapp.retrofit.IOpenWeatherMap;
import com.naimsplanet.weatherapp.retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mServices;
    TextView text_city_name, text_geocord;
    RecyclerView forecast;
    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if(instance==null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServices = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemview = inflater.inflate(R.layout.fragment_forecast, container, false);

        text_city_name = itemview.findViewById(R.id.text_forecast_city_name);
        text_geocord = itemview.findViewById(R.id.text_forecast_geocord);
        forecast = itemview.findViewById(R.id.recycler_forecast);
        forecast.setHasFixedSize(true);
        forecast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        getForcastWeatherInformation();

        return itemview;
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

    private void getForcastWeatherInformation() {
        compositeDisposable.add(mServices.getForecastWeatherByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {

                        displayForecastWeather(weatherForecastResult);

                    }


                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("Error",""+throwable.getMessage());
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        text_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
        text_geocord.setText(new StringBuilder("[").append(String.valueOf(weatherForecastResult.city.coord)));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(),weatherForecastResult);
        forecast.setAdapter(adapter);

    }

}
