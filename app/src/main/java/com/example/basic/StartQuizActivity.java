package com.example.basic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartQuizActivity extends AppCompatActivity {

    private TextView quizQuestionNumber;
    private TextView quizDifficultyText;
    private TextView aiQuestionText;
    private TextView answerResultText;

    private Button optionAButton;
    private Button optionBButton;
    private Button optionCButton;
    private Button optionDButton;
    private Button submitAnswerButton;
    private Button nextQuestionButton;

    private QuizApiService quizApiService;

    private long currentQuestionId = -1;

    private String selectedAnswer = "";

    private boolean questionLoaded = false;
    private boolean answerSubmitted = false;

    private int questionNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_quiz);

        quizQuestionNumber =
                findViewById(R.id.quizQuestionNumber);

        quizDifficultyText =
                findViewById(R.id.quizDifficultyText);

        aiQuestionText =
                findViewById(R.id.aiQuestionText);

        answerResultText =
                findViewById(R.id.answerResultText);

        optionAButton =
                findViewById(R.id.optionAButton);

        optionBButton =
                findViewById(R.id.optionBButton);

        optionCButton =
                findViewById(R.id.optionCButton);

        optionDButton =
                findViewById(R.id.optionDButton);

        submitAnswerButton =
                findViewById(R.id.submitAnswerButton);

        nextQuestionButton =
                findViewById(R.id.nextQuestionButton);

        quizApiService =
                QuizApiClient.getApiService();

        setupAnswerButtons();

        submitAnswerButton.setOnClickListener(
                view -> submitAnswer()
        );

        nextQuestionButton.setOnClickListener(
                view -> loadNextQuestion()
        );

        loadNextQuestion();
    }

    private void setupAnswerButtons() {

        optionAButton.setOnClickListener(
                view -> selectAnswer("A")
        );

        optionBButton.setOnClickListener(
                view -> selectAnswer("B")
        );

        optionCButton.setOnClickListener(
                view -> selectAnswer("C")
        );

        optionDButton.setOnClickListener(
                view -> selectAnswer("D")
        );
    }

    private void loadNextQuestion() {

        resetQuestionState();

        submitAnswerButton.setEnabled(false);

        submitAnswerButton.setText(
                "LOADING..."
        );

        SharedPreferences preferences =
                getSharedPreferences(
                        "UserData",
                        MODE_PRIVATE
                );

        String userId =
                preferences.getString(
                        "USER_MOBILE",
                        ""
                );

        if (userId.trim().isEmpty()) {

            aiQuestionText.setText(
                    "Unable to identify your account."
            );

            Toast.makeText(
                    StartQuizActivity.this,
                    "Please log in again.",
                    Toast.LENGTH_SHORT
            ).show();

            resetSubmitButton();

            return;
        }

        String topic = "nutrition";

        quizApiService
                .getNextQuestion(userId, topic)
                .enqueue(new Callback<QuizNextResponse>() {

                    @Override
                    public void onResponse(
                            Call<QuizNextResponse> call,
                            Response<QuizNextResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            aiQuestionText.setText(
                                    "Unable to load a quiz question."
                            );

                            Toast.makeText(
                                    StartQuizActivity.this,
                                    "Server error.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            resetSubmitButton();

                            return;
                        }

                        QuizNextResponse quizResponse =
                                response.body();

                        if ("success".equals(
                                quizResponse.getStatus()
                        )) {

                            displayQuestion(
                                    quizResponse
                            );

                            Toast.makeText(
                                    StartQuizActivity.this,
                                    "Question loaded.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            String message =
                                    quizResponse.getMessage();

                            if (message == null
                                    || message.trim().isEmpty()) {

                                message =
                                        "No new quiz question is available right now.";
                            }

                            aiQuestionText.setText(
                                    message
                            );

                            hideAnswerControls();

                            Toast.makeText(
                                    StartQuizActivity.this,
                                    "No question available.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            resetSubmitButton();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<QuizNextResponse> call,
                            Throwable t
                    ) {

                        android.util.Log.e(
                                "StartQuizActivity",
                                "Quiz request failed",
                                t
                        );

                        aiQuestionText.setText(
                                "Unable to connect to the quiz server."
                        );

                        hideAnswerControls();

                        Toast.makeText(
                                StartQuizActivity.this,
                                "Connection failed: " + t.getClass().getSimpleName(),
                                Toast.LENGTH_LONG
                        ).show();

                        resetSubmitButton();
                    }
                });
    }

    private void displayQuestion(
            QuizNextResponse quizResponse
    ) {

        currentQuestionId =
                quizResponse.getId();

        questionNumber++;

        questionLoaded = true;
        answerSubmitted = false;
        selectedAnswer = "";

        quizQuestionNumber.setText(
                "QUESTION " + questionNumber
        );

        String difficulty =
                quizResponse.getDifficulty();

        if (difficulty == null
                || difficulty.trim().isEmpty()) {

            difficulty = "UNKNOWN";
        }

        quizDifficultyText.setText(
                difficulty.toUpperCase()
        );

        aiQuestionText.setText(
                quizResponse.getQuestion()
        );

        Map<String, String> options =
                quizResponse.getOptions();

        if (options != null) {

            optionAButton.setText(
                    "A  " + safeOption(
                            options.get("A")
                    )
            );

            optionBButton.setText(
                    "B  " + safeOption(
                            options.get("B")
                    )
            );

            optionCButton.setText(
                    "C  " + safeOption(
                            options.get("C")
                    )
            );

            optionDButton.setText(
                    "D  " + safeOption(
                            options.get("D")
                    )
            );
        }

        showAnswerControls();

        submitAnswerButton.setEnabled(false);
        submitAnswerButton.setText(
                "SUBMIT ANSWER"
        );

        answerResultText.setVisibility(
                TextView.GONE
        );

        nextQuestionButton.setVisibility(
                TextView.GONE
        );

        resetOptionButtons();
    }

    private String safeOption(String option) {

        if (option == null
                || option.trim().isEmpty()) {

            return "Option unavailable";
        }

        return option;
    }

    private void selectAnswer(String answer) {

        if (!questionLoaded
                || answerSubmitted) {

            return;
        }

        selectedAnswer = answer;

        resetOptionButtons();

        Button selectedButton = null;

        if ("A".equals(answer)) {
            selectedButton = optionAButton;
        } else if ("B".equals(answer)) {
            selectedButton = optionBButton;
        } else if ("C".equals(answer)) {
            selectedButton = optionCButton;
        } else if ("D".equals(answer)) {
            selectedButton = optionDButton;
        }

        if (selectedButton != null) {

            selectedButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.rgb(
                                    234,
                                    246,
                                    216
                            )
                    )
            );
        }

        submitAnswerButton.setEnabled(true);
    }

    private void submitAnswer() {

        if (!questionLoaded
                || answerSubmitted
                || currentQuestionId <= 0
                || selectedAnswer.isEmpty()) {

            Toast.makeText(
                    StartQuizActivity.this,
                    "Please select an answer.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        submitAnswerButton.setEnabled(false);

        submitAnswerButton.setText(
                "CHECKING..."
        );

        SharedPreferences preferences =
                getSharedPreferences(
                        "UserData",
                        MODE_PRIVATE
                );

        String userId =
                preferences.getString(
                        "USER_MOBILE",
                        ""
                );

        quizApiService
                .submitAnswer(
                        userId,
                        currentQuestionId,
                        selectedAnswer
                )
                .enqueue(new Callback<QuizAnswerResponse>() {

                    @Override
                    public void onResponse(
                            Call<QuizAnswerResponse> call,
                            Response<QuizAnswerResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            submitAnswerButton.setEnabled(true);

                            submitAnswerButton.setText(
                                    "SUBMIT ANSWER"
                            );

                            Toast.makeText(
                                    StartQuizActivity.this,
                                    "Unable to check answer.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        QuizAnswerResponse answerResponse =
                                response.body();

                        if ("success".equals(
                                answerResponse.getStatus()
                        )) {

                            answerSubmitted = true;

                            showAnswerResult(
                                    answerResponse
                            );

                        } else {

                            submitAnswerButton.setEnabled(true);

                            submitAnswerButton.setText(
                                    "SUBMIT ANSWER"
                            );

                            String message =
                                    answerResponse.getMessage();

                            if (message == null
                                    || message.trim().isEmpty()) {

                                message =
                                        "Unable to check this answer.";
                            }

                            Toast.makeText(
                                    StartQuizActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<QuizAnswerResponse> call,
                            Throwable t
                    ) {

                        submitAnswerButton.setEnabled(true);

                        submitAnswerButton.setText(
                                "SUBMIT ANSWER"
                        );

                        Toast.makeText(
                                StartQuizActivity.this,
                                "Connection failed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showAnswerResult(
            QuizAnswerResponse answerResponse
    ) {

        StringBuilder result =
                new StringBuilder();

        if (answerResponse.isCorrect()) {

            updateQuizProgress(true);
            result.append(
                    "✓ Correct!\n\n"
            );

            result.append(
                    "Good job. Keep going."
            );

        } else {

            updateQuizProgress(false);
            result.append(
                    "✕ Incorrect\n\n"
            );

            result.append(
                    "Correct answer: "
            );

            result.append(
                    answerResponse.getCorrectAnswer()
            );
        }

        answerResultText.setText(
                result.toString()
        );

        answerResultText.setVisibility(
                TextView.VISIBLE
        );

        submitAnswerButton.setVisibility(
                TextView.GONE
        );

        disableOptionButtons();

        nextQuestionButton.setVisibility(
                TextView.VISIBLE
        );
    }

    private void updateQuizProgress(boolean isCorrect) {
        android.content.SharedPreferences preferences =
                getSharedPreferences("QuizProgress", MODE_PRIVATE);

        int totalXp = preferences.getInt("totalXp", 0);

        if (isCorrect) {
            totalXp += 10;
        }

        String today = new java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        String lastAnswerDate = preferences.getString("lastAnswerDate", "");
        int currentStreak = preferences.getInt("currentStreak", 0);

        if (!today.equals(lastAnswerDate)) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1);

            String yesterday = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd",
                    java.util.Locale.getDefault()
            ).format(calendar.getTime());

            if (yesterday.equals(lastAnswerDate)) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }
        }

        preferences.edit()
                .putInt("totalXp", totalXp)
                .putInt("currentStreak", currentStreak)
                .putString("lastAnswerDate", today)
                .apply();
    }

    private void resetQuestionState() {

        questionLoaded = false;
        answerSubmitted = false;
        selectedAnswer = "";
        currentQuestionId = -1;

        answerResultText.setVisibility(
                TextView.GONE
        );

        nextQuestionButton.setVisibility(
                TextView.GONE
        );

        showAnswerControls();

        resetOptionButtons();
    }

    private void showAnswerControls() {

        optionAButton.setVisibility(
                TextView.VISIBLE
        );

        optionBButton.setVisibility(
                TextView.VISIBLE
        );

        optionCButton.setVisibility(
                TextView.VISIBLE
        );

        optionDButton.setVisibility(
                TextView.VISIBLE
        );

        submitAnswerButton.setVisibility(
                TextView.VISIBLE
        );
    }

    private void hideAnswerControls() {

        optionAButton.setVisibility(
                TextView.GONE
        );

        optionBButton.setVisibility(
                TextView.GONE
        );

        optionCButton.setVisibility(
                TextView.GONE
        );

        optionDButton.setVisibility(
                TextView.GONE
        );

        submitAnswerButton.setVisibility(
                TextView.GONE
        );
    }

    private void disableOptionButtons() {

        optionAButton.setEnabled(false);
        optionBButton.setEnabled(false);
        optionCButton.setEnabled(false);
        optionDButton.setEnabled(false);
    }

    private void resetOptionButtons() {

        int normalColor =
                android.graphics.Color.rgb(
                        255,
                        255,
                        255
                );

        optionAButton.setEnabled(true);
        optionBButton.setEnabled(true);
        optionCButton.setEnabled(true);
        optionDButton.setEnabled(true);

        optionAButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        normalColor
                )
        );

        optionBButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        normalColor
                )
        );

        optionCButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        normalColor
                )
        );

        optionDButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        normalColor
                )
        );
    }

    private void resetSubmitButton() {

        submitAnswerButton.setEnabled(false);

        submitAnswerButton.setText(
                "SUBMIT ANSWER"
        );
    }
}