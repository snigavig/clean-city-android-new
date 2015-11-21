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
import android.widget.ImageView;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.activity.MainActivity;
import com.goodcodeforfun.cleancitybattery.model.MyClusterItem;
import com.goodcodeforfun.cleancitybattery.view.ClickableMapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class PointsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String MAP_VIEW_SAVE_STATE = "mapViewSaveState";
    private static final int ZOOM = 13;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private final SlidingUpPanelLayout.PanelSlideListener slideListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            if (slideOffset < 1 || slideOffset > 0) {
                ImageView arrow = mainActivityWeakReference.get().getLocationDetailsFragment().getArrow();
                if (null != arrow) {
                    arrow.setRotation(180 * slideOffset);
                }
            }
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
    private ClusterManager<MyClusterItem> mClusterManager;
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

    public void updateMap(HashMap<String, LatLng> map) {
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

                LatLng position = entry.getValue();
                double lat = position.latitude;
                double lng = position.longitude;
                MyClusterItem item = new MyClusterItem(lat, lng, entry.getKey(), BitmapDescriptorFactory.fromBitmap(markerBitmap));
                mClusterManager.addItem(item);
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
        mClusterManager = new ClusterManager<>(CleanCityApplication.getContext(), mGoogleMap);
        mClusterManager.setRenderer(new MarkerRenderer());
        mGoogleMap.setOnCameraChangeListener(mClusterManager);
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
        if (null != marker.getSnippet()) {
            getGoogleMap().getUiSettings().setMapToolbarEnabled(true);
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            //TODO: uncomment when FAB functionality is back
            //mainActivityWeakReference.get().getFloatingActionButton().hide();
            mainActivityWeakReference.get().setLocationDetailsFragment(new LocationDetailsFragment());
            mainActivityWeakReference.get().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentDetails, mainActivityWeakReference.get().getLocationDetailsFragment())
                    .commit();
            mainActivityWeakReference.get().getLocationDetailsFragment().setCurrentLocation(marker.getSnippet());
            //not sure about legitimacy of this, but works
            return false;
        } else {
            return true;
        }
    }

    private class MarkerRenderer extends DefaultClusterRenderer<MyClusterItem> {

        public MarkerRenderer() {
            super(CleanCityApplication.getContext(), mGoogleMap, mClusterManager);
        }


        @Override
        protected void onBeforeClusterItemRendered(MyClusterItem iten, MarkerOptions markerOptions) {
            markerOptions.icon(iten.getBitmap()).snippet(iten.getSnippet());
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() >= 2;
        }
    }
}