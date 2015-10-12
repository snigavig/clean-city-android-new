package com.goodcodeforfun.cleancitybattery.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by snigavig on 27.09.15.
 */
public class EventBusHelper {
    private static MainThreadEventBus mBus;

    public EventBusHelper() {
    }

    public static void register(Context context) {
        getBus().register(context);
    }

    public static void unregister(Context context) {
        getBus().unregister(context);
    }

    public static MainThreadEventBus getBus() {
        if (null == mBus)
            mBus = new MainThreadEventBus(ThreadEnforcer.MAIN);
        return mBus;
    }

    public static class MainThreadEventBus extends Bus {

        private final Handler mHandler = new Handler(Looper.getMainLooper());

        public MainThreadEventBus(ThreadEnforcer enforcer) {
            super(enforcer);
        }

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainThreadEventBus.super.post(event);
                    }
                });
            }
        }
    }
}
