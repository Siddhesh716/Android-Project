package com.example.basic;
import android.os.Bundle;
import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        final String[] exercises = {
                "Monday: Running",
                "Tuesday: Skipping",
                "Wednesday: Yoga",
                "Thursday: Cycling",
                "Friday: Weightlifting",
                "Saturday: Swimming",
                "Sunday: Rest"
        };

        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int adjustedDay = (currentDayOfWeek == 1) ? 6 : currentDayOfWeek - 2;

        ListView listView = findViewById(R.id.exercise_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                if (position == adjustedDay) {
                    textView.setBackgroundColor(Color.YELLOW);
                } else {
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }
                view.setOnClickListener(v -> showEditDialog(position, exercises));
                return view;
            }
        };
        listView.setAdapter(adapter);
        ImageView exerciseImage = findViewById(R.id.exercise_image);
    }

    private void showEditDialog(int position, String[] exercises) {
        String currentExercise = exercises[position].split(": ")[1];
        final EditText editText = new EditText(ExerciseActivity.this);
        editText.setText(currentExercise);
        editText.setSelection(editText.getText().length());

        new AlertDialog.Builder(ExerciseActivity.this)
                .setTitle("Change in Plan ?")
                .setMessage("Edit the Exercise for the Day!")
                .setView(editText)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newExercise = editText.getText().toString();
                    if (!newExercise.isEmpty()) {
                        exercises[position] = exercises[position].split(": ")[0] + ": " + newExercise;
                        ((ArrayAdapter) ((ListView) findViewById(R.id.exercise_list)).getAdapter()).notifyDataSetChanged();
                        Toast.makeText(ExerciseActivity.this, "Exercise updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ExerciseActivity.this, "Exercise cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}