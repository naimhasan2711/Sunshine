package com.naimsplanet.weatherapp.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.naimsplanet.weatherapp.R;
import com.naimsplanet.weatherapp.common.Common;
import com.naimsplanet.weatherapp.model.WeatherResult;
import com.naimsplanet.weatherapp.retrofit.IOpenWeatherMap;
import com.naimsplanet.weatherapp.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    static CityFragment instance;
    private List<String>cityList;
    private MaterialSearchBar searchBar;
    private ImageView weather_image;
    private TextView city_text, humidity_text, description_text, wind_text, sunrise_text, sunset_text, pressure_text, temperature_text, date_text, geocord_text, min_temp_text, max_temp_text;
    private LinearLayout linearLayout;
    private ProgressBar loading;
    private CompositeDisposable compositeDisposable;
    private IOpenWeatherMap mServices;

    public static CityFragment getInstance() {
        if (instance == null)
            instance = new CityFragment();
        return instance;
    }


    public CityFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServices = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemview = inflater.inflate(R.layout.fragment_city, container, false);
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
        searchBar = itemview.findViewById(R.id.search_bar);
        searchBar.setEnabled(false);

        new LoadCities().execute();
        return itemview;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {
            cityList = new ArrayList<>();
            try{
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);
                String readed;

                while ((readed = in.readLine())!=null)
                {
                    builder.append(readed);
                }

                cityList = new Gson().fromJson(builder.toString(),new TypeToken<List<String>>(){}.getType());



            } catch (IOException e) {
                e.printStackTrace();
            }

            return cityList;
        }

        @Override
        protected void onSuccess(final List<String> strings) {
            super.onSuccess(strings);
            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    List<String>suggest = new ArrayList<>();
                    for(String search : strings)
                    {
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        {
                            suggest.add(search);
                        }
                    }
                    searchBar.setLastSuggestions(suggest);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {
                    
                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());
                    searchBar.setLastSuggestions(cityList);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
            searchBar.setLastSuggestions(strings);
            loading.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mServices.getWeatherByCityName(cityName,
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
