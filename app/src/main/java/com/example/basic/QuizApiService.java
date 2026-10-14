package com.example.basic;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QuizApiService {

    @GET("quiz/next")
    Call<QuizNextResponse> getNextQuestion(
            @Query("user_id") String userId,
            @Query("topic") String topic
    );

    @POST("quiz/answer")
    Call<QuizAnswerResponse> submitAnswer(
            @Query("user_id") String userId,
            @Query("question_id") long questionId,
            @Query("selected_answer") String selectedAnswer
    );
}