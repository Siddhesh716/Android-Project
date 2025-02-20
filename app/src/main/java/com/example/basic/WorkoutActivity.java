package com.example.basic;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.CountDownTimer;

public class WorkoutActivity extends AppCompatActivity {

    private TextView timerTextView, selectedWorkoutTextView;
    private Button fullBodyWorkoutButton, coreWorkoutButton, upperBodyWorkoutButton, cardioWorkoutButton,legsWorkoutButton, stretchingWorkoutButton, yogaWorkoutButton, hiitWorkoutButton, startStopButton;
    private Spinner timeSpinner;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 60000;
    private String selectedWorkoutType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        timerTextView = findViewById(R.id.timerTextView);
        selectedWorkoutTextView = findViewById(R.id.selectedWorkoutTextView);
        fullBodyWorkoutButton = findViewById(R.id.fullBodyWorkoutButton);
        coreWorkoutButton = findViewById(R.id.coreWorkoutButton);
        upperBodyWorkoutButton = findViewById(R.id.upperBodyWorkoutButton);
        cardioWorkoutButton = findViewById(R.id.cardioWorkoutButton);
        legsWorkoutButton = findViewById(R.id.legsWorkoutButton);
        stretchingWorkoutButton = findViewById(R.id.stretchingWorkoutButton);
        yogaWorkoutButton = findViewById(R.id.yogaWorkoutButton);
        hiitWorkoutButton = findViewById(R.id.hiitWorkoutButton);
        startStopButton = findViewById(R.id.startStopButton);
        timeSpinner = findViewById(R.id.timeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        fullBodyWorkoutButton.setOnClickListener(v -> selectWorkout("Full Body Workout"));
        coreWorkoutButton.setOnClickListener(v -> selectWorkout("Core Workout"));
        upperBodyWorkoutButton.setOnClickListener(v -> selectWorkout("Upper Body Workout"));
        cardioWorkoutButton.setOnClickListener(v -> selectWorkout("Cardio Workout"));
        legsWorkoutButton.setOnClickListener(v -> selectWorkout("Legs Workout"));
        stretchingWorkoutButton.setOnClickListener(v -> selectWorkout("Stretching"));
        yogaWorkoutButton.setOnClickListener(v -> selectWorkout("Yoga"));
        hiitWorkoutButton.setOnClickListener(v -> selectWorkout("HIIT Workout"));
        startStopButton.setOnClickListener(v -> toggleTimer());
        setButtonsEnabled(true);
        startStopButton.setEnabled(false);
        timeSpinner.setEnabled(false);
    }

    private void selectWorkout(String workoutType) {
        selectedWorkoutType = workoutType;
        selectedWorkoutTextView.setText("Selected Workout: " + workoutType);
        timeSpinner.setEnabled(true);
        startStopButton.setEnabled(true);
    }

    private void startWorkout() {
        String selectedTime = timeSpinner.getSelectedItem().toString();
        switch (selectedTime) {
            case "1 minute":
                timeLeftInMillis = 60000;
                break;
            case "2 minutes":
                timeLeftInMillis = 120000;
                break;
            case "5 minutes":
                timeLeftInMillis = 300000;
                break;
            default:
                timeLeftInMillis = 60000;
                break;
        }
        timerTextView.setText("Starting " + selectedWorkoutType);
        setButtonsEnabled(false);
        timeSpinner.setEnabled(false);
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Workout finished! Great job!");
                setButtonsEnabled(true);
                startStopButton.setEnabled(false);
                timeSpinner.setEnabled(true);
            }
        };
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftText = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText("Time remaining: " + timeLeftText);
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            startStopButton.setText("Start");
        } else {
            startWorkout();
            countDownTimer.start();
            isTimerRunning = true;
            startStopButton.setText("Stop");
        }
    }

    private void setButtonsEnabled(boolean isEnabled) {
        fullBodyWorkoutButton.setEnabled(isEnabled);
        coreWorkoutButton.setEnabled(isEnabled);
        upperBodyWorkoutButton.setEnabled(isEnabled);
        cardioWorkoutButton.setEnabled(isEnabled);
        legsWorkoutButton.setEnabled(isEnabled);
        stretchingWorkoutButton.setEnabled(isEnabled);
        yogaWorkoutButton.setEnabled(isEnabled);
        hiitWorkoutButton.setEnabled(isEnabled);
    }
}