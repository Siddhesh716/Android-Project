package com.example.basic;

import java.util.HashMap;
import java.util.Map;

public class NutritionCatalog {

    private final Map<String, NutritionInfo> foods;

    public NutritionCatalog() {
        foods = new HashMap<>();
        initializeCatalog();
    }

    private void initializeCatalog() {

        /*
         * Nutrition records used by the current Diet Plans.
         *
         * Source hierarchy:
         * 1. ICMR-NIN IFCT 2017 for Indian food composition.
         * 2. USDA FoodData Central where an appropriate record is needed.
         *
         * IMPORTANT:
         * Composite meals below are standardized recipe records.
         * They are NOT claimed to be individual IFCT food records.
         *
         * Serving sizes are fixed for this first implementation.
         */


        // =========================================================
        // BASIC FOODS
        // =========================================================

        addFood(
                "banana",
                new NutritionInfo(
                        105.0,
                        1.29,
                        26.95,
                        0.39,
                        3.07,
                        6.0,
                        0.31,
                        32.0,
                        422.0,
                        1.0,
                        3.0,
                        10.3,
                        0.0,
                        0.12,
                        0.59,
                        0.04,
                        0.10,
                        0.43,
                        0.0,
                        24.0,
                        "1 medium banana",
                        "USDA FoodData Central — banana, raw"
                )
        );


        addFood(
                "oats",
                new NutritionInfo(
                        190.0,
                        6.3,
                        32.1,
                        3.5,
                        5.1,
                        21.0,
                        2.0,
                        79.0,
                        164.0,
                        2.0,
                        0.0,
                        0.0,
                        0.0,
                        0.34,
                        0.0,
                        0.19,
                        0.08,
                        0.12,
                        0.0,
                        28.0,
                        "50 g dry oats",
                        "USDA FoodData Central — oats"
                )
        );


        addFood(
                "toast",
                new NutritionInfo(
                        140.0,
                        5.0,
                        26.0,
                        2.0,
                        2.0,
                        80.0,
                        1.8,
                        20.0,
                        80.0,
                        250.0,
                        0.0,
                        0.0,
                        0.0,
                        0.15,
                        0.0,
                        0.15,
                        0.10,
                        0.08,
                        0.0,
                        45.0,
                        "2 slices",
                        "USDA FoodData Central — toasted bread"
                )
        );


        addFood(
                "yogurt",
                new NutritionInfo(
                        92.0,
                        5.0,
                        7.0,
                        4.0,
                        0.0,
                        180.0,
                        0.05,
                        17.0,
                        234.0,
                        58.0,
                        31.0,
                        0.5,
                        0.0,
                        0.06,
                        0.0,
                        0.04,
                        0.14,
                        0.05,
                        0.45,
                        12.0,
                        "150 g",
                        "USDA FoodData Central — plain yogurt"
                )
        );


        addFood(
                "egg",
                new NutritionInfo(
                        144.0,
                        12.6,
                        0.72,
                        9.51,
                        0.0,
                        56.0,
                        1.75,
                        12.0,
                        138.0,
                        142.0,
                        160.0,
                        0.0,
                        2.0,
                        1.05,
                        0.3,
                        0.04,
                        0.46,
                        0.12,
                        1.11,
                        44.0,
                        "2 eggs",
                        "USDA FoodData Central — whole egg"
                )
        );


        addAlias("eggs", "egg");


        addFood(
                "honey",
                new NutritionInfo(
                        46.0,
                        0.04,
                        12.5,
                        0.0,
                        0.0,
                        1.0,
                        0.04,
                        0.4,
                        6.0,
                        0.6,
                        0.0,
                        0.1,
                        0.0,
                        0.0,
                        0.0,
                        0.01,
                        0.01,
                        0.01,
                        0.0,
                        0.5,
                        "1 tablespoon",
                        "USDA FoodData Central — honey"
                )
        );


        addFood(
                "quinoa",
                new NutritionInfo(
                        222.0,
                        8.0,
                        39.4,
                        3.6,
                        5.2,
                        31.0,
                        2.8,
                        118.0,
                        318.0,
                        13.0,
                        0.0,
                        0.0,
                        0.0,
                        1.2,
                        0.0,
                        0.20,
                        0.20,
                        0.20,
                        0.0,
                        42.0,
                        "150 g cooked",
                        "USDA FoodData Central — cooked quinoa"
                )
        );


        addFood(
                "chicken",
                new NutritionInfo(
                        248.0,
                        46.5,
                        0.0,
                        5.4,
                        0.0,
                        21.0,
                        1.0,
                        40.0,
                        350.0,
                        120.0,
                        5.0,
                        0.0,
                        0.2,
                        0.5,
                        0.0,
                        0.20,
                        0.25,
                        0.70,
                        0.30,
                        8.0,
                        "150 g cooked",
                        "USDA FoodData Central — cooked chicken breast"
                )
        );


        addAlias("grilled chicken", "chicken");


        addFood(
                "fish",
                new NutritionInfo(
                        190.0,
                        39.0,
                        0.0,
                        4.0,
                        0.0,
                        25.0,
                        0.8,
                        35.0,
                        400.0,
                        80.0,
                        20.0,
                        0.0,
                        10.0,
                        1.0,
                        0.0,
                        0.10,
                        0.15,
                        0.50,
                        2.0,
                        15.0,
                        "150 g cooked fish",
                        "USDA FoodData Central — cooked fish"
                )
        );


        addAlias("grilled fish", "fish");


        addFood(
                "tofu",
                new NutritionInfo(
                        144.0,
                        15.6,
                        2.8,
                        8.7,
                        2.3,
                        350.0,
                        2.5,
                        60.0,
                        180.0,
                        15.0,
                        0.0,
                        0.0,
                        0.0,
                        0.1,
                        0.0,
                        0.10,
                        0.10,
                        0.10,
                        0.0,
                        29.0,
                        "150 g",
                        "USDA FoodData Central — tofu"
                )
        );


        // =========================================================
        // EXISTING RICE RECORD
        // =========================================================

        addFood(
                "rice",
                new NutritionInfo(
                        356.55,
                        7.94,
                        78.24,
                        0.52,
                        2.81,
                        7.49,
                        0.65,
                        19.30,
                        108.00,
                        2.34,
                        0.00,
                        0.00,
                        0.00,
                        0.06,
                        0.03,
                        0.05,
                        0.05,
                        0.12,
                        0.00,
                        9.32,
                        "100 g",
                        "IFCT 2017 — Food Code A015"
                )
        );

        addAlias("brown rice", "rice");
        addAlias("white rice", "rice");


        // =========================================================
        // LENTILS
        // =========================================================

        addFood(
                "lentils",
                new NutritionInfo(
                        174.0,
                        12.0,
                        29.0,
                        0.6,
                        7.9,
                        19.0,
                        3.3,
                        36.0,
                        369.0,
                        2.0,
                        0.0,
                        1.5,
                        0.0,
                        0.1,
                        1.7,
                        0.17,
                        0.07,
                        0.18,
                        0.0,
                        181.0,
                        "150 g cooked",
                        "USDA FoodData Central — cooked lentils"
                )
        );


        addAlias("lentil dal", "lentils");
        addAlias("dal", "lentils");


        // =========================================================
        // SALAD
        // =========================================================

        addFood(
                "salad",
                new NutritionInfo(
                        35.0,
                        1.5,
                        7.0,
                        0.3,
                        2.5,
                        35.0,
                        0.7,
                        15.0,
                        220.0,
                        25.0,
                        300.0,
                        25.0,
                        0.0,
                        0.3,
                        40.0,
                        0.04,
                        0.05,
                        0.10,
                        0.0,
                        25.0,
                        "100 g",
                        "Standard mixed raw-vegetable salad recipe"
                )
        );


        // =========================================================
        // COMPOSITE MEALS
        // These records exist so your current DietActivity can
        // display nutrition for the complete named meal.
        // =========================================================


        addFood(
                "veg curry",
                new NutritionInfo(
                        120.0,
                        3.0,
                        12.0,
                        6.0,
                        4.0,
                        45.0,
                        1.2,
                        25.0,
                        300.0,
                        120.0,
                        350.0,
                        25.0,
                        0.0,
                        1.0,
                        10.0,
                        0.08,
                        0.10,
                        0.15,
                        0.0,
                        35.0,
                        "150 g",
                        "Standard mixed-vegetable curry recipe"
                )
        );

        addAlias("vegetable curry", "veg curry");


        addFood(
                "smoothie",
                new NutritionInfo(
                        220.0,
                        8.0,
                        35.0,
                        5.0,
                        4.0,
                        220.0,
                        1.0,
                        45.0,
                        400.0,
                        80.0,
                        90.0,
                        12.0,
                        1.0,
                        0.5,
                        1.0,
                        0.10,
                        0.20,
                        0.20,
                        0.40,
                        25.0,
                        "250 ml",
                        "Standard banana-yogurt smoothie recipe"
                )
        );


        addFood(
                "soup",
                new NutritionInfo(
                        90.0,
                        3.0,
                        14.0,
                        2.5,
                        3.0,
                        40.0,
                        1.0,
                        25.0,
                        300.0,
                        300.0,
                        250.0,
                        15.0,
                        0.0,
                        0.3,
                        10.0,
                        0.08,
                        0.10,
                        0.15,
                        0.0,
                        30.0,
                        "250 g",
                        "Standard mixed-vegetable soup recipe"
                )
        );


        addFood(
                "poha",
                new NutritionInfo(
                        220.0,
                        5.0,
                        42.0,
                        4.0,
                        3.0,
                        30.0,
                        1.5,
                        45.0,
                        180.0,
                        250.0,
                        20.0,
                        15.0,
                        0.0,
                        1.0,
                        2.0,
                        0.10,
                        0.08,
                        0.15,
                        0.0,
                        35.0,
                        "150 g prepared",
                        "Standard poha recipe"
                )
        );


        addFood(
                "green tea",
                new NutritionInfo(
                        2.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        1.0,
                        20.0,
                        2.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        "200 ml",
                        "Plain brewed green tea"
                )
        );


        addFood(
                "pancakes",
                new NutritionInfo(
                        230.0,
                        6.0,
                        32.0,
                        8.0,
                        1.5,
                        100.0,
                        1.5,
                        20.0,
                        110.0,
                        300.0,
                        70.0,
                        0.0,
                        0.5,
                        0.5,
                        0.5,
                        0.08,
                        0.15,
                        0.08,
                        0.5,
                        80.0,
                        "2 pancakes",
                        "Standard pancake recipe"
                )
        );


        addFood(
                "pasta",
                new NutritionInfo(
                        220.0,
                        8.0,
                        43.0,
                        1.3,
                        2.5,
                        20.0,
                        2.0,
                        45.0,
                        130.0,
                        5.0,
                        0.0,
                        0.0,
                        0.0,
                        0.1,
                        0.0,
                        0.10,
                        0.08,
                        0.10,
                        0.0,
                        120.0,
                        "150 g cooked",
                        "USDA FoodData Central — cooked pasta"
                )
        );


        addFood(
                "steamed vegetables",
                new NutritionInfo(
                        100.0,
                        4.0,
                        18.0,
                        1.0,
                        6.0,
                        60.0,
                        1.2,
                        40.0,
                        450.0,
                        50.0,
                        500.0,
                        40.0,
                        0.0,
                        1.0,
                        30.0,
                        0.12,
                        0.15,
                        0.30,
                        0.0,
                        70.0,
                        "200 g",
                        "Standard steamed mixed-vegetable recipe"
                )
        );


        addFood(
                "roasted veggies",
                new NutritionInfo(
                        130.0,
                        4.0,
                        18.0,
                        5.0,
                        5.0,
                        55.0,
                        1.2,
                        35.0,
                        400.0,
                        80.0,
                        450.0,
                        35.0,
                        0.0,
                        1.0,
                        25.0,
                        0.10,
                        0.12,
                        0.25,
                        0.0,
                        60.0,
                        "200 g",
                        "Standard roasted mixed-vegetable recipe"
                )
        );


        addFood(
                "stir-fried tofu",
                new NutritionInfo(
                        230.0,
                        18.0,
                        8.0,
                        15.0,
                        3.0,
                        300.0,
                        3.0,
                        70.0,
                        300.0,
                        300.0,
                        300.0,
                        20.0,
                        0.0,
                        1.0,
                        25.0,
                        0.15,
                        0.12,
                        0.20,
                        0.0,
                        35.0,
                        "150 g prepared",
                        "Standard tofu and vegetable stir-fry recipe"
                )
        );


        addFood(
                "sandwich",
                new NutritionInfo(
                        300.0,
                        14.0,
                        38.0,
                        10.0,
                        5.0,
                        100.0,
                        2.5,
                        35.0,
                        250.0,
                        500.0,
                        250.0,
                        15.0,
                        0.0,
                        1.0,
                        20.0,
                        0.15,
                        0.20,
                        0.20,
                        0.3,
                        60.0,
                        "1 sandwich",
                        "Standard vegetable-protein sandwich recipe"
                )
        );


        addFood(
                "idli",
                new NutritionInfo(
                        174.0,
                        6.0,
                        35.0,
                        1.0,
                        2.0,
                        25.0,
                        1.5,
                        35.0,
                        120.0,
                        200.0,
                        0.0,
                        0.0,
                        0.0,
                        0.1,
                        0.0,
                        0.08,
                        0.06,
                        0.10,
                        0.0,
                        25.0,
                        "3 idli",
                        "Standard idli recipe"
                )
        );


        addFood(
                "coconut chutney",
                new NutritionInfo(
                        90.0,
                        1.0,
                        4.0,
                        8.0,
                        2.0,
                        10.0,
                        0.5,
                        20.0,
                        100.0,
                        80.0,
                        0.0,
                        2.0,
                        0.0,
                        0.3,
                        1.0,
                        0.03,
                        0.02,
                        0.03,
                        0.0,
                        5.0,
                        "50 g",
                        "Standard coconut chutney recipe"
                )
        );


        // =========================================================
        // ALIASES FOR EXISTING PLAN TEXT
        // =========================================================

        addAlias("light salad", "salad");
        addAlias("mixed salad", "salad");

        addAlias("roasted vegetables", "roasted veggies");
        addAlias("steamed vegetable", "steamed vegetables");

        addAlias("stir fried tofu", "stir-fried tofu");
        addAlias("tofu stir fry", "stir-fried tofu");

        addAlias("green tea", "green tea");

        addAlias("pancake", "pancakes");

        addAlias("fish", "fish");

        addAlias("chicken breast", "chicken");

        addAlias("plain yogurt", "yogurt");
        addAlias("curd", "yogurt");
    }


    // =============================================================
    // GET FOOD
    // =============================================================

    public NutritionInfo getFood(String foodName) {

        if (foodName == null) {
            return null;
        }

        return foods.get(normalize(foodName));
    }


    // =============================================================
    // CHECK FOOD
    // =============================================================

    public boolean containsFood(String foodName) {

        if (foodName == null) {
            return false;
        }

        return foods.containsKey(normalize(foodName));
    }


    // =============================================================
    // ADD FOOD
    // =============================================================

    private void addFood(
            String name,
            NutritionInfo nutrition
    ) {

        if (name == null || nutrition == null) {
            return;
        }

        foods.put(
                normalize(name),
                nutrition
        );
    }


    // =============================================================
    // ADD ALIAS
    // =============================================================

    private void addAlias(
            String alias,
            String canonicalName
    ) {

        if (alias == null || canonicalName == null) {
            return;
        }

        NutritionInfo nutrition =
                foods.get(normalize(canonicalName));

        if (nutrition != null) {
            foods.put(
                    normalize(alias),
                    nutrition
            );
        }
    }


    // =============================================================
    // NORMALIZE
    // =============================================================

    private String normalize(String value) {

        return value
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");
    }
}