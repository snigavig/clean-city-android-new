package com.goodcodeforfun.cleancitybattery.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.event.ApplicationLoadedEvent;
import com.goodcodeforfun.cleancitybattery.util.EventBusHelper;
import com.squareup.otto.Subscribe;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;

    public static void restart(Context context) {
        int delay = 1;
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CleanCityApplication app;
        app = CleanCityApplication.getInstance();
        if (null != app && app.isInitialised()) {
            startMainActivity();
        } else {
            restart(this);
        }
        super.onCreate(savedInstanceState);
        EventBusHelper.register(this);
        setContentView(R.layout.activity_splash);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }

    @Subscribe
    public void newApplicationLoadedUpdate(ApplicationLoadedEvent event) {
        switch ((ApplicationLoadedEvent.ApplicationLoadedType) event.getType()) {
            case STARTED:
                showProgress();
                break;
            case COMPLETED:
                hideProgress();
                switch (event.getResultCode()) {
                    case ApplicationLoadedEvent.OK_RESULT_CODE:
                        startMainActivity();
                        break;
                    case ApplicationLoadedEvent.FAIL_RESULT_CODE:
                        //TODO: think again
                        break;
                    default:
                        //no!
                        break;
                }
                break;
            default:
                //nthn
                break;

        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProgress() {
        if (null != mProgressBar)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (null != mProgressBar)
            mProgressBar.setVisibility(View.GONE);
    }
}
