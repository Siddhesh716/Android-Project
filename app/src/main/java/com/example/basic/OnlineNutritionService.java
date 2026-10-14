package com.example.basic;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineNutritionService {

    public interface NutritionCallback {
        void onSuccess(NutritionInfo nutritionInfo);

        void onError(String message);
    }

    private final FoodDataApi api;
    private final String apiKey;


    // =========================================================
    // FIXED USDA FOOD MAPPING
    // =========================================================

    private static final Map<String, Long> FIXED_FDC_IDS =
            new HashMap<>();

    static {

        FIXED_FDC_IDS.put("oats", 2346396L);
        FIXED_FDC_IDS.put("brown rice", 2512380L);
        FIXED_FDC_IDS.put("white rice", 2512381L);
        FIXED_FDC_IDS.put("banana", 1105314L);
        FIXED_FDC_IDS.put("apple", 171688L);
        FIXED_FDC_IDS.put("orange", 746771L);
        FIXED_FDC_IDS.put("egg", 748967L);
        FIXED_FDC_IDS.put("chicken breast", 2646170L);
        FIXED_FDC_IDS.put("salmon", 2684441L);
        FIXED_FDC_IDS.put("tuna", 2747673L);
        FIXED_FDC_IDS.put("milk", 746782L);
        FIXED_FDC_IDS.put("greek yogurt", 2259794L);
        FIXED_FDC_IDS.put("lentils", 2644283L);
        FIXED_FDC_IDS.put("chickpeas", 2644282L);
        FIXED_FDC_IDS.put("almonds", 2346393L);
        FIXED_FDC_IDS.put("peanut butter", 2758989L);
        FIXED_FDC_IDS.put("broccoli", 747447L);
        FIXED_FDC_IDS.put("spinach", 1999633L);
        FIXED_FDC_IDS.put("carrot", 2258586L);
        FIXED_FDC_IDS.put("potato", 2346401L);
        FIXED_FDC_IDS.put("sweet potato", 2346404L);
    }


    public OnlineNutritionService() {

        api = NutritionApiClient.getApi();
        apiKey = NutritionApiClient.getApiKey();
    }


    // =========================================================
    // MAIN NUTRITION METHOD
    // =========================================================

    public void getNutritionForFood(
            String foodName,
            NutritionCallback callback
    ) {

        if (foodName == null ||
                foodName.trim().isEmpty()) {

            callback.onError(
                    "Food name is empty."
            );

            return;
        }


        FoodQuantity foodQuantity =
                parseFoodQuantity(foodName.trim());


        if (foodQuantity == null ||
                foodQuantity.foodName.isEmpty()) {

            callback.onError(
                    "Unable to understand food quantity: " +
                            foodName
            );

            return;
        }


        // =====================================================
        // FIRST: CHECK FIXED USDA FOODS
        // =====================================================

        Long fixedFdcId =
                getFixedFdcId(
                        foodQuantity.foodName
                );


        if (fixedFdcId != null) {

            android.util.Log.d(
                    "USDA_FIXED",
                    "Using fixed USDA record: " +
                            foodQuantity.foodName +
                            " | FDC ID = " +
                            fixedFdcId
            );


            getNutritionFromFixedFood(
                    fixedFdcId,
                    foodQuantity,
                    callback
            );

            return;
        }


        // =====================================================
        // FALLBACK: USDA SEARCH
        // =====================================================

        searchFood(
                foodQuantity,
                callback
        );
    }


    // =========================================================
    // FIND FIXED USDA FOOD
    // =========================================================

    private Long getFixedFdcId(
            String foodName
    ) {

        if (foodName == null ||
                foodName.trim().isEmpty()) {

            return null;
        }


        String normalized =
                normalizeFoodName(foodName);


        // Handle common plurals
        if (normalized.equals("oat")) {
            normalized = "oats";
        }

        if (normalized.equals("bananas")) {
            normalized = "banana";
        }

        if (normalized.equals("apples")) {
            normalized = "apple";
        }

        if (normalized.equals("oranges")) {
            normalized = "orange";
        }

        if (normalized.equals("eggs")) {
            normalized = "egg";
        }

        if (normalized.equals("almond")) {
            normalized = "almonds";
        }

        if (normalized.equals("lentil")) {
            normalized = "lentils";
        }

        if (normalized.equals("chickpea")) {
            normalized = "chickpeas";
        }

        if (normalized.equals("carrots")) {
            normalized = "carrot";
        }

        if (normalized.equals("potatoes")) {
            normalized = "potato";
        }


        return FIXED_FDC_IDS.get(normalized);
    }


    // =========================================================
    // DIRECT USDA LOOKUP USING FIXED FDC ID
    // =========================================================

    private void getNutritionFromFixedFood(
            long fdcId,
            FoodQuantity foodQuantity,
            NutritionCallback callback
    ) {

        api.getFoodDetails(
                fdcId,
                apiKey
        ).enqueue(
                new Callback<FoodDetailResponse>() {

                    @Override
                    public void onResponse(
                            Call<FoodDetailResponse> call,
                            Response<FoodDetailResponse> response
                    ) {

                        if (!response.isSuccessful() ||
                                response.body() == null) {

                            callback.onError(
                                    "USDA food details failed. HTTP: " +
                                            response.code()
                            );

                            return;
                        }


                        // IMPORTANT:
                        // FoodDetailResponse itself IS the food.
                        FoodDetailResponse food =
                                response.body();


                        android.util.Log.d(
                                "USDA_FIXED",
                                "========================================"
                        );

                        android.util.Log.d(
                                "USDA_FIXED",
                                "Requested Food = " +
                                        foodQuantity.foodName
                        );

                        android.util.Log.d(
                                "USDA_FIXED",
                                "USDA Description = " +
                                        food.description
                        );

                        android.util.Log.d(
                                "USDA_FIXED",
                                "FDC ID = " +
                                        food.fdcId
                        );

                        android.util.Log.d(
                                "USDA_FIXED",
                                "Data Type = " +
                                        food.dataType
                        );

                        android.util.Log.d(
                                "USDA_FIXED",
                                "========================================"
                        );


                        double grams =
                                resolveQuantityInGrams(
                                        food,
                                        foodQuantity
                                );


                        if (grams <= 0) {

                            callback.onError(
                                    "Unable to determine weight for: " +
                                            foodQuantity.foodName
                            );

                            return;
                        }


                        NutritionInfo nutrition =
                                convertToNutritionInfo(
                                        food,
                                        grams,
                                        foodQuantity
                                );


                        if (nutrition == null) {

                            callback.onError(
                                    "No usable nutrient data found for: " +
                                            foodQuantity.foodName
                            );

                            return;
                        }


                        callback.onSuccess(
                                nutrition
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<FoodDetailResponse> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "USDA request failed: " +
                                        (
                                                t.getMessage() != null
                                                        ? t.getMessage()
                                                        : "Unknown error"
                                        )
                        );
                    }
                }
        );
    }


    // =========================================================
    // USDA SEARCH
    // =========================================================

    private void searchFood(
            FoodQuantity foodQuantity,
            NutritionCallback callback
    ) {

        String cleanedFoodName =
                foodQuantity.foodName.trim();


        api.searchFoods(
                apiKey,
                cleanedFoodName,
                25
        ).enqueue(
                new Callback<FoodSearchResponse>() {

                    @Override
                    public void onResponse(
                            Call<FoodSearchResponse> call,
                            Response<FoodSearchResponse> response
                    ) {

                        if (!response.isSuccessful() ||
                                response.body() == null) {

                            callback.onError(
                                    "USDA search failed. HTTP: " +
                                            response.code()
                            );

                            return;
                        }


                        if (response.body().foods == null ||
                                response.body().foods.isEmpty()) {

                            callback.onError(
                                    "No USDA food found for: " +
                                            cleanedFoodName
                            );

                            return;
                        }


                        android.util.Log.d(
                                "USDA_SEARCH",
                                "========== SEARCH: " +
                                        cleanedFoodName +
                                        " =========="
                        );


                        for (
                                FoodSearchResponse.Food result :
                                response.body().foods
                        ) {

                            android.util.Log.d(
                                    "USDA_SEARCH",
                                    "RESULT = " +
                                            result.description +
                                            " | FDC ID = " +
                                            result.fdcId +
                                            " | TYPE = " +
                                            result.dataType
                            );
                        }


                        FoodSearchResponse.Food selectedFood =
                                chooseBestFood(
                                        response.body().foods,
                                        cleanedFoodName
                                );


                        if (selectedFood == null) {

                            callback.onError(
                                    "No suitable USDA food found for: " +
                                            cleanedFoodName
                            );

                            return;
                        }


                        getFoodDetails(
                                selectedFood.fdcId,
                                foodQuantity,
                                callback
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<FoodSearchResponse> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "USDA search failed: " +
                                        (
                                                t.getMessage() != null
                                                        ? t.getMessage()
                                                        : "Unknown error"
                                        )
                        );
                    }
                }
        );
    }


    // =========================================================
    // CHOOSE BEST USDA SEARCH RESULT
    // =========================================================

    private FoodSearchResponse.Food chooseBestFood(
            List<FoodSearchResponse.Food> foods,
            String requestedFood
    ) {

        if (foods == null ||
                foods.isEmpty()) {

            return null;
        }


        String normalizedRequested =
                normalizeFoodName(requestedFood);


        FoodSearchResponse.Food exactFoundation = null;
        FoodSearchResponse.Food exactSRLegacy = null;
        FoodSearchResponse.Food exactSurvey = null;
        FoodSearchResponse.Food exactOther = null;
        FoodSearchResponse.Food exactBranded = null;

        FoodSearchResponse.Food relevantFoundation = null;
        FoodSearchResponse.Food relevantSRLegacy = null;
        FoodSearchResponse.Food relevantSurvey = null;
        FoodSearchResponse.Food relevantOther = null;
        FoodSearchResponse.Food relevantBranded = null;


        for (
                FoodSearchResponse.Food food :
                foods
        ) {

            if (food == null ||
                    food.description == null) {

                continue;
            }


            String description =
                    normalizeFoodName(
                            food.description
                    );


            boolean exact =
                    description.equals(
                            normalizedRequested
                    );


            boolean strictMatch =
                    isStrictFoodMatch(
                            description,
                            normalizedRequested
                    );


            if (!strictMatch) {
                continue;
            }


            String dataType =
                    food.dataType == null
                            ? ""
                            : food.dataType.toLowerCase(
                            Locale.getDefault()
                    );


            if (exact) {

                if (dataType.contains("foundation")) {

                    exactFoundation = food;

                } else if (dataType.contains("sr legacy")) {

                    exactSRLegacy = food;

                } else if (dataType.contains("survey")) {

                    exactSurvey = food;

                } else if (dataType.contains("other")) {

                    exactOther = food;

                } else if (dataType.contains("branded")) {

                    exactBranded = food;
                }

            } else {

                if (dataType.contains("foundation") &&
                        relevantFoundation == null) {

                    relevantFoundation = food;

                } else if (dataType.contains("sr legacy") &&
                        relevantSRLegacy == null) {

                    relevantSRLegacy = food;

                } else if (dataType.contains("survey") &&
                        relevantSurvey == null) {

                    relevantSurvey = food;

                } else if (dataType.contains("other") &&
                        relevantOther == null) {

                    relevantOther = food;

                } else if (dataType.contains("branded") &&
                        relevantBranded == null) {

                    relevantBranded = food;
                }
            }
        }


        if (exactFoundation != null) {
            return exactFoundation;
        }

        if (exactSRLegacy != null) {
            return exactSRLegacy;
        }

        if (exactSurvey != null) {
            return exactSurvey;
        }

        if (exactOther != null) {
            return exactOther;
        }

        if (exactBranded != null) {
            return exactBranded;
        }

        if (relevantFoundation != null) {
            return relevantFoundation;
        }

        if (relevantSRLegacy != null) {
            return relevantSRLegacy;
        }

        if (relevantSurvey != null) {
            return relevantSurvey;
        }

        if (relevantOther != null) {
            return relevantOther;
        }

        if (relevantBranded != null) {
            return relevantBranded;
        }


        return null;
    }


    // =========================================================
    // STRICT FOOD MATCH
    // =========================================================

    private boolean isStrictFoodMatch(
            String description,
            String requested
    ) {

        if (description == null ||
                requested == null) {

            return false;
        }


        if (description.equals(requested)) {
            return true;
        }


        String[] excludedWords = {
                "sandwich",
                "dressing",
                "sauce",
                "broth",
                "soup",
                "chips",
                "powder",
                "flour",
                "bread",
                "cake",
                "pie",
                "muffin",
                "cookie",
                "babyfood",
                "baby",
                "beverage",
                "milk",
                "yogurt",
                "wrap",
                "meal kit",
                "sticks",
                "pudding",
                "cereal",
                "bar",
                "juice",
                "drink"
        };


        for (String excluded :
                excludedWords) {

            if (description.contains(excluded) &&
                    !requested.contains(excluded)) {

                return false;
            }
        }


        if (requested.equals("grilled fish")) {

            return description.contains("fish") &&
                    description.contains("grilled");
        }


        if (requested.equals("steamed vegetables")) {

            return description.contains("vegetable") &&
                    description.contains("steamed");
        }


        if (requested.equals("salad")) {

            return description.contains("salad");
        }


        if (requested.equals("poha")) {

            return description.equals("poha");
        }


        if (requested.equals("green tea")) {

            return description.contains("green tea");
        }


        String[] requestedWords =
                requested.split("\\s+");


        for (String word :
                requestedWords) {

            if (word.length() <= 1) {
                continue;
            }


            Pattern pattern =
                    Pattern.compile(
                            "\\b" +
                                    Pattern.quote(word) +
                                    "\\b"
                    );


            Matcher matcher =
                    pattern.matcher(description);


            if (!matcher.find()) {
                return false;
            }
        }


        return true;
    }


    // =========================================================
    // NORMALIZE FOOD NAME
    // =========================================================

    private String normalizeFoodName(
            String value
    ) {

        if (value == null) {
            return "";
        }


        return value
                .trim()
                .toLowerCase(Locale.getDefault())
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }


    // =========================================================
    // GET FOOD DETAILS - FALLBACK SEARCH RESULT
    // =========================================================

    private void getFoodDetails(
            long fdcId,
            FoodQuantity foodQuantity,
            NutritionCallback callback
    ) {

        api.getFoodDetails(
                fdcId,
                apiKey
        ).enqueue(
                new Callback<FoodDetailResponse>() {

                    @Override
                    public void onResponse(
                            Call<FoodDetailResponse> call,
                            Response<FoodDetailResponse> response
                    ) {

                        if (!response.isSuccessful() ||
                                response.body() == null) {

                            callback.onError(
                                    "USDA food details failed. HTTP: " +
                                            response.code()
                            );

                            return;
                        }


                        // IMPORTANT:
                        // FoodDetailResponse itself IS the food.
                        FoodDetailResponse food =
                                response.body();


                        android.util.Log.d(
                                "USDA_DETAILS",
                                "Food = " +
                                        food.description +
                                        " | FDC ID = " +
                                        food.fdcId +
                                        " | TYPE = " +
                                        food.dataType +
                                        " | Requested = " +
                                        foodQuantity.foodName +
                                        " | Quantity = " +
                                        foodQuantity.quantity +
                                        " " +
                                        foodQuantity.unit
                        );


                        double grams =
                                resolveQuantityInGrams(
                                        food,
                                        foodQuantity
                                );


                        if (grams <= 0) {

                            callback.onError(
                                    "Unable to determine weight for: " +
                                            foodQuantity.foodName
                            );

                            return;
                        }


                        NutritionInfo nutrition =
                                convertToNutritionInfo(
                                        food,
                                        grams,
                                        foodQuantity
                                );


                        if (nutrition == null) {

                            callback.onError(
                                    "No usable nutrient data found for: " +
                                            foodQuantity.foodName
                            );

                            return;
                        }


                        callback.onSuccess(
                                nutrition
                        );
                    }


                    @Override
                    public void onFailure(
                            Call<FoodDetailResponse> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "USDA request failed: " +
                                        (
                                                t.getMessage() != null
                                                        ? t.getMessage()
                                                        : "Unknown error"
                                        )
                        );
                    }
                }
        );
    }


    // =========================================================
    // PARSE FOOD QUANTITY
    // =========================================================

    private FoodQuantity parseFoodQuantity(
            String input
    ) {

        if (input == null ||
                input.trim().isEmpty()) {

            return null;
        }


        String value =
                input.trim();


        // -----------------------------------------------------
        // EXPLICIT WEIGHT / VOLUME
        // -----------------------------------------------------

        Pattern weightPattern =
                Pattern.compile(
                        "^\\s*(\\d+(?:\\.\\d+)?)\\s*" +
                                "(g|gram|grams|kg|ml|l|liter|litre)\\s+" +
                                "(.+?)\\s*$",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher weightMatcher =
                weightPattern.matcher(value);


        if (weightMatcher.matches()) {

            double quantity =
                    Double.parseDouble(
                            weightMatcher.group(1)
                    );


            String unit =
                    weightMatcher.group(2);


            String foodName =
                    weightMatcher.group(3).trim();


            return new FoodQuantity(
                    quantity,
                    unit,
                    foodName
            );
        }


        // -----------------------------------------------------
        // COUNT SUFFIX
        // -----------------------------------------------------

        Pattern countSuffixPattern =
                Pattern.compile(
                        "^\\s*(\\d+(?:\\.\\d+)?)\\s+" +
                                "(.+?)\\s+" +
                                "(slice|slices|piece|pieces|egg|eggs|idli|idlis|roti|rotis|pancake|pancakes)" +
                                "\\s*$",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher countSuffixMatcher =
                countSuffixPattern.matcher(value);


        if (countSuffixMatcher.matches()) {

            double quantity =
                    Double.parseDouble(
                            countSuffixMatcher.group(1)
                    );


            String foodName =
                    countSuffixMatcher.group(2).trim();


            String unit =
                    countSuffixMatcher.group(3);


            return new FoodQuantity(
                    quantity,
                    unit,
                    foodName
            );
        }


        // -----------------------------------------------------
        // KNOWN COUNT FOODS
        // -----------------------------------------------------

        Pattern knownCountPattern =
                Pattern.compile(
                        "^\\s*(\\d+(?:\\.\\d+)?)\\s+" +
                                "(banana|bananas|egg|eggs|idli|idlis|roti|rotis|pancake|pancakes)" +
                                "\\s*$",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher knownCountMatcher =
                knownCountPattern.matcher(value);


        if (knownCountMatcher.matches()) {

            double quantity =
                    Double.parseDouble(
                            knownCountMatcher.group(1)
                    );


            String foodName =
                    knownCountMatcher.group(2);


            String unit;


            if (foodName.equalsIgnoreCase("banana") ||
                    foodName.equalsIgnoreCase("bananas")) {

                unit = "piece";

            } else {

                unit = foodName;
            }


            return new FoodQuantity(
                    quantity,
                    unit,
                    foodName
            );
        }


        // -----------------------------------------------------
        // PREFIX COUNT UNITS
        // -----------------------------------------------------

        Pattern prefixCountPattern =
                Pattern.compile(
                        "^\\s*(\\d+(?:\\.\\d+)?)\\s+" +
                                "(slice|slices|piece|pieces|egg|eggs|idli|idlis|roti|rotis|pancake|pancakes)" +
                                "\\s+(.+?)\\s*$",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher prefixCountMatcher =
                prefixCountPattern.matcher(value);


        if (prefixCountMatcher.matches()) {

            double quantity =
                    Double.parseDouble(
                            prefixCountMatcher.group(1)
                    );


            String unit =
                    prefixCountMatcher.group(2);


            String foodName =
                    prefixCountMatcher.group(3).trim();


            return new FoodQuantity(
                    quantity,
                    unit,
                    foodName
            );
        }


        // -----------------------------------------------------
        // DEFAULT = 100 g
        // -----------------------------------------------------

        return new FoodQuantity(
                100.0,
                "g",
                value
        );
    }


    // =========================================================
    // RESOLVE QUANTITY TO GRAMS
    // =========================================================

    private double resolveQuantityInGrams(
            FoodDetailResponse food,
            FoodQuantity foodQuantity
    ) {

        if (food == null ||
                foodQuantity == null) {

            return -1;
        }


        String unit =
                foodQuantity.unit
                        .toLowerCase(Locale.getDefault())
                        .trim();


        if (unit.equals("g") ||
                unit.equals("gram") ||
                unit.equals("grams")) {

            return foodQuantity.quantity;
        }


        if (unit.equals("kg")) {

            return foodQuantity.quantity * 1000.0;
        }


        if (unit.equals("ml") ||
                unit.equals("l") ||
                unit.equals("liter") ||
                unit.equals("litre")) {

            return resolveLiquidQuantity(
                    food,
                    foodQuantity
            );
        }


        return findMatchingPortionWeight(
                food,
                foodQuantity
        );
    }


    // =========================================================
    // FIND MATCHING USDA PORTION
    // =========================================================

    private double findMatchingPortionWeight(
            FoodDetailResponse food,
            FoodQuantity foodQuantity
    ) {

        if (food == null ||
                food.foodPortions == null ||
                food.foodPortions.isEmpty()) {

            return -1;
        }


        String requestedUnit =
                foodQuantity.unit
                        .toLowerCase(Locale.getDefault())
                        .trim();


        // -----------------------------------------------------
        // BANANA PIECE
        // -----------------------------------------------------

        if (requestedUnit.equals("piece") ||
                requestedUnit.equals("pieces")) {

            if (foodQuantity.foodName
                    .toLowerCase(Locale.getDefault())
                    .contains("banana")) {

                for (
                        FoodDetailResponse.FoodPortion portion :
                        food.foodPortions
                ) {

                    if (portion == null) {
                        continue;
                    }


                    String modifier =
                            safeLower(portion.modifier);


                    String measure =
                            portion.measureUnit == null
                                    ? ""
                                    : safeLower(
                                    portion.measureUnit.name
                            );


                    String abbreviation =
                            portion.measureUnit == null
                                    ? ""
                                    : safeLower(
                                    portion.measureUnit.abbreviation
                            );


                    if (modifier.contains("medium") ||
                            measure.contains("medium") ||
                            abbreviation.contains("medium")) {

                        double gramWeight =
                                parseGramWeight(
                                        portion.gramWeight
                                );


                        if (gramWeight > 0) {

                            return foodQuantity.quantity *
                                    gramWeight;
                        }
                    }
                }
            }
        }


        // -----------------------------------------------------
        // NORMAL PORTION MATCH
        // -----------------------------------------------------

        for (
                FoodDetailResponse.FoodPortion portion :
                food.foodPortions
        ) {

            if (portionMatchesUnit(
                    portion,
                    requestedUnit,
                    foodQuantity.foodName
            )) {

                double gramWeight =
                        parseGramWeight(
                                portion.gramWeight
                        );


                if (gramWeight > 0) {

                    return foodQuantity.quantity *
                            gramWeight;
                }
            }
        }


        // -----------------------------------------------------
        // SECOND PASS: MODIFIER / MEASURE MATCH
        // -----------------------------------------------------

        for (
                FoodDetailResponse.FoodPortion portion :
                food.foodPortions
        ) {

            if (portion == null) {
                continue;
            }


            String modifier =
                    safeLower(portion.modifier);


            String measure =
                    portion.measureUnit == null
                            ? ""
                            : safeLower(
                            portion.measureUnit.name
                    );


            String abbreviation =
                    portion.measureUnit == null
                            ? ""
                            : safeLower(
                            portion.measureUnit.abbreviation
                    );


            if (modifier.contains(requestedUnit) ||
                    measure.contains(requestedUnit) ||
                    abbreviation.contains(requestedUnit) ||
                    requestedUnit.contains(modifier)) {

                double gramWeight =
                        parseGramWeight(
                                portion.gramWeight
                        );


                if (gramWeight > 0) {

                    return foodQuantity.quantity *
                            gramWeight;
                }
            }
        }


        // -----------------------------------------------------
        // ONLY ONE USDA PORTION
        // -----------------------------------------------------

        if (food.foodPortions.size() == 1) {

            FoodDetailResponse.FoodPortion portion =
                    food.foodPortions.get(0);


            double gramWeight =
                    parseGramWeight(
                            portion.gramWeight
                    );


            if (gramWeight > 0) {

                return foodQuantity.quantity *
                        gramWeight;
            }
        }


        return -1;
    }


    // =========================================================
    // PORTION MATCH
    // =========================================================

    private boolean portionMatchesUnit(
            FoodDetailResponse.FoodPortion portion,
            String requestedUnit,
            String requestedFood
    ) {

        if (portion == null) {
            return false;
        }


        String modifier =
                safeLower(
                        portion.modifier
                );


        String measure =
                portion.measureUnit == null
                        ? ""
                        : safeLower(
                        portion.measureUnit.name
                );


        String abbreviation =
                portion.measureUnit == null
                        ? ""
                        : safeLower(
                        portion.measureUnit.abbreviation
                );


        String combined =
                modifier +
                        " " +
                        measure +
                        " " +
                        abbreviation;


        switch (requestedUnit) {

            case "slice":
            case "slices":

                return combined.contains("slice");


            case "piece":
            case "pieces":

                return combined.contains("piece") ||
                        combined.contains("medium");


            case "egg":
            case "eggs":

                return combined.contains("egg");


            case "idli":
            case "idlis":

                return combined.contains("idli");


            case "roti":
            case "rotis":

                return combined.contains("roti") ||
                        combined.contains("chapati");


            case "pancake":
            case "pancakes":

                return combined.contains("pancake");


            default:

                return combined.contains(
                        requestedUnit
                );
        }
    }


    // =========================================================
    // LIQUID QUANTITY
    // =========================================================

    private double resolveLiquidQuantity(
            FoodDetailResponse food,
            FoodQuantity foodQuantity
    ) {

        if (food == null ||
                food.servingSize == null ||
                food.servingSize <= 0 ||
                food.servingSizeUnit == null) {

            return -1;
        }


        String servingUnit =
                food.servingSizeUnit
                        .toLowerCase(Locale.getDefault())
                        .trim();


        double servingMl;


        if (servingUnit.equals("ml") ||
                servingUnit.equals("milliliter") ||
                servingUnit.equals("milliliters")) {

            servingMl =
                    food.servingSize;

        } else if (
                servingUnit.equals("l") ||
                        servingUnit.equals("liter") ||
                        servingUnit.equals("litre")
        ) {

            servingMl =
                    food.servingSize * 1000.0;

        } else {

            return -1;
        }


        if (food.foodPortions == null ||
                food.foodPortions.isEmpty()) {

            return -1;
        }


        for (
                FoodDetailResponse.FoodPortion portion :
                food.foodPortions
        ) {

            double gramWeight =
                    parseGramWeight(
                            portion.gramWeight
                    );


            if (gramWeight > 0) {

                /*
                 * Convert the USDA serving proportion
                 * to grams for the requested volume.
                 */
                return foodQuantity.quantity *
                        gramWeight /
                        servingMl;
            }
        }


        return -1;
    }


    // =========================================================
    // PARSE GRAM WEIGHT
    // =========================================================

    private double parseGramWeight(
            String gramWeight
    ) {

        if (gramWeight == null ||
                gramWeight.trim().isEmpty()) {

            return -1;
        }


        try {

            return Double.parseDouble(
                    gramWeight.trim()
            );

        } catch (NumberFormatException e) {

            return -1;
        }
    }


    // =========================================================
    // CONVERT USDA NUTRIENTS
    // =========================================================

    private NutritionInfo convertToNutritionInfo(
            FoodDetailResponse food,
            double grams,
            FoodQuantity foodQuantity
    ) {

        if (food == null ||
                food.foodNutrients == null ||
                food.foodNutrients.isEmpty()) {

            return null;
        }


        double calories = 0;
        double protein = 0;
        double carbohydrates = 0;
        double fat = 0;
        double fibre = 0;

        double calcium = 0;
        double iron = 0;
        double magnesium = 0;
        double potassium = 0;
        double sodium = 0;

        double vitaminA = 0;
        double vitaminC = 0;
        double vitaminD = 0;
        double vitaminE = 0;
        double vitaminK = 0;

        double vitaminB1 = 0;
        double vitaminB2 = 0;
        double vitaminB6 = 0;
        double vitaminB12 = 0;
        double folate = 0;


        double multiplier =
                grams / 100.0;


        for (
                FoodDetailResponse.FoodNutrient nutrient :
                food.foodNutrients
        ) {

            if (nutrient == null ||
                    nutrient.nutrient == null ||
                    nutrient.nutrient.name == null ||
                    nutrient.amount == null) {

                continue;
            }


            String name =
                    nutrient.nutrient.name
                            .toLowerCase(
                                    Locale.getDefault()
                            )
                            .trim();


            String unitName =
                    nutrient.nutrient.unitName == null
                            ? ""
                            : nutrient.nutrient.unitName
                            .toLowerCase(
                                    Locale.getDefault()
                            )
                            .trim();


            double value =
                    nutrient.amount *
                            multiplier;


            // -------------------------------------------------
            // ENERGY
            // -------------------------------------------------

            if (name.contains("energy") &&
                    unitName.equals("kcal")) {

                calories = value;
            }


            // -------------------------------------------------
            // MACRONUTRIENTS
            // -------------------------------------------------

            else if (
                    name.equals("protein") ||
                            name.contains("protein")
            ) {

                protein = value;

            } else if (
                    name.contains("carbohydrate")
            ) {

                carbohydrates = value;

            } else if (
                    name.contains("total lipid") ||
                            name.contains("total fat")
            ) {

                fat = value;

            } else if (
                    name.contains("fiber") ||
                            name.contains("fibre")
            ) {

                fibre = value;
            }


            // -------------------------------------------------
            // MINERALS
            // -------------------------------------------------

            else if (
                    name.contains("calcium")
            ) {

                calcium = value;

            } else if (
                    name.contains("iron")
            ) {

                iron = value;

            } else if (
                    name.contains("magnesium")
            ) {

                magnesium = value;

            } else if (
                    name.contains("potassium")
            ) {

                potassium = value;

            } else if (
                    name.contains("sodium")
            ) {

                sodium = value;
            }


            // -------------------------------------------------
            // VITAMINS
            // -------------------------------------------------

            else if (
                    name.contains("vitamin a")
            ) {

                vitaminA = value;

            } else if (
                    name.contains("vitamin c")
            ) {

                vitaminC = value;

            } else if (
                    name.contains("vitamin d")
            ) {

                vitaminD = value;

            } else if (
                    name.contains("vitamin e")
            ) {

                vitaminE = value;

            } else if (
                    name.contains("vitamin k")
            ) {

                vitaminK = value;

            } else if (
                    name.contains("thiamin")
            ) {

                vitaminB1 = value;

            } else if (
                    name.contains("riboflavin")
            ) {

                vitaminB2 = value;

            } else if (
                    name.contains("vitamin b-6") ||
                            name.contains("vitamin b6")
            ) {

                vitaminB6 = value;

            } else if (
                    name.contains("vitamin b-12") ||
                            name.contains("vitamin b12")
            ) {

                vitaminB12 = value;

            } else if (
                    name.contains("folate")
            ) {

                folate = value;
            }
        }


        String servingText =
                formatServing(
                        foodQuantity,
                        grams
                );


        return new NutritionInfo(
                calories,
                protein,
                carbohydrates,
                fat,
                fibre,
                calcium,
                iron,
                magnesium,
                potassium,
                sodium,
                vitaminA,
                vitaminC,
                vitaminD,
                vitaminE,
                vitaminK,
                vitaminB1,
                vitaminB2,
                vitaminB6,
                vitaminB12,
                folate,
                servingText,
                "USDA FoodData Central"
        );
    }


    // =========================================================
    // FORMAT SERVING
    // =========================================================

    private String formatServing(
            FoodQuantity foodQuantity,
            double grams
    ) {

        return String.format(
                Locale.getDefault(),
                "%.0f %s %s (%.1f g)",
                foodQuantity.quantity,
                foodQuantity.unit,
                foodQuantity.foodName,
                grams
        );
    }


    // =========================================================
    // SAFE LOWERCASE
    // =========================================================

    private String safeLower(
            String value
    ) {

        if (value == null) {
            return "";
        }


        return value
                .toLowerCase(Locale.getDefault())
                .trim();
    }


    // =========================================================
    // FOOD QUANTITY
    // =========================================================

    private static class FoodQuantity {

        final double quantity;
        final String unit;
        final String foodName;


        FoodQuantity(
                double quantity,
                String unit,
                String foodName
        ) {

            this.quantity = quantity;
            this.unit = unit;
            this.foodName = foodName;
        }
    }
}