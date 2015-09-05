package com.goodcodeforfun.cleancitybattery;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goodcodeforfun.cleancitybattery.network.NetworkService;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private final Handler mDrawerActionHandler = new Handler();
    private final PointsMapFragment mPointsMapFragment = new PointsMapFragment();
    private final PointsListFragment mPointsListFragment = new PointsListFragment();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    //private final EventsFragment mEventsFragment = new EventsFragment();
    private int mNavItemId;

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.drawer_item_1;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        // listen for navigation events
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, mPointsMapFragment)
                .commit();
        navigate(mNavItemId);
        Intent mServiceIntent = new Intent(this, NetworkService.class);
        mServiceIntent.setAction(NetworkService.ACTION_GET_LIST_LOCATIONS);
        startService(mServiceIntent);
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
                break;
            case R.id.drawer_item_2:
                //
                break;
            case R.id.drawer_item_3:
                //
                break;
            case R.id.drawer_item_4:
                //
                break;
            default:
                // ignore
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

//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        Log.d("!!!!!!!!!!!!!!!!", String.valueOf(item.getItemId()));
//
//        if (mDrawerToggle.isDrawerIndicatorEnabled() &&
//                mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        } else if (item.getItemId() == android.R.id.home &&
//                getSupportFragmentManager().popBackStackImmediate()) {
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }

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
    }
}
