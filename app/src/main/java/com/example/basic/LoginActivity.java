package com.example.basic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText nameField, mobileField;
    private Button loginButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nameField = findViewById(R.id.nameField);
        mobileField = findViewById(R.id.mobileField);
        loginButton = findViewById(R.id.loginButton);
        databaseHelper = new DatabaseHelper(this);

        mobileField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.matches("[0-9]*")) {
                    mobileField.setText(input.replaceAll("[^0-9]", ""));
                    mobileField.setSelection(mobileField.getText().length());
                }
                if (input.length() > 10) {
                    mobileField.setText(input.substring(0, 10));
                    mobileField.setSelection(mobileField.getText().length());
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String mobile = mobileField.getText().toString();

                if (name.isEmpty() || mobile.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    User user = databaseHelper.getUserDetails(name, mobile);
                    if (user != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_NAME", user.getName());
                        editor.putString("USER_MOBILE", user.getMobile());
                        editor.putString("USER_AGE", user.getAge());
                        editor.putString("USER_EMAIL", user.getEmail());
                        editor.putString("USER_WEIGHT", user.getWeight());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.putExtra("USER_NAME", user.getName());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
