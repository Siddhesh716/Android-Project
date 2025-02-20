package com.example.basic;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView quizTitle, quizQuestion;
    private RadioGroup quizOptions;
    private RadioButton option1, option2, option3, option4;
    private Button submitQuiz;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int selectedAnswer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizTitle = findViewById(R.id.quizTitle);
        quizQuestion = findViewById(R.id.quizQuestion);
        quizOptions = findViewById(R.id.quizOptions);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        submitQuiz = findViewById(R.id.submitQuiz);

        loadQuestions();
        displayQuestion();

        quizOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.option1) selectedAnswer = 1;
            else if (checkedId == R.id.option2) selectedAnswer = 2;
            else if (checkedId == R.id.option3) selectedAnswer = 3;
            else if (checkedId == R.id.option4) selectedAnswer = 4;
        });

        submitQuiz.setOnClickListener(view -> checkAnswer());
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("Which Macronutrient is essential for muscle growth?", "Carbohydrates", "Protein", "Fats", "Vitamins", 2));
        questionList.add(new Question("What is the recommended daily water intake for an average adult?", "1 liter", "2 liters", "3 liters", "4 liters", 2));
        questionList.add(new Question("Which exercise is best for improving cardiovascular endurance?", "Squats", "Bench Press", "Jump Rope", "Deadlifts", 3));
        questionList.add(new Question("Which vitamin is crucial for bone health?", "Vitamin A", "Vitamin B12", "Vitamin C", "Vitamin D", 4));
        questionList.add(new Question("Which of these exercises primarily targets the core muscles?", "Push-ups", "Plank", "Lunges", "Pull-ups", 2));
        questionList.add(new Question("What is the ideal heart rate zone for fat burning?", "50-60% of max HR", "60-70% of max HR", "70-80% of max HR", "80-90% of max HR", 2));
        questionList.add(new Question("Which hormone plays a significant role in muscle growth?", "Insulin", "Cortisol", "Testosterone", "Adrenaline", 3));
        questionList.add(new Question("What is the primary function of carbohydrates in the body?", "Build muscle", "Provide energy", "Store fat", "Boost immunity", 2));
        questionList.add(new Question("Which mineral is essential for muscle contraction?", "Iron", "Magnesium", "Calcium", "Zinc", 3));
        questionList.add(new Question("How many hours of sleep are recommended for muscle recovery?", "4-5 hours", "5-6 hours", "7-9 hours", "10-12 hours", 3));
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question q = questionList.get(currentQuestionIndex);
            quizQuestion.setText(q.getQuestion());
            option1.setText(q.getOption1());
            option2.setText(q.getOption2());
            option3.setText(q.getOption3());
            option4.setText(q.getOption4());
            quizOptions.clearCheck();
            selectedAnswer = -1;
        } else {
            Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkAnswer() {
        if (selectedAnswer == -1) {
            Toast.makeText(this, "Please Select An Answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedAnswer == currentQuestion.getCorrectOption()) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong! Correct Answer: " + getCorrectAnswer(currentQuestion), Toast.LENGTH_LONG).show();
        }

        currentQuestionIndex++;
        displayQuestion();
    }

    private String getCorrectAnswer(Question question) {
        switch (question.getCorrectOption()) {
            case 1: return question.getOption1();
            case 2: return question.getOption2();
            case 3: return question.getOption3();
            case 4: return question.getOption4();
            default: return "";
        }
    }
}