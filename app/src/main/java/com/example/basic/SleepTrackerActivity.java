package com.example.basic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SleepTrackerActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private boolean isSleeping = false;
    private long sleepStartTime, sleepEndTime;
    private TextView sleepText;
    private Handler handler = new Handler();
    private boolean screenOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);

        sleepText = findViewById(R.id.sleepText);

        // Register screen on/off receiver
        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

        // Setup accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float movement = Math.abs(x - lastX) + Math.abs(y - lastY) + Math.abs(z - lastZ);

        // Detect when the phone is still
        if (movement < 0.5) {
            if (!isSleeping) {
                isSleeping = true;
                sleepStartTime = System.currentTimeMillis();
            }
        } else {
            if (isSleeping) {
                isSleeping = false;
                sleepEndTime = System.currentTimeMillis();
                long sleepDuration = (sleepEndTime - sleepStartTime) / 1000; // Convert to seconds
                sleepText.setText("You slept for: " + formatTime(sleepDuration));
            }
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Screen On/Off Receiver
    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                sleepStartTime = System.currentTimeMillis();
                screenOff = true;
                handler.post(updateSleepDuration);
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                sleepEndTime = System.currentTimeMillis();
                screenOff = false;
                handler.removeCallbacks(updateSleepDuration);
                long sleepDuration = (sleepEndTime - sleepStartTime) / 1000; // Convert to seconds
                sleepText.setText("Estimated Sleep: " + formatTime(sleepDuration));
            }
        }
    };

    // Runnable to update sleep duration in real time
    private final Runnable updateSleepDuration = new Runnable() {
        @Override
        public void run() {
            if (screenOff) {
                long sleepDuration = (System.currentTimeMillis() - sleepStartTime) / 1000;
                sleepText.setText("Sleeping for: " + formatTime(sleepDuration));
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    // Method to format time in HH:MM:SS
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        unregisterReceiver(screenReceiver);
        handler.removeCallbacks(updateSleepDuration);
    }
}