package com.example.basic;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.widget.NumberPicker;

public class WorkoutActivity extends AppCompatActivity {
    private static final long EXERCISE_DURATION_MS = 30_000L;
    private static final long REST_DURATION_MS = 10_000L;
    private TextView totalWorkoutsText;
    private TextView workoutStreakText;
    private TextView lastWorkoutText;
    private Button fullBodyWorkoutButton;
    private Button coreWorkoutButton;
    private Button upperBodyWorkoutButton;
    private Button cardioWorkoutButton;
    private Button legsWorkoutButton;
    private Button stretchingWorkoutButton;
    private Button yogaWorkoutButton;
    private Button hiitWorkoutButton;
    private Button featuredWorkoutButton;
    private View customWorkoutCard;
    private CountDownTimer countDownTimer;
    private SharedPreferences workoutPreferences;
    private GoalProgressManager goalProgressManager;
    private String selectedWorkoutType = "";
    private boolean isTimerRunning = false;
    private boolean sessionRecorded = false;
    private AlertDialog sessionDialog;
    private TextView sessionExerciseName;
    private TextView sessionInstructions;
    private TextView sessionTimerText;
    private TextView sessionProgressText;
    private TextView sessionPhaseText;
    private VideoView sessionVideo;
    private Button pauseResumeButton;
    private Button skipButton;
    private WorkoutRoutine activeRoutine;
    private int exerciseIndex = 0;
    private int completedExercises = 0;
    private long phaseTimeLeftMs = EXERCISE_DURATION_MS;
    private long phaseStartedAtMs = 0L;
    private long activePhaseElapsedMs = 0L;
    private long completedWorkMs = 0L;
    private boolean resting = false;
    private boolean paused = false;
    private boolean sessionFinished = false;
    private final List<WorkoutExercise> exerciseLibrary =
            new ArrayList<>();

    private static class WorkoutExercise {
        final String name;
        final String instructions;
        final String category;
        final int durationSeconds;
        final int restSeconds;
        final String difficulty;
        final String equipment;

        WorkoutExercise(
                String name,
                String instructions,
                String category
        ) {
            this(
                    name,
                    instructions,
                    category,
                    30,
                    10,
                    "Beginner",
                    "No equipment"
            );
        }

        WorkoutExercise(
                String name,
                String instructions,
                String category,
                int durationSeconds,
                int restSeconds,
                String difficulty,
                String equipment
        ) {
            this.name = name;
            this.instructions = instructions;
            this.category = category;
            this.durationSeconds = durationSeconds;
            this.restSeconds = restSeconds;
            this.difficulty = difficulty;
            this.equipment = equipment;
        }
    }

    private static class WorkoutRoutine {
        final String name;
        final String difficulty;
        final String equipment;
        final List<WorkoutExercise> exercises;

        WorkoutRoutine(
                String name,
                String difficulty,
                String equipment,
                List<WorkoutExercise> exercises
        ) {
            this.name = name;
            this.difficulty = difficulty;
            this.equipment = equipment;
            this.exercises = new ArrayList<>(exercises);
        }

        long getTotalDurationMs() {
            long total = 0L;

            for (int i = 0; i < exercises.size(); i++) {
                WorkoutExercise exercise = exercises.get(i);

                total += exercise.durationSeconds * 1000L;

                if (i < exercises.size() - 1) {
                    total += exercise.restSeconds * 1000L;
                }
            }

            return total;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        bindViews();
        workoutPreferences = getSharedPreferences(
                "WorkoutData",
                MODE_PRIVATE
        );
        goalProgressManager = new GoalProgressManager(this);
        // Intentionally do not clear WorkoutData here.
        // Existing workout totals and streaks must be preserved.
        buildExerciseLibrary();
        setupWorkoutCategoryButtons();
        setupFeaturedWorkout();
        setupCustomWorkoutEntry();
        setupWorkoutHistoryEntry();
        refreshWorkoutStatistics();
        String incomingWorkout = getIntent().getStringExtra("WORKOUT_TYPE");
        if (incomingWorkout != null &&
                !incomingWorkout.trim().isEmpty()) {
        }
    }
    private void bindViews() {
        fullBodyWorkoutButton =
                findViewById(R.id.fullBodyWorkoutButton);
        coreWorkoutButton =
                findViewById(R.id.coreWorkoutButton);
        upperBodyWorkoutButton =
                findViewById(R.id.upperBodyWorkoutButton);
        cardioWorkoutButton =
                findViewById(R.id.cardioWorkoutButton);
        legsWorkoutButton =
                findViewById(R.id.legsWorkoutButton);
        stretchingWorkoutButton =
                findViewById(R.id.stretchingWorkoutButton);
        yogaWorkoutButton =
                findViewById(R.id.yogaWorkoutButton);
        hiitWorkoutButton =
                findViewById(R.id.hiitWorkoutButton);
        featuredWorkoutButton =
                findViewById(R.id.featuredWorkoutButton);
        totalWorkoutsText =
                findViewById(R.id.totalWorkoutsText);
        workoutStreakText =
                findViewById(R.id.workoutStreakText);
        lastWorkoutText =
                findViewById(R.id.lastWorkoutText);
        customWorkoutCard = findViewById(R.id.customWorkoutCard);
    }

    private void buildExerciseLibrary() {
        exerciseLibrary.clear();

        exerciseLibrary.add(new WorkoutExercise(
                "Jumping Jacks",
                "Stand upright. Jump your feet apart while raising your arms, then return to the starting position.",
                "Cardio"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Squats",
                "Keep your feet shoulder-width apart. Push your hips back, bend your knees, and stand up with control.",
                "Legs"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Push-ups",
                "Keep your body in a straight line. Lower your chest with control and push back up. Use your knees if needed.",
                "Upper Body"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "High Knees",
                "Stand tall and alternate lifting your knees toward hip height. Move at a pace you can maintain safely.",
                "Cardio"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Plank",
                "Keep your elbows under your shoulders and your body straight. Avoid letting your hips sag.",
                "Core"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Bicycle Crunches",
                "Lie on your back and alternate bringing your elbow toward the opposite knee without pulling your neck.",
                "Core"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Mountain Climbers",
                "Start in a high plank and alternate bringing each knee toward your chest while keeping your torso stable.",
                "Cardio"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Reverse Lunges",
                "Step one foot backward, lower both knees with control, then return to standing. Alternate legs.",
                "Legs"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Calf Raises",
                "Stand tall, rise onto the balls of your feet, pause briefly, and lower your heels slowly.",
                "Legs"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Arm Circles",
                "Extend your arms to the sides and make small controlled circles. Reverse direction halfway through.",
                "Upper Body"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Shoulder Taps",
                "Start in a high plank. Tap the opposite shoulder with one hand while keeping your hips steady.",
                "Upper Body"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Hamstring Stretch",
                "Extend one leg comfortably and hinge forward from your hips until you feel a gentle stretch.",
                "Stretching"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Quad Stretch",
                "Stand with support if needed. Bring one heel toward your glutes and keep your knees close together.",
                "Stretching"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Shoulder Stretch",
                "Bring one arm across your chest and gently support it with the opposite arm.",
                "Stretching"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Cat-Cow",
                "On hands and knees, gently alternate between arching and rounding your back while breathing steadily.",
                "Yoga"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Child's Pose",
                "Kneel, sit your hips toward your heels, and reach your arms forward as far as comfortable.",
                "Yoga"
        ));

        exerciseLibrary.add(new WorkoutExercise(
                "Cobra Pose",
                "Lie on your stomach and gently lift your chest using your back muscles. Avoid forcing the lower back.",
                "Yoga"
        ));
    }

    private void setupWorkoutCategoryButtons() {
        fullBodyWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Full Body Workout"))
        );

        coreWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Core Workout"))
        );

        upperBodyWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Upper Body Workout"))
        );

        cardioWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Cardio Workout"))
        );

        legsWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Legs Workout"))
        );

        stretchingWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Stretching"))
        );

        yogaWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("Yoga"))
        );

        hiitWorkoutButton.setOnClickListener(
                v -> openRoutinePreview(createRoutine("HIIT Workout"))
        );

    }

    private void setupFeaturedWorkout() {
        featuredWorkoutButton.setOnClickListener(v ->
                openRoutinePreview(createRoutine("Full Body Workout"))
        );
    }

    private void setupCustomWorkoutEntry() {
        customWorkoutCard.setOnClickListener(v -> openCustomBuilder());
    }

    private void setupWorkoutHistoryEntry() {
        View historyCard = findViewById(R.id.workoutHistoryCard);

        if (historyCard != null) {
            historyCard.setOnClickListener(v -> {
                Intent intent = new Intent(
                        WorkoutActivity.this,
                        WorkoutHistoryActivity.class
                );
                startActivity(intent);
            });
        }
    }

    private WorkoutRoutine createRoutine(String name) {
        List<WorkoutExercise> exercises = new ArrayList<>();
        String difficulty = "Beginner";
        String equipment = "No equipment";

        switch (name) {
            case "Core Workout":
                exercises.add(findExercise("Plank"));
                exercises.add(findExercise("Bicycle Crunches"));
                exercises.add(findExercise("Mountain Climbers"));
                break;

            case "Upper Body Workout":
                exercises.add(findExercise("Push-ups"));
                exercises.add(findExercise("Arm Circles"));
                exercises.add(findExercise("Shoulder Taps"));
                difficulty = "Beginner";
                break;

            case "Cardio Workout":
                exercises.add(findExercise("High Knees"));
                exercises.add(findExercise("Jumping Jacks"));
                exercises.add(findExercise("Mountain Climbers"));
                break;

            case "Legs Workout":
                exercises.add(findExercise("Squats"));
                exercises.add(findExercise("Reverse Lunges"));
                exercises.add(findExercise("Calf Raises"));
                break;

            case "Stretching":
                exercises.add(findExercise("Hamstring Stretch"));
                exercises.add(findExercise("Quad Stretch"));
                exercises.add(findExercise("Shoulder Stretch"));
                break;

            case "Yoga":
                exercises.add(findExercise("Cat-Cow"));
                exercises.add(findExercise("Child's Pose"));
                exercises.add(findExercise("Cobra Pose"));
                break;

            case "HIIT Workout":
                exercises.add(findExercise("High Knees"));
                exercises.add(findExercise("Squats"));
                exercises.add(findExercise("Mountain Climbers"));
                exercises.add(findExercise("Plank"));
                difficulty = "Intermediate";
                break;

            case "Full Body Workout":
            default:
                exercises.add(findExercise("Jumping Jacks"));
                exercises.add(findExercise("Squats"));
                exercises.add(findExercise("Push-ups"));
                exercises.add(findExercise("High Knees"));
                break;
        }

        if (exercises.contains(null)) {
            exercises.removeIf(exercise -> exercise == null);
        }

        return new WorkoutRoutine(
                name,
                difficulty,
                equipment,
                exercises
        );
    }

    private WorkoutExercise findExercise(String name) {
        for (WorkoutExercise exercise : exerciseLibrary) {
            if (exercise.name.equals(name)) {
                return exercise;
            }
        }
        return null;
    }

    private void openRoutinePreview(WorkoutRoutine routine) {
        if (routine == null || routine.exercises.isEmpty()) {
            Toast.makeText(
                    this,
                    "No exercises are available for this routine.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        selectedWorkoutType = routine.name;

        LinearLayout content = createVerticalLayout();

        TextView title = createText(
                routine.name,
                24,
                true
        );
        content.addView(title);

        TextView metadata = createText(
                routine.difficulty + "  •  " +
                        routine.equipment + "  •  " +
                        formatDuration(routine.getTotalDurationMs()),
                14,
                false
        );
        metadata.setPadding(0, dp(8), 0, dp(16));
        content.addView(metadata);

        TextView description = createText(
                "Complete each exercise in order. Rest periods are included between exercises.",
                15,
                false
        );
        content.addView(description);

        TextView exercisesHeading = createText(
                "Exercises",
                19,
                true
        );
        exercisesHeading.setPadding(0, dp(20), 0, dp(8));
        content.addView(exercisesHeading);

        for (int i = 0; i < routine.exercises.size(); i++) {
            WorkoutExercise exercise = routine.exercises.get(i);

            TextView item = createText(
                    (i + 1) + ". " + exercise.name +
                            "\n" + exercise.durationSeconds +
                            " seconds • " + exercise.instructions,
                    14,
                    false
            );

            item.setPadding(0, dp(10), 0, dp(10));
            content.addView(item);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(wrapInScrollView(content))
                .setNegativeButton("Back", null)
                .setPositiveButton("Start workout", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            dialog.dismiss();
                            startGuidedSession(routine);
                        })
        );

        dialog.show();
    }

    private void openCustomBuilder() {
        String[] names = new String[]{
                "Jumping Jacks",
                "Squats",
                "Push-ups",
                "High Knees",
                "Plank",
                "Bicycle Crunches",
                "Mountain Climbers",
                "Reverse Lunges",
                "Calf Raises",
                "Arm Circles",
                "Shoulder Taps",
                "Hamstring Stretch",
                "Quad Stretch",
                "Shoulder Stretch",
                "Cat-Cow",
                "Child's Pose",
                "Cobra Pose"
        };

        boolean[] checked = new boolean[names.length];

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create your workout")
                .setMultiChoiceItems(
                        names,
                        checked,
                        (d, which, isChecked) -> checked[which] = isChecked
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Configure exercises", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            List<WorkoutExercise> chosen =
                                    new ArrayList<>();

                            for (int i = 0; i < names.length; i++) {
                                if (checked[i]) {
                                    WorkoutExercise exercise =
                                            findExercise(names[i]);

                                    if (exercise != null) {
                                        chosen.add(exercise);
                                    }
                                }
                            }

                            if (chosen.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "Select at least one exercise.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            dialog.dismiss();
                            configureCustomExercise(chosen, 0);
                        })
        );

        dialog.show();
    }

    private void configureCustomExercise(
            List<WorkoutExercise> chosen,
            int index
    ) {
        if (index >= chosen.size()) {
            WorkoutRoutine customRoutine = new WorkoutRoutine(
                    "Custom Workout",
                    "Custom",
                    "No equipment",
                    chosen
            );

            openRoutinePreview(customRoutine);
            return;
        }

        WorkoutExercise exercise = chosen.get(index);

        int[] durations = {
                15, 30, 45, 60, 90, 120, 180, 240, 300
        };

        int[] rests = {
                0, 5, 10, 15, 20, 30, 45, 60
        };

        String[] durationLabels = {
                "15 sec", "30 sec", "45 sec", "1 min",
                "1 min 30 sec", "2 min", "3 min",
                "4 min", "5 min"
        };

        String[] restLabels = {
                "0 sec", "5 sec", "10 sec", "15 sec",
                "20 sec", "30 sec", "45 sec", "1 min"
        };

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(24), dp(12), dp(24), dp(8));

        TextView durationLabel = new TextView(this);
        durationLabel.setText("Exercise duration");
        durationLabel.setTextSize(16);
        layout.addView(durationLabel);

        NumberPicker durationPicker = new NumberPicker(this);
        durationPicker.setMinValue(0);
        durationPicker.setMaxValue(durations.length - 1);
        durationPicker.setDisplayedValues(durationLabels);
        durationPicker.setWrapSelectorWheel(false);

        int durationIndex = 1;
        for (int i = 0; i < durations.length; i++) {
            if (durations[i] == exercise.durationSeconds) {
                durationIndex = i;
                break;
            }
        }
        durationPicker.setValue(durationIndex);
        layout.addView(durationPicker);

        TextView restLabel = new TextView(this);
        restLabel.setText("Rest after this exercise");
        restLabel.setTextSize(16);

        LinearLayout.LayoutParams restLabelParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        restLabelParams.topMargin = dp(12);
        layout.addView(restLabel, restLabelParams);

        NumberPicker restPicker = new NumberPicker(this);
        restPicker.setMinValue(0);
        restPicker.setMaxValue(rests.length - 1);
        restPicker.setDisplayedValues(restLabels);
        restPicker.setWrapSelectorWheel(false);

        int restIndex = 2;
        for (int i = 0; i < rests.length; i++) {
            if (rests[i] == exercise.restSeconds) {
                restIndex = i;
                break;
            }
        }
        restPicker.setValue(restIndex);
        layout.addView(restPicker);

        new AlertDialog.Builder(this)
                .setTitle(
                        "Configure " + exercise.name
                                + " (" + (index + 1)
                                + " of " + chosen.size() + ")"
                )
                .setView(layout)
                .setNegativeButton("Cancel", (d, which) -> {
                    // Cancel configuration without starting the workout.
                })
                .setPositiveButton("Next", (d, which) -> {
                    WorkoutExercise configuredExercise =
                            new WorkoutExercise(
                                    exercise.name,
                                    exercise.instructions,
                                    exercise.category,
                                    durations[durationPicker.getValue()],
                                    rests[restPicker.getValue()],
                                    exercise.difficulty,
                                    exercise.equipment
                            );

                    chosen.set(index, configuredExercise);

                    configureCustomExercise(chosen, index + 1);
                })
                .show();
    }

    private void startGuidedSession(WorkoutRoutine routine) {
        if (routine == null || routine.exercises.isEmpty()) {
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        activeRoutine = routine;
        exerciseIndex = 0;
        completedExercises = 0;
        completedWorkMs = 0L;
        phaseTimeLeftMs =
                routine.exercises.get(0).durationSeconds * 1000L;
        activePhaseElapsedMs = 0L;
        resting = false;
        paused = false;
        sessionFinished = false;
        sessionRecorded = false;
        isTimerRunning = true;

        LinearLayout root = createVerticalLayout();
        root.setPadding(dp(20), dp(18), dp(20), dp(18));

        sessionProgressText = createText("", 14, true);
        root.addView(sessionProgressText);

        sessionVideo = new VideoView(this);
        sessionVideo.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(180)
        ));

        /*
         * dashboard.mp4 shows barbell weightlifting.
         * Only show it for the upper-body routine as a general
         * strength visual, not as an exact exercise tutorial.
         */
        if ("Upper Body Workout".equals(routine.name)) {
            root.addView(sessionVideo);

            TextView mediaNote = createText(
                    "General strength visual",
                    12,
                    false
            );
            root.addView(mediaNote);

            try {
                Uri videoUri = Uri.parse(
                        "android.resource://" +
                                getPackageName() + "/" +
                                R.raw.dashboard
                );

                sessionVideo.setVideoURI(videoUri);
                sessionVideo.setOnPreparedListener(player -> {
                    player.setLooping(true);
                    sessionVideo.start();
                });
            } catch (Exception ignored) {
                sessionVideo.setVisibility(View.GONE);
            }
        }

        sessionPhaseText = createText("", 16, true);
        sessionPhaseText.setPadding(0, dp(12), 0, dp(4));
        root.addView(sessionPhaseText);

        sessionExerciseName = createText("", 25, true);
        sessionExerciseName.setPadding(0, dp(6), 0, dp(8));
        root.addView(sessionExerciseName);

        sessionInstructions = createText("", 15, false);
        root.addView(sessionInstructions);

        sessionTimerText = createText("", 36, true);
        sessionTimerText.setGravity(Gravity.CENTER);
        sessionTimerText.setPadding(0, dp(20), 0, dp(20));
        root.addView(sessionTimerText);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        pauseResumeButton = new Button(this);
        pauseResumeButton.setText("Pause");

        skipButton = new Button(this);
        skipButton.setText("Skip");

        buttons.addView(
                pauseResumeButton,
                new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1)
        );

        buttons.addView(
                skipButton,
                new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1)
        );

        root.addView(buttons);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Guided workout")
                .setView(wrapInScrollView(root))
                .setNegativeButton("End workout", null)
                .create();

        sessionDialog = dialog;

        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setOnClickListener(v -> confirmEndSession());
        });

        dialog.setOnDismissListener(ignored -> {
            stopPhaseTimer();
            if (sessionVideo != null) {
                sessionVideo.stopPlayback();
            }

            isTimerRunning = false;
            sessionDialog = null;
        });

        pauseResumeButton.setOnClickListener(v -> togglePause());
        skipButton.setOnClickListener(v -> skipCurrentPhase());

        dialog.show();
        showExercisePhase();
    }

    private void showExercisePhase() {
        if (activeRoutine == null ||
                exerciseIndex >= activeRoutine.exercises.size()) {
            finishGuidedSession();
            return;
        }

        resting = false;
        paused = false;
        activePhaseElapsedMs = 0L;

        WorkoutExercise exercise =
                activeRoutine.exercises.get(exerciseIndex);

        phaseTimeLeftMs = exercise.durationSeconds * 1000L;

        sessionProgressText.setText(
                "Exercise " + (exerciseIndex + 1) +
                        " of " + activeRoutine.exercises.size() +
                        "  •  Completed " + completedExercises
        );

        sessionPhaseText.setText("WORK");
        sessionExerciseName.setText(exercise.name);
        sessionInstructions.setText(exercise.instructions);
        pauseResumeButton.setText("Pause");

        startPhaseTimer();
    }

    private void showRestPhase() {
        resting = true;
        paused = false;
        activePhaseElapsedMs = 0L;

        WorkoutExercise completedExercise =
                activeRoutine.exercises.get(exerciseIndex);

        phaseTimeLeftMs = completedExercise.restSeconds * 1000L;

        sessionProgressText.setText(
                "Exercise " + completedExercises +
                        " completed • Next " +
                        (exerciseIndex + 2) + " of " +
                        activeRoutine.exercises.size()
        );

        sessionPhaseText.setText("REST");
        sessionExerciseName.setText("Take a short rest");
        sessionInstructions.setText(
                "Breathe steadily, relax, and prepare for the next exercise."
        );

        pauseResumeButton.setText("Pause");
        startPhaseTimer();
    }

    private void startPhaseTimer() {
        stopPhaseTimer();

        if (sessionFinished || paused || sessionDialog == null) {
            return;
        }

        phaseStartedAtMs = System.currentTimeMillis();

        countDownTimer = new CountDownTimer(
                phaseTimeLeftMs,
                250
        ) {
            @Override
            public void onTick(long millisUntilFinished) {
                phaseTimeLeftMs = millisUntilFinished;
                updateSessionTimer();
            }

            @Override
            public void onFinish() {
                phaseTimeLeftMs = 0L;
                updateSessionTimer();

                if (sessionFinished || sessionDialog == null) {
                    return;
                }

                if (!resting) {
                    WorkoutExercise exercise =
                            activeRoutine.exercises.get(exerciseIndex);

                    completedWorkMs +=
                            exercise.durationSeconds * 1000L;

                    completedExercises++;
                }

                advanceSession();
            }
        };

        countDownTimer.start();
        updateSessionTimer();
    }

    private void updateSessionTimer() {
        if (sessionTimerText == null) {
            return;
        }

        long seconds = Math.max(
                0L,
                (phaseTimeLeftMs + 999L) / 1000L
        );

        sessionTimerText.setText(String.format(
                Locale.getDefault(),
                "%02d:%02d",
                seconds / 60,
                seconds % 60
        ));
    }

    private void advanceSession() {
        if (resting) {
            exerciseIndex++;
            showExercisePhase();
            return;
        }

        if (exerciseIndex >= activeRoutine.exercises.size() - 1) {
            finishGuidedSession();
        } else {
            showRestPhase();
        }
    }

    private void togglePause() {
        if (sessionFinished || sessionDialog == null) {
            return;
        }

        if (!paused) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }

            paused = true;
            pauseResumeButton.setText("Resume");

            if (sessionVideo != null &&
                    sessionVideo.isPlaying()) {
                sessionVideo.pause();
            }

            sessionPhaseText.setText(
                    (resting ? "REST" : "WORK") + " • PAUSED"
            );
        } else {
            paused = false;
            pauseResumeButton.setText("Pause");

            if (sessionVideo != null &&
                    sessionVideo.getVisibility() == View.VISIBLE) {
                sessionVideo.start();
            }

            sessionPhaseText.setText(resting ? "REST" : "WORK");
            startPhaseTimer();
        }
    }

    private void skipCurrentPhase() {
        if (sessionFinished || sessionDialog == null || paused) {
            return;
        }

        stopPhaseTimer();

        if (!resting) {
            /*
             * Skipping an exercise does not count it as completed.
             * Its elapsed time is excluded from completed-work time.
             */
            exerciseIndex++;

            if (exerciseIndex >= activeRoutine.exercises.size()) {
                finishGuidedSession();
            } else {
                showExercisePhase();
            }
        } else {
            exerciseIndex++;
            showExercisePhase();
        }
    }

    private void confirmEndSession() {
        if (sessionDialog == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("End workout?")
                .setMessage(
                        "Your unfinished session will not count as a completed workout."
                )
                .setNegativeButton("Continue", (dialog, which) -> {
                    // Keep the current session open.
                })
                .setPositiveButton("End", (dialog, which) ->
                        endSessionEarly()
                )
                .show();
    }

    private void endSessionEarly() {
        if (sessionFinished) {
            return;
        }

        stopPhaseTimer();
        sessionFinished = true;
        isTimerRunning = false;

        /*
         * Only completed work is eligible for goal progress.
         * A partially completed routine does not increase the
         * completed-workout total or streak.
         */
        recordCompatibleGoalProgress(
                completedWorkMs / 60000.0,
                activeRoutine
        );

        if (sessionDialog != null) {
            sessionDialog.dismiss();
        }

        showSessionSummary(false);
    }

    private void finishGuidedSession() {
        if (sessionFinished) {
            return;
        }

        sessionFinished = true;
        isTimerRunning = false;

        if (!sessionRecorded && completedExercises > 0) {
            saveWorkoutHistory();
            sessionRecorded = true;

            recordWorkoutCompletion();

            recordCompatibleGoalProgress(
                    completedWorkMs / 60000.0,
                    activeRoutine
            );

            refreshWorkoutStatistics();
        }

        if (sessionDialog != null) {
            sessionDialog.dismiss();
        }

        showSessionSummary(true);
    }

    private void showSessionSummary(boolean completed) {
        String status = completed
                ? "Routine completed!"
                : "Workout ended early";

        long totalSeconds = completedWorkMs / 1000L;

        String message =
                status +
                        "\n\nExercises completed: " + completedExercises +
                        "\nActive exercise time: " +
                        formatDuration(completedWorkMs) +
                        "\n\n" +
                        (completed
                                ? "Your workout statistics have been updated."
                                : "Your completed-workout total and streak were not changed.");

        new AlertDialog.Builder(this)
                .setTitle("Workout summary")
                .setMessage(message)
                .setPositiveButton("Done", (dialog, which) -> {
                    setButtonsEnabled(true);
                    refreshWorkoutStatistics();
                })
                .setNeutralButton("Repeat", (dialog, which) -> {
                    if (activeRoutine != null) {
                        startGuidedSession(activeRoutine);
                    }
                })
                .show();
    }

    private void recordCompatibleGoalProgress(
            double completedMinutes,
            WorkoutRoutine routine
    ) {
        if (completedMinutes <= 0 || routine == null) {
            return;
        }

        String name = routine.name.toLowerCase(Locale.ROOT);

        if (name.contains("cardio") || name.contains("hiit")) {
            goalProgressManager.addJoggingMinutes(completedMinutes);
        } else if (name.contains("yoga") || name.contains("stretch")) {
            goalProgressManager.addMeditationMinutes(completedMinutes);
        }
    }

    private void stopPhaseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void saveWorkoutHistory() {
        if (activeRoutine == null) {
            return;
        }

        SharedPreferences historyPrefs =
                getSharedPreferences("WorkoutHistory", MODE_PRIVATE);

        String savedHistory =
                historyPrefs.getString("completed_workouts", "[]");

        try {
            JSONArray history = new JSONArray(savedHistory);

            JSONObject record = new JSONObject();
            long completedAt = System.currentTimeMillis();

            record.put("name", activeRoutine.name);
            record.put("timestamp", completedAt);
            record.put(
                    "date",
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                    ).format(new Date(completedAt))
            );
            record.put(
                    "duration",
                    formatDuration(completedWorkMs)
            );
            record.put("exercisesCompleted", completedExercises);
            record.put("totalExercises", activeRoutine.exercises.size());

            history.put(record);

            historyPrefs.edit()
                    .putString("completed_workouts", history.toString())
                    .apply();
            Toast.makeText(
                    this,
                    "Workout history saved: " + history.length(),
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            android.util.Log.e(
                    "WorkoutHistory",
                    "Could not save completed workout",
                    e
            );
        }
    }

    /*
     * The legacy timer remains available for compatibility with
     * the current XML until the guided session is fully tested.
     */
    private void recordWorkoutCompletion() {
        int totalWorkouts = workoutPreferences.getInt(
                "TOTAL_WORKOUTS",
                0
        );

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        String lastWorkoutDate = workoutPreferences.getString(
                "LAST_WORKOUT_DATE",
                ""
        );

        int streak = workoutPreferences.getInt(
                "WORKOUT_STREAK",
                0
        );

        if (lastWorkoutDate.isEmpty()) {
            streak = 1;
        } else if (!lastWorkoutDate.equals(today)) {
            try {
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        );

                dateFormat.setLenient(false);

                Date lastDate = dateFormat.parse(lastWorkoutDate);
                Date currentDate = dateFormat.parse(today);

                if (lastDate != null && currentDate != null) {
                    long difference =
                            currentDate.getTime() - lastDate.getTime();

                    long daysDifference = difference /
                            (1000L * 60L * 60L * 24L);

                    streak = daysDifference == 1
                            ? streak + 1
                            : 1;
                } else {
                    streak = 1;
                }
            } catch (Exception e) {
                streak = 1;
            }
        }

        totalWorkouts++;

        workoutPreferences.edit()
                .putInt("TOTAL_WORKOUTS", totalWorkouts)
                .putInt("WORKOUT_STREAK", streak)
                .putString("LAST_WORKOUT_DATE", today)
                .apply();
    }

    private void refreshWorkoutStatistics() {
        int totalWorkouts = workoutPreferences.getInt(
                "TOTAL_WORKOUTS",
                0
        );

        int streak = workoutPreferences.getInt(
                "WORKOUT_STREAK",
                0
        );

        String lastWorkoutDate = workoutPreferences.getString(
                "LAST_WORKOUT_DATE",
                ""
        );

        totalWorkoutsText.setText(String.valueOf(totalWorkouts));

        workoutStreakText.setText(
                streak + (streak == 1 ? " day" : " days")
        );

        lastWorkoutText.setText(
                lastWorkoutDate.isEmpty()
                        ? "Your latest workout will appear here."
                        : "Last workout: " + lastWorkoutDate
        );
    }

    private void setButtonsEnabled(boolean enabled) {
        fullBodyWorkoutButton.setEnabled(enabled);
        coreWorkoutButton.setEnabled(enabled);
        upperBodyWorkoutButton.setEnabled(enabled);
        cardioWorkoutButton.setEnabled(enabled);
        legsWorkoutButton.setEnabled(enabled);
        stretchingWorkoutButton.setEnabled(enabled);
        yogaWorkoutButton.setEnabled(enabled);
        hiitWorkoutButton.setEnabled(enabled);
        featuredWorkoutButton.setEnabled(enabled);
        customWorkoutCard.setEnabled(enabled);
    }

    private LinearLayout createVerticalLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(16));
        return layout;
    }

    private TextView createText(
            String text,
            int sizeSp,
            boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(sizeSp);
        view.setTextColor(0xFF172033);

        if (bold) {
            view.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        return view;
    }

    private ScrollView wrapInScrollView(View child) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(child);
        return scrollView;
    }

    private int dp(int value) {
        return (int) (
                value * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    private String formatDuration(long milliseconds) {
        long seconds = Math.max(0L, milliseconds / 1000L);

        if (seconds >= 60L) {
            return (seconds / 60L) + " min " +
                    (seconds % 60L) + " sec";
        }

        return seconds + " sec";
    }

    @Override
    protected void onDestroy() {
        stopPhaseTimer();

        if (sessionVideo != null) {
            sessionVideo.stopPlayback();
        }

        super.onDestroy();
    }
}