package com.goodcodeforfun.cleancitybattery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.model.Location;
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
        findViewById(R.id.choose_on_map_button).setOnClickListener(this);
        findViewById(R.id.add_location_button).setOnClickListener(this);
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
            case R.id.choose_on_map_button:
                Intent intent = new Intent(this, ChooseLocationOnMapActivity.class);
                startActivityForResult(intent, CHOOSE_ON_MAP_REQUEST);
            default:
                //muah
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_ON_MAP_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPosition = new LatLng(
                        data.getDoubleExtra(ChooseLocationOnMapActivity.POSITION_LAT_KEY, 0),
                        data.getDoubleExtra(ChooseLocationOnMapActivity.POSITION_LON_KEY, 0)
                );
            }
        }
    }

}
