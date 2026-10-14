package com.example.basic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ExerciseActivity extends AppCompatActivity {

    private String[] exercises = {
            "Monday: Running",
            "Tuesday: Skipping",
            "Wednesday: Yoga",
            "Thursday: Cycling",
            "Friday: Weightlifting",
            "Saturday: Swimming",
            "Sunday: Rest"
    };

    private int adjustedDay;

    private ArrayAdapter<String> adapter;

    private TextView todayExerciseDay;
    private TextView todayExerciseName;
    private TextView todayExerciseCategory;
    private TextView todayExerciseDescription;

    private ImageView exerciseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise);

        Calendar calendar = Calendar.getInstance();

        int currentDayOfWeek =
                calendar.get(Calendar.DAY_OF_WEEK);

        adjustedDay =
                (currentDayOfWeek == Calendar.SUNDAY)
                        ? 6
                        : currentDayOfWeek - 2;

        todayExerciseDay =
                findViewById(R.id.todayExerciseDay);

        todayExerciseName =
                findViewById(R.id.todayExerciseName);

        todayExerciseCategory =
                findViewById(R.id.todayExerciseCategory);

        todayExerciseDescription =
                findViewById(R.id.todayExerciseDescription);

        exerciseImage =
                findViewById(R.id.exercise_image);

        TextView todayEditButton =
                findViewById(R.id.todayEditButton);

        updateTodayWorkout();

        todayEditButton.setOnClickListener(v ->
                showEditDialog(
                        adjustedDay,
                        exercises
                )
        );

        ListView listView =
                findViewById(R.id.exercise_list);

        adapter =
                new ArrayAdapter<String>(
                        this,
                        0,
                        exercises
                ) {

                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ) {

                        View view = convertView;

                        if (view == null) {

                            view = LayoutInflater
                                    .from(ExerciseActivity.this)
                                    .inflate(
                                            R.layout.item_exercise,
                                            parent,
                                            false
                                    );
                        }

                        TextView day =
                                view.findViewById(
                                        R.id.exerciseDay
                                );

                        TextView name =
                                view.findViewById(
                                        R.id.exerciseName
                                );

                        TextView category =
                                view.findViewById(
                                        R.id.exerciseCategory
                                );

                        TextView arrow =
                                view.findViewById(
                                        R.id.exerciseArrow
                                );

                        String fullExercise =
                                exercises[position];

                        String[] parts =
                                fullExercise.split(
                                        ": ",
                                        2
                                );

                        String dayName =
                                parts[0];

                        String exerciseName =
                                parts.length > 1
                                        ? parts[1]
                                        : "";

                        day.setText(
                                getDayShortName(dayName)
                        );

                        name.setText(
                                exerciseName
                        );

                        category.setText(
                                getExerciseCategory(
                                        exerciseName
                                )
                        );

                        if (position == adjustedDay) {

                            day.setTextColor(
                                    Color.WHITE
                            );

                            day.setBackgroundResource(
                                    R.drawable.exercise_day_selected_background
                            );

                            name.setTextColor(
                                    Color.parseColor(
                                            "#1B5E20"
                                    )
                            );

                            arrow.setTextColor(
                                    Color.parseColor(
                                            "#1B5E20"
                                    )
                            );

                        } else {

                            day.setTextColor(
                                    Color.parseColor(
                                            "#777777"
                                    )
                            );

                            day.setBackgroundResource(
                                    R.drawable.exercise_day_background
                            );

                            name.setTextColor(
                                    Color.parseColor(
                                            "#212121"
                                    )
                            );

                            arrow.setTextColor(
                                    Color.parseColor(
                                            "#777777"
                                    )
                            );
                        }

                        view.setOnClickListener(v ->
                                showEditDialog(
                                        position,
                                        exercises
                                )
                        );

                        return view;
                    }
                };

        listView.setAdapter(adapter);
    }

    private void updateTodayWorkout() {

        String fullExercise =
                exercises[adjustedDay];

        String[] parts =
                fullExercise.split(
                        ": ",
                        2
                );

        String dayName =
                parts[0];

        String exerciseName =
                parts.length > 1
                        ? parts[1]
                        : "";

        todayExerciseDay.setText(
                dayName.toUpperCase()
        );

        todayExerciseName.setText(
                exerciseName
        );

        String category =
                getExerciseCategory(
                        exerciseName
                );

        todayExerciseCategory.setText(
                category
        );

        todayExerciseDescription.setText(
                getExerciseDescription(
                        exerciseName
                )
        );

        /*
         * Update the hero image according to
         * today's exercise.
         */
        updateExerciseImage(
                exerciseName
        );
    }

    private void updateExerciseImage(
            String exercise
    ) {

        String value =
                exercise.toLowerCase().trim();

        if (value.contains("running")) {

            exerciseImage.setImageResource(
                    R.drawable.exe
            );

        } else if (value.contains("skipping") ||
                value.contains("jump rope") ||
                value.contains("jumping rope")) {

            exerciseImage.setImageResource(
                    R.drawable.exe2
            );

        } else if (value.contains("yoga")) {

            exerciseImage.setImageResource(
                    R.drawable.exe3
            );

        } else if (value.contains("cycling") ||
                value.contains("biking") ||
                value.contains("bike")) {

            exerciseImage.setImageResource(
                    R.drawable.exe4
            );

        } else if (value.contains("weight") ||
                value.contains("strength") ||
                value.contains("lifting") ||
                value.contains("gym")) {

            exerciseImage.setImageResource(
                    R.drawable.exe5
            );

        } else if (value.contains("swimming") ||
                value.contains("swim")) {

            exerciseImage.setImageResource(
                    R.drawable.exe6
            );

        } else if (value.contains("rest") ||
                value.contains("recovery") ||
                value.contains("recover")) {

            exerciseImage.setImageResource(
                    R.drawable.exe7
            );

        } else {

            /*
             * Unknown exercise:
             * keep the original image as fallback.
             */
            exerciseImage.setImageResource(
                    R.drawable.exe
            );
        }
    }

    private String getDayShortName(
            String day
    ) {

        switch (day) {

            case "Monday":
                return "MON";

            case "Tuesday":
                return "TUE";

            case "Wednesday":
                return "WED";

            case "Thursday":
                return "THU";

            case "Friday":
                return "FRI";

            case "Saturday":
                return "SAT";

            case "Sunday":
                return "SUN";

            default:
                return day.substring(
                        0,
                        Math.min(
                                3,
                                day.length()
                        )
                ).toUpperCase();
        }
    }

    private String getExerciseCategory(
            String exercise
    ) {

        String value =
                exercise.toLowerCase();

        if (value.contains("running") ||
                value.contains("skipping") ||
                value.contains("cycling") ||
                value.contains("swimming")) {

            return "CARDIO";
        }

        if (value.contains("weight") ||
                value.contains("strength") ||
                value.contains("lifting") ||
                value.contains("gym")) {

            return "STRENGTH";
        }

        if (value.contains("yoga") ||
                value.contains("stretch") ||
                value.contains("mobility")) {

            return "MOBILITY";
        }

        if (value.contains("rest") ||
                value.contains("recovery") ||
                value.contains("recover")) {

            return "RECOVERY";
        }

        return "TRAINING";
    }

    private String getExerciseDescription(
            String exercise
    ) {

        String value =
                exercise.toLowerCase();

        if (value.contains("running")) {

            return "Build endurance and stay active.";
        }

        if (value.contains("skipping")) {

            return "Improve coordination, stamina and conditioning.";
        }

        if (value.contains("yoga")) {

            return "Improve mobility, flexibility and recovery.";
        }

        if (value.contains("cycling")) {

            return "Build cardiovascular endurance and lower-body strength.";
        }

        if (value.contains("weight") ||
                value.contains("strength") ||
                value.contains("lifting")) {

            return "Build strength and develop overall muscle fitness.";
        }

        if (value.contains("swimming")) {

            return "Train your full body while improving endurance.";
        }

        if (value.contains("rest") ||
                value.contains("recovery")) {

            return "Recovery is part of training. Give your body time to reset.";
        }

        return "Stay consistent and complete your planned session.";
    }

    private void showEditDialog(
            int position,
            String[] exercises
    ) {

        String[] parts =
                exercises[position].split(
                        ": ",
                        2
                );

        String currentExercise =
                parts.length > 1
                        ? parts[1]
                        : "";

        final EditText editText =
                new EditText(
                        ExerciseActivity.this
                );

        editText.setSingleLine(true);

        editText.setText(
                currentExercise
        );

        editText.setSelection(
                editText.getText().length()
        );

        new AlertDialog.Builder(
                ExerciseActivity.this
        )
                .setTitle("Change in Plan ?")
                .setMessage(
                        "Edit the Exercise for the Day!"
                )
                .setView(editText)
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            String newExercise =
                                    editText
                                            .getText()
                                            .toString()
                                            .trim();

                            if (!newExercise.isEmpty()) {

                                exercises[position] =
                                        parts[0]
                                                + ": "
                                                + newExercise;

                                adapter.notifyDataSetChanged();

                                if (position ==
                                        adjustedDay) {

                                    updateTodayWorkout();
                                }

                                Toast.makeText(
                                        ExerciseActivity.this,
                                        "Exercise updated",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        ExerciseActivity.this,
                                        "Exercise cannot be empty",
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
}