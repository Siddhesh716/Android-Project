package com.example.basic;
import android.content.Context;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public final class DailyFitnessArticleScheduler {

    private static final String WORK_NAME =
            "daily_fitness_article_notification";

    private DailyFitnessArticleScheduler() {
    }

    public static void scheduleNext(Context context) {
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        DailyFitnessArticleWorker.class,
                        15,
                        TimeUnit.MINUTES
                ).build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                );
    }

    public static void cancel(Context context) {
        WorkManager.getInstance(context.getApplicationContext())
                .cancelUniqueWork(WORK_NAME);
    }
}