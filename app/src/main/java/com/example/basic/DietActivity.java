package com.example.basic;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {
    private HashMap<String, String> dietPlans;
    private TextView dietPlanText;
    private EditText editDietPlan;
    private String selectedDay;
    private Button editButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        VideoView videoView = findViewById(R.id.diet_video);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.diet1;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        dietPlans = new HashMap<>();
        dietPlans.put("Monday", "🥣 Breakfast: Oats + Banana\n🍛 Lunch: Brown Rice + Veg Curry\n🍽 Dinner: Grilled Fish + Salad");
        dietPlans.put("Tuesday", "🥣 Breakfast: Smoothie + Toast\n🍛 Lunch: Chicken + Quinoa\n🍽 Dinner: Soup + Yogurt");
        dietPlans.put("Wednesday", "🥣 Breakfast: Poha + Green Tea\n🍛 Lunch: Roti + Dal + Salad\n🍽 Dinner: Steamed Vegetables");
        dietPlans.put("Thursday", "🥣 Breakfast: Pancakes + Honey\n🍛 Lunch: Pasta + Grilled Chicken\n🍽 Dinner: Roasted Veggies");
        dietPlans.put("Friday", "🥣 Breakfast: Eggs + Toast\n🍛 Lunch: Rice + Lentils + Yogurt\n🍽 Dinner: Stir-fried Tofu");
        dietPlans.put("Saturday", "🥣 Breakfast: Smoothie + Oats\n🍛 Lunch: Sandwich + Soup\n🍽 Dinner: Brown Rice + Veg Curry");
        dietPlans.put("Sunday", "🥣 Breakfast: Idli + Coconut Chutney\n🍛 Lunch: Grilled Fish + Rice\n🍽 Dinner: Light Salad + Soup");

        dietPlanText = findViewById(R.id.diet_plan_text);
        editDietPlan = findViewById(R.id.edit_diet_plan);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_diet_plan_button);
        Spinner spinner = findViewById(R.id.day_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(Calendar.getInstance().getTime());
        int spinnerPosition = adapter.getPosition(currentDay);
        spinner.setSelection(spinnerPosition);
        selectedDay = currentDay;

        dietPlanText.setText(dietPlans.get(selectedDay));
        editDietPlan.setText(dietPlans.get(selectedDay));
        editDietPlan.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = parent.getItemAtPosition(position).toString();
                dietPlanText.setText(dietPlans.get(selectedDay));
                editDietPlan.setText(dietPlans.get(selectedDay));
                editDietPlan.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        editButton.setOnClickListener(v -> {
            editDietPlan.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        saveButton.setOnClickListener(v -> {
            String updatedPlan = editDietPlan.getText().toString();
            dietPlans.put(selectedDay, updatedPlan);
            dietPlanText.setText(updatedPlan);
            editDietPlan.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });
    }
}