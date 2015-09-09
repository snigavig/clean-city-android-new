package com.goodcodeforfun.cleancitybattery.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.network.ErrorHandler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by snigavig on 09.09.15.
 */

public class DeviceStateHelper {

    private static final int RESPONSE_OK = 200;
    private static final int CONNECT_TIMEOUT = 1500;

    private static Boolean isNetworkInterfaceConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) CleanCityApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static Boolean isApiConnected() {
        if (isNetworkInterfaceConnected()) {
            try {
                return new checkApiConnection()
                        .execute(CleanCityApplication.FULL_URL)
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                Log.d(CleanCityApplication.LOG_TAG, e.getMessage());
            }
        } else {
            ErrorHandler.broadcastMessage("Error: No connection to the Internet");
            return false;
        }
        return false;
    }

    private static class checkApiConnection extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) (url.openConnection());
                httpURLConnection.setRequestProperty("User-Agent", "Test");
                httpURLConnection.setRequestProperty("Connection", "close");
                httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
                httpURLConnection.connect();
                return (httpURLConnection.getResponseCode() == RESPONSE_OK);
            } catch (Exception e) {
                Log.d(CleanCityApplication.LOG_TAG, e.getMessage());
                return Boolean.FALSE;
            }
        }

        protected void onPostExecute(Boolean isConnected) {
            if (!isConnected)
                ErrorHandler.broadcastMessage("Error: No connection to the API");
        }
    }

}