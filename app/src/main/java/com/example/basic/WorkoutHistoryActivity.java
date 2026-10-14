package com.example.basic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkoutHistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private SharedPreferences historyPreferences;

    private static final String PREFS_NAME = "WorkoutHistory";
    private static final String HISTORY_KEY = "completed_workouts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        historyPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        historyContainer = findViewById(R.id.historyContainer);

        displayHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (historyContainer != null && historyPreferences != null) {
            displayHistory();
        }
    }

    private void displayHistory() {
        historyContainer.removeAllViews();

        String savedHistory =
                historyPreferences.getString(HISTORY_KEY, "[]");

        try {
            JSONArray history = new JSONArray(savedHistory);

            if (history.length() == 0) {
                TextView empty = new TextView(this);
                empty.setText(
                        "No completed workouts yet.\n\n"
                                + "Finish a guided workout and it will appear here."
                );
                empty.setTextColor(0xFF667085);
                empty.setTextSize(15);
                empty.setGravity(Gravity.CENTER);
                empty.setPadding(dp(20), dp(36), dp(20), dp(36));

                historyContainer.addView(empty);
                return;
            }

            // Show the newest completed workout first.
            for (int i = history.length() - 1; i >= 0; i--) {
                JSONObject workout = history.getJSONObject(i);

                String name = workout.optString("name", "Workout");
                String date = workout.optString("date", "");
                String duration = workout.optString("duration", "0 min");
                int exercises = workout.optInt("exercisesCompleted", 0);
                int totalExercises = workout.optInt("totalExercises", 0);

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(18), dp(16), dp(18), dp(16));
                card.setBackgroundColor(0xFFFFFFFF);
                card.setElevation(dp(2));

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                cardParams.bottomMargin = dp(14);

                TextView nameText = new TextView(this);
                nameText.setText(name);
                nameText.setTextColor(0xFF172033);
                nameText.setTextSize(18);
                nameText.setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                );
                card.addView(nameText);

                TextView dateText = new TextView(this);
                dateText.setText(date);
                dateText.setTextColor(0xFF667085);
                dateText.setTextSize(13);

                LinearLayout.LayoutParams dateParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                dateParams.topMargin = dp(6);
                card.addView(dateText, dateParams);

                TextView detailsText = new TextView(this);
                detailsText.setText(
                        "Active time: " + duration
                                + "\nExercises completed: "
                                + exercises + " / " + totalExercises
                );
                detailsText.setTextColor(0xFF344054);
                detailsText.setTextSize(14);

                LinearLayout.LayoutParams detailsParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                detailsParams.topMargin = dp(12);
                card.addView(detailsText, detailsParams);

                historyContainer.addView(card, cardParams);
            }

        } catch (Exception e) {
            android.util.Log.e(
                    "WorkoutHistory",
                    "Could not load workout history",
                    e
            );

            TextView error = new TextView(this);
            error.setText("Workout history could not be loaded.");
            error.setTextColor(0xFFB42318);
            error.setTextSize(14);
            historyContainer.addView(error);
        }
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }
}