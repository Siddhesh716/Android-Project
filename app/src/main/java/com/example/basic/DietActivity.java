package com.example.basic;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {

    private static final String DIET_PREFS =
            "DietPlans";

    private static final String PROGRESS_PREFS =
            "DietProgress";

    /*
     * Version used to replace old saved diet plans with
     * the new USDA-supported diet plans.
     */
    private static final String DIET_PLAN_VERSION_KEY =
            "diet_plan_version";

    private static final int CURRENT_DIET_PLAN_VERSION =
            2;


    private HashMap<String, String> dietPlans;

    private OnlineNutritionService onlineNutritionService;


    private TextView breakfastText;
    private TextView lunchText;
    private TextView dinnerText;

    private TextView breakfastTime;
    private TextView lunchTime;
    private TextView dinnerTime;


    private TextView nutritionScoreText;
    private TextView nutritionStatusText;
    private TextView mealsCompletedText;
    private TextView progressCountText;


    private TextView waterCountText;
    private TextView nutritionTipText;


    private ProgressBar dietProgressBar;
    private ProgressBar waterProgressBar;


    private Button breakfastDoneButton;
    private Button lunchDoneButton;
    private Button dinnerDoneButton;
    private Button addWaterButton;


    private Button editButton;
    private Button saveButton;
    private EditText editDietPlan;


    private TextView dayMon;
    private TextView dayTue;
    private TextView dayWed;
    private TextView dayThu;
    private TextView dayFri;
    private TextView daySat;
    private TextView daySun;


    private CardView breakfastCard;
    private CardView lunchCard;
    private CardView dinnerCard;


    private View breakfastNutritionPanel;
    private View lunchNutritionPanel;
    private View dinnerNutritionPanel;


    private TextView breakfastNutritionText;
    private TextView lunchNutritionText;
    private TextView dinnerNutritionText;


    private TextView breakfastSourceText;
    private TextView lunchSourceText;
    private TextView dinnerSourceText;


    private String selectedDay;

    private int completedMeals = 0;

    private int waterUnits = 4;


    private final int SELECTED_COLOR =
            Color.rgb(25, 135, 84);

    private final int SELECTED_TEXT_COLOR =
            Color.WHITE;

    private final int NORMAL_TEXT_COLOR =
            Color.rgb(23, 34, 29);

    private final int NORMAL_BACKGROUND_COLOR =
            Color.WHITE;

    private final int NORMAL_BORDER_COLOR =
            Color.rgb(224, 230, 226);


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_diet
        );


        initializeViews();

        initializeDietPlans();
        loadSavedDietPlans();

        onlineNutritionService =
                new OnlineNutritionService();

        setupDaySelector();

        setupMealButtons();

        setupMealNutritionPanels();

        setupWaterButton();

        setupEditFunction();

        loadCurrentDay();
    }


    // =========================================================
    // VIEWS
    // =========================================================

    private void initializeViews() {

        breakfastText =
                findViewById(
                        R.id.breakfast_text
                );

        lunchText =
                findViewById(
                        R.id.lunch_text
                );

        dinnerText =
                findViewById(
                        R.id.dinner_text
                );


        breakfastTime =
                findViewById(
                        R.id.breakfast_time
                );

        lunchTime =
                findViewById(
                        R.id.lunch_time
                );

        dinnerTime =
                findViewById(
                        R.id.dinner_time
                );


        nutritionScoreText =
                findViewById(
                        R.id.nutrition_score_text
                );

        nutritionStatusText =
                findViewById(
                        R.id.nutrition_status_text
                );

        mealsCompletedText =
                findViewById(
                        R.id.meals_completed_text
                );

        progressCountText =
                findViewById(
                        R.id.progress_count_text
                );


        waterCountText =
                findViewById(
                        R.id.water_count_text
                );

        nutritionTipText =
                findViewById(
                        R.id.nutrition_tip_text
                );


        dietProgressBar =
                findViewById(
                        R.id.diet_progress_bar
                );

        waterProgressBar =
                findViewById(
                        R.id.water_progress_bar
                );


        breakfastDoneButton =
                findViewById(
                        R.id.breakfast_done_button
                );

        lunchDoneButton =
                findViewById(
                        R.id.lunch_done_button
                );

        dinnerDoneButton =
                findViewById(
                        R.id.dinner_done_button
                );

        addWaterButton =
                findViewById(
                        R.id.add_water_button
                );


        editButton =
                findViewById(
                        R.id.edit_button
                );

        saveButton =
                findViewById(
                        R.id.save_diet_plan_button
                );

        editDietPlan =
                findViewById(
                        R.id.edit_diet_plan
                );


        dayMon =
                findViewById(
                        R.id.day_mon
                );

        dayTue =
                findViewById(
                        R.id.day_tue
                );

        dayWed =
                findViewById(
                        R.id.day_wed
                );

        dayThu =
                findViewById(
                        R.id.day_thu
                );

        dayFri =
                findViewById(
                        R.id.day_fri
                );

        daySat =
                findViewById(
                        R.id.day_sat
                );

        daySun =
                findViewById(
                        R.id.day_sun
                );


        breakfastCard =
                findViewById(
                        R.id.breakfast_card
                );

        lunchCard =
                findViewById(
                        R.id.lunch_card
                );

        dinnerCard =
                findViewById(
                        R.id.dinner_card
                );


        breakfastNutritionPanel =
                findViewById(
                        R.id.breakfast_nutrition_panel
                );

        lunchNutritionPanel =
                findViewById(
                        R.id.lunch_nutrition_panel
                );

        dinnerNutritionPanel =
                findViewById(
                        R.id.dinner_nutrition_panel
                );


        breakfastNutritionText =
                findViewById(
                        R.id.breakfast_nutrition_text
                );

        lunchNutritionText =
                findViewById(
                        R.id.lunch_nutrition_text
                );

        dinnerNutritionText =
                findViewById(
                        R.id.dinner_nutrition_text
                );


        breakfastSourceText =
                findViewById(
                        R.id.breakfast_source_text
                );

        lunchSourceText =
                findViewById(
                        R.id.lunch_source_text
                );

        dinnerSourceText =
                findViewById(
                        R.id.dinner_source_text
                );
    }


    // =========================================================
    // DEFAULT DIET PLANS
    // =========================================================

    private void initializeDietPlans() {

        dietPlans =
                new HashMap<>();


        // =====================================================
        // MONDAY
        // =====================================================

        dietPlans.put(
                "Monday",
                "🥣 Breakfast: 50 g Oats + 1 Banana\n" +
                        "🍛 Lunch: 150 g Brown Rice + 100 g Broccoli + 100 g Carrot\n" +
                        "🍽 Dinner: 150 g Chicken Breast + 100 g Spinach"
        );


        // =====================================================
        // TUESDAY
        // =====================================================

        dietPlans.put(
                "Tuesday",
                "🥣 Breakfast: 100 g Egg + 200 g Milk + 1 Apple\n" +
                        "🍛 Lunch: 150 g White Rice + 150 g Lentils + 100 g Broccoli\n" +
                        "🍽 Dinner: 150 g Salmon + 150 g Potato"
        );


        // =====================================================
        // WEDNESDAY
        // =====================================================

        dietPlans.put(
                "Wednesday",
                "🥣 Breakfast: 50 g Oats + 150 g Greek Yogurt + 1 Orange\n" +
                        "🍛 Lunch: 150 g Brown Rice + 150 g Chickpeas + 100 g Carrot\n" +
                        "🍽 Dinner: 150 g Chicken Breast + 100 g Sweet Potato + 100 g Spinach"
        );


        // =====================================================
        // THURSDAY
        // =====================================================

        dietPlans.put(
                "Thursday",
                "🥣 Breakfast: 2 Eggs + 1 Banana + 20 g Almonds\n" +
                        "🍛 Lunch: 150 g White Rice + 150 g Lentils + 100 g Broccoli\n" +
                        "🍽 Dinner: 150 g Tuna + 150 g Potato + 100 g Carrot"
        );


        // =====================================================
        // FRIDAY
        // =====================================================

        dietPlans.put(
                "Friday",
                "🥣 Breakfast: 50 g Oats + 200 g Milk + 1 Apple\n" +
                        "🍛 Lunch: 150 g Brown Rice + 150 g Chickpeas + 100 g Spinach\n" +
                        "🍽 Dinner: 150 g Chicken Breast + 150 g Sweet Potato + 100 g Broccoli"
        );


        // =====================================================
        // SATURDAY
        // =====================================================

        dietPlans.put(
                "Saturday",
                "🥣 Breakfast: 150 g Greek Yogurt + 1 Banana + 20 g Almonds\n" +
                        "🍛 Lunch: 150 g White Rice + 150 g Lentils + 100 g Carrot\n" +
                        "🍽 Dinner: 150 g Salmon + 150 g Sweet Potato + 100 g Spinach"
        );


        // =====================================================
        // SUNDAY
        // =====================================================

        dietPlans.put(
                "Sunday",
                "🥣 Breakfast: 2 Eggs + 50 g Oats + 1 Orange\n" +
                        "🍛 Lunch: 150 g Brown Rice + 150 g Chickpeas + 100 g Broccoli\n" +
                        "🍽 Dinner: 150 g Tuna + 150 g Potato + 100 g Carrot"
        );
    }


    // =========================================================
    // PERSISTENT DIET PLANS
    // =========================================================

    private void loadSavedDietPlans() {

        SharedPreferences preferences =
                getSharedPreferences(
                        DIET_PREFS,
                        MODE_PRIVATE
                );


        int savedVersion =
                preferences.getInt(
                        DIET_PLAN_VERSION_KEY,
                        0
                );


        /*
         * If this is the first run of the new diet system,
         * remove the old saved plans so that the new USDA-based
         * default plans are actually displayed.
         *
         * User-edited plans will continue to work normally
         * after this version is stored.
         */
        if (savedVersion != CURRENT_DIET_PLAN_VERSION) {

            preferences.edit()
                    .remove("Monday")
                    .remove("Tuesday")
                    .remove("Wednesday")
                    .remove("Thursday")
                    .remove("Friday")
                    .remove("Saturday")
                    .remove("Sunday")
                    .putInt(
                            DIET_PLAN_VERSION_KEY,
                            CURRENT_DIET_PLAN_VERSION
                    )
                    .apply();

            return;
        }


        String[] days = {
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };


        for (String day : days) {

            String savedPlan =
                    preferences.getString(
                            day,
                            null
                    );


            if (savedPlan != null &&
                    !savedPlan.trim().isEmpty()) {

                dietPlans.put(
                        day,
                        savedPlan
                );
            }
        }
    }


    private void saveDietPlan(
            String day,
            String plan
    ) {

        getSharedPreferences(
                DIET_PREFS,
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        day,
                        plan
                )
                .putInt(
                        DIET_PLAN_VERSION_KEY,
                        CURRENT_DIET_PLAN_VERSION
                )
                .apply();
    }


    // =========================================================
    // DAY SELECTOR
    // =========================================================

    private void setupDaySelector() {

        dayMon.setOnClickListener(
                v -> selectDay("Monday")
        );

        dayTue.setOnClickListener(
                v -> selectDay("Tuesday")
        );

        dayWed.setOnClickListener(
                v -> selectDay("Wednesday")
        );

        dayThu.setOnClickListener(
                v -> selectDay("Thursday")
        );

        dayFri.setOnClickListener(
                v -> selectDay("Friday")
        );

        daySat.setOnClickListener(
                v -> selectDay("Saturday")
        );

        daySun.setOnClickListener(
                v -> selectDay("Sunday")
        );
    }


    private void selectDay(
            String day
    ) {

        selectedDay = day;

        updateDaySelector();

        displayDietPlan();

        loadMealCompletion();

        loadWaterProgress();

        updateNutritionTip();

        updateEditButtonState();

        collapseNutritionPanels();
    }


    private void updateDaySelector() {

        resetDayStyle(dayMon);
        resetDayStyle(dayTue);
        resetDayStyle(dayWed);
        resetDayStyle(dayThu);
        resetDayStyle(dayFri);
        resetDayStyle(daySat);
        resetDayStyle(daySun);


        switch (selectedDay) {

            case "Monday":
                setSelectedDayStyle(dayMon);
                break;

            case "Tuesday":
                setSelectedDayStyle(dayTue);
                break;

            case "Wednesday":
                setSelectedDayStyle(dayWed);
                break;

            case "Thursday":
                setSelectedDayStyle(dayThu);
                break;

            case "Friday":
                setSelectedDayStyle(dayFri);
                break;

            case "Saturday":
                setSelectedDayStyle(daySat);
                break;

            case "Sunday":
                setSelectedDayStyle(daySun);
                break;
        }
    }


    private void setSelectedDayStyle(
            TextView dayView
    ) {

        GradientDrawable background =
                new GradientDrawable();

        background.setShape(
                GradientDrawable.RECTANGLE
        );

        background.setColor(
                SELECTED_COLOR
        );

        background.setCornerRadius(
                18f *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );

        dayView.setBackground(
                background
        );

        dayView.setTextColor(
                SELECTED_TEXT_COLOR
        );
    }


    private void resetDayStyle(
            TextView dayView
    ) {

        GradientDrawable background =
                new GradientDrawable();

        background.setShape(
                GradientDrawable.RECTANGLE
        );

        background.setColor(
                NORMAL_BACKGROUND_COLOR
        );

        background.setStroke(
                (int) (
                        1 *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                ),
                NORMAL_BORDER_COLOR
        );

        background.setCornerRadius(
                18f *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );

        dayView.setBackground(
                background
        );

        dayView.setTextColor(
                NORMAL_TEXT_COLOR
        );
    }


    private String getTodayDay() {

        return new SimpleDateFormat(
                "EEEE",
                Locale.getDefault()
        )
                .format(
                        Calendar.getInstance()
                                .getTime()
                );
    }


    private boolean isTodaySelected() {

        return selectedDay != null &&
                selectedDay.equals(
                        getTodayDay()
                );
    }


    private void loadCurrentDay() {

        selectedDay =
                getTodayDay();

        updateDaySelector();

        displayDietPlan();

        loadMealCompletion();

        loadWaterProgress();

        updateNutritionTip();

        updateEditButtonState();

        collapseNutritionPanels();
    }


    // =========================================================
    // DISPLAY DIET
    // =========================================================

    private void displayDietPlan() {

        if (selectedDay == null) {
            return;
        }


        String plan =
                dietPlans.get(
                        selectedDay
                );

        if (plan == null ||
                plan.trim().isEmpty()) {

            breakfastText.setText(
                    "No breakfast plan"
            );

            lunchText.setText(
                    "No lunch plan"
            );

            dinnerText.setText(
                    "No dinner plan"
            );

            updateNutritionPanels();

            return;
        }


        String[] meals =
                plan.split("\\n");

        Toast.makeText(
                this,
                "PLAN:\n" + plan,
                Toast.LENGTH_LONG
        ).show();


        String breakfast =
                "Breakfast";

        String lunch =
                "Lunch";

        String dinner =
                "Dinner";


        for (String meal : meals) {

            String lowerMeal =
                    meal.toLowerCase(
                            Locale.getDefault()
                    );


            if (lowerMeal.contains(
                    "breakfast"
            )) {

                breakfast =
                        removeMealLabel(
                                meal
                        );

            } else if (
                    lowerMeal.contains(
                            "lunch"
                    )
            ) {

                lunch =
                        removeMealLabel(
                                meal
                        );

            } else if (
                    lowerMeal.contains(
                            "dinner"
                    )
            ) {

                dinner =
                        removeMealLabel(
                                meal
                        );
            }
        }


        breakfastText.setText(
                breakfast
        );

        lunchText.setText(
                lunch
        );

        dinnerText.setText(
                dinner
        );


        breakfastTime.setText(
                "7:30 AM"
        );

        lunchTime.setText(
                "1:00 PM"
        );

        dinnerTime.setText(
                "8:00 PM"
        );


        editDietPlan.setText(
                plan
        );


        updateNutritionPanels();
    }


    private String removeMealLabel(
            String meal
    ) {

        String result =
                meal.trim();


        int colonPosition =
                result.indexOf(":");


        if (colonPosition >= 0) {

            result =
                    result.substring(
                                    colonPosition + 1
                            )
                            .trim();
        }


        return result;
    }


    // =========================================================
    // NUTRITION PANELS
    // =========================================================

    private void setupMealNutritionPanels() {

        breakfastCard.setOnClickListener(
                v -> toggleNutritionPanel(
                        breakfastNutritionPanel
                )
        );


        lunchCard.setOnClickListener(
                v -> toggleNutritionPanel(
                        lunchNutritionPanel
                )
        );


        dinnerCard.setOnClickListener(
                v -> toggleNutritionPanel(
                        dinnerNutritionPanel
                )
        );


        updateNutritionPanels();
    }


    private void toggleNutritionPanel(
            View panel
    ) {

        if (panel.getVisibility() ==
                View.VISIBLE) {

            panel.setVisibility(
                    View.GONE
            );

        } else {

            panel.setVisibility(
                    View.VISIBLE
            );
        }
    }


    private void collapseNutritionPanels() {

        if (breakfastNutritionPanel != null) {

            breakfastNutritionPanel.setVisibility(
                    View.GONE
            );
        }


        if (lunchNutritionPanel != null) {

            lunchNutritionPanel.setVisibility(
                    View.GONE
            );
        }


        if (dinnerNutritionPanel != null) {

            dinnerNutritionPanel.setVisibility(
                    View.GONE
            );
        }
    }


    private void updateNutritionPanels() {

        if (onlineNutritionService == null) {
            return;
        }


        String breakfast =
                breakfastText.getText()
                        .toString()
                        .trim();


        String lunch =
                lunchText.getText()
                        .toString()
                        .trim();


        String dinner =
                dinnerText.getText()
                        .toString()
                        .trim();


        breakfastNutritionText.setText(
                "Loading nutrition data..."
        );

        breakfastSourceText.setText(
                "Retrieving data from USDA FoodData Central..."
        );


        lunchNutritionText.setText(
                "Loading nutrition data..."
        );

        lunchSourceText.setText(
                "Retrieving data from USDA FoodData Central..."
        );


        dinnerNutritionText.setText(
                "Loading nutrition data..."
        );

        dinnerSourceText.setText(
                "Retrieving data from USDA FoodData Central..."
        );


        calculateOnlineMealNutrition(
                breakfast,
                breakfastNutritionText,
                breakfastSourceText
        );


        calculateOnlineMealNutrition(
                lunch,
                lunchNutritionText,
                lunchSourceText
        );


        calculateOnlineMealNutrition(
                dinner,
                dinnerNutritionText,
                dinnerSourceText
        );
    }


    private void calculateOnlineMealNutrition(
            String mealText,
            TextView nutritionText,
            TextView sourceText
    ) {

        if (mealText == null ||
                mealText.trim().isEmpty()) {

            displayNutrition(
                    null,
                    nutritionText,
                    sourceText
            );

            return;
        }


        String[] foods =
                mealText.split("\\+");


        calculateFoodSequentially(
                foods,
                0,
                null,
                nutritionText,
                sourceText
        );
    }


    private void calculateFoodSequentially(
            String[] foods,
            int index,
            NutritionInfo total,
            TextView nutritionText,
            TextView sourceText
    ) {

        if (foods == null ||
                index >= foods.length) {

            if (total == null) {

                displayNutrition(
                        null,
                        nutritionText,
                        sourceText
                );

            } else {

                displayNutrition(
                        total,
                        nutritionText,
                        sourceText
                );
            }

            return;
        }


        String foodName =
                foods[index]
                        .trim();


        if (foodName.isEmpty()) {

            calculateFoodSequentially(
                    foods,
                    index + 1,
                    total,
                    nutritionText,
                    sourceText
            );

            return;
        }


        onlineNutritionService.getNutritionForFood(
                foodName,
                new OnlineNutritionService.NutritionCallback() {

                    @Override
                    public void onSuccess(
                            NutritionInfo nutritionInfo
                    ) {

                        NutritionInfo newTotal;


                        if (total == null) {

                            newTotal =
                                    nutritionInfo;

                        } else {

                            newTotal =
                                    total.add(
                                            nutritionInfo
                                    );
                        }


                        calculateFoodSequentially(
                                foods,
                                index + 1,
                                newTotal,
                                nutritionText,
                                sourceText
                        );
                    }


                    @Override
                    public void onError(
                            String message
                    ) {

                        nutritionText.setText(
                                "Nutrition data is not available " +
                                        "for all ingredients in this meal."
                        );


                        sourceText.setText(
                                message
                        );
                    }
                }
        );
    }


    private void displayNutrition(
            NutritionInfo nutrition,
            TextView nutritionText,
            TextView sourceText
    ) {

        if (nutrition == null) {

            nutritionText.setText(
                    "Nutrition data is not available " +
                            "for all ingredients in this meal."
            );


            sourceText.setText(
                    "No verified online source record was found."
            );


            return;
        }


        nutritionText.setText(
                formatNutrition(
                        nutrition
                )
        );


        sourceText.setText(
                "Source: USDA FoodData Central"
        );
    }


    private String formatNutrition(
            NutritionInfo nutrition
    ) {

        return
                "Calories       " +
                        formatNumber(
                                nutrition.getCalories()
                        ) +
                        " kcal\n" +

                        "Protein        " +
                        formatNumber(
                                nutrition.getProtein()
                        ) +
                        " g\n" +

                        "Carbohydrates  " +
                        formatNumber(
                                nutrition.getCarbohydrates()
                        ) +
                        " g\n" +

                        "Fibre          " +
                        formatNumber(
                                nutrition.getFibre()
                        ) +
                        " g\n" +

                        "Fat            " +
                        formatNumber(
                                nutrition.getFat()
                        ) +
                        " g\n\n" +

                        "Calcium        " +
                        formatNumber(
                                nutrition.getCalcium()
                        ) +
                        " mg\n" +

                        "Iron           " +
                        formatNumber(
                                nutrition.getIron()
                        ) +
                        " mg\n" +

                        "Magnesium      " +
                        formatNumber(
                                nutrition.getMagnesium()
                        ) +
                        " mg\n" +

                        "Potassium      " +
                        formatNumber(
                                nutrition.getPotassium()
                        ) +
                        " mg\n" +

                        "Sodium         " +
                        formatNumber(
                                nutrition.getSodium()
                        ) +
                        " mg\n\n" +

                        "Vitamin A      " +
                        formatNumber(
                                nutrition.getVitaminA()
                        ) +
                        " µg\n" +

                        "Vitamin C      " +
                        formatNumber(
                                nutrition.getVitaminC()
                        ) +
                        " mg\n" +

                        "Vitamin D      " +
                        formatNumber(
                                nutrition.getVitaminD()
                        ) +
                        " µg\n" +

                        "Vitamin E      " +
                        formatNumber(
                                nutrition.getVitaminE()
                        ) +
                        " mg\n" +

                        "Vitamin K      " +
                        formatNumber(
                                nutrition.getVitaminK()
                        ) +
                        " µg\n\n" +

                        "Vitamin B1     " +
                        formatNumber(
                                nutrition.getVitaminB1()
                        ) +
                        " mg\n" +

                        "Vitamin B2     " +
                        formatNumber(
                                nutrition.getVitaminB2()
                        ) +
                        " mg\n" +

                        "Vitamin B6     " +
                        formatNumber(
                                nutrition.getVitaminB6()
                        ) +
                        " mg\n" +

                        "Vitamin B12    " +
                        formatNumber(
                                nutrition.getVitaminB12()
                        ) +
                        " µg\n" +

                        "Folate         " +
                        formatNumber(
                                nutrition.getFolate()
                        ) +
                        " µg\n\n" +

                        "Serving        " +
                        nutrition.getServing();
    }


    private String formatNumber(
            double value
    ) {

        if (value == Math.floor(value)) {

            return String.format(
                    Locale.getDefault(),
                    "%.0f",
                    value
            );
        }


        return String.format(
                Locale.getDefault(),
                "%.1f",
                value
        );
    }


    // =========================================================
    // MEAL COMPLETION
    // =========================================================

    private void setupMealButtons() {

        breakfastDoneButton.setOnClickListener(
                v -> completeMeal("breakfast")
        );

        lunchDoneButton.setOnClickListener(
                v -> completeMeal("lunch")
        );

        dinnerDoneButton.setOnClickListener(
                v -> completeMeal("dinner")
        );
    }


    private void completeMeal(
            String meal
    ) {

        if (selectedDay == null) {
            return;
        }


        if (!isTodaySelected()) {

            Toast.makeText(
                    this,
                    "You can only update today's meals.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        SharedPreferences preferences =
                getSharedPreferences(
                        PROGRESS_PREFS,
                        MODE_PRIVATE
                );


        String key =
                selectedDay +
                        "_" +
                        meal;


        boolean alreadyCompleted =
                preferences.getBoolean(
                        key,
                        false
                );


        if (alreadyCompleted) {
            return;
        }


        preferences.edit()
                .putBoolean(
                        key,
                        true
                )
                .apply();


        loadMealCompletion();
    }


    private void loadMealCompletion() {

        if (selectedDay == null) {
            return;
        }


        SharedPreferences preferences =
                getSharedPreferences(
                        PROGRESS_PREFS,
                        MODE_PRIVATE
                );


        boolean breakfastDone =
                preferences.getBoolean(
                        selectedDay +
                                "_breakfast",
                        false
                );


        boolean lunchDone =
                preferences.getBoolean(
                        selectedDay +
                                "_lunch",
                        false
                );


        boolean dinnerDone =
                preferences.getBoolean(
                        selectedDay +
                                "_dinner",
                        false
                );


        completedMeals = 0;


        if (breakfastDone) {
            completedMeals++;
        }


        if (lunchDone) {
            completedMeals++;
        }


        if (dinnerDone) {
            completedMeals++;
        }


        updateMealButtons(
                breakfastDone,
                lunchDone,
                dinnerDone
        );


        updateNutritionProgress();
    }


    private void updateMealButtons(
            boolean breakfastDone,
            boolean lunchDone,
            boolean dinnerDone
    ) {

        breakfastDoneButton.setText(
                breakfastDone
                        ? "✓  MEAL COMPLETED"
                        : "MARK AS EATEN"
        );


        lunchDoneButton.setText(
                lunchDone
                        ? "✓  MEAL COMPLETED"
                        : "MARK AS EATEN"
        );


        dinnerDoneButton.setText(
                dinnerDone
                        ? "✓  MEAL COMPLETED"
                        : "MARK AS EATEN"
        );


        boolean today =
                isTodaySelected();


        breakfastDoneButton.setEnabled(
                today && !breakfastDone
        );


        lunchDoneButton.setEnabled(
                today && !lunchDone
        );


        dinnerDoneButton.setEnabled(
                today && !dinnerDone
        );


        breakfastDoneButton.setAlpha(
                breakfastDone
                        ? 0.65f
                        : today
                        ? 1.0f
                        : 0.45f
        );


        lunchDoneButton.setAlpha(
                lunchDone
                        ? 0.65f
                        : today
                        ? 1.0f
                        : 0.45f
        );


        dinnerDoneButton.setAlpha(
                dinnerDone
                        ? 0.65f
                        : today
                        ? 1.0f
                        : 0.45f
        );
    }


    private void updateNutritionProgress() {

        int percentage =
                (completedMeals * 100) / 3;


        nutritionScoreText.setText(
                percentage + "%"
        );


        mealsCompletedText.setText(
                completedMeals +
                        " / 3 meals completed"
        );


        progressCountText.setText(
                completedMeals +
                        " / 3"
        );


        dietProgressBar.setProgress(
                completedMeals
        );


        if (completedMeals == 0) {

            nutritionStatusText.setText(
                    "GET STARTED"
            );

        } else if (completedMeals == 1) {

            nutritionStatusText.setText(
                    "GOOD START"
            );

        } else if (completedMeals == 2) {

            nutritionStatusText.setText(
                    "ALMOST THERE"
            );

        } else {

            nutritionStatusText.setText(
                    "ALL MEALS COMPLETE"
            );
        }
    }


    // =========================================================
    // WATER
    // =========================================================

    private void setupWaterButton() {

        addWaterButton.setOnClickListener(
                v -> {

                    if (!isTodaySelected()) {

                        Toast.makeText(
                                this,
                                "You can only update today's hydration.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    SharedPreferences preferences =
                            getSharedPreferences(
                                    PROGRESS_PREFS,
                                    MODE_PRIVATE
                            );


                    if (waterUnits < 16) {

                        waterUnits++;


                        preferences.edit()
                                .putInt(
                                        getWaterKey(),
                                        waterUnits
                                )
                                .apply();


                        updateWaterProgress();

                    } else {

                        Toast.makeText(
                                this,
                                "Daily hydration target reached.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        loadWaterProgress();
    }


    private String getWaterKey() {

        if (selectedDay == null) {

            return "WATER_UNITS";
        }


        return selectedDay +
                "_WATER_UNITS";
    }


    private void loadWaterProgress() {

        if (selectedDay == null) {
            return;
        }


        SharedPreferences preferences =
                getSharedPreferences(
                        PROGRESS_PREFS,
                        MODE_PRIVATE
                );


        waterUnits =
                preferences.getInt(
                        getWaterKey(),
                        4
                );


        if (waterUnits < 0) {
            waterUnits = 0;
        }


        if (waterUnits > 16) {
            waterUnits = 16;
        }


        updateWaterProgress();
    }


    private void updateWaterProgress() {

        double litres =
                waterUnits * 0.25;


        String litreText;


        if (litres ==
                Math.floor(litres)) {

            litreText =
                    String.format(
                            Locale.getDefault(),
                            "%.0f",
                            litres
                    );

        } else {

            litreText =
                    String.format(
                            Locale.getDefault(),
                            "%.2f",
                            litres
                    );
        }


        waterCountText.setText(
                litreText +
                        " / 4 L"
        );


        waterProgressBar.setMax(
                16
        );


        waterProgressBar.setProgress(
                waterUnits
        );


        if (!isTodaySelected()) {

            addWaterButton.setText(
                    "+  ADD WATER"
            );

            addWaterButton.setEnabled(
                    false
            );

            addWaterButton.setAlpha(
                    0.45f
            );

            return;
        }


        if (waterUnits >= 16) {

            addWaterButton.setText(
                    "✓  DAILY TARGET REACHED"
            );

            addWaterButton.setEnabled(
                    false
            );

            addWaterButton.setAlpha(
                    0.65f
            );

        } else {

            addWaterButton.setText(
                    "+  ADD WATER"
            );

            addWaterButton.setEnabled(
                    true
            );

            addWaterButton.setAlpha(
                    1.0f
            );
        }
    }


    // =========================================================
    // NUTRITION TIP
    // =========================================================

    private void updateNutritionTip() {

        if (selectedDay == null) {
            return;
        }


        String tip;


        switch (selectedDay) {

            case "Monday":

                tip =
                        "Start the week with a balanced breakfast and stay consistent with your meals.";

                break;


            case "Tuesday":

                tip =
                        "Keep your meals balanced and remember to drink water throughout the day.";

                break;


            case "Wednesday":

                tip =
                        "Include a variety of foods in your meals to keep your nutrition balanced.";

                break;


            case "Thursday":

                tip =
                        "Try to keep your meals consistent around your daily training routine.";

                break;


            case "Friday":

                tip =
                        "A balanced dinner can help you finish the week with good nutrition habits.";

                break;


            case "Saturday":

                tip =
                        "Enjoy your meals while keeping hydration and portion awareness in mind.";

                break;


            case "Sunday":

                tip =
                        "Use Sunday to stay consistent and prepare yourself for the week ahead.";

                break;


            default:

                tip =
                        "Stay hydrated and keep your meals balanced throughout the day.";

                break;
        }


        nutritionTipText.setText(
                tip
        );
    }


    // =========================================================
    // EDIT DIET PLAN
    // =========================================================

    private void setupEditFunction() {

        editDietPlan.setVisibility(
                View.GONE
        );


        saveButton.setVisibility(
                View.GONE
        );


        editButton.setVisibility(
                View.VISIBLE
        );

        updateEditButtonState();


        editButton.setOnClickListener(
                v -> {

                    editDietPlan.setVisibility(
                            View.VISIBLE
                    );


                    saveButton.setVisibility(
                            View.VISIBLE
                    );


                    editButton.setVisibility(
                            View.GONE
                    );


                    editDietPlan.requestFocus();
                }
        );


        saveButton.setOnClickListener(
                v -> saveEditedDietPlan()
        );
    }

    private void updateEditButtonState() {

        boolean todaySelected =
                isTodaySelected();

        editButton.setEnabled(
                todaySelected
        );

        editButton.setAlpha(
                todaySelected
                        ? 1.0f
                        : 0.45f
        );
    }


    private void saveEditedDietPlan() {

        if (selectedDay == null) {

            Toast.makeText(
                    this,
                    "Please select a day first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String updatedPlan =
                editDietPlan
                        .getText()
                        .toString()
                        .trim();


        if (updatedPlan.isEmpty()) {

            Toast.makeText(
                    this,
                    "Diet plan cannot be empty.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        dietPlans.put(
                selectedDay,
                updatedPlan
        );


        saveDietPlan(
                selectedDay,
                updatedPlan
        );


        displayDietPlan();


        editDietPlan.setVisibility(
                View.GONE
        );


        saveButton.setVisibility(
                View.GONE
        );


        editButton.setVisibility(
                View.VISIBLE
        );


        Toast.makeText(
                this,
                "Diet plan updated and saved.",
                Toast.LENGTH_SHORT
        ).show();
    }
}