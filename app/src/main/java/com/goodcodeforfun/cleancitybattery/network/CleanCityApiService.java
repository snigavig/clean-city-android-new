package com.goodcodeforfun.cleancitybattery.network;

import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.model.Type;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by snigavig on 05.09.15.
 */
public interface CleanCityApiService {
    @GET("/locations")
    Call<List<Location>> listLocations();

    @POST("/locations")
    Call<Location> addLocation(@Body Location location);

    @GET("/garbageTypes")
    Call<List<Type>> listTypes();

}