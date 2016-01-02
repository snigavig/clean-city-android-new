
package com.goodcodeforfun.cleancitybattery;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.app.Application;
import com.goodcodeforfun.cleancitybattery.event.ApplicationLoadedEvent;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.model.Type;
import com.goodcodeforfun.cleancitybattery.network.CleanCityApiService;
import com.goodcodeforfun.cleancitybattery.network.NetworkService;
import com.goodcodeforfun.cleancitybattery.util.EventBusHelper;
import com.goodcodeforfun.cleancitybattery.util.SharedPreferencesHelper;
import com.goodcodeforfun.cleancitybattery.util.StringArraySerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Produce;
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
    private ApplicationLoadedEvent lastUpdate;

    public CleanCityApplication(Context context) {
        super();
        mContext = context;
    }

    public CleanCityApplication() {
        super();
        mContext = this;
    }

    public static CleanCityApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    public CleanCityApiService getNetworkService() {
        if (null == networkService) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
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
        init();
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    @Produce
    public ApplicationLoadedEvent produceApplicationLoadedUpdate() {
        if (null != lastUpdate)
            return new ApplicationLoadedEvent(this.lastUpdate);
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBusHelper.unregister(this);
    }

    public void init() {
        EventBusHelper.register(this);
        mInstance = this;
        Configuration.Builder configurationBuilder = new Configuration.Builder(getContext().getApplicationContext());
        configurationBuilder.addModelClass(Location.class);
        configurationBuilder.addModelClass(Type.class);
        configurationBuilder.addTypeSerializer(StringArraySerializer.class);
        ActiveAndroid.initialize(configurationBuilder.create());
        mSharedPreferencesHelper = new SharedPreferencesHelper(getContext().getApplicationContext());
        //TODO: Move to scheduler
        NetworkService.startActionGetListTypes(getContext());
        NetworkService.startActionGetListLocations(getContext());

        isInitialised = true;
        lastUpdate = new ApplicationLoadedEvent(
                ApplicationLoadedEvent.ApplicationLoadedType.COMPLETED,
                ApplicationLoadedEvent.OK_RESULT_CODE);
        EventBusHelper.getBus().post(lastUpdate);
    }
}