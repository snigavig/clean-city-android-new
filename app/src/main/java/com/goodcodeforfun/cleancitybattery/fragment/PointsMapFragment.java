package com.goodcodeforfun.cleancitybattery.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.activity.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class PointsMapFragment extends Fragment implements OnMapReadyCallback {
    private static final int ZOOM = 13;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private GoogleMap mGoogleMap;
    private MapView mapView;

    public void clearMap() {
        if (null != mGoogleMap)
            mGoogleMap.clear();
    }

    public void updateMap(ArrayList<LatLng> points, LatLngBounds bounds) {
        if (null != mGoogleMap) {
            mGoogleMap.clear();

            int px = getResources().getDimensionPixelSize(R.dimen.map_marker_size);
            Bitmap markerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(markerBitmap);
            Drawable shape = ContextCompat.getDrawable(CleanCityApplication.getInstance(), R.drawable.ic_place_24dp);
            if (shape != null) {
                shape.setBounds(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
                shape.draw(canvas);
            }

            for (LatLng point : points
                    ) {
                mGoogleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                );
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_map_view).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_list_view) {
            MainActivity activity = mainActivityWeakReference.get();
            activity.showList();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mainActivityWeakReference = new WeakReference<>((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.points_map_fragment, container, false);
        MapsInitializer.initialize(getActivity());
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
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
        mainActivityWeakReference.get().restartLocationsLoader();
    }

    private void moveMapCameraToPosition(android.location.Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        if (null != mGoogleMap) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
            SmartLocation.with(CleanCityApplication.getInstance()).location().stop();
        }
    }

    @Override
    public void onPause() {
        if (null != mapView)
            mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (null != mapView)
            mapView.onDestroy();
        super.onDestroyView();
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