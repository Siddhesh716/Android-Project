package com.example.basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {

    private static final String DATABASE_NAME = "userDatabase";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_WEIGHT = "weight";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        createTable();
    }

    private void createTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_MOBILE + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_WEIGHT + " REAL);";
        db.execSQL(createTableQuery);
    }

    public long insertUser(String name, String mobile, String age, String email, String weight) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_AGE, Integer.parseInt(age));  // Store as integer
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_WEIGHT, Float.parseFloat(weight));  // Store as float
        return db.insert(TABLE_NAME, null, values);
    }

    public boolean checkUser(String name, String mobile) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NAME + "=? AND " + COLUMN_MOBILE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{name, mobile});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    public User getUserDetails(String name, String mobile) {
        SQLiteDatabase db = this.db;
        User user = null;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NAME + "=? AND " + COLUMN_MOBILE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{name, mobile});

        if (cursor.moveToFirst()) {
            // Ensure proper parsing of numeric values
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE)),  // Return as String for simplicity
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT))
            );
        }
        cursor.close();
        return user;
    }
}