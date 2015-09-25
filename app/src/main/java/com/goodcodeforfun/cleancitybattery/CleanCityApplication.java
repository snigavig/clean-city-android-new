package com.goodcodeforfun.cleancitybattery;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.goodcodeforfun.cleancitybattery.network.CleanCityApiService;
import com.goodcodeforfun.cleancitybattery.network.NetworkService;
import com.goodcodeforfun.cleancitybattery.util.SharedPreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by snigavig on 05.09.15.
 */
public class CleanCityApplication extends Application {
    public static final String LOG_TAG = "cleancity-app";
    private static final String BASE_URL = "http://146.185.190.210";
    private static final int PORT = 3000;
    public static final String FULL_URL = BASE_URL + ":" + PORT;
    private static SharedPreferencesHelper mSharedPreferencesHelper;
    private static CleanCityApplication mInstance;
    private CleanCityApiService networkService;

    public static CleanCityApplication getInstance() {
        return mInstance;
    }

    public CleanCityApiService getNetworkService() {
        return networkService;
    }

    public SharedPreferencesHelper getSharedPreferencesHelper() {
        return mSharedPreferencesHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);
        mInstance = this;
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FULL_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        networkService = retrofit.create(CleanCityApiService.class);
        ActiveAndroid.initialize(this);
        //TODO: Move to scheduler
        NetworkService.startActionGetListTypes(CleanCityApplication.getInstance());
        NetworkService.startActionGetListLocations(CleanCityApplication.getInstance());
    }
}