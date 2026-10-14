package com.example.basic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoalsActivity extends AppCompatActivity {

    private GoalProgressManager goalManager;

    private final List<GoalProgressManager.Goal> goalsList =
            new ArrayList<>();

    private LinearLayout goalsContainer;

    private TextView activeGoalName;
    private TextView activeGoalDescription;
    private TextView activeGoalProgressValue;
    private TextView activeGoalProgressText;
    private TextView goalsCount;

    private ProgressBar activeGoalProgress;

    private Button updateGoalButton;
    private Button startGoalButton;

    private int activeGoalPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goals);

        goalManager =
                new GoalProgressManager(this);

        setupVideo();

        // ---------------------------------------------------------
        // VIEW REFERENCES
        // ---------------------------------------------------------

        goalsContainer =
                findViewById(R.id.goalsContainer);

        activeGoalName =
                findViewById(R.id.activeGoalName);

        activeGoalDescription =
                findViewById(R.id.activeGoalDescription);

        activeGoalProgressValue =
                findViewById(R.id.activeGoalProgressValue);

        activeGoalProgressText =
                findViewById(R.id.activeGoalProgressText);

        activeGoalProgress =
                findViewById(R.id.activeGoalProgress);

        goalsCount =
                findViewById(R.id.goalsCount);

        updateGoalButton =
                findViewById(R.id.updateGoalButton);

        startGoalButton =
                findViewById(R.id.startGoalButton);

        // ---------------------------------------------------------
        // LOAD GOALS
        // ---------------------------------------------------------

        loadGoals();

        setupGoalsList();

        // ---------------------------------------------------------
        // ADD GOAL
        // ---------------------------------------------------------

        Button addGoalButton =
                findViewById(R.id.add_goal_button);

        addGoalButton.setOnClickListener(
                v -> showAddGoalDialog()
        );

        // ---------------------------------------------------------
        // UPDATE PROGRESS
        // ---------------------------------------------------------

        updateGoalButton.setOnClickListener(
                v -> showProgressDialog()
        );

        // ---------------------------------------------------------
        // START ACTIVE GOAL
        // ---------------------------------------------------------

        startGoalButton.setOnClickListener(
                v -> startActiveGoal()
        );

        // ---------------------------------------------------------
        // INITIAL UI
        // ---------------------------------------------------------

        updateGoalCount();

        updateActiveGoal();
    }

    // =========================================================
    // VIDEO
    // =========================================================

    private void setupVideo() {

        VideoView videoView =
                findViewById(R.id.goals_video);

        String videoPath =
                "android.resource://"
                        + getPackageName()
                        + "/"
                        + R.raw.main;

        Uri uri =
                Uri.parse(videoPath);

        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(mp -> {

            mp.setLooping(true);

            videoView.start();
        });
    }

    // =========================================================
    // START ACTIVE GOAL
    // =========================================================

    private void startActiveGoal() {

        if (goalsList.isEmpty()) {
            return;
        }

        GoalProgressManager.Goal goal =
                goalsList.get(
                        activeGoalPosition
                );

        if (goal.isCompleted()) {

            Toast.makeText(
                    GoalsActivity.this,
                    "This goal is already complete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ---------------------------------------------------------
        // RUNNING GOAL → GPS RUN TRACKER
        // ---------------------------------------------------------

        if (isRunningGoal(goal)) {

            Intent intent =
                    new Intent(
                            GoalsActivity.this,
                            RunningTrackerActivity.class
                    );

            startActivity(intent);

            return;
        }

        // ---------------------------------------------------------
        // TIME GOALS → EXISTING STOPWATCH
        // ---------------------------------------------------------

        if (isTimeGoal(goal)) {

            startTimeGoal();

            return;
        }

        // ---------------------------------------------------------
        // OTHER GOALS
        // ---------------------------------------------------------

        Toast.makeText(
                GoalsActivity.this,
                "Use UPDATE PROGRESS for this goal",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================================================
    // LOAD GOALS
    // =========================================================

    private void loadGoals() {

        goalsList.clear();

        goalsList.addAll(
                goalManager.getGoals()
        );

        activeGoalPosition =
                goalManager.getActiveGoalPosition();

        if (goalsList.isEmpty()) {

            activeGoalPosition = 0;

        } else if (
                activeGoalPosition >=
                        goalsList.size()
        ) {

            activeGoalPosition =
                    goalsList.size() - 1;
        }
    }

    // =========================================================
    // GOALS LIST
    // =========================================================

    private void setupGoalsList() {

        if (goalsContainer == null) {
            return;
        }

        goalsContainer.removeAllViews();

        for (int i = 0;
             i < goalsList.size();
             i++) {

            final int position = i;

            GoalProgressManager.Goal goal =
                    goalsList.get(i);

            View goalView =
                    LayoutInflater
                            .from(
                                    GoalsActivity.this
                            )
                            .inflate(
                                    R.layout.item_goal,
                                    goalsContainer,
                                    false
                            );

            TextView goalName =
                    goalView.findViewById(
                            R.id.goalName
                    );

            TextView goalNumber =
                    goalView.findViewById(
                            R.id.goalNumber
                    );

            TextView goalProgress =
                    goalView.findViewById(
                            R.id.goalProgress
                    );

            TextView goalStatus =
                    goalView.findViewById(
                            R.id.goalStatus
                    );

            // -----------------------------------------------------
            // GOAL NAME
            // -----------------------------------------------------

            goalName.setText(
                    goal.getName()
            );

            // -----------------------------------------------------
            // GOAL NUMBER
            // -----------------------------------------------------

            goalNumber.setText(
                    String.valueOf(
                            position + 1
                    )
            );

            // -----------------------------------------------------
            // PROGRESS
            // -----------------------------------------------------

            goalProgress.setText(
                    formatProgress(
                            goal.getProgressPercentage()
                    )
            );

            // -----------------------------------------------------
            // STATUS
            // -----------------------------------------------------

            if (position ==
                    activeGoalPosition) {

                goalStatus.setText(
                        "ACTIVE"
                );

                goalStatus.setTextColor(
                        getColor(
                                R.color.buttonBackground
                        )
                );

            } else if (
                    goal.isCompleted()
            ) {

                goalStatus.setText(
                        "DONE"
                );

                goalStatus.setTextColor(
                        getColor(
                                R.color.buttonBackground
                        )
                );

            } else {

                goalStatus.setText(
                        "GOAL"
                );

                goalStatus.setTextColor(
                        getColor(
                                android.R.color.darker_gray
                        )
                );
            }

            // -----------------------------------------------------
            // CLICK GOAL
            // -----------------------------------------------------

            goalView.setOnClickListener(
                    v -> {

                        activeGoalPosition =
                                position;

                        goalManager
                                .setActiveGoalPosition(
                                        activeGoalPosition
                                );

                        updateActiveGoal();

                        setupGoalsList();

                        showGoalDialog(
                                position
                        );
                    }
            );

            // -----------------------------------------------------
            // ADD TO CONTAINER
            // -----------------------------------------------------

            goalsContainer.addView(
                    goalView
            );
        }
    }

    // =========================================================
    // ACTIVE GOAL
    // =========================================================

    private void updateActiveGoal() {

        if (goalsList.isEmpty()) {

            activeGoalName.setText(
                    "No active goal"
            );

            activeGoalDescription.setText(
                    "Add a goal to start your fitness journey."
            );

            activeGoalProgressValue.setText(
                    "0"
            );

            activeGoalProgressText.setText(
                    "0%"
            );

            activeGoalProgress.setProgress(
                    0
            );

            updateGoalButtons();

            return;
        }

        if (activeGoalPosition >=
                goalsList.size()) {

            activeGoalPosition =
                    goalsList.size() - 1;

            goalManager.setActiveGoalPosition(
                    activeGoalPosition
            );
        }

        GoalProgressManager.Goal goal =
                goalsList.get(
                        activeGoalPosition
                );

        double progress =
                goal.getProgressPercentage();

        activeGoalName.setText(
                goal.getName()
        );

        activeGoalDescription.setText(
                getGoalDescription(
                        goal
                )
        );

        activeGoalProgressValue.setText(
                getProgressValueText(
                        goal
                )
        );

        activeGoalProgressText.setText(
                formatProgress(
                        progress
                )
        );

        activeGoalProgress.setProgress(
                (int) Math.round(
                        progress
                )
        );

        updateGoalButtons();
    }

    // =========================================================
    // BUTTON STATE
    // =========================================================

    private void updateGoalButtons() {

        if (goalsList.isEmpty()) {

            updateGoalButton.setEnabled(false);

            startGoalButton.setEnabled(false);

            startGoalButton.setText(
                    "START"
            );

            return;
        }

        GoalProgressManager.Goal goal =
                goalsList.get(
                        activeGoalPosition
                );

        updateGoalButton.setEnabled(
                !goal.isCompleted()
        );

        boolean canStart =
                isRunningGoal(goal)
                        ||
                        isTimeGoal(goal);

        startGoalButton.setEnabled(
                canStart
                        &&
                        !goal.isCompleted()
        );

        // ---------------------------------------------------------
        // BUTTON TEXT
        // ---------------------------------------------------------

        if (isRunningGoal(goal)) {

            startGoalButton.setText(
                    "START RUN"
            );

        } else if (isTimeGoal(goal)) {

            startGoalButton.setText(
                    "START STOPWATCH"
            );

        } else {

            startGoalButton.setText(
                    "START"
            );
        }
    }

    // =========================================================
    // UPDATE PROGRESS
    // =========================================================

    private void showProgressDialog() {

        if (goalsList.isEmpty()) {
            return;
        }

        GoalProgressManager.Goal goal =
                goalsList.get(
                        activeGoalPosition
                );

        if (goal.isCompleted()) {

            Toast.makeText(
                    GoalsActivity.this,
                    "This goal is already complete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        final EditText input =
                new EditText(
                        GoalsActivity.this
                );

        input.setSingleLine(true);

        input.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        if (isRunningGoal(goal)) {

            input.setHint(
                    "Enter total distance completed in km"
            );

        } else if (
                isWeightLossGoal(goal)
        ) {

            input.setHint(
                    "Enter weight lost in grams"
            );

        } else if (
                isTimeGoal(goal)
        ) {

            input.setHint(
                    "Enter total minutes completed"
            );

        } else {

            input.setHint(
                    "Enter progress percentage"
            );
        }

        new AlertDialog.Builder(
                GoalsActivity.this
        )
                .setTitle(
                        "Update Progress"
                )
                .setMessage(
                        getProgressDialogMessage(
                                goal
                        )
                )
                .setView(input)
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String value =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (value.isEmpty()) {

                                Toast.makeText(
                                        GoalsActivity.this,
                                        "Enter a progress value",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            try {

                                double enteredValue =
                                        Double.parseDouble(
                                                value
                                        );

                                if (enteredValue < 0) {

                                    Toast.makeText(
                                            GoalsActivity.this,
                                            "Progress cannot be negative",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                double completedValue =
                                        convertInputToCompletedValue(
                                                goal,
                                                enteredValue
                                        );

                                goalManager.setProgress(
                                        activeGoalPosition,
                                        completedValue
                                );

                                refreshGoals();

                                if (
                                        goalsList
                                                .get(
                                                        activeGoalPosition
                                                )
                                                .isCompleted()
                                ) {

                                    Toast.makeText(
                                            GoalsActivity.this,
                                            "Goal completed",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            GoalsActivity.this,
                                            "Progress updated",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                            } catch (
                                    NumberFormatException e
                            ) {

                                Toast.makeText(
                                        GoalsActivity.this,
                                        "Enter a valid number",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =========================================================
    // INPUT → COMPLETED VALUE
    // =========================================================

    private double convertInputToCompletedValue(
            GoalProgressManager.Goal goal,
            double enteredValue
    ) {

        if (goal.getType().equals(
                GoalProgressManager.TYPE_WEIGHT_LOSS
        )) {

            return Math.min(
                    goal.getCompleted() + enteredValue,
                    goal.getTarget()
            );
        }

        if (goal.getType().equals(
                GoalProgressManager.TYPE_CUSTOM
        )) {

            return (
                    enteredValue
                            / 100.0
            )
                    *
                    goal.getTarget();
        }

        return Math.min(
                enteredValue,
                goal.getTarget()
        );
    }

    // =========================================================
    // TIME GOAL
    // =========================================================

    private void startTimeGoal() {

        if (goalsList.isEmpty()) {
            return;
        }

        GoalProgressManager.Goal goal =
                goalsList.get(
                        activeGoalPosition
                );

        if (!isTimeGoal(goal)) {

            Toast.makeText(
                    GoalsActivity.this,
                    "Use UPDATE PROGRESS for this goal",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        showStopwatch(goal);
    }

    // =========================================================
    // STOPWATCH
    // =========================================================

    private void showStopwatch(
            GoalProgressManager.Goal goal
    ) {

        View stopwatchView =
                getLayoutInflater().inflate(
                        R.layout.stopwatch_layout,
                        null
                );

        final Chronometer chronometer =
                stopwatchView.findViewById(
                        R.id.chronometer
                );

        chronometer.setBase(
                SystemClock.elapsedRealtime()
        );

        chronometer.start();

        AlertDialog dialog =
                new AlertDialog.Builder(
                        GoalsActivity.this
                )
                        .setTitle(
                                goal.getName()
                        )
                        .setView(
                                stopwatchView
                        )
                        .setPositiveButton(
                                "Stop",
                                null
                        )
                        .setNegativeButton(
                                "Reset",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button stopButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    Button resetButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_NEGATIVE
                            );

                    stopButton.setOnClickListener(
                            v -> {

                                long elapsedMillis =
                                        SystemClock
                                                .elapsedRealtime()
                                                -
                                                chronometer
                                                        .getBase();

                                double minutes =
                                        elapsedMillis
                                                /
                                                60000.0;

                                updateProgressFromStopwatch(
                                        goal,
                                        minutes
                                );

                                chronometer.stop();

                                dialog.dismiss();
                            }
                    );

                    resetButton.setOnClickListener(
                            v -> {

                                chronometer.setBase(
                                        SystemClock
                                                .elapsedRealtime()
                                );

                                chronometer.start();

                                Toast.makeText(
                                        GoalsActivity.this,
                                        "Stopwatch reset",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    );
                }
        );

        dialog.show();
    }

    // =========================================================
    // UPDATE FROM STOPWATCH
    // =========================================================

    private void updateProgressFromStopwatch(
            GoalProgressManager.Goal goal,
            double minutes
    ) {

        if (minutes <= 0) {

            Toast.makeText(
                    GoalsActivity.this,
                    "No time recorded",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (
                goal.getType().equals(
                        GoalProgressManager.TYPE_JOGGING
                )
        ) {

            goalManager.addJoggingMinutes(
                    minutes
            );

        } else if (
                goal.getType().equals(
                        GoalProgressManager.TYPE_MEDITATION
                )
        ) {

            goalManager.addMeditationMinutes(
                    minutes
            );
        }

        refreshGoals();

        GoalProgressManager.Goal updatedGoal =
                goalsList.get(
                        activeGoalPosition
                );

        if (updatedGoal.isCompleted()) {

            Toast.makeText(
                    GoalsActivity.this,
                    "Goal completed",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    GoalsActivity.this,
                    String.format(
                            Locale.getDefault(),
                            "Session: %.1f min added",
                            minutes
                    ),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // ADD GOAL
    // =========================================================

    private void showAddGoalDialog() {

        final EditText inputGoal =
                new EditText(
                        GoalsActivity.this
                );

        inputGoal.setSingleLine(true);

        inputGoal.setHint(
                "Enter your new goal"
        );

        new AlertDialog.Builder(
                GoalsActivity.this
        )
                .setTitle(
                        "Add New Goal"
                )
                .setMessage(
                        "Custom goals use percentage-based progress."
                )
                .setView(inputGoal)
                .setPositiveButton(
                        "Add",
                        (dialog, which) -> {

                            String newGoal =
                                    inputGoal.getText()
                                            .toString()
                                            .trim();

                            if (newGoal.isEmpty()) {

                                Toast.makeText(
                                        GoalsActivity.this,
                                        "Goal cannot be empty",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            goalManager.addGoal(
                                    newGoal
                            );

                            refreshGoals();

                            Toast.makeText(
                                    GoalsActivity.this,
                                    "Goal added",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =========================================================
    // GOAL DIALOG
    // =========================================================

    private void showGoalDialog(
            int position
    ) {

        GoalProgressManager.Goal goal =
                goalsList.get(position);

        new AlertDialog.Builder(
                GoalsActivity.this
        )
                .setTitle(
                        "Goal Selected"
                )
                .setMessage(
                        goal.getName()
                                + "\n\n"
                                + getProgressValueText(
                                goal
                        )
                                + "\n"
                                + formatProgress(
                                goal.getProgressPercentage()
                        )
                )
                .setPositiveButton(
                        "Update Progress",
                        (dialog, which) -> {

                            activeGoalPosition =
                                    position;

                            showProgressDialog();
                        }
                )
                .setNegativeButton(
                        "Delete",
                        (dialog, which) -> {

                            goalManager.deleteGoal(
                                    position
                            );

                            refreshGoals();

                            Toast.makeText(
                                    GoalsActivity.this,
                                    "Goal deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .setNeutralButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =========================================================
    // REFRESH
    // =========================================================

    private void refreshGoals() {

        loadGoals();

        setupGoalsList();

        updateGoalCount();

        updateActiveGoal();
    }

    // =========================================================
    // GOAL COUNT
    // =========================================================

    private void updateGoalCount() {

        int count =
                goalsList.size();

        goalsCount.setText(
                count
                        +
                        (
                                count == 1
                                        ? " GOAL"
                                        : " GOALS"
                        )
        );
    }

    // =========================================================
    // GOAL IDENTIFICATION
    // =========================================================

    private boolean isRunningGoal(
            GoalProgressManager.Goal goal
    ) {

        return GoalProgressManager.TYPE_RUNNING
                .equals(
                        goal.getType()
                );
    }

    private boolean isWeightLossGoal(
            GoalProgressManager.Goal goal
    ) {

        return GoalProgressManager.TYPE_WEIGHT_LOSS
                .equals(
                        goal.getType()
                );
    }

    private boolean isTimeGoal(
            GoalProgressManager.Goal goal
    ) {

        return GoalProgressManager.TYPE_JOGGING
                .equals(
                        goal.getType()
                )
                ||
                GoalProgressManager.TYPE_MEDITATION
                        .equals(
                                goal.getType()
                        );
    }

    // =========================================================
    // DESCRIPTIONS
    // =========================================================

    private String getGoalDescription(
            GoalProgressManager.Goal goal
    ) {

        switch (goal.getType()) {

            case GoalProgressManager.TYPE_RUNNING:

                return "Build your running distance and improve endurance.";

            case GoalProgressManager.TYPE_WEIGHT_LOSS:

                return "Work toward your target through consistent healthy habits.";

            case GoalProgressManager.TYPE_JOGGING:

                return "Improve cardiovascular fitness with regular jogging.";

            case GoalProgressManager.TYPE_MEDITATION:

                return "Build a daily mindfulness and recovery routine.";

            default:

                return "Stay consistent and make steady progress toward your goal.";
        }
    }

    // =========================================================
    // PROGRESS DISPLAY
    // =========================================================

    private String getProgressValueText(
            GoalProgressManager.Goal goal
    ) {

        double completed =
                goal.getCompleted();

        double target =
                goal.getTarget();

        switch (goal.getType()) {

            case GoalProgressManager.TYPE_RUNNING:

                return String.format(
                        Locale.getDefault(),
                        "%.2f / %.2f km",
                        completed,
                        target
                );

            case GoalProgressManager.TYPE_WEIGHT_LOSS:

                return String.format(
                        Locale.getDefault(),
                        "%.0f / %.0f g",
                        completed,
                        target
                );

            case GoalProgressManager.TYPE_JOGGING:

            case GoalProgressManager.TYPE_MEDITATION:

                return String.format(
                        Locale.getDefault(),
                        "%.1f / %.1f min",
                        completed,
                        target
                );

            default:

                return String.format(
                        Locale.getDefault(),
                        "%.0f%% complete",
                        goal.getProgressPercentage()
                );
        }
    }

    // =========================================================
    // PROGRESS DIALOG MESSAGE
    // =========================================================

    private String getProgressDialogMessage(
            GoalProgressManager.Goal goal
    ) {

        switch (goal.getType()) {

            case GoalProgressManager.TYPE_RUNNING:

                return "Enter the total distance you have completed toward "
                        + formatTarget(goal)
                        + ".";

            case GoalProgressManager.TYPE_WEIGHT_LOSS:

                return "Enter the amount of weight you have lost toward "
                        + formatTarget(goal)
                        + ".";

            case GoalProgressManager.TYPE_JOGGING:

                return "Enter the total jogging time completed toward "
                        + formatTarget(goal)
                        + ".";

            case GoalProgressManager.TYPE_MEDITATION:

                return "Enter the total meditation time completed toward "
                        + formatTarget(goal)
                        + ".";

            default:

                return "Enter your current progress as a percentage from 0 to 100.";
        }
    }

    // =========================================================
    // FORMAT TARGET
    // =========================================================

    private String formatTarget(
            GoalProgressManager.Goal goal
    ) {

        switch (goal.getType()) {

            case GoalProgressManager.TYPE_RUNNING:

                return String.format(
                        Locale.getDefault(),
                        "%.2f km",
                        goal.getTarget()
                );

            case GoalProgressManager.TYPE_WEIGHT_LOSS:

                return String.format(
                        Locale.getDefault(),
                        "%.0f g",
                        goal.getTarget()
                );

            case GoalProgressManager.TYPE_JOGGING:

            case GoalProgressManager.TYPE_MEDITATION:

                return String.format(
                        Locale.getDefault(),
                        "%.0f minutes",
                        goal.getTarget()
                );

            default:

                return "100%";
        }
    }

    // =========================================================
    // FORMAT PROGRESS
    // =========================================================

    private String formatProgress(
            double progress
    ) {

        return String.format(
                Locale.getDefault(),
                "%.0f%%",
                Math.max(
                        0,
                        Math.min(
                                100,
                                progress
                        )
                )
        );
    }

    // =========================================================
    // LIFECYCLE
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (goalManager != null) {

            refreshGoals();
        }
    }
}