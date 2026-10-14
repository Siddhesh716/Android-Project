package com.example.basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {

    private static final String DATABASE_NAME = "userDatabase";

    // -----------------------------------------
    // USERS TABLE
    // -----------------------------------------
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_WEIGHT = "weight";

    // -----------------------------------------
    // QUIZ QUESTIONS TABLE
    // -----------------------------------------
    private static final String QUIZ_TABLE = "quiz_questions";

    private static final String QUIZ_ID = "id";
    private static final String QUIZ_QUESTION = "question";
    private static final String QUIZ_OPTION_A = "option_a";
    private static final String QUIZ_OPTION_B = "option_b";
    private static final String QUIZ_OPTION_C = "option_c";
    private static final String QUIZ_OPTION_D = "option_d";
    private static final String QUIZ_CORRECT_ANSWER = "correct_answer";
    private static final String QUIZ_DIFFICULTY = "difficulty";
    private static final String QUIZ_USED = "used";
    private static final String QUIZ_CREATED_AT = "created_at";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {

        db = context.openOrCreateDatabase(
                DATABASE_NAME,
                Context.MODE_PRIVATE,
                null
        );

        createTable();
        createQuizTable();
    }

    // =========================================================
    // USERS TABLE
    // =========================================================

    private void createTable() {

        String createTableQuery =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_MOBILE + " TEXT, " +
                        COLUMN_AGE + " INTEGER, " +
                        COLUMN_EMAIL + " TEXT, " +
                        COLUMN_WEIGHT + " REAL" +
                        ");";

        db.execSQL(createTableQuery);
    }

    public long insertUser(
            String name,
            String mobile,
            String age,
            String email,
            String weight
    ) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_AGE, Integer.parseInt(age));
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_WEIGHT, Float.parseFloat(weight));

        return db.insert(TABLE_NAME, null, values);
    }

    public boolean checkUser(String name, String mobile) {

        String query =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " +
                        COLUMN_NAME + "=? AND " +
                        COLUMN_MOBILE + "=?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{name, mobile}
        );

        boolean userExists = cursor.getCount() > 0;

        cursor.close();

        return userExists;
    }

    public User getUserDetails(String name, String mobile) {

        User user = null;

        String query =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " +
                        COLUMN_NAME + "=? AND " +
                        COLUMN_MOBILE + "=?";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{name, mobile}
        );

        if (cursor.moveToFirst()) {

            user = new User(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_NAME)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_MOBILE)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_AGE)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_EMAIL)
                    ),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)
                    )
            );
        }

        cursor.close();

        return user;
    }

    public int deleteUser(String name, String mobile) {
        return db.delete(
                TABLE_NAME,
                COLUMN_NAME + "=? AND " + COLUMN_MOBILE + "=?",
                new String[]{name, mobile}
        );
    }

    // =========================================================
    // QUIZ QUESTIONS TABLE
    // =========================================================

    private void createQuizTable() {

        String createQuizTableQuery =
                "CREATE TABLE IF NOT EXISTS " + QUIZ_TABLE + " (" +

                        QUIZ_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        QUIZ_QUESTION +
                        " TEXT NOT NULL, " +

                        QUIZ_OPTION_A +
                        " TEXT NOT NULL, " +

                        QUIZ_OPTION_B +
                        " TEXT NOT NULL, " +

                        QUIZ_OPTION_C +
                        " TEXT NOT NULL, " +

                        QUIZ_OPTION_D +
                        " TEXT NOT NULL, " +

                        QUIZ_CORRECT_ANSWER +
                        " TEXT NOT NULL, " +

                        QUIZ_DIFFICULTY +
                        " TEXT, " +

                        QUIZ_USED +
                        " INTEGER DEFAULT 0, " +

                        QUIZ_CREATED_AT +
                        " INTEGER" +

                        ");";

        db.execSQL(createQuizTableQuery);
    }

    // =========================================================
    // SAVE A GENERATED QUIZ QUESTION
    // =========================================================

    public long insertQuizQuestion(
            String question,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctAnswer,
            String difficulty
    ) {

        ContentValues values = new ContentValues();

        values.put(QUIZ_QUESTION, question);
        values.put(QUIZ_OPTION_A, optionA);
        values.put(QUIZ_OPTION_B, optionB);
        values.put(QUIZ_OPTION_C, optionC);
        values.put(QUIZ_OPTION_D, optionD);
        values.put(QUIZ_CORRECT_ANSWER, correctAnswer);
        values.put(QUIZ_DIFFICULTY, difficulty);
        values.put(QUIZ_USED, 0);
        values.put(
                QUIZ_CREATED_AT,
                System.currentTimeMillis()
        );

        return db.insert(
                QUIZ_TABLE,
                null,
                values
        );
    }

    // =========================================================
    // CHECK WHETHER EXACT QUESTION ALREADY EXISTS
    // =========================================================

    public boolean quizQuestionExists(String question) {

        String query =
                "SELECT " + QUIZ_ID +
                        " FROM " + QUIZ_TABLE +
                        " WHERE " + QUIZ_QUESTION + "=? " +
                        "LIMIT 1";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{question}
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // =========================================================
    // MARK QUESTION AS USED
    // =========================================================

    public boolean markQuizQuestionUsed(long questionId) {

        ContentValues values = new ContentValues();

        values.put(QUIZ_USED, 1);

        int rowsUpdated = db.update(
                QUIZ_TABLE,
                values,
                QUIZ_ID + "=?",
                new String[]{String.valueOf(questionId)}
        );

        return rowsUpdated > 0;
    }

    // =========================================================
    // COUNT TOTAL STORED QUESTIONS
    // =========================================================

    public int getQuizQuestionCount() {

        String query =
                "SELECT COUNT(*) FROM " + QUIZ_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    // =========================================================
    // COUNT UNUSED QUESTIONS
    // =========================================================

    public int getUnusedQuizQuestionCount() {

        String query =
                "SELECT COUNT(*) FROM " +
                        QUIZ_TABLE +
                        " WHERE " +
                        QUIZ_USED +
                        "=0";

        Cursor cursor = db.rawQuery(query, null);

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    // =========================================================
// GET ONE UNUSED QUIZ QUESTION
// =========================================================

    public Cursor getOneUnusedQuizQuestion() {

        String query =
                "SELECT * FROM " +
                        QUIZ_TABLE +
                        " WHERE " +
                        QUIZ_USED +
                        "=0 " +
                        "ORDER BY " +
                        QUIZ_ID +
                        " ASC " +
                        "LIMIT 1";

        return db.rawQuery(query, null);
    }

    // =========================================================
// GET ONE UNUSED QUESTION AS QUIZQUESTION OBJECT
// =========================================================

    public QuizQuestion getNextUnusedQuizQuestion() {

        Cursor cursor = getOneUnusedQuizQuestion();

        QuizQuestion quizQuestion = null;

        if (cursor.moveToFirst()) {

            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(QUIZ_ID)
            );

            String question = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_QUESTION)
            );

            String optionA = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_OPTION_A)
            );

            String optionB = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_OPTION_B)
            );

            String optionC = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_OPTION_C)
            );

            String optionD = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_OPTION_D)
            );

            String correctAnswer = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_CORRECT_ANSWER)
            );

            String difficulty = cursor.getString(
                    cursor.getColumnIndexOrThrow(QUIZ_DIFFICULTY)
            );

            int usedValue = cursor.getInt(
                    cursor.getColumnIndexOrThrow(QUIZ_USED)
            );

            quizQuestion = new QuizQuestion(
                    id,
                    question,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    correctAnswer,
                    difficulty,
                    usedValue == 1
            );
        }

        cursor.close();

        return quizQuestion;
    }
}