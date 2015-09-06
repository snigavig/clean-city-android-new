package com.goodcodeforfun.cleancitybattery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.goodcodeforfun.cleancitybattery.R;

/**
 * Created by snigavig on 06.09.15.
 */
public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CHOOSE_ON_MAP_REQUEST = 4444;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        mySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, MainActivity.LocationType.values()));
        findViewById(R.id.choose_on_map_button).setOnClickListener(this);
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
                break;
            case R.id.choose_on_map_button:
                Intent intent = new Intent(this, ChooseLocationOnMapActivity.class);
                startActivityForResult(intent, CHOOSE_ON_MAP_REQUEST);
            default:
                //muah
        }
    }
}
