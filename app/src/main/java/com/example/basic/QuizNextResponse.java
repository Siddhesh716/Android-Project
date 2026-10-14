package com.example.basic;

import java.util.Map;

public class QuizNextResponse {

    private String status;
    private long id;
    private String question;
    private Map<String, String> options;
    private String difficulty;
    private String topic;
    private String source;
    private String recommended_difficulty;
    private double accuracy_percentage;
    private String message;

    public String getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public String getSource() {
        return source;
    }

    public String getRecommendedDifficulty() {
        return recommended_difficulty;
    }

    public double getAccuracyPercentage() {
        return accuracy_percentage;
    }

    public String getMessage() {
        return message;
    }
}