package com.example.basic;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class GoalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        VideoView videoView = findViewById(R.id.goals_video);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.main;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });
        final List<String> goalsList = new ArrayList<>();
        goalsList.add("Run 2 km");
        goalsList.add("Lose 100 g Weight");
        goalsList.add("Jogging for 30 minutes");
        goalsList.add("Meditate for 10 minutes");
        ListView listView = findViewById(R.id.goals_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goalsList);
        listView.setAdapter(adapter);
        Button addGoalButton = findViewById(R.id.add_goal_button);
        addGoalButton.setOnClickListener(v -> {
            final EditText inputGoal = new EditText(GoalsActivity.this);
            inputGoal.setHint("Enter your new goal");
            new AlertDialog.Builder(GoalsActivity.this)
                    .setTitle("Add New Goal")
                    .setView(inputGoal)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String newGoal = inputGoal.getText().toString();
                        if (!newGoal.isEmpty()) {
                            goalsList.add(newGoal);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(GoalsActivity.this, "Goal cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String clickedGoal = goalsList.get(position);
            new AlertDialog.Builder(GoalsActivity.this)
                    .setTitle("Ready to Work")
                    .setMessage("Let's Begin: " + clickedGoal)
                    .setPositiveButton("Start Stopwatch", (dialog, which) -> showStopwatch())
                    .setNegativeButton("Delete", (dialog, which) -> {
                        goalsList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(GoalsActivity.this, "Goal deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        });
    }
    private void showStopwatch() {
        View stopwatchView = getLayoutInflater().inflate(R.layout.stopwatch_layout, null);
        final Chronometer chronometer = stopwatchView.findViewById(R.id.chronometer);
        chronometer.start();
        new AlertDialog.Builder(GoalsActivity.this)
                .setTitle("Stopwatch")
                .setView(stopwatchView)
                .setPositiveButton("Stop", (dialog, which) -> chronometer.stop())
                .setNegativeButton("Reset", (dialog, which) -> chronometer.setBase(SystemClock.elapsedRealtime()))
                .show();
    }
}