package com.goodcodeforfun.cleancitybattery.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.event.LocationsUpdateEvent;
import com.goodcodeforfun.cleancitybattery.fragment.PointsListFragment;
import com.goodcodeforfun.cleancitybattery.fragment.PointsMapFragment;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.model.Type;
import com.goodcodeforfun.cleancitybattery.network.ErrorHandler;
import com.goodcodeforfun.cleancitybattery.network.NetworkService;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final int COUNTER_GOAL = 7;
    private static final String NAV_ITEM_ID = "navItemId";
    private static final String CURRENT_LOCATION_TYPE = "currentLocationType";
    private static final int LOCATION_LOADER_ID = 1234;
    private final Handler mDrawerActionHandler = new Handler();
    private final PointsMapFragment mPointsMapFragment = new PointsMapFragment();
    private final PointsListFragment mPointsListFragment = new PointsListFragment();
    private HashMap<MenuItem, String> CUSTOM_TYPES = new HashMap<>();
    private int counter = 0;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private LoaderManager mLoaderManager;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private String mCurrentLocationType;
    private final LoaderManager.LoaderCallbacks<Cursor> mLocationLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null != mPointsMapFragment && mPointsMapFragment.isVisible())
                mPointsMapFragment.clearMap();
            showProgress();
            return new CursorLoader(MainActivity.this,
                    ContentProvider.createUri(Location.class, null),
                    null, "Type = ?", new String[]{mCurrentLocationType}, null
            );

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (null != mPointsMapFragment && mPointsMapFragment.isVisible()) {
                if (data.getCount() != 0) {
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    ArrayList<LatLng> mArrayList = new ArrayList<>();
                    LatLng position;
                    for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                        position = new LatLng(
                                data.getDouble(data.getColumnIndex(Location.COLUMN_LATITUDE)),
                                data.getDouble(data.getColumnIndex(Location.COLUMN_LONGTITUDE))
                        );
                        boundsBuilder.include(position);
                        mArrayList.add(position);
                    }
                    hideProgress();
                    mPointsMapFragment.updateMap(mArrayList, boundsBuilder.build());
                } else {
                    mPointsMapFragment.clearMap();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            showProgress();
        }
    };

    public static LocationType findByAbbr(String abbr) {
        for (LocationType v : LocationType.values()) {
            if (v.toString().equals(abbr)) {
                return v;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        restartLocationsLoader();
    }



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        showProgress();

        mLoaderManager = getSupportLoaderManager();

        mainActivityWeakReference = new WeakReference<>(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        IntentFilter statusIntentFilter = new IntentFilter(
                NetworkService.ACTION_BROADCAST);

        ErrorHandler.ResponseReceiver responseReceiver =
                new ErrorHandler.ResponseReceiver(findViewById(R.id.button_add_location));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver,
                statusIntentFilter);

        findViewById(R.id.button_add_location).setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.drawer_item_1;
            mCurrentLocationType = LocationType.battery.name();
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
            mCurrentLocationType = savedInstanceState.getString(CURRENT_LOCATION_TYPE);
        }

        // listen for navigation events
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        Menu mMenu = navigationView.getMenu();

        List<Type> customTypes = new Select().from(Type.class).execute();

        for (Type type : customTypes) {
            final String name = type.getName();
            final String typeValue = type.getValue();
            int newId = mMenu.size();
            MenuItem item = mMenu.add(R.id.default_group, newId, Menu.NONE, name);
            item.setCheckable(true);
            item.setIcon(R.drawable.ic_cycle_24dp);
            if (!CUSTOM_TYPES.containsKey(item))
                CUSTOM_TYPES.put(item, typeValue);
        }

        // select the correct nav menu item
        if (null != mMenu) {
            MenuItem item = mMenu.findItem(mNavItemId);
            if (null != item)
                item.setChecked(true);
        }
        setDrawerIndicator();
        // set up the hamburger icon to open and close the drawer
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, mPointsMapFragment)
                .commit();
        navigate(mNavItemId);

        restartLocationsLoader();
        CleanCityApplication.getInstance().getEventBusHelper().getBus().register(this);
        ImageView avatar = (ImageView) findViewById(R.id.avatarImageView);
        Picasso.with(this).load(R.drawable.cat_default_avatar).into(avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (COUNTER_GOAL != counter) {
                    counter++;
                } else {
                    counter = 0;
                    Intent intent = new Intent(mainActivityWeakReference.get(), MiniGamesActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setDrawerIndicator() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    //Just an idea...
    @SuppressWarnings("unused")
    public void setHomeAsUpIndicator() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open,
                R.string.close);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void showList() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, mPointsListFragment)
                .commit();
    }

    public void showMap() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, mPointsMapFragment)
                .commit();
    }

    private void navigate(final int itemId) {
        switch (itemId) {
            case R.id.drawer_item_1:
                mCurrentLocationType = LocationType.battery.name();
                break;
            case R.id.drawer_item_2:
                mCurrentLocationType = LocationType.glass.name();
                break;
            case R.id.drawer_item_3:
                mCurrentLocationType = LocationType.paper.name();
                break;
            case R.id.drawer_item_4:
                mCurrentLocationType = LocationType.plastic.name();
                break;
            default:
                // custom type
                for (Map.Entry<MenuItem, String> item : CUSTOM_TYPES.entrySet()) {
                    if (itemId == item.getKey().getItemId()) {
                        mCurrentLocationType = item.getValue();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        // update highlighted item in the navigation menu
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
                restartLocationsLoader();
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
        outState.putString(CURRENT_LOCATION_TYPE, mCurrentLocationType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_location:
                Intent intent = new Intent(this, AddLocationActivity.class);
                startActivity(intent);
                break;
            default:
                //meh
        }
    }

    public void restartLocationsLoader() {
        if (null != mLoaderManager)
            mLoaderManager.restartLoader(LOCATION_LOADER_ID, null, mLocationLoaderCallbacks);
    }

    @Subscribe
    public void newLocationsUpdate(LocationsUpdateEvent event) {
        switch ((LocationsUpdateEvent.LocationUpdateEventType) event.getType()) {
            case STARTED:
                showProgress();
                break;
            case COMPLETED:
                hideProgress();
                switch (event.getResultCode()) {
                    case LocationsUpdateEvent.OK_RESULT_CODE:
                        restartLocationsLoader();
                        break;
                    case LocationsUpdateEvent.FAIL_RESULT_CODE:
                        //TODO: think
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

    private void showProgress() {
        if (null != mProgressBar)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (null != mProgressBar)
            mProgressBar.setVisibility(View.GONE);
    }

    public enum LocationType {
        battery(CleanCityApplication.getInstance().getString(R.string.item_1)),
        glass(CleanCityApplication.getInstance().getString(R.string.item_2)),
        paper(CleanCityApplication.getInstance().getString(R.string.item_3)),
        plastic(CleanCityApplication.getInstance().getString(R.string.item_4));

        private final String readableName;

        LocationType(String readableName) {
            this.readableName = readableName;
        }

        @Override
        public String toString() {
            return readableName;
        }
    }
}
