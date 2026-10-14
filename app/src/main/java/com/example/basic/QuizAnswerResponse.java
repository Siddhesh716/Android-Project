package com.example.basic;

public class QuizAnswerResponse {

    private String status;
    private boolean correct;
    private String correct_answer;
    private String message;

    public String getStatus() {
        return status;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getCorrectAnswer() {
        return correct_answer;
    }

    public String getMessage() {
        return message;
    }
}