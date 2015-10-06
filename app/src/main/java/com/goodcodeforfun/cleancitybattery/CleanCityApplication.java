
package com.goodcodeforfun.cleancitybattery;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.goodcodeforfun.cleancitybattery.event.ApplicationLoadedEvent;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.model.Type;
import com.goodcodeforfun.cleancitybattery.network.CleanCityApiService;
import com.goodcodeforfun.cleancitybattery.util.EventBusHelper;
import com.goodcodeforfun.cleancitybattery.util.SharedPreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

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
    private static Context mContext;
    private static Picasso mPicasso;
    private CleanCityApiService networkService;
    private boolean isInitialised;

    public static CleanCityApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    public CleanCityApiService getNetworkService() {
        if (null == networkService) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FULL_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            networkService = retrofit.create(CleanCityApiService.class);
        }
        return networkService;
    }

    public SharedPreferencesHelper getSharedPreferencesHelper() {
        return mSharedPreferencesHelper;
    }

    public synchronized Picasso getPicasso() {
        if (null == mPicasso) {
            mPicasso = new Picasso.Builder(mInstance)
                    .downloader(new OkHttpDownloader(new OkHttpClient()))   //new OkHttpClient() can be configured to have interceptors.
                    .loggingEnabled(true)
                    .build();
        }
        return mPicasso;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getInstance();
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClass(Location.class);
        configurationBuilder.addModelClass(Type.class);
        ActiveAndroid.initialize(configurationBuilder.create());
        isInitialised = true;
        EventBusHelper.getBus().post(
                new ApplicationLoadedEvent(
                        ApplicationLoadedEvent.ApplicationLoadedType.COMPLETED,
                        ApplicationLoadedEvent.NO_CODE));
    }

    public boolean isInitialised() {
        return isInitialised;
    }
}