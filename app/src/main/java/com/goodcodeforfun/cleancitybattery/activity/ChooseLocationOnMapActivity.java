package com.goodcodeforfun.cleancitybattery.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by snigavig on 06.09.15.
 */
public class ChooseLocationOnMapActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    public static final String POSITION_LAT_KEY = "POSITION_LAT";
    public static final String POSITION_LON_KEY = "POSITION_LON";
    private static final int ZOOM = 18;
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private LatLng mCenterOfMap;
    private Button mSavePositionButton;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location_on_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSavePositionButton = (Button) findViewById(R.id.save_position_button);
        mSavePositionButton.setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        MapsInitializer.initialize(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_position_button:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(POSITION_LAT_KEY, mCenterOfMap.latitude);
                returnIntent.putExtra(POSITION_LON_KEY, mCenterOfMap.longitude);
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            default:
                //muah
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        Location lastLocation = SmartLocation.with(CleanCityApplication.getInstance()).location().getLastLocation();
        if (null != lastLocation) {
            moveMapCameraToPosition(lastLocation);
        } else {
            SmartLocation.with(CleanCityApplication.getInstance()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        moveMapCameraToPosition(location);
                    }
                });
        }
    }

    private void moveMapCameraToPosition(android.location.Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        if (null != mGoogleMap) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
        }
        if (null != mGoogleMap) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    mCenterOfMap = mGoogleMap.getCameraPosition().target;
                    mSavePositionButton.setEnabled(true);
                }
            });
            SmartLocation.with(this).location().stop();
        }
    }

    @Override
    public void onPause() {
        if (null != mapView)
            mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (null != mapView)
            mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if (null != mapView)
            mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onResume() {
        if (null != mapView)
            mapView.onResume();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mapView)
            mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
