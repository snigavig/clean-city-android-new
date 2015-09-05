package com.goodcodeforfun.cleancitybattery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;

public class PointsMapFragment extends Fragment implements OnMapReadyCallback {

    private WeakReference<MainActivity> mainActivityWeakReference;
    private GoogleMap mGoogleMap;
    private MapView mapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        MainActivity activity = mainActivityWeakReference.get();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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