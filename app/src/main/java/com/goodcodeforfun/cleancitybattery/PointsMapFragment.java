package com.goodcodeforfun.cleancitybattery;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.content.ContentProvider;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;

public class PointsMapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_LOADER_ID = 1234;
    private final LoaderManager.LoaderCallbacks<Cursor> mLocationLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(getActivity(),
                    ContentProvider.createUri(Location.class, null),
                    null, null, null, null
            );

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() != 0) {
                for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                    Log.d("!!!!!!!!!!!!!LAT", data.getString(data.getColumnIndex(Location.COLUMN_LATITUDE)));
                    Log.d("!!!!!!!!!!!!!LON", data.getString(data.getColumnIndex(Location.COLUMN_LONGTITUDE)));
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };
    private WeakReference<MainActivity> mainActivityWeakReference;
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private LoaderManager mLoaderManager;

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
        mLoaderManager = getActivity().getSupportLoaderManager();
        mLoaderManager.initLoader(LOCATION_LOADER_ID, null, mLocationLoaderCallbacks);
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