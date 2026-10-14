package com.example.basic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecommendedWorkoutActivity extends AppCompatActivity {

    private TextView recommendedTitle;
    private TextView recommendedInfo;
    private TextView recommendedDescription;
    private TextView coachMessage;

    private Button startRecommendedWorkoutButton;
    private Button changeWorkoutButton;

    private String workoutType = "Full Body Workout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_workout);

        recommendedTitle =
                findViewById(R.id.recommendedTitle);

        recommendedInfo =
                findViewById(R.id.recommendedInfo);

        recommendedDescription =
                findViewById(R.id.recommendedDescription);

        coachMessage =
                findViewById(R.id.coachMessage);

        startRecommendedWorkoutButton =
                findViewById(R.id.startRecommendedWorkoutButton);

        changeWorkoutButton =
                findViewById(R.id.changeWorkoutButton);

        String receivedWorkout =
                getIntent().getStringExtra("WORKOUT_TYPE");

        if (receivedWorkout != null &&
                !receivedWorkout.isEmpty()) {

            workoutType = receivedWorkout;
        }

        displayRecommendation();

        startRecommendedWorkoutButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecommendedWorkoutActivity.this,
                    WorkoutActivity.class
            );

            intent.putExtra(
                    "WORKOUT_TYPE",
                    workoutType
            );

            startActivity(intent);
        });

        changeWorkoutButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecommendedWorkoutActivity.this,
                    WorkoutActivity.class
            );

            startActivity(intent);
        });
    }

    private void displayRecommendation() {

        recommendedTitle.setText(workoutType);

        switch (workoutType) {

            case "Cardio Workout":

                recommendedInfo.setText(
                        "15–20 min • Moderate"
                );

                recommendedDescription.setText(
                        "A cardio-focused session designed " +
                                "to improve endurance and keep you active."
                );

                break;

            case "HIIT Workout":

                recommendedInfo.setText(
                        "10–15 min • High Intensity"
                );

                recommendedDescription.setText(
                        "A short, intense workout to challenge " +
                                "your body and boost your energy."
                );

                break;

            case "Stretching":

                recommendedInfo.setText(
                        "10–15 min • Easy"
                );

                recommendedDescription.setText(
                        "A mobility-focused session to improve " +
                                "flexibility and help your body recover."
                );

                break;

            case "Full Body Workout":

            default:

                recommendedInfo.setText(
                        "15–20 min • Moderate"
                );

                recommendedDescription.setText(
                        "A balanced workout targeting multiple " +
                                "muscle groups for a complete session."
                );

                break;
        }

        coachMessage.setText(
                "Selected by your Daily Fitness Coach"
        );
    }
}