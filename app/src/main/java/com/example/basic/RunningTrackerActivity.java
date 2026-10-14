package com.example.basic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

public class RunningTrackerActivity extends AppCompatActivity {

    private static final double TARGET_DISTANCE_KM = 2.0;

    private TextView distanceText;
    private TextView timeText;
    private TextView progressText;
    private TextView statusText;

    private ProgressBar progressBar;

    private Button startRunButton;
    private Button finishRunButton;

    private FusedLocationProviderClient locationClient;

    private LocationCallback locationCallback;

    private Location lastLocation;

    private double totalDistanceMeters = 0;

    private long runStartTime = 0;

    private boolean isRunning = false;

    private GoalProgressManager goalProgressManager;

    private final ActivityResultLauncher<String>
            locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            startTracking();

                        } else {

                            statusText.setText(
                                    "Location permission denied"
                            );

                            Toast.makeText(
                                    RunningTrackerActivity.this,
                                    "GPS permission is required for automatic distance tracking",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_running_tracker
        );

        distanceText =
                findViewById(
                        R.id.runningDistanceText
                );

        timeText =
                findViewById(
                        R.id.runningTimeText
                );

        progressText =
                findViewById(
                        R.id.runningProgressText
                );

        statusText =
                findViewById(
                        R.id.runningStatusText
                );

        progressBar =
                findViewById(
                        R.id.runningProgressBar
                );

        startRunButton =
                findViewById(
                        R.id.startRunButton
                );

        finishRunButton =
                findViewById(
                        R.id.finishRunButton
                );

        locationClient =
                LocationServices
                        .getFusedLocationProviderClient(
                                this
                        );

        goalProgressManager =
                new GoalProgressManager(this);

        setupLocationCallback();

        updateDisplay();

        startRunButton.setOnClickListener(
                v -> checkLocationPermission()
        );

        finishRunButton.setOnClickListener(
                v -> finishRun()
        );
    }

    // ---------------------------------------------------------
    // LOCATION PERMISSION
    // ---------------------------------------------------------

    private void checkLocationPermission() {

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        == PackageManager.PERMISSION_GRANTED
        ) {

            startTracking();

        } else {

            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }
    }

    // ---------------------------------------------------------
    // LOCATION CALLBACK
    // ---------------------------------------------------------

    private void setupLocationCallback() {

        locationCallback =
                new LocationCallback() {

                    @Override
                    public void onLocationResult(
                            LocationResult locationResult
                    ) {

                        if (!isRunning) {
                            return;
                        }

                        for (
                                Location location :
                                locationResult
                                        .getLocations()
                        ) {

                            processLocation(
                                    location
                            );
                        }
                    }
                };
    }

    // ---------------------------------------------------------
    // START TRACKING
    // ---------------------------------------------------------

    private void startTracking() {

        if (isRunning) {
            return;
        }

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }

        totalDistanceMeters = 0;

        lastLocation = null;

        runStartTime =
                SystemClock.elapsedRealtime();

        isRunning = true;

        statusText.setText(
                "GPS tracking active"
        );

        startRunButton.setEnabled(
                false
        );

        finishRunButton.setEnabled(
                true
        );

        updateDisplay();

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        2000
                )
                        .setMinUpdateIntervalMillis(
                                1000
                        )
                        .setWaitForAccurateLocation(
                                true
                        )
                        .build();

        locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper()
        );
    }

    // ---------------------------------------------------------
    // PROCESS LOCATION
    // ---------------------------------------------------------

    private void processLocation(
            Location location
    ) {

        if (location == null) {
            return;
        }

        if (lastLocation == null) {

            lastLocation =
                    new Location(location);

            updateDisplay();

            return;
        }

        float distance =
                lastLocation.distanceTo(
                        location
                );

        /*
         * Ignore obviously inaccurate GPS jumps.
         *
         * This prevents a single bad location
         * from adding an unrealistic distance.
         */
        if (
                location.hasAccuracy()
                        &&
                        location.getAccuracy() > 50
        ) {

            return;
        }

        if (distance > 0 &&
                distance < 100) {

            totalDistanceMeters += distance;

            lastLocation =
                    new Location(location);

            updateDisplay();

            if (
                    totalDistanceMeters
                            >=
                            TARGET_DISTANCE_KM
                                    * 1000
            ) {

                finishRun();
            }
        }
    }

    // ---------------------------------------------------------
    // DISPLAY
    // ---------------------------------------------------------

    private void updateDisplay() {

        double distanceKm =
                totalDistanceMeters / 1000.0;

        double progress =
                (
                        distanceKm
                                / TARGET_DISTANCE_KM
                )
                        * 100.0;

        if (progress < 0) {
            progress = 0;
        }

        if (progress > 100) {
            progress = 100;
        }

        distanceText.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f km",
                        distanceKm
                )
        );

        progressText.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f%%",
                        progress
                )
        );

        progressBar.setProgress(
                (int) Math.round(progress)
        );

        updateTimeDisplay();
    }

    // ---------------------------------------------------------
    // TIME
    // ---------------------------------------------------------

    private void updateTimeDisplay() {

        if (!isRunning ||
                runStartTime == 0) {

            timeText.setText(
                    "00:00"
            );

            return;
        }

        long elapsedMillis =
                SystemClock.elapsedRealtime()
                        - runStartTime;

        long totalSeconds =
                elapsedMillis / 1000;

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        timeText.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );

        if (isRunning) {

            timeText.postDelayed(
                    this::updateTimeDisplay,
                    1000
            );
        }
    }

    // ---------------------------------------------------------
    // FINISH RUN
    // ---------------------------------------------------------

    private void finishRun() {

        if (!isRunning) {
            return;
        }

        stopLocationUpdates();

        isRunning = false;

        double distanceKm =
                totalDistanceMeters / 1000.0;

        if (distanceKm > 0) {

            goalProgressManager
                    .addRunningDistance(
                            distanceKm
                    );
        }

        updateDisplay();

        startRunButton.setEnabled(
                true
        );

        finishRunButton.setEnabled(
                false
        );

        if (
                distanceKm
                        >= TARGET_DISTANCE_KM
        ) {

            statusText.setText(
                    "2 km goal completed!"
            );

            Toast.makeText(
                    this,
                    "Run goal completed",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            statusText.setText(
                    "Run saved"
            );

            Toast.makeText(
                    this,
                    String.format(
                            Locale.getDefault(),
                            "%.2f km added to your goal",
                            distanceKm
                    ),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ---------------------------------------------------------
    // STOP GPS
    // ---------------------------------------------------------

    private void stopLocationUpdates() {

        locationClient.removeLocationUpdates(
                locationCallback
        );
    }

    // ---------------------------------------------------------
    // LIFECYCLE
    // ---------------------------------------------------------

    @Override
    protected void onStop() {

        super.onStop();

        /*
         * Stop GPS when leaving this screen.
         *
         * The current run is not automatically
         * saved here. The user should use
         * FINISH RUN.
         */
        if (isRunning) {

            stopLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {

        stopLocationUpdates();

        super.onDestroy();
    }
}