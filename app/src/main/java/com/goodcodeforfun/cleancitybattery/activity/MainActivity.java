package com.goodcodeforfun.cleancitybattery.activity;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.event.LocationsUpdateEvent;
import com.goodcodeforfun.cleancitybattery.fragment.LocationDetailsFragment;
import com.goodcodeforfun.cleancitybattery.fragment.PointsListFragment;
import com.goodcodeforfun.cleancitybattery.fragment.PointsMapFragment;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.model.Type;
import com.goodcodeforfun.cleancitybattery.network.ErrorHandler;
import com.goodcodeforfun.cleancitybattery.network.NetworkService;
import com.goodcodeforfun.cleancitybattery.util.EventBusHelper;
import com.goodcodeforfun.cleancitybattery.util.SnackbarHelper;
import com.goodcodeforfun.cleancitybattery.view.ClickableMapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ClickableMapView.UpdateMapAfterUserInterection {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final int COUNTER_GOAL = 7;
    private static final String NAV_ITEM_ID = "navItemId";
    private static final String CURRENT_LOCATION_TYPE = "currentLocationType";
    private static final int LOCATION_LOADER_ID = 1234;
    private final Handler mDrawerActionHandler = new Handler();
    private final PointsMapFragment mPointsMapFragment = new PointsMapFragment();
    private final PointsListFragment mPointsListFragment = new PointsListFragment();
    private final HashMap<MenuItem, String> CUSTOM_TYPES = new HashMap<>();
    private LocationDetailsFragment mLocationDetailsFragment = new LocationDetailsFragment();
    private int counter = 0;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView mAvatar;
    //TODO: uncomment when FAB functionality is back
    //private FloatingActionButton mFloatingActionButton;
    private Menu mMenu;
    private int mNavItemId;
    private LoaderManager mLoaderManager;
    private WeakReference<MainActivity> mainActivityWeakReference;
    private String mCurrentLocationType;
    private final LoaderManager.LoaderCallbacks<Cursor> mLocationLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null != mPointsMapFragment && mPointsMapFragment.isVisible())
                mPointsMapFragment.clearMap();
            showProgress();
            if (mCurrentLocationType.equals(LocationType.all.name())) {
                return new CursorLoader(MainActivity.this,
                        ContentProvider.createUri(Location.class, null),
                        null, null, null, null
                );
            } else {
                return new CursorLoader(MainActivity.this,
                        ContentProvider.createUri(Location.class, null),
                        null, "Type = ?", new String[]{mCurrentLocationType}, null
                );
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (null != mPointsMapFragment) {
                if (null != data) {
                    if (data.getCount() != 0) {
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        HashMap<String, LatLng> map = new HashMap<>();
                        LatLng position;
                        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                            position = new LatLng(
                                    data.getDouble(data.getColumnIndex(Location.COLUMN_LATITUDE)),
                                    data.getDouble(data.getColumnIndex(Location.COLUMN_LONGTITUDE))
                            );
                            boundsBuilder.include(position);
                            map.put(data.getString(data.getColumnIndexOrThrow(Location.COLUMN_API_ID)), position);
                        }
                        hideProgress();
                        mPointsMapFragment.updateMap(map);
                    } else {
                        mPointsMapFragment.clearMap();
                        hideProgress();
                        SnackbarHelper.show(mPointsMapFragment.getView(), getString(R.string.no_points_warning));
                    }
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
            if (v.toResourceString(CleanCityApplication.getInstance()).equals(abbr)) {
                return v;
            }
        }
        return null;
    }

    private static <T> void initLoader(final int loaderId, final Bundle args, final LoaderManager.LoaderCallbacks<T> callbacks,
                                       final LoaderManager loaderManager) {
        loaderManager.restartLoader(loaderId, args, callbacks);
        //need this to recreate it every damn time.
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public LocationDetailsFragment getLocationDetailsFragment() {
        return mLocationDetailsFragment;
    }

    public void setLocationDetailsFragment(LocationDetailsFragment fragment) {
        mLocationDetailsFragment = fragment;
    }

    //TODO: uncomment when FAB functionality is back
//    public FloatingActionButton getFloatingActionButton() {
//        return mFloatingActionButton;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusHelper.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusHelper.unregister(this);
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
                //TODO: uncomment when FAB functionality is back
                //new ErrorHandler.ResponseReceiver(findViewById(R.id.button_add_location));
                new ErrorHandler.ResponseReceiver(mDrawerLayout);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver,
                statusIntentFilter);

        //TODO: uncomment when FAB functionality is back
        //mFloatingActionButton = (FloatingActionButton) findViewById(R.id.button_add_location);
        //mFloatingActionButton.setOnClickListener(this);

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


        LinearLayout headerView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navigation_header, null);

        mAvatar = (ImageView) headerView.findViewById(R.id.avatarImageView);
        Picasso.with(this).load(R.drawable.cat_default_avatar).into(mAvatar);
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (COUNTER_GOAL != counter) {
                    counter++;
                } else {
                    counter = 0;
                    //TODO: uncomment when the game is ready.
                    //Intent intent = new Intent(mainActivityWeakReference.get(), MiniGamesActivity.class);
                    //startActivity(intent);
                }
            }
        });

        navigationView.addHeaderView(headerView);

        navigationView.setNavigationItemSelectedListener(this);
        mMenu = navigationView.getMenu();

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
                .replace(R.id.contentMap, mPointsMapFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentList, mPointsListFragment).hide(mPointsListFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentDetails, mLocationDetailsFragment)
                .commit();

        navigate(mNavItemId);

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

    //TODO: make them show/ide instead of replace.
    public void showList() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .hide(mPointsMapFragment)
                .show(mPointsListFragment)
                .commit();
    }

    public void showMap() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .hide(mPointsListFragment)
                .show(mPointsMapFragment)
                .commit();
    }

    private void navigate(final int itemId) {
        switch (itemId) {
            case R.id.drawer_item_0:
                mCurrentLocationType = LocationType.all.name();
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
            //TODO: uncomment when FAB functionality is back
//            case R.id.button_add_location:
//                Intent intent = new Intent(this, AddLocationActivity.class);
//                startActivity(intent);
//                break;
            default:
                //meh
        }
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

    @Override
    public void onUpdateMapAfterUserInterection() {
        if (SlidingUpPanelLayout.PanelState.HIDDEN != mPointsMapFragment.getLayout().getPanelState()) {
            mPointsMapFragment.getLayout().setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            mPointsMapFragment.getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
            //TODO: uncomment when FAB functionality is back
            //getFloatingActionButton().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<Type> customTypes = new Select().from(Type.class).execute();
        for (Type type : customTypes) {
            final String typeValue = type.getValue();
            if (!CUSTOM_TYPES.containsValue(typeValue)) {
                final String name = type.getName();
                int newId = mMenu.size();
                MenuItem item = mMenu.add(R.id.default_group, newId, Menu.NONE, name);
                item.setCheckable(true);
                item.setIcon(R.drawable.ic_cycle_24dp);
                CUSTOM_TYPES.put(item, typeValue);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void restartLocationsLoader() {
        if (null != mLoaderManager)
            initLoader(LOCATION_LOADER_ID, null, mLocationLoaderCallbacks, mLoaderManager);
    }

    public WeakReference<MainActivity> getMainActivityWeakReference() {
        return mainActivityWeakReference;
    }

    public void setMainActivityWeakReference(WeakReference<MainActivity> mainActivityWeakReference) {
        this.mainActivityWeakReference = mainActivityWeakReference;
    }

    public enum LocationType {
        all(R.string.item_0),
        battery(R.string.item_1),
        glass(R.string.item_2),
        paper(R.string.item_3),
        plastic(R.string.item_4);

        private final int resId;

        LocationType(int resId) {
            this.resId = resId;
        }

        public String toResourceString(Context ctx) {
            return resource(ctx);
        }

        public String resource(Context ctx) {
            return ctx.getString(resId);
        }
    }
}
