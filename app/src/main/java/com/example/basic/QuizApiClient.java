package com.example.basic;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizApiClient {

    private static final String BASE_URL =
            "http://192.168.12.101:8001/";

    private static final OkHttpClient httpClient =
            new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

    private static final Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();

    public static QuizApiService getApiService() {
        return retrofit.create(QuizApiService.class);
    }
}