package com.example.basic;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Xml;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyFitnessArticleWorker extends Worker {

    private static final String CHANNEL_ID = "daily_fitness_articles";
    private static final String CHANNEL_NAME = "Daily Fitness Articles";

    private static final String PREFS_NAME = "DailyFitnessArticlePrefs";
    private static final String LAST_ARTICLE_KEY = "lastArticleLink";
    private static final String[] FEED_QUERIES = {
            "fitness exercise science research",
            "strength training workout research",
            "sports nutrition healthy eating",
            "sleep recovery muscle recovery exercise",
            "cardio heart health physical activity"
    };

    public DailyFitnessArticleWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        try {
            createNotificationChannel(context);

            android.content.SharedPreferences prefs =
                    context.getSharedPreferences(
                            PREFS_NAME, Context.MODE_PRIVATE);

            Article article = findArticle();

            if (article == null) {
                return Result.success();
            }

// Skip the article if we already notified the user about this link.
            String lastArticleLink = prefs.getString(LAST_ARTICLE_KEY, "");

            if (article.link.equals(lastArticleLink)) {
                return Result.success();
            }

            Intent openArticleIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(article.link)
            );

            PendingIntent contentIntent = PendingIntent.getActivity(
                    context,
                    9100,
                    openArticleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                            | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.fit)
                            .setContentTitle("Today's Fitness Article")
                            .setContentText(article.title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(
                                            article.title
                                                    + "\n\nSource: "
                                                    + article.source
                                                    + "\nTap to read the full article."
                                    ))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setContentIntent(contentIntent);

            // Notification permission must already have been granted
            // on Android 13 and newer.
            if (Build.VERSION.SDK_INT >= 33
                    && context.checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return Result.success();
            }

            NotificationManagerCompat.from(context).notify(
                    9100,
                    notification.build()
            );

            prefs.edit()
                    .putString(LAST_ARTICLE_KEY, article.link)
                    .apply();

            return Result.success();

        } catch (Exception e) {
            return Result.retry();
        }
    }

    private Article findArticle() {
        for (String query : FEED_QUERIES) {
            try {
                String encodedQuery = URLEncoder.encode(
                        query, StandardCharsets.UTF_8.name());

                String feedUrl =
                        "https://news.google.com/rss/search?q="
                                + encodedQuery
                                + "&hl=en-IN&gl=IN&ceid=IN:en"
                                + "&when=7d"
                                + "&_refresh="
                                + System.currentTimeMillis();

                List<Article> articles = fetchFeed(feedUrl);

                if (!articles.isEmpty()) {
                    // Google News RSS normally lists recent results first.
                    return articles.get(0);
                }

            } catch (Exception ignored) {
                // Try the next fitness query.
            }
        }

        return null;
    }

    private List<Article> fetchFeed(String feedUrl) throws Exception {
        List<Article> result = new ArrayList<>();
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)
                    new URL(feedUrl).openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setUseCaches(false);
            connection.setRequestProperty(
                    "User-Agent", "BasicFitnessAndroidApp/1.0");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("RSS request failed");
            }

            try (InputStream input = connection.getInputStream()) {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, StandardCharsets.UTF_8.name());

                String title = "";
                String link = "";
                String source = "";
                boolean insideItem = false;

                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = parser.getName();

                        if ("item".equalsIgnoreCase(tag)) {
                            insideItem = true;
                            title = "";
                            link = "";
                            source = "";

                        } else if (insideItem) {
                            if ("title".equalsIgnoreCase(tag)) {
                                title = parser.nextText().trim();

                            } else if ("link".equalsIgnoreCase(tag)) {
                                link = parser.nextText().trim();

                            } else if ("source".equalsIgnoreCase(tag)) {
                                source = parser.nextText().trim();
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG
                            && "item".equalsIgnoreCase(parser.getName())) {

                        if (!title.isEmpty() && !link.isEmpty()
                                && isRelevant(title + " " + source)) {
                            result.add(new Article(title, link, source));
                        }

                        insideItem = false;
                    }

                    eventType = parser.next();
                }
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    private boolean isRelevant(String text) {
        String lower = text.toLowerCase(Locale.ROOT);

        String[] keywords = {
                "fitness", "exercise", "workout", "training",
                "strength", "muscle", "cardio", "nutrition",
                "protein", "diet", "health", "recovery",
                "sleep", "running", "walking", "weightlifting",
                "physical activity", "sports science", "heart health"
        };

        for (String keyword : keywords) {
            if (lower.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription(
                    "A daily fitness article selected from recent news.");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private static class Article {
        final String title;
        final String link;
        final String source;

        Article(String title, String link, String source) {
            this.title = title;
            this.link = link;
            this.source = source;
        }
    }
}