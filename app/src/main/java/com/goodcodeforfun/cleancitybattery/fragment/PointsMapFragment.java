package com.goodcodeforfun.cleancitybattery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
    public static final int ZOOM = 15;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private GoogleMap mGoogleMap;
    private MapView mapView;

    public void updateMap(ArrayList<LatLng> points, LatLngBounds bounds) {
        if (null != mGoogleMap) {
            mGoogleMap.clear();
            for (LatLng point : points
                    ) {
                mGoogleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
            }
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 1000, 1000, 0); //(bounds, padding);
//            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        SmartLocation.with(getActivity()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                        if (null != mGoogleMap) {
                            //mGoogleMap.addMarker(new MarkerOptions().position(position).title("Location marker"));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
                        }
                    }
                });
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