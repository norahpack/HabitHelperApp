package com.example.habithelper.applications;

import android.app.Application;
import com.example.habithelper.models.Habit;
import com.example.habithelper.models.TrackDay;
import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class HabitHelperApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "6451dc13-b7e8-4f0e-aeda-69bfe181db94";

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        ParseObject.registerSubclass(TrackDay.class);
        ParseObject.registerSubclass(Habit.class);

        // Use for monitoring Parse OkHttp traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("bRJ34uQroW7CiHFOIUpYvRQOGbwwMmFUala8fa1L")
                .clientKey("xoiEPVeO6Pz7KnJdQpGThmZRAPy9iGCJp38I435D")
                .server("https://parseapi.back4app.com").build());
    }
}
