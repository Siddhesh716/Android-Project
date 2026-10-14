package com.example.basic;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private Button loginButton, signupButton;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isLoggedIn = getSharedPreferences(
                "LoginSession",
                MODE_PRIVATE
        ).getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            Intent intent = new Intent(
                    MainActivity.this,
                    DashboardActivity.class
            );
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        DailyFitnessArticleScheduler.scheduleNext(this);
        videoView = findViewById(R.id.videoView);

        videoUri = Uri.parse(
                "android.resource://" + getPackageName() + "/" + R.raw.exercise
        );

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        playVideo();

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        playVideo();
    }

    private void playVideo() {
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);

            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            if (videoWidth > 0 && videoHeight > 0) {
                float videoRatio = (float) videoWidth / videoHeight;
                float viewRatio = (float) videoView.getWidth() / videoView.getHeight();

                if (viewRatio > videoRatio) {
                    videoView.getLayoutParams().width = videoView.getWidth();
                    videoView.getLayoutParams().height =
                            (int) (videoView.getWidth() / videoRatio);
                } else {
                    videoView.getLayoutParams().height = videoView.getHeight();
                    videoView.getLayoutParams().width =
                            (int) (videoView.getHeight() * videoRatio);
                }

                videoView.requestLayout();
            }

            videoView.start();
        });
    }
}