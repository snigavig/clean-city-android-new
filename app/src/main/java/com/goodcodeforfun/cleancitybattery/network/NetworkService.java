package com.goodcodeforfun.cleancitybattery.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.model.Location;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkService extends IntentService {
    public static final String ACTION_GET_LIST_LOCATIONS = "com.goodcodeforfun.cleancitybattery.network.action.GET_LIST_LOCATIONS";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.goodcodeforfun.cleancitybattery.network.action.FOO";
    private static final String ACTION_BAZ = "com.goodcodeforfun.cleancitybattery.network.action.BAZ";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.goodcodeforfun.cleancitybattery.network.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.goodcodeforfun.cleancitybattery.network.extra.PARAM2";

    public NetworkService() {
        super("NetworkService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionGetListLocations(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GET_LIST_LOCATIONS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_GET_LIST_LOCATIONS:
                    handleActionGetListLocations();
                    break;
                default:
                    //wat
            }
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    private void handleActionGetListLocations() {
        Call<List<Location>> locations = CleanCityApplication.getInstance().getNetworkService().listLocations();
        Response<List<Location>> locationsResponse = null;
        try {
            locationsResponse = locations.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != locationsResponse) {
            ActiveAndroid.beginTransaction();
            try {
                for (Location location : locationsResponse.body()) {
                    location.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
