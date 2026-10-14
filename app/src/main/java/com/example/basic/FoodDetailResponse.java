package com.example.basic;

import java.util.List;

public class FoodDetailResponse {

    public long fdcId;

    public String description;

    public String dataType;

    public Double servingSize;

    public String servingSizeUnit;

    public List<FoodNutrient> foodNutrients;

    public List<FoodPortion> foodPortions;


    // =========================================================
    // USDA NUTRIENT
    // =========================================================

    public static class FoodNutrient {

        public Nutrient nutrient;

        public Double amount;
    }


    // =========================================================
    // USDA NUTRIENT DEFINITION
    // =========================================================

    public static class Nutrient {

        public int id;

        public String name;

        public String unitName;
    }


    // =========================================================
    // USDA FOOD PORTION
    // =========================================================

    public static class FoodPortion {

        public Double amount;

        public String modifier;

        public String gramWeight;

        public MeasureUnit measureUnit;
    }


    // =========================================================
    // USDA MEASURE UNIT
    // =========================================================

    public static class MeasureUnit {

        public String name;

        public String abbreviation;
    }
}