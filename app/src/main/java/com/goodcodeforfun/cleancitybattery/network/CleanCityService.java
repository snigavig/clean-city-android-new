package com.goodcodeforfun.cleancitybattery.network;

import com.goodcodeforfun.cleancitybattery.model.Location;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by snigavig on 05.09.15.
 */
public interface CleanCityService {
    @GET("/locations")
    Call<List<Location>> listLocations();
}