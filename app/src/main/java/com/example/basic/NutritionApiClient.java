package com.example.basic;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NutritionApiClient {

    private static final String BASE_URL =
            "https://api.nal.usda.gov/fdc/v1/";

    private static final String API_KEY =
            "lmGrLjgcqR2BE6KH3OsNN5evRwWLr1bau1QjhX4v";

    private static FoodDataApi api;

    public static FoodDataApi getApi() {

        if (api == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(FoodDataApi.class);
        }

        return api;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}