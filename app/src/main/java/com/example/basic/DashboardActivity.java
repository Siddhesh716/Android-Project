package com.example.basic;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    private TextView greetingText;
    private TextView dashboardText;
    private TextView dashboardSubtitle;
    // Activity tracker
    private RadioGroup activityPeriodGroup;
    private TextView activityTrackerSubtitle;
    private TextView periodWorkoutCount;
    private TextView periodActiveTime;
    private TextView activityChartTitle;
    private LinearLayout activityChartContainer;
    private TextView activityTrackerMessage;
    // Fitness milestones
    private TextView workoutMilestoneTitle;
    private TextView workoutMilestoneProgress;
    private ProgressBar workoutMilestoneBar;
    private TextView streakMilestoneTitle;
    private TextView streakMilestoneProgress;
    private ProgressBar streakMilestoneBar;
    // Quick Access
    private CardView profileCard;
    private CardView goalsCard;
    private CardView dietCard;
    private CardView quickWorkoutCard;
    private CardView bmiCard;
    private CardView quizCard;
    private SharedPreferences userPreferences;
    private SharedPreferences workoutPreferences;
    private SharedPreferences historyPreferences;
    private JSONArray workoutHistory = new JSONArray();
    private static final int PERIOD_WEEKLY = 0;
    private static final int PERIOD_MONTHLY = 1;

    private static final int WORKOUT_MILESTONE = 5;
    private static final int STREAK_MILESTONE = 3;

    private final SimpleDateFormat historyDateFormat =
            new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sessionPreferences =
                getSharedPreferences("LoginSession", MODE_PRIVATE);

        boolean isLoggedIn = sessionPreferences.getBoolean(
                "IS_LOGGED_IN",
                false
        );

        if (!isLoggedIn) {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupUserGreeting();
        setupQuickAccess();
        setupActivityPeriodSelector();
        refreshDashboardData();
    }

    private void initializeViews() {

        greetingText = findViewById(R.id.greetingText);
        dashboardText = findViewById(R.id.dashboardText);
        dashboardSubtitle = findViewById(R.id.dashboardSubtitle);
        activityPeriodGroup = findViewById(R.id.activityPeriodGroup);
        activityTrackerSubtitle =
                findViewById(R.id.activityTrackerSubtitle);
        periodWorkoutCount = findViewById(R.id.periodWorkoutCount);
        periodActiveTime = findViewById(R.id.periodActiveTime);
        activityChartTitle = findViewById(R.id.activityChartTitle);
        activityChartContainer =
                findViewById(R.id.activityChartContainer);
        activityTrackerMessage =
                findViewById(R.id.activityTrackerMessage);

        workoutMilestoneTitle =
                findViewById(R.id.workoutMilestoneTitle);
        workoutMilestoneProgress =
                findViewById(R.id.workoutMilestoneProgress);
        workoutMilestoneBar =
                findViewById(R.id.workoutMilestoneBar);

        streakMilestoneTitle =
                findViewById(R.id.streakMilestoneTitle);
        streakMilestoneProgress =
                findViewById(R.id.streakMilestoneProgress);
        streakMilestoneBar =
                findViewById(R.id.streakMilestoneBar);

        userPreferences = getSharedPreferences(
                "UserData",
                MODE_PRIVATE
        );

        workoutPreferences = getSharedPreferences(
                "WorkoutData",
                MODE_PRIVATE
        );

        historyPreferences = getSharedPreferences(
                "WorkoutHistory",
                MODE_PRIVATE
        );
    }

    private void setupUserGreeting() {

        String userName = userPreferences.getString(
                "USER_NAME",
                ""
        );

        greetingText.setText(getGreeting());

        dashboardText.setText(
                userName.trim().isEmpty() ? "Welcome" : userName
        );

        dashboardSubtitle.setText("Let's make today count.");
    }

    private String getGreeting() {

        int hour = Calendar.getInstance().get(
                Calendar.HOUR_OF_DAY
        );

        if (hour >= 5 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else if (hour >= 17 && hour < 22) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }

    private void setupQuickAccess() {

        profileCard = findViewById(R.id.profileCard);
        goalsCard = findViewById(R.id.goalsCard);
        dietCard = findViewById(R.id.dietCard);
        quickWorkoutCard = findViewById(R.id.quickWorkoutCard);
        bmiCard = findViewById(R.id.bmiCard);
        quizCard = findViewById(R.id.quizCard);

        profileCard.setOnClickListener(v ->
                startActivity(new Intent(
                        DashboardActivity.this,
                        ProfileActivity.class
                ))
        );

        goalsCard.setOnClickListener(v ->
                startActivity(new Intent(
                        DashboardActivity.this,
                        GoalsActivity.class
                ))
        );

        dietCard.setOnClickListener(v ->
                startActivity(new Intent(
                        DashboardActivity.this,
                        DietActivity.class
                ))
        );

        quickWorkoutCard.setOnClickListener(v ->
                startActivity(new Intent(
                        DashboardActivity.this,
                        WorkoutActivity.class
                ))
        );

        bmiCard.setOnClickListener(v ->
                startActivity(new Intent(
                        DashboardActivity.this,
                        BMI.class
                ))
        );

        quizCard.setOnClickListener(v ->
                startActivity(new Intent(
                        DashboardActivity.this,
                        QuizActivity.class
                ))
        );
    }

    private void setupActivityPeriodSelector() {

        activityPeriodGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    if (checkedId == R.id.weeklyActivityButton) {
                        updateActivityTracker(PERIOD_WEEKLY);
                    } else if (
                            checkedId == R.id.monthlyActivityButton
                    ) {
                        updateActivityTracker(PERIOD_MONTHLY);
                    }
                }
        );
    }

    private void refreshDashboardData() {

        loadWorkoutHistory();
        updateActivityTracker(getSelectedPeriod());
        updateFitnessMilestones();
    }

    private int getSelectedPeriod() {

        return activityPeriodGroup.getCheckedRadioButtonId()
                == R.id.monthlyActivityButton
                ? PERIOD_MONTHLY
                : PERIOD_WEEKLY;
    }

    private void loadWorkoutHistory() {

        String savedHistory = historyPreferences.getString(
                "completed_workouts",
                "[]"
        );

        try {
            workoutHistory = new JSONArray(savedHistory);
        } catch (Exception e) {
            workoutHistory = new JSONArray();
            android.util.Log.e(
                    "DashboardActivity",
                    "Unable to read workout history",
                    e
            );
        }
    }

    private String formatWorkoutDate(String date) {

        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            inputFormat.setLenient(false);

            Date parsedDate = inputFormat.parse(date);

            if (parsedDate == null) {
                return date;
            }

            return new SimpleDateFormat(
                    "dd MMM",
                    Locale.getDefault()
            ).format(parsedDate);

        } catch (Exception e) {
            return date;
        }
    }
    private void updateActivityTracker(int period) {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        if (period == PERIOD_WEEKLY) {

            start.set(
                    Calendar.DAY_OF_WEEK,
                    start.getFirstDayOfWeek()
            );

            setStartOfDay(start);

            end.setTime(start.getTime());
            end.add(Calendar.DAY_OF_YEAR, 7);

            activityTrackerSubtitle.setText(
                    "Your activity this week"
            );

            activityChartTitle.setText("Workouts by day");

        } else {

            start.set(Calendar.DAY_OF_MONTH, 1);
            setStartOfDay(start);

            end.setTime(start.getTime());
            end.add(Calendar.MONTH, 1);

            activityTrackerSubtitle.setText(
                    "Your activity this month"
            );

            activityChartTitle.setText("Workouts by week");
        }

        long startMillis = start.getTimeInMillis();
        long endMillis = end.getTimeInMillis();

        int workoutCount = 0;
        long activeTimeMillis = 0L;

        int bucketCount = period == PERIOD_WEEKLY ? 7 : 5;
        int[] activityBuckets = new int[bucketCount];

        Calendar workoutDate = Calendar.getInstance();

        for (int i = 0; i < workoutHistory.length(); i++) {

            JSONObject record = workoutHistory.optJSONObject(i);

            if (record == null) {
                continue;
            }

            long timestamp = record.optLong("timestamp", -1L);

            if (timestamp < startMillis || timestamp >= endMillis) {
                continue;
            }

            workoutCount++;

            activeTimeMillis += parseDurationMillis(
                    record.optString("duration", "0 sec")
            );

            workoutDate.setTimeInMillis(timestamp);

            if (period == PERIOD_WEEKLY) {

                int dayIndex = dayIndexFromWeekStart(
                        workoutDate.get(Calendar.DAY_OF_WEEK),
                        start.getFirstDayOfWeek()
                );

                activityBuckets[dayIndex]++;

            } else {

                int dayOfMonth =
                        workoutDate.get(Calendar.DAY_OF_MONTH);

                int weekIndex = Math.min(
                        (dayOfMonth - 1) / 7,
                        4
                );

                activityBuckets[weekIndex]++;
            }
        }

        periodWorkoutCount.setText(
                String.valueOf(workoutCount)
        );

        periodActiveTime.setText(
                formatActiveTime(activeTimeMillis)
        );

        renderActivityChart(activityBuckets, period);

        if (workoutCount == 0) {
            activityTrackerMessage.setText(
                    period == PERIOD_WEEKLY
                            ? "No completed workouts this week yet. Start a workout to begin your weekly progress."
                            : "No completed workouts this month yet. Your activity will appear here after your first completed workout."
            );
        } else {
            activityTrackerMessage.setText(
                    "You've completed " + workoutCount +
                            (workoutCount == 1
                                    ? " workout"
                                    : " workouts") +
                            " in this " +
                            (period == PERIOD_WEEKLY ? "week" : "month") +
                            ". Keep building your consistency!"
            );
        }
    }

    private int dayIndexFromWeekStart(
            int dayOfWeek,
            int firstDayOfWeek
    ) {
        return (dayOfWeek - firstDayOfWeek + 7) % 7;
    }

    private void setStartOfDay(Calendar calendar) {

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private long parseDurationMillis(String duration) {

        if (duration == null || duration.trim().isEmpty()) {
            return 0L;
        }

        try {
            long minutes = 0L;
            long seconds = 0L;

            String[] parts = duration.trim().split("\\s+");

            for (int i = 0; i < parts.length - 1; i++) {

                String value = parts[i];

                if (!value.matches("\\d+")) {
                    continue;
                }

                long number = Long.parseLong(value);
                String unit = parts[i + 1].toLowerCase(Locale.ROOT);

                if (unit.startsWith("min")) {
                    minutes = number;
                } else if (unit.startsWith("sec")) {
                    seconds = number;
                }
            }

            return (minutes * 60L + seconds) * 1000L;

        } catch (Exception e) {
            return 0L;
        }
    }

    private String formatActiveTime(long milliseconds) {

        long totalMinutes = milliseconds / 60000L;
        long hours = totalMinutes / 60L;
        long minutes = totalMinutes % 60L;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }

        return totalMinutes + " min";
    }

    private void renderActivityChart(
            int[] values,
            int period
    ) {

        activityChartContainer.removeAllViews();

        int maximum = 0;

        for (int value : values) {
            maximum = Math.max(maximum, value);
        }

        String[] weeklyLabels = getWeeklyLabels();
        String[] monthlyLabels = {
                "1–7", "8–14", "15–21", "22–28", "29+"
        };

        for (int i = 0; i < values.length; i++) {

            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            float columnWeight = 1f;

            LinearLayout.LayoutParams columnParams =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(120),
                            columnWeight
                    );

            columnParams.setMargins(dp(2), 0, dp(2), 0);

            TextView countText = new TextView(this);
            countText.setText(String.valueOf(values[i]));
            countText.setTextColor(Color.rgb(90, 100, 94));
            countText.setTextSize(11);
            countText.setGravity(Gravity.CENTER);

            column.addView(
                    countText,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(18)
                    )
            );

            int availableHeight = 72;

            int barHeight = maximum == 0
                    ? 4
                    : Math.max(
                    8,
                    (int) (
                            (values[i] / (float) maximum)
                                    * availableHeight
                    )
            );

            View bar = new View(this);

            GradientDrawable barBackground = new GradientDrawable();
            barBackground.setColor(
                    values[i] > 0
                            ? Color.rgb(39, 133, 79)
                            : Color.rgb(225, 233, 227)
            );
            barBackground.setCornerRadius(dp(6));
            bar.setBackground(barBackground);

            LinearLayout.LayoutParams barParams =
                    new LinearLayout.LayoutParams(
                            dp(18),
                            dp(barHeight)
                    );

            barParams.gravity = Gravity.CENTER_HORIZONTAL;
            barParams.topMargin = dp(4);

            column.addView(bar, barParams);

            TextView label = new TextView(this);
            label.setText(
                    period == PERIOD_WEEKLY
                            ? weeklyLabels[i]
                            : monthlyLabels[i]
            );
            label.setTextColor(Color.rgb(100, 100, 100));
            label.setTextSize(10);
            label.setGravity(Gravity.CENTER);
            label.setSingleLine(true);

            LinearLayout.LayoutParams labelParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(22)
                    );

            labelParams.topMargin = dp(4);

            column.addView(label, labelParams);

            activityChartContainer.addView(column, columnParams);
        }
    }

    private String[] getWeeklyLabels() {

        Calendar calendar = Calendar.getInstance();

        calendar.set(
                Calendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek()
        );

        setStartOfDay(calendar);

        String[] labels = new String[7];

        SimpleDateFormat dayFormat =
                new SimpleDateFormat("EE", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            labels[i] = dayFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return labels;
    }

    private void updateFitnessMilestones() {

        int totalWorkouts = workoutPreferences.getInt(
                "TOTAL_WORKOUTS",
                0
        );

        int streak = workoutPreferences.getInt(
                "WORKOUT_STREAK",
                0
        );

        int workoutProgress = Math.min(
                totalWorkouts,
                WORKOUT_MILESTONE
        );

        workoutMilestoneBar.setMax(WORKOUT_MILESTONE);
        workoutMilestoneBar.setProgress(workoutProgress);

        if (totalWorkouts >= WORKOUT_MILESTONE) {
            workoutMilestoneTitle.setText("Milestone reached!");
            workoutMilestoneProgress.setText(
                    totalWorkouts + " workouts completed"
            );
        } else {
            workoutMilestoneTitle.setText("First steps");
            workoutMilestoneProgress.setText(
                    workoutProgress + " of " +
                            WORKOUT_MILESTONE + " workouts"
            );
        }

        int streakProgress = Math.min(
                streak,
                STREAK_MILESTONE
        );

        streakMilestoneBar.setMax(STREAK_MILESTONE);
        streakMilestoneBar.setProgress(streakProgress);

        if (streak >= STREAK_MILESTONE) {
            streakMilestoneTitle.setText("Consistency achieved!");
            streakMilestoneProgress.setText(
                    streak + " day streak"
            );
        } else {
            streakMilestoneTitle.setText("Build a streak");
            streakMilestoneProgress.setText(
                    streakProgress + " of " +
                            STREAK_MILESTONE + " days"
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userPreferences != null) {
            setupUserGreeting();
        }

        if (workoutPreferences != null &&
                historyPreferences != null) {
            refreshDashboardData();
        }
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}