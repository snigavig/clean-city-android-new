package com.goodcodeforfun.cleancitybattery.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.activity.MainActivity;
import com.goodcodeforfun.cleancitybattery.model.Location;

import java.lang.ref.WeakReference;

/**
 * Created by snigavig on 27.09.15.
 */
public class LocationDetailsFragment extends Fragment {

    private static final int LOCATION_LOADER_ID = 2345;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private LoaderManager mLoaderManager;
    private TextView mNameView;
    private String mCurrentLocation;

    private final LoaderManager.LoaderCallbacks<Cursor> mLocationLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(CleanCityApplication.getInstance(),
                    ContentProvider.createUri(Location.class, null),
                    null, Location.COLUMN_API_ID + " = ?", new String[]{mCurrentLocation}, null
            );

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() != 0) {
                data.moveToFirst();
                if (null != mNameView) {
                    String name = data.getString(data.getColumnIndex(Location.COLUMN_NAME));
                    if (null != name)
                        mNameView.setText(name);
                }
            }
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //showProgress();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(false);
        mLoaderManager = getLoaderManager();
        mainActivityWeakReference = new WeakReference<>((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_details_fragment, container, false);
        mNameView = (TextView) rootView.findViewById(R.id.nameTextView);
        return rootView;
    }

    public void setCurrentLocation(String mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
        restartLocationLoader();
    }

    public void restartLocationLoader() {
        if (null != mLoaderManager)
            mLoaderManager.restartLoader(LOCATION_LOADER_ID, null, mLocationLoaderCallbacks);
    }
}
