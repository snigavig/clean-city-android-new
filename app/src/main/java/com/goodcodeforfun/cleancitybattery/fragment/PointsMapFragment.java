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
import com.goodcodeforfun.cleancitybattery.view.ClickableMapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class PointsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static final String MAP_VIEW_SAVE_STATE = "mapViewSaveState";
    private static final int ZOOM = 13;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private final SlidingUpPanelLayout.PanelSlideListener slideListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            mainActivityWeakReference.get().getLocationDetailsFragment().getArrow().setRotation(180 * slideOffset);
        }


        @Override
        public void onPanelExpanded(View panel) {
        }


        @Override
        public void onPanelCollapsed(View panel) {
        }


        @Override
        public void onPanelAnchored(View panel) {
        }


        @Override
        public void onPanelHidden(View panel) {
        }
    };
    private GoogleMap mGoogleMap;
    private ClickableMapView mapView;
    private SlidingUpPanelLayout mLayout;

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public SlidingUpPanelLayout getLayout() {
        return mLayout;
    }

    public void clearMap() {
        if (null != mGoogleMap)
            mGoogleMap.clear();
    }

    public void updateMap(ArrayList<LatLng> points, LatLngBounds bounds, HashMap<String, LatLng> map) {
        if (null != mGoogleMap) {
            mGoogleMap.clear();

            int px = getResources().getDimensionPixelSize(R.dimen.map_marker_size);
            Bitmap markerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(markerBitmap);
            Drawable shape = ContextCompat.getDrawable(CleanCityApplication.getContext(), R.drawable.ic_place_24dp);
            if (shape != null) {
                shape.setBounds(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
                shape.draw(canvas);
            }

            for (Map.Entry<String, LatLng> entry : map.entrySet()
                    ) {
                mGoogleMap.addMarker(new MarkerOptions()
                                .position(entry.getValue())
                                .snippet(entry.getKey())
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
        mapView = (ClickableMapView) v.findViewById(R.id.map);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle(MAP_VIEW_SAVE_STATE) : null;
        mapView.onCreate(mapViewSavedInstanceState);
        mLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(slideListener);
        mapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        mGoogleMap = googleMap;

        Location lastLocation = SmartLocation.with(mainActivityWeakReference.get()).location().getLastLocation();
        if (null != lastLocation) {
            moveMapCameraToPosition(lastLocation);
        } else {
            SmartLocation.with(CleanCityApplication.getContext()).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(android.location.Location location) {
                            moveMapCameraToPosition(location);
                        }
                    });
        }
        //mainActivityWeakReference.get().restartLocationsLoader();
    }

    private void moveMapCameraToPosition(android.location.Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        if (null != mGoogleMap) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
            SmartLocation.with(mainActivityWeakReference.get()).location().stop();
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
        if (null != mapView) {
            mapView.onSaveInstanceState(outState);
            final Bundle mapViewSaveState = new Bundle(outState);
            mapView.onSaveInstanceState(mapViewSaveState);
            outState.putBundle(MAP_VIEW_SAVE_STATE, mapViewSaveState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getGoogleMap().getUiSettings().setMapToolbarEnabled(true);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mainActivityWeakReference.get().getFloatingActionButton().hide();
        mainActivityWeakReference.get().getLocationDetailsFragment().setCurrentLocation(marker.getSnippet());
        //not sure about legitimacy of this, but works
        return false;
    }
}