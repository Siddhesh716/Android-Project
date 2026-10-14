package com.example.basic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private LinearLayout startQuizCard;
    private LinearLayout todaysTopicCard;

    private TextView quizXpText;
    private TextView quizStreakText;

    private static final String PREFS_NAME = "QuizProgress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        startQuizCard = findViewById(R.id.startQuizCard);
        todaysTopicCard = findViewById(R.id.todaysTopicCard);

        quizXpText = findViewById(R.id.quizXpText);
        quizStreakText = findViewById(R.id.quizStreakText);

        setupCardNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuizProgress();
    }

    private void setupCardNavigation() {
        startQuizCard.setOnClickListener(view -> {
            Intent intent = new Intent(
                    QuizActivity.this,
                    StartQuizActivity.class
            );
            startActivity(intent);
        });

        todaysTopicCard.setOnClickListener(view -> {
            Intent intent = new Intent(
                    QuizActivity.this,
                    TodaysTopicActivity.class
            );
            startActivity(intent);
        });
    }

    private void loadQuizProgress() {
        SharedPreferences preferences =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int totalXp = preferences.getInt("totalXp", 0);
        int currentStreak = preferences.getInt("currentStreak", 0);

        quizXpText.setText(String.valueOf(totalXp));
        quizStreakText.setText(currentStreak + " 🔥");
    }
}