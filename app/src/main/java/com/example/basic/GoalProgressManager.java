package com.example.basic;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoalProgressManager {

    private static final String PREF_NAME = "GoalsData";
    private static final String GOALS_KEY = "GOALS_JSON";
    private static final String ACTIVE_GOAL_KEY = "ACTIVE_GOAL";

    private final SharedPreferences preferences;

    public GoalProgressManager(Context context) {
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // ---------------------------------------------------------
    // GOAL DATA
    // ---------------------------------------------------------

    public static class Goal {

        private String name;
        private String type;
        private double target;
        private double completed;

        public Goal(
                String name,
                String type,
                double target,
                double completed
        ) {
            this.name = name;
            this.type = type;
            this.target = target;
            this.completed = completed;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public double getTarget() {
            return target;
        }

        public double getCompleted() {
            return completed;
        }

        public void setCompleted(double completed) {
            this.completed = Math.max(0, completed);
        }

        public double getProgressPercentage() {

            if (target <= 0) {
                return 0;
            }

            double percentage =
                    (completed / target) * 100.0;

            if (percentage < 0) {
                return 0;
            }

            if (percentage > 100) {
                return 100;
            }

            return percentage;
        }

        public boolean isCompleted() {
            return getProgressPercentage() >= 100;
        }
    }

    // ---------------------------------------------------------
    // GOAL TYPES
    // ---------------------------------------------------------

    public static final String TYPE_RUNNING =
            "RUNNING";

    public static final String TYPE_WEIGHT_LOSS =
            "WEIGHT_LOSS";

    public static final String TYPE_JOGGING =
            "JOGGING";

    public static final String TYPE_MEDITATION =
            "MEDITATION";

    public static final String TYPE_CUSTOM =
            "CUSTOM";

    // ---------------------------------------------------------
    // LOAD GOALS
    // ---------------------------------------------------------

    public List<Goal> getGoals() {

        List<Goal> goals =
                new ArrayList<>();

        String savedGoals =
                preferences.getString(
                        GOALS_KEY,
                        null
                );

        if (savedGoals == null ||
                savedGoals.isEmpty()) {

            createDefaultGoals();

            savedGoals =
                    preferences.getString(
                            GOALS_KEY,
                            null
                    );
        }

        if (savedGoals == null) {
            return goals;
        }

        try {

            JSONArray jsonArray =
                    new JSONArray(savedGoals);

            for (int i = 0;
                 i < jsonArray.length();
                 i++) {

                JSONObject object =
                        jsonArray.getJSONObject(i);

                String name =
                        object.optString(
                                "name",
                                ""
                        );

                String type =
                        object.optString(
                                "type",
                                TYPE_CUSTOM
                        );

                double target =
                        object.optDouble(
                                "target",
                                100
                        );

// -------------------------------------------------
// MIGRATE ORIGINAL DEFAULT GOALS
// -------------------------------------------------

                if ("Run 2 km".equalsIgnoreCase(name)) {

                    type = TYPE_RUNNING;
                    target = 2.0;

                } else if (
                        "Lose 100 g Weight".equalsIgnoreCase(name)
                ) {

                    type = TYPE_WEIGHT_LOSS;
                    target = 100.0;

                } else if (
                        "Jogging for 30 minutes".equalsIgnoreCase(name)
                ) {

                    type = TYPE_JOGGING;
                    target = 30.0;

                } else if (
                        "Meditate for 10 minutes".equalsIgnoreCase(name)
                ) {

                    type = TYPE_MEDITATION;
                    target = 10.0;
                }

                double completed =
                        object.optDouble(
                                "completed",
                                0
                        );

                if (name.isEmpty()) {
                    continue;
                }

                if (target <= 0) {
                    target = 100;
                }

                goals.add(
                        new Goal(
                                name,
                                type,
                                target,
                                Math.max(
                                        0,
                                        completed
                                )
                        )
                );
            }

        } catch (JSONException e) {

            e.printStackTrace();

            createDefaultGoals();

            return getGoals();
        }

// Save migrated/corrected goal types
        saveGoals(goals);

        return goals;
    }

    // ---------------------------------------------------------
    // SAVE GOALS
    // ---------------------------------------------------------

    public void saveGoals(
            List<Goal> goals
    ) {

        JSONArray jsonArray =
                new JSONArray();

        try {

            for (Goal goal : goals) {

                JSONObject object =
                        new JSONObject();

                object.put(
                        "name",
                        goal.getName()
                );

                object.put(
                        "type",
                        goal.getType()
                );

                object.put(
                        "target",
                        goal.getTarget()
                );

                object.put(
                        "completed",
                        goal.getCompleted()
                );

                jsonArray.put(object);
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        preferences.edit()
                .putString(
                        GOALS_KEY,
                        jsonArray.toString()
                )
                .apply();
    }

    // ---------------------------------------------------------
    // DEFAULT GOALS
    // ---------------------------------------------------------

    private void createDefaultGoals() {

        List<Goal> goals =
                new ArrayList<>();

        goals.add(
                new Goal(
                        "Run 2 km",
                        TYPE_RUNNING,
                        2.0,
                        0
                )
        );

        goals.add(
                new Goal(
                        "Lose 100 g Weight",
                        TYPE_WEIGHT_LOSS,
                        100.0,
                        0
                )
        );

        goals.add(
                new Goal(
                        "Jogging for 30 minutes",
                        TYPE_JOGGING,
                        30.0,
                        0
                )
        );

        goals.add(
                new Goal(
                        "Meditate for 10 minutes",
                        TYPE_MEDITATION,
                        10.0,
                        0
                )
        );

        saveGoals(goals);

        preferences.edit()
                .putInt(
                        ACTIVE_GOAL_KEY,
                        0
                )
                .apply();
    }

    // ---------------------------------------------------------
    // ACTIVE GOAL
    // ---------------------------------------------------------

    public int getActiveGoalPosition() {

        List<Goal> goals =
                getGoals();

        if (goals.isEmpty()) {
            return 0;
        }

        int position =
                preferences.getInt(
                        ACTIVE_GOAL_KEY,
                        0
                );

        if (position < 0) {
            position = 0;
        }

        if (position >= goals.size()) {
            position = goals.size() - 1;
        }

        return position;
    }

    public void setActiveGoalPosition(
            int position
    ) {

        List<Goal> goals =
                getGoals();

        if (goals.isEmpty()) {
            position = 0;

        } else {

            if (position < 0) {
                position = 0;
            }

            if (position >= goals.size()) {
                position = goals.size() - 1;
            }
        }

        preferences.edit()
                .putInt(
                        ACTIVE_GOAL_KEY,
                        position
                )
                .apply();
    }

    // ---------------------------------------------------------
    // ADD GOAL
    // ---------------------------------------------------------

    public void addGoal(String name) {

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String goalName = name.trim();

        String lowerName =
                goalName.toLowerCase(Locale.getDefault());

        String type = TYPE_CUSTOM;
        double target = 100.0;

        // ---------------------------------------------------------
        // RUNNING GOAL
        // Examples:
        // Run 20 km
        // Running 10 km
        // Run 5 kilometers
        // ---------------------------------------------------------

        if (
                lowerName.contains("run ") ||
                        lowerName.contains("running ") ||
                        lowerName.contains("run")
        ) {

            Double extractedTarget =
                    extractNumber(lowerName);

            if (
                    extractedTarget != null &&
                            (
                                    lowerName.contains("km") ||
                                            lowerName.contains("kilometer")
                            )
            ) {

                type = TYPE_RUNNING;
                target = extractedTarget;
            }
        }

        // ---------------------------------------------------------
        // JOGGING GOAL
        // Examples:
        // Jog 30 minutes
        // Jogging for 20 minutes
        // ---------------------------------------------------------

        else if (
                lowerName.contains("jog") ||
                        lowerName.contains("jogging")
        ) {

            Double extractedTarget =
                    extractNumber(lowerName);

            if (
                    extractedTarget != null &&
                            (
                                    lowerName.contains("minute") ||
                                            lowerName.contains("min")
                            )
            ) {

                type = TYPE_JOGGING;
                target = extractedTarget;
            }
        }

        // ---------------------------------------------------------
        // MEDITATION GOAL
        // Examples:
        // Meditate 20 minutes
        // Meditation for 10 minutes
        // ---------------------------------------------------------

        else if (
                lowerName.contains("meditat")
        ) {

            Double extractedTarget =
                    extractNumber(lowerName);

            if (
                    extractedTarget != null &&
                            (
                                    lowerName.contains("minute") ||
                                            lowerName.contains("min")
                            )
            ) {

                type = TYPE_MEDITATION;
                target = extractedTarget;
            }
        }

        // ---------------------------------------------------------
        // WEIGHT LOSS GOAL
        // Examples:
        // Lose 100 g weight
        // Lose 500 grams
        // Weight loss 200 g
        // ---------------------------------------------------------

        else if (
                lowerName.contains("lose") &&
                        (
                                lowerName.contains("weight") ||
                                        lowerName.contains(" g") ||
                                        lowerName.contains("gram")
                        )
        ) {

            Double extractedTarget =
                    extractNumber(lowerName);

            if (
                    extractedTarget != null &&
                            (
                                    lowerName.contains(" g") ||
                                            lowerName.contains("gram")
                            )
            ) {

                type = TYPE_WEIGHT_LOSS;
                target = extractedTarget;
            }
        }

        // ---------------------------------------------------------
        // SAVE GOAL
        // ---------------------------------------------------------

        List<Goal> goals =
                getGoals();

        goals.add(
                new Goal(
                        goalName,
                        type,
                        target,
                        0
                )
        );

        saveGoals(goals);

        setActiveGoalPosition(
                goals.size() - 1
        );
    }

    private Double extractNumber(String text) {

        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile(
                        "(\\d+(?:\\.\\d+)?)"
                );

        java.util.regex.Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            try {

                return Double.parseDouble(
                        matcher.group(1)
                );

            } catch (NumberFormatException e) {

                return null;
            }
        }

        return null;
    }

    // ---------------------------------------------------------
    // DELETE GOAL
    // ---------------------------------------------------------

    public void deleteGoal(
            int position
    ) {

        List<Goal> goals =
                getGoals();

        if (position < 0 ||
                position >= goals.size()) {
            return;
        }

        goals.remove(position);

        saveGoals(goals);

        int activePosition =
                getActiveGoalPosition();

        if (goals.isEmpty()) {

            setActiveGoalPosition(0);

        } else if (
                activePosition >= goals.size()
        ) {

            setActiveGoalPosition(
                    goals.size() - 1
            );

        } else if (
                position < activePosition
        ) {

            setActiveGoalPosition(
                    activePosition - 1
            );
        }
    }

    // ---------------------------------------------------------
    // ADD PROGRESS
    // ---------------------------------------------------------

    public void addProgress(
            int position,
            double amount
    ) {

        if (amount <= 0) {
            return;
        }

        List<Goal> goals =
                getGoals();

        if (position < 0 ||
                position >= goals.size()) {
            return;
        }

        Goal goal =
                goals.get(position);

        double newCompleted =
                goal.getCompleted()
                        + amount;

        goal.setCompleted(
                Math.min(
                        newCompleted,
                        goal.getTarget()
                )
        );

        saveGoals(goals);
    }

    // ---------------------------------------------------------
    // SET PROGRESS
    // ---------------------------------------------------------

    public void setProgress(
            int position,
            double completed
    ) {

        List<Goal> goals =
                getGoals();

        if (position < 0 ||
                position >= goals.size()) {
            return;
        }

        Goal goal =
                goals.get(position);

        goal.setCompleted(
                Math.min(
                        Math.max(
                                0,
                                completed
                        ),
                        goal.getTarget()
                )
        );

        saveGoals(goals);
    }

    // ---------------------------------------------------------
    // ADD RUNNING DISTANCE
    // ---------------------------------------------------------

    public void addRunningDistance(
            double kilometers
    ) {

        if (kilometers <= 0) {
            return;
        }

        List<Goal> goals =
                getGoals();

        boolean changed = false;

        for (Goal goal : goals) {

            if (TYPE_RUNNING.equals(
                    goal.getType()
            )) {

                double newValue =
                        goal.getCompleted()
                                + kilometers;

                goal.setCompleted(
                        Math.min(
                                newValue,
                                goal.getTarget()
                        )
                );

                changed = true;
            }
        }

        if (changed) {
            saveGoals(goals);
        }
    }

    // ---------------------------------------------------------
    // ADD JOGGING TIME
    // ---------------------------------------------------------

    public void addJoggingMinutes(
            double minutes
    ) {

        if (minutes <= 0) {
            return;
        }

        addProgressToType(
                TYPE_JOGGING,
                minutes
        );
    }

    // ---------------------------------------------------------
    // ADD MEDITATION TIME
    // ---------------------------------------------------------

    public void addMeditationMinutes(
            double minutes
    ) {

        if (minutes <= 0) {
            return;
        }

        addProgressToType(
                TYPE_MEDITATION,
                minutes
        );
    }

    // ---------------------------------------------------------
    // ADD PROGRESS TO GOAL TYPE
    // ---------------------------------------------------------

    private void addProgressToType(
            String type,
            double amount
    ) {

        List<Goal> goals =
                getGoals();

        boolean changed = false;

        for (Goal goal : goals) {

            if (type.equals(
                    goal.getType()
            )) {

                double newValue =
                        goal.getCompleted()
                                + amount;

                goal.setCompleted(
                        Math.min(
                                newValue,
                                goal.getTarget()
                        )
                );

                changed = true;
            }
        }

        if (changed) {
            saveGoals(goals);
        }
    }

    // ---------------------------------------------------------
    // WEIGHT LOSS
    // ---------------------------------------------------------

    public void addWeightLoss(
            double grams
    ) {

        if (grams <= 0) {
            return;
        }

        addProgressToType(
                TYPE_WEIGHT_LOSS,
                grams
        );
    }

    // ---------------------------------------------------------
    // WORKOUT INTEGRATION
    // ---------------------------------------------------------

    public void recordWorkout(
            String workoutType,
            double minutes
    ) {

        if (workoutType == null) {
            workoutType = "";
        }

        String value =
                workoutType.toLowerCase(
                        Locale.getDefault()
                );

        if (value.contains("cardio")) {

            addProgressToType(
                    TYPE_JOGGING,
                    minutes
            );

        } else if (
                value.contains("yoga") ||
                        value.contains("stretch")
        ) {

            addProgressToType(
                    TYPE_MEDITATION,
                    minutes
            );
        }
    }
}