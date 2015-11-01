package com.goodcodeforfun.cleancitybattery.model;

/**
 * Created by snigavig on 01.11.15.
 */

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class MyClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String snippet;
    private final BitmapDescriptor bitmap;


    public MyClusterItem(double lat, double lng, String snippet, BitmapDescriptor bitmap) {
        this.mPosition = new LatLng(lat, lng);
        this.snippet = snippet;
        this.bitmap = bitmap;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getSnippet() {
        return snippet;
    }

    public BitmapDescriptor getBitmap() {
        return bitmap;
    }
}