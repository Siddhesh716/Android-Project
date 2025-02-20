package com.example.basic;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BMI extends AppCompatActivity {
    private EditText heightInput, weightInput;
    private Button calculateButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi);

        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            float height = Float.parseFloat(heightStr) / 100;
            float weight = Float.parseFloat(weightStr);
            float bmi = weight / (height * height);
            String bmiCategory;
            if (bmi < 16.0) {
                bmiCategory = "Severe Thinness";
            } else if (bmi >= 16.0 && bmi < 17.0) {
                bmiCategory = "Moderate Thinness";
            } else if (bmi >= 17.0 && bmi < 18.5) {
                bmiCategory = "Mild Thinness";
            } else if (bmi >= 18.5 && bmi < 25.0) {
                bmiCategory = "Normal Weight";
            } else if (bmi >= 25.0 && bmi < 30.0) {
                bmiCategory = "Overweight";
            } else if (bmi >= 30.0 && bmi < 35.0) {
                bmiCategory = "Obesity Class 1";
            } else if (bmi >= 35.0 && bmi < 40.0) {
                bmiCategory = "Obesity Class 2";
            } else {
                bmiCategory = "Obesity Class 3";
            }
            resultText.setText("Your BMI: " + String.format("%.2f", bmi) + "\nCategory: " + bmiCategory);
        } else {
            resultText.setText("Please Enter Valid Values!");
        }
    }
}