package com.example.basic;

import java.util.List;

public class FoodSearchResponse {

    public List<Food> foods;

    public static class Food {

        public long fdcId;
        public String description;
        public String dataType;
    }
}