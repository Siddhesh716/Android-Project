package com.example.basic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class SignupActivity extends AppCompatActivity {

    private EditText nameField, mobileField, ageField, emailField, weightField;
    private Button submitButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameField = findViewById(R.id.nameField);
        mobileField = findViewById(R.id.mobileField);
        ageField = findViewById(R.id.ageField);
        emailField = findViewById(R.id.emailField);
        weightField = findViewById(R.id.weightField);
        submitButton = findViewById(R.id.submitButton);
        databaseHelper = new DatabaseHelper(this);

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.matches("[a-zA-Z ]*")) { // Only allow letters and spaces
                    nameField.setText(input.replaceAll("[^a-zA-Z ]", ""));
                    nameField.setSelection(nameField.getText().length());
                }
            }
        });

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

        ageField.addTextChangedListener(new TextWatcher() {
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
                    ageField.setText(input.replaceAll("[^0-9]", ""));
                    ageField.setSelection(ageField.getText().length());
                }
                if (input.length() > 3) {
                    ageField.setText(input.substring(0, 3));
                    ageField.setSelection(ageField.getText().length());
                }
            }
        });

        weightField.addTextChangedListener(new TextWatcher() {
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
                    weightField.setText(input.replaceAll("[^0-9]", ""));
                    weightField.setSelection(weightField.getText().length());
                }
                if (input.length() > 3) {
                    weightField.setText(input.substring(0, 3));
                    weightField.setSelection(weightField.getText().length());
                }
            }
        });

        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.contains("@")) {
                    emailField.setError("Email must contain '@' symbol");
                } else {
                    emailField.setError(null);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String mobile = mobileField.getText().toString();
                String age = ageField.getText().toString();
                String email = emailField.getText().toString();
                String weight = weightField.getText().toString();

                if (name.isEmpty() || mobile.isEmpty() || age.isEmpty() || email.isEmpty() || weight.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (!email.contains("@")) {
                        Toast.makeText(SignupActivity.this, "Please enter a valid Email Address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long result = databaseHelper.insertUser(name, mobile, age, email, weight);

                    if (result != -1) {
                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_NAME", name);
                        editor.putString("USER_MOBILE", mobile);
                        editor.putString("USER_AGE", age);
                        editor.putString("USER_EMAIL", email);
                        editor.putString("USER_WEIGHT", weight);
                        editor.apply(); // Save changes

                        Toast.makeText(SignupActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Error in Saving Data", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}