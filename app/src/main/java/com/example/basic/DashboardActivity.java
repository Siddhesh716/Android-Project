package com.example.basic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private VideoView videoView;
    private TextView quoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        videoView = findViewById(R.id.videoView);
        quoteText = findViewById(R.id.quoteText);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userName = sharedPreferences.getString("USER_NAME", "");

        TextView dashboardText = findViewById(R.id.dashboardText);
        if (!userName.isEmpty()) {
            dashboardText.setText("Welcome, " + userName + "!");
        } else {
            dashboardText.setText("Welcome to the Dashboard");
        }

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.goals2);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        quoteText.setText(getDailyQuote());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                } else if (id == R.id.nav_exercises) {
                    startActivity(new Intent(DashboardActivity.this, ExerciseActivity.class));
                } else if (id == R.id.nav_goals) {
                    startActivity(new Intent(DashboardActivity.this, GoalsActivity.class));
                } else if (id == R.id.nav_diet_plans) {
                    startActivity(new Intent(DashboardActivity.this, DietActivity.class));
                } else if (id == R.id.nav_quick_workout) {
                    startActivity(new Intent(DashboardActivity.this, WorkoutActivity.class));
                } else if (id == R.id.nav_bmi) {
                    startActivity(new Intent(DashboardActivity.this, BMI.class));
                } else if (id == R.id.nav_sleep) {
                    startActivity(new Intent(DashboardActivity.this, SleepTrackerActivity.class));
                }else if (id == R.id.nav_quiz) {
                    startActivity(new Intent(DashboardActivity.this, QuizActivity.class));
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private String getDailyQuote() {
        String[] quotes = {
                "Stay Strong, The Week has Just Begun!",
                "Keep Pushing Forward!",
                "Halfway There, Don't Give Up!",
                "Keep Your Eyes on the Goal!",
                "The Weekend is Near, Stay Focused!",
                "Relax & Recharge!",
                "Prepare for a Great Week Ahead!"
        };
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int adjustedDay = (dayOfWeek == 1) ? 0 : dayOfWeek - 2;
        return quotes[adjustedDay];
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}