package com.goodcodeforfun.cleancitybattery.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.util.DeviceStateHelper;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class NetworkService extends IntentService {
    public static final String ACTION_GET_LIST_LOCATIONS =
            "com.goodcodeforfun.cleancitybattery.network.action.GET_LIST_LOCATIONS";
    public static final String ACTION_POST_LOCATION =
            "com.goodcodeforfun.cleancitybattery.network.action.POST_LOCATION";
    public static final String ACTION_BROADCAST =
            "com.goodcodeforfun.cleancitybattery.network.action.BROADCAST";
    public static final String EXTRA_LOCATION_DB_ID =
            "com.goodcodeforfun.cleancitybattery.network.extra.LOCATION_DB_ID";
    public static final String EXTRA_RESPONCE_MESSAGE =
            "com.goodcodeforfun.cleancitybattery.network.MESSAGE";

    public NetworkService() {
        super("NetworkService");
    }

    public static void startActionGetListLocations(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GET_LIST_LOCATIONS);
        context.startService(intent);
    }

    public static void startActionPostLocation(Context context, String locationDbId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GET_LIST_LOCATIONS);
        intent.putExtra(EXTRA_LOCATION_DB_ID, locationDbId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && DeviceStateHelper.isApiConnected()) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_GET_LIST_LOCATIONS:
                    handleActionGetListLocations();
                    break;
                case ACTION_POST_LOCATION:
                    final String locationDbId = intent.getStringExtra(EXTRA_LOCATION_DB_ID);
                    handleActionPostLocation(locationDbId);
                default:
                    //wat
            }
        }
    }

    private void handleActionGetListLocations() {
        Call<List<Location>> locationsCall = CleanCityApplication.getInstance().getNetworkService().listLocations();

        Response<List<Location>> locationsResponse = null;
        try {
            locationsResponse = locationsCall.execute();
        } catch (IOException e) {
            ErrorHandler.handleRetrofitError(e);
        }
        if (null != locationsResponse) {
            if (locationsResponse.isSuccess()) {
                ActiveAndroid.beginTransaction();
                try {
                    for (Location location : locationsResponse.body()) {
                        location.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                    getContentResolver().notifyChange(ContentProvider.createUri(Location.class, null), null);
                }
            } else {
                ErrorHandler.handleErrorResponce(locationsResponse.errorBody(), locationsResponse.message(), locationsResponse.code());
            }
        }
    }

    private void handleActionPostLocation(String locationDbId) {
        Location location = new Select()
                .distinct()
                .from(Location.class)
                .where("_id = ?", locationDbId)
                .executeSingle();

        if (null != location) {
            location.setUser(null);
            location.setApiId(null);

            Call<Location> locationCall = CleanCityApplication.getInstance().getNetworkService().addLocation(location);

            location.delete();
            try {
                Response<Location> locationsResponse = locationCall.execute();
                if (null != locationsResponse && null != locationsResponse.body()) {
                    if (locationsResponse.isSuccess()) {
                        locationsResponse.body().save();
                        Toast.makeText(CleanCityApplication.getInstance(), "Location successfully added", Toast.LENGTH_SHORT).show();
                    } else {
                        ErrorHandler.handleErrorResponce(locationsResponse.errorBody(), locationsResponse.message(), locationsResponse.code());
                        Toast.makeText(CleanCityApplication.getInstance(), "Location not added, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException e) {
                ErrorHandler.handleRetrofitError(e);
            }
        }
    }
}
