package com.goodcodeforfun.cleancitybattery.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.util.SnackbarHelper;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

/**
 * Created by snigavig on 09.09.15.
 */
public class ErrorHandler {
    public ErrorHandler() {
    }

    public static void handleRetrofitError(IOException e) {
        if (null != e.getMessage())
            Log.d(CleanCityApplication.LOG_TAG, e.getMessage());
        broadcastMessage("Ой, вибачте, невеличкі проблеми");
    }

    public static void handleErrorResponse(ResponseBody errorBody, String message, int code) {
        Log.d(CleanCityApplication.LOG_TAG, String.valueOf(code));
        Log.d(CleanCityApplication.LOG_TAG, message);
        Log.d(CleanCityApplication.LOG_TAG, errorBody.toString());
        broadcastMessage(message);
    }

    public static void broadcastMessage(String message) {
        Intent localIntent =
                new Intent(NetworkService.ACTION_BROADCAST)
                        .putExtra(NetworkService.EXTRA_RESPONCE_MESSAGE, message);
        LocalBroadcastManager.getInstance(CleanCityApplication.getContext()).sendBroadcast(localIntent);
    }


    public static class ResponseReceiver extends BroadcastReceiver {
        private final View mParentView;

        // Prevents instantiation
        public ResponseReceiver(View view) {
            this.mParentView = view;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SnackbarHelper.show(mParentView, intent.getStringExtra(NetworkService.EXTRA_RESPONCE_MESSAGE));
        }
    }
}
