package com.naimsplanet.weatherapp.common;

import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.gms.location.LocationCallback;

import java.util.Date;

public class Common {

    public static final String APP_ID = "2c8162e213c638ce0de55df02cd8d26c";
    public static Location current_location = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDate(long dt) {

        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a EEEE dd/MM/yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToHour(long sunrise) {

        Date date = new Date(sunrise * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixTooDate(long dt) {

        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a EEE dd");
        String formatted = sdf.format(date);
        return formatted;
    }
}
