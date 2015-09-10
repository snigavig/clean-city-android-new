package com.goodcodeforfun.cleancitybattery.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.network.ErrorHandler;
import com.goodcodeforfun.cleancitybattery.network.NetworkService;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by snigavig on 06.09.15.
 */
public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHOOSE_ON_MAP_REQUEST = 4444;
    private LatLng mPosition;
    private Spinner mSpinner;
    private EditText editTextName;
    private EditText address;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editTextName = (EditText) findViewById(R.id.editTextName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, MainActivity.LocationType.values()));
        findViewById(R.id.add_location_button).setOnClickListener(this);
        address = (EditText) findViewById(R.id.editText3);
        address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (address.getRight() - address.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Intent intent = new Intent(CleanCityApplication.getInstance(), ChooseLocationOnMapActivity.class);
                        startActivityForResult(intent, CHOOSE_ON_MAP_REQUEST);
                        return true;
                    }
                }
                return false;
            }
        });
        setLocationIconGrayTint();

        IntentFilter statusIntentFilter = new IntentFilter(
                NetworkService.ACTION_BROADCAST);

        ErrorHandler.ResponseReceiver responseReceiver =
                new ErrorHandler.ResponseReceiver(findViewById(R.id.scrollView));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver,
                statusIntentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_location_button:
                String spinnerItemText = null;
                MainActivity.LocationType type = null;

                if (null != mSpinner) {
                    spinnerItemText = mSpinner.getSelectedItem().toString();
                    type = MainActivity.findByAbbr(spinnerItemText);
                }
                if (null != editTextName &&
                        null != mPosition &&
                        0 != mPosition.latitude &&
                        0 != mPosition.longitude &&
                        null != spinnerItemText &&
                        !spinnerItemText.isEmpty() &&
                        null != type) {
                    Location location = new Location();
                    location.setName(editTextName.getText().toString());
                    location.setType(type.name());
                    location.setLatitude(mPosition.latitude);
                    location.setLongitude(mPosition.longitude);
                    location.save();

                    Intent mServicePostIntent = new Intent(this, NetworkService.class);
                    mServicePostIntent.setAction(NetworkService.ACTION_POST_LOCATION);
                    mServicePostIntent.putExtra(NetworkService.EXTRA_LOCATION_DB_ID, location.getId().toString());
                    startService(mServicePostIntent);
                    finish();
                }
                break;
            default:
                //muah
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_ON_MAP_REQUEST) {
            if (resultCode == RESULT_OK) {
                setLocationIconGreenTint();
                mPosition = new LatLng(
                        data.getDoubleExtra(ChooseLocationOnMapActivity.POSITION_LAT_KEY, 0),
                        data.getDoubleExtra(ChooseLocationOnMapActivity.POSITION_LON_KEY, 0)
                );
            }
        }
    }

    private void setLocationIconGrayTint() {
        Drawable normalDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pin_drop_24dp1);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, R.color.inactive));
        address.setCompoundDrawablesWithIntrinsicBounds(null, null, wrapDrawable, null);
    }

    private void setLocationIconGreenTint() {
        Drawable normalDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pin_drop_24dp1);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, R.color.primary));
        address.setCompoundDrawablesWithIntrinsicBounds(null, null, wrapDrawable, null);
    }
}
