package com.goodcodeforfun.cleancitybattery.activity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CleanCityApplication app = CleanCityApplication.getInstance();
        if (null != app && app.isInitialised())
            startMainActivity();
        super.onCreate(savedInstanceState);
        if (!EventBusHelper.isRegistered())
            EventBusHelper.register(this);
        setContentView(R.layout.activity_splash);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBusHelper.isRegistered())
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
