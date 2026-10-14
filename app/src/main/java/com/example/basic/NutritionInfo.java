package com.example.basic;

public class NutritionInfo {

    private final double calories;
    private final double protein;
    private final double carbohydrates;
    private final double fat;
    private final double fibre;

    private final double calcium;
    private final double iron;
    private final double magnesium;
    private final double potassium;
    private final double sodium;

    private final double vitaminA;
    private final double vitaminC;
    private final double vitaminD;
    private final double vitaminE;
    private final double vitaminK;

    private final double vitaminB1;
    private final double vitaminB2;
    private final double vitaminB6;
    private final double vitaminB12;
    private final double folate;

    private final String serving;
    private final String source;

    public NutritionInfo(
            double calories,
            double protein,
            double carbohydrates,
            double fat,
            double fibre,
            double calcium,
            double iron,
            double magnesium,
            double potassium,
            double sodium,
            double vitaminA,
            double vitaminC,
            double vitaminD,
            double vitaminE,
            double vitaminK,
            double vitaminB1,
            double vitaminB2,
            double vitaminB6,
            double vitaminB12,
            double folate,
            String serving,
            String source
    ) {

        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.fibre = fibre;

        this.calcium = calcium;
        this.iron = iron;
        this.magnesium = magnesium;
        this.potassium = potassium;
        this.sodium = sodium;

        this.vitaminA = vitaminA;
        this.vitaminC = vitaminC;
        this.vitaminD = vitaminD;
        this.vitaminE = vitaminE;
        this.vitaminK = vitaminK;

        this.vitaminB1 = vitaminB1;
        this.vitaminB2 = vitaminB2;
        this.vitaminB6 = vitaminB6;
        this.vitaminB12 = vitaminB12;
        this.folate = folate;

        this.serving = serving;
        this.source = source;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getFat() {
        return fat;
    }

    public double getFibre() {
        return fibre;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getIron() {
        return iron;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public double getPotassium() {
        return potassium;
    }

    public double getSodium() {
        return sodium;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public double getVitaminK() {
        return vitaminK;
    }

    public double getVitaminB1() {
        return vitaminB1;
    }

    public double getVitaminB2() {
        return vitaminB2;
    }

    public double getVitaminB6() {
        return vitaminB6;
    }

    public double getVitaminB12() {
        return vitaminB12;
    }

    public double getFolate() {
        return folate;
    }

    public String getServing() {
        return serving;
    }

    public String getSource() {
        return source;
    }

    public NutritionInfo add(NutritionInfo other) {

        if (other == null) {
            return this;
        }

        return new NutritionInfo(

                calories + other.calories,
                protein + other.protein,
                carbohydrates + other.carbohydrates,
                fat + other.fat,
                fibre + other.fibre,

                calcium + other.calcium,
                iron + other.iron,
                magnesium + other.magnesium,
                potassium + other.potassium,
                sodium + other.sodium,

                vitaminA + other.vitaminA,
                vitaminC + other.vitaminC,
                vitaminD + other.vitaminD,
                vitaminE + other.vitaminE,
                vitaminK + other.vitaminK,

                vitaminB1 + other.vitaminB1,
                vitaminB2 + other.vitaminB2,
                vitaminB6 + other.vitaminB6,
                vitaminB12 + other.vitaminB12,
                folate + other.folate,

                "",
                "Calculated from source-backed food records."
        );
    }

    public NutritionInfo multiply(double factor) {

        return new NutritionInfo(

                calories * factor,
                protein * factor,
                carbohydrates * factor,
                fat * factor,
                fibre * factor,

                calcium * factor,
                iron * factor,
                magnesium * factor,
                potassium * factor,
                sodium * factor,

                vitaminA * factor,
                vitaminC * factor,
                vitaminD * factor,
                vitaminE * factor,
                vitaminK * factor,

                vitaminB1 * factor,
                vitaminB2 * factor,
                vitaminB6 * factor,
                vitaminB12 * factor,
                folate * factor,

                serving,
                source
        );
    }
}