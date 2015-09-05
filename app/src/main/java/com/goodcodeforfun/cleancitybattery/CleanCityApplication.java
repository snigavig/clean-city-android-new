package com.goodcodeforfun.cleancitybattery;

import android.app.Application;
import android.content.Context;

import com.goodcodeforfun.cleancitybattery.network.CleanCityService;
import com.goodcodeforfun.cleancitybattery.util.SharedPreferencesHelper;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by snigavig on 05.09.15.
 */
public class CleanCityApplication extends Application {
    public static final String LOG_TAG = "frontier-app";
    private static Context mContext;
    private static SharedPreferencesHelper mSharedPreferencesHelper;
    private static CleanCityApplication mInstance;
    private CleanCityService networkService;

    public static CleanCityApplication getInstance() {
        return mInstance;
    }

    public CleanCityService getNetworkService() {
        return networkService;
    }

    public SharedPreferencesHelper getSharedPreferencesHelper() {
        return mSharedPreferencesHelper;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);
        mInstance = this;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://146.185.190.210:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        networkService = retrofit.create(CleanCityService.class);
    }
}