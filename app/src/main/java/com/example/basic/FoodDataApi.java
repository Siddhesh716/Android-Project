package com.example.basic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodDataApi {

    @GET("foods/search")
    Call<FoodSearchResponse> searchFoods(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("pageSize") int pageSize
    );

    @GET("food/{fdcId}")
    Call<FoodDetailResponse> getFoodDetails(
            @retrofit2.http.Path("fdcId") long fdcId,
            @Query("api_key") String apiKey
    );
}