
package com.example.basic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodaysTopicActivity extends AppCompatActivity {

    private LinearLayout articleContainer;
    private TextView articleStatus;
    private Button refreshButton;
    private Button searchButton;
    private EditText searchInput;

    private Button savedArticlesButton;
    private boolean showingSavedArticles = false;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final List<Article> articles = new ArrayList<>();

    private String currentSearchQuery = "";

    private static final String PREFS_NAME = "TodaysTopicPrefs";
    private static final String SHOWN_LINKS_KEY = "shownArticleLinks";

    private static final String BOOKMARKS_PREFS = "ArticleBookmarks";
    private static final String BOOKMARKS_KEY = "bookmarkedArticles";

    private final String[] searchQueries = {
            "fitness exercise science research",
            "strength training workout research",
            "sports nutrition healthy eating",
            "sleep recovery muscle recovery exercise",
            "cardio heart health physical activity"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_topic);
        articleContainer = findViewById(R.id.articleContainer);
        articleStatus = findViewById(R.id.articleStatus);
        refreshButton = findViewById(R.id.refreshArticlesButton);
        searchButton = findViewById(R.id.searchArticlesButton);
        searchInput = findViewById(R.id.articleSearchInput);
        savedArticlesButton = findViewById(R.id.savedArticlesButton);

        searchButton.setOnClickListener(v -> searchTopic());

        refreshButton.setOnClickListener(v ->
                loadArticles(currentSearchQuery));

        savedArticlesButton.setOnClickListener(v -> {
            showingSavedArticles = !showingSavedArticles;

            savedArticlesButton.setText(
                    showingSavedArticles
                            ? "★ Show All Articles"
                            : "☆ Saved Articles"
            );

            displayArticles();

            articleStatus.setText(
                    showingSavedArticles
                            ? "Showing saved articles"
                            : "Showing all loaded articles"
            );
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTopic();
                return true;
            }
            return false;
        });

        // Load the normal fitness feed when the screen opens.
        loadArticles("");
    }

    private void searchTopic() {
        String query = searchInput.getText().toString().trim();

        if (query.isEmpty()) {
            searchInput.setError("Enter a topic to search");
            return;
        }

        if (query.length() > 100) {
            searchInput.setError(
                    "Please keep the topic under 100 characters");
            return;
        }

        loadArticles(query);
    }

    private void loadArticles(String query) {
        currentSearchQuery = query == null ? "" : query.trim();

        refreshButton.setEnabled(false);
        searchButton.setEnabled(false);

        articleContainer.removeAllViews();

        articleStatus.setText(
                currentSearchQuery.isEmpty()
                        ? "Fetching fitness articles..."
                        : "Searching for \"" + currentSearchQuery + "\"..."
        );

        executor.execute(() -> {
            List<Article> results = new ArrayList<>();
            Set<String> seenLinks = new HashSet<>();
            String errorMessage = null;

            try {
                String[] queriesToFetch;

                if (currentSearchQuery.isEmpty()) {
                    queriesToFetch = searchQueries;
                } else {
                    // A custom search uses the entered topic.
                    queriesToFetch = new String[]{
                            currentSearchQuery
                    };
                }

                for (String queryToFetch : queriesToFetch) {
                    String encodedQuery = URLEncoder.encode(
                            queryToFetch,
                            StandardCharsets.UTF_8.name()
                    );

                    String feedUrl =
                            "https://news.google.com/rss/search?q="
                                    + encodedQuery
                                    + "&hl=en-IN&gl=IN&ceid=IN:en"
                                    + "&when=7d"
                                    + "&_refresh="
                                    + System.currentTimeMillis();

                    List<Article> feedArticles = fetchFeed(feedUrl);

                    for (Article article : feedArticles) {
                        boolean relevant;

                        if (currentSearchQuery.isEmpty()) {
                            relevant = isRelevantArticle(article);
                        } else {
                            // Google News already searches the custom topic.
                            relevant = true;
                        }

                        if (article.link != null
                                && !article.link.isEmpty()
                                && relevant
                                && seenLinks.add(article.link)) {
                            results.add(article);
                        }
                    }
                }

            } catch (Exception e) {
                errorMessage = e.getMessage();
            }

            // Load links shown during earlier refreshes.
            Set<String> previouslyShown = new HashSet<>(
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .getStringSet(
                                    SHOWN_LINKS_KEY,
                                    new HashSet<>()
                            )
            );

            // Unseen articles first; within each group,
            // order by publication date, newest first.
            Collections.sort(results, (a, b) -> {
                boolean aSeen = previouslyShown.contains(a.link);
                boolean bSeen = previouslyShown.contains(b.link);

                if (aSeen != bSeen) {
                    return aSeen ? 1 : -1;
                }

                return Long.compare(
                        getPublishedTime(b.date),
                        getPublishedTime(a.date)
                );
            });

            final String finalError = errorMessage;

            runOnUiThread(() -> {
                refreshButton.setEnabled(true);
                searchButton.setEnabled(true);

                if (results.isEmpty()) {
                    articleStatus.setText(
                            "No articles found."
                                    + (finalError != null
                                    ? "\nDetails: " + finalError
                                    : "\nTry another topic or refresh.")
                    );
                    return;
                }

                articles.clear();
                articles.addAll(results);

                String statusText = "Loaded "
                        + articles.size() + " articles";

                if (!currentSearchQuery.isEmpty()) {
                    statusText += " for \"" + currentSearchQuery + "\"";
                }

                articleStatus.setText(statusText + ".");

                displayArticles();

                // Remember displayed article links persistently.
                Set<String> shownLinks = new HashSet<>(
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .getStringSet(
                                        SHOWN_LINKS_KEY,
                                        new HashSet<>()
                                )
                );

                for (Article article : articles) {
                    shownLinks.add(article.link);
                }

                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putStringSet(SHOWN_LINKS_KEY, shownLinks)
                        .apply();
            });
        });
    }

    private boolean isRelevantArticle(Article article) {
        String text = (article.title + " " + article.source)
                .toLowerCase(Locale.ROOT);

        String[] relevantKeywords = {
                "fitness", "exercise", "workout", "workouts",
                "training", "strength", "muscle", "cardio",
                "nutrition", "protein", "diet", "healthy eating",
                "health", "recovery", "sleep", "running",
                "walking", "weightlifting", "physical activity",
                "sports science", "heart health"
        };

        for (String keyword : relevantKeywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private long getPublishedTime(String publishedDate) {
        if (publishedDate == null || publishedDate.trim().isEmpty()) {
            return 0L;
        }

        String[] patterns = {
                "EEE, dd MMM yyyy HH:mm:ss Z",
                "EEE, dd MMM yyyy HH:mm:ss z"
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat formatter =
                        new SimpleDateFormat(pattern, Locale.US);

                formatter.setLenient(false);
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

                Date parsed = formatter.parse(publishedDate.trim());

                if (parsed != null) {
                    return parsed.getTime();
                }

            } catch (java.text.ParseException ignored) {
                // Try the next supported date format.
            }
        }

        return 0L;
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
                    "Cache-Control", "no-cache");
            connection.setRequestProperty(
                    "Pragma", "no-cache");
            connection.setRequestProperty(
                    "User-Agent", "BasicFitnessAndroidApp/1.0");

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(
                        "RSS request returned HTTP " + responseCode);
            }

            try (InputStream input = connection.getInputStream()) {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, StandardCharsets.UTF_8.name());

                int eventType = parser.getEventType();

                String title = "";
                String link = "";
                String date = "";
                String source = "";

                boolean insideItem = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = parser.getName();

                        if ("item".equalsIgnoreCase(tag)) {
                            insideItem = true;
                            title = "";
                            link = "";
                            date = "";
                            source = "";

                        } else if (insideItem) {
                            if ("title".equalsIgnoreCase(tag)) {
                                title = parser.nextText().trim();

                            } else if ("link".equalsIgnoreCase(tag)) {
                                link = parser.nextText().trim();

                            } else if ("pubDate".equalsIgnoreCase(tag)) {
                                date = parser.nextText().trim();

                            } else if ("source".equalsIgnoreCase(tag)) {
                                source = parser.nextText().trim();
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG
                            && "item".equalsIgnoreCase(parser.getName())) {

                        if (!title.isEmpty() && !link.isEmpty()) {
                            result.add(new Article(
                                    title, link, date, source));
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

    private void displayArticles() {
        articleContainer.removeAllViews();

        int displayedCount = 0;

        for (Article article : articles) {
            if (showingSavedArticles
                    && !isArticleBookmarked(article.link)) {
                continue;
            }

            displayedCount++;

            // Article card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(16), dp(16), dp(16), dp(16));
            card.setBackground(createRoundedBackground(
                    0xFFFFFFFF, 0xFFE3EBE7, 16));

            card.setClickable(true);
            card.setFocusable(true);
            card.setElevation(dp(2));

            // Header: category label and article actions
            LinearLayout cardHeader = new LinearLayout(this);
            cardHeader.setOrientation(LinearLayout.HORIZONTAL);
            cardHeader.setGravity(Gravity.CENTER_VERTICAL);

            TextView label = new TextView(this);
            label.setText("FITNESS & WELLNESS");
            label.setTextColor(0xFF16866A);
            label.setTextSize(10);
            label.setTypeface(null, Typeface.BOLD);
            label.setLetterSpacing(0.08f);

            label.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            // Save button
            Button bookmarkButton = new Button(this);
            bookmarkButton.setTextSize(12);
            bookmarkButton.setAllCaps(false);

            bookmarkButton.setText(
                    isArticleBookmarked(article.link)
                            ? "★ Saved" : "☆ Save"
            );

            bookmarkButton.setTextColor(0xFF16866A);
            bookmarkButton.setBackgroundTintList(
                    ColorStateList.valueOf(0xFFE7F3EF));

            // Share button
            Button shareButton = new Button(this);
            shareButton.setText("Share");
            shareButton.setTextSize(12);
            shareButton.setAllCaps(false);
            shareButton.setTextColor(0xFFFFFFFF);
            shareButton.setBackgroundTintList(
                    ColorStateList.valueOf(0xFF16866A));

            LinearLayout.LayoutParams actionParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            dp(44)
                    );

            actionParams.setMarginStart(dp(4));

            cardHeader.addView(label);
            cardHeader.addView(bookmarkButton, actionParams);
            cardHeader.addView(shareButton, actionParams);

            bookmarkButton.setOnClickListener(v -> {
                boolean saved = toggleBookmark(article);

                Toast.makeText(
                        this,
                        saved ? "Article bookmarked" : "Bookmark removed",
                        Toast.LENGTH_SHORT
                ).show();

                // Re-render so an unbookmarked article disappears
                // immediately when the saved-only filter is active.
                if (showingSavedArticles && !saved) {
                    displayArticles();
                } else {
                    bookmarkButton.setText(
                            saved ? "★ Saved" : "☆ Save"
                    );
                }
            });

            shareButton.setOnClickListener(v -> shareArticle(article));

            // Article title
            TextView titleView = new TextView(this);
            titleView.setText(article.title);
            titleView.setTextColor(0xFF172B27);
            titleView.setTextSize(18);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setLineSpacing(dp(2), 1.0f);
            titleView.setPadding(0, dp(9), 0, dp(12));

            // Source and publication date
            String sourceText = article.source == null
                    || article.source.trim().isEmpty()
                    ? "Source available on article page"
                    : article.source.trim();

            String dateText = article.date == null
                    ? "" : article.date.trim();

            TextView sourceView = new TextView(this);
            sourceView.setText(
                    dateText.isEmpty()
                            ? sourceText
                            : sourceText + "\n" + dateText
            );
            sourceView.setTextColor(0xFF73827C);
            sourceView.setTextSize(12);
            sourceView.setLineSpacing(dp(3), 1.0f);

            // Read article indicator
            TextView linkHint = new TextView(this);
            linkHint.setText("Read article  ↗");
            linkHint.setTextColor(0xFF16866A);
            linkHint.setTextSize(14);
            linkHint.setTypeface(null, Typeface.BOLD);
            linkHint.setPadding(0, dp(16), 0, 0);

            card.addView(cardHeader);
            card.addView(titleView);
            card.addView(sourceView);
            card.addView(linkHint);

            // Preserve existing article-opening behavior.
            // Clicking the card opens the original article.
            card.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(article.link)
                    );
                    startActivity(intent);
                } catch (Exception e) {
                    articleStatus.setText(
                            "Unable to open this article.");
                }
            });

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.bottomMargin = dp(14);

            articleContainer.addView(card, cardParams);
        }

        if (showingSavedArticles && displayedCount == 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText(
                    "No saved articles in the current list.\n"
                            + "Search or load articles, then tap Save."
            );
            emptyView.setTextColor(0xFF6C7C77);
            emptyView.setTextSize(14);
            emptyView.setPadding(dp(8), dp(20), dp(8), dp(20));

            articleContainer.addView(emptyView);
        }
    }

    private void shareArticle(Article article) {
        if (article == null
                || article.link == null
                || article.link.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Article link is unavailable",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        StringBuilder message = new StringBuilder();

        message.append(article.title.trim());

        if (article.source != null
                && !article.source.trim().isEmpty()) {
            message.append("\nSource: ")
                    .append(article.source.trim());
        }

        message.append("\n\nRead the full article:\n")
                .append(article.link.trim());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message.toString());

        Intent chooser = Intent.createChooser(
                shareIntent,
                "Share fitness article"
        );

        startActivity(chooser);
    }

    private boolean isArticleBookmarked(String link) {
        if (link == null || link.isEmpty()) {
            return false;
        }

        return getSharedPreferences(BOOKMARKS_PREFS, MODE_PRIVATE)
                .getStringSet(BOOKMARKS_KEY, new HashSet<>())
                .contains(link);
    }

    private boolean toggleBookmark(Article article) {
        if (article.link == null || article.link.isEmpty()) {
            return false;
        }

        SharedPreferences prefs =
                getSharedPreferences(BOOKMARKS_PREFS, MODE_PRIVATE);

        Set<String> savedLinks = new HashSet<>(
                prefs.getStringSet(BOOKMARKS_KEY, new HashSet<>())
        );

        boolean saved;

        if (savedLinks.contains(article.link)) {
            savedLinks.remove(article.link);
            saved = false;
        } else {
            savedLinks.add(article.link);
            saved = true;
        }

        prefs.edit()
                .putStringSet(BOOKMARKS_KEY, savedLinks)
                .apply();

        return saved;
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable createRoundedBackground(
            int fillColor,
            int strokeColor,
            int radiusDp) {

        GradientDrawable background = new GradientDrawable();

        background.setColor(fillColor);
        background.setCornerRadius(dp(radiusDp));
        background.setStroke(dp(1), strokeColor);

        return background;
    }

    private static class Article {
        final String title;
        final String link;
        final String date;
        final String source;

        Article(String title, String link, String date, String source) {
            this.title = title;
            this.link = link;
            this.date = date;
            this.source = source;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}