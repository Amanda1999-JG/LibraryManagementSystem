package com.example.librarymanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Library.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "CREATE TABLE Book (" +
                            "BOOK_ID TEXT PRIMARY KEY," +
                            "TITLE TEXT NOT NULL," +
                            "PUBLISHER_NAME TEXT);"
            );
            db.execSQL(
                    "CREATE TABLE Publisher (" +
                            "NAME TEXT PRIMARY KEY," +
                            "ADDRESS TEXT," +
                            "PHONE TEXT);"
            );
            db.execSQL(
                    "CREATE TABLE Branch (" +
                            "BRANCH_ID TEXT PRIMARY KEY," +
                            "BRANCH_NAME TEXT," +
                            "ADDRESS TEXT);"
            );
            db.execSQL(
                    "CREATE TABLE Member (" +
                            "CARD_NO TEXT PRIMARY KEY," +
                            "NAME TEXT," +
                            "ADDRESS TEXT," +
                            "PHONE TEXT," +
                            "UNPAID_DUES REAL);"
            );
            db.execSQL(
                    "CREATE TABLE Book_Author (" +
                            "BOOK_ID TEXT," +
                            "AUTHOR_NAME TEXT," +
                            "PRIMARY KEY (BOOK_ID, AUTHOR_NAME)," +
                            "FOREIGN KEY (BOOK_ID) REFERENCES Book(BOOK_ID));"
            );
            db.execSQL(
                    "CREATE TABLE Book_Copy (" +
                            "BOOK_ID TEXT," +
                            "BRANCH_ID TEXT," +
                            "ACCESS_NO TEXT PRIMARY KEY," +
                            "FOREIGN KEY (BOOK_ID) REFERENCES Book(BOOK_ID)," +
                            "FOREIGN KEY (BRANCH_ID) REFERENCES Branch(BRANCH_ID));"
            );
            db.execSQL(
                    "CREATE TABLE Book_Loan (" +
                            "ACCESS_NO TEXT," +
                            "BRANCH_ID TEXT," +
                            "CARD_NO TEXT," +
                            "DATE_OUT TEXT," +
                            "DATE_DUE TEXT," +
                            "DATE_RETURNED TEXT," +
                            "PRIMARY KEY (ACCESS_NO, BRANCH_ID, CARD_NO, DATE_OUT)," +
                            "FOREIGN KEY (ACCESS_NO, BRANCH_ID) REFERENCES Book_Copy(ACCESS_NO, BRANCH_ID)," +
                            "FOREIGN KEY (CARD_NO) REFERENCES Member(CARD_NO)," +
                            "FOREIGN KEY (BRANCH_ID) REFERENCES Branch(BRANCH_ID));"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Book_Loan");
        db.execSQL("DROP TABLE IF EXISTS Book_Copy");
        db.execSQL("DROP TABLE IF EXISTS Book_Author");
        db.execSQL("DROP TABLE IF EXISTS Member");
        db.execSQL("DROP TABLE IF EXISTS Branch");
        db.execSQL("DROP TABLE IF EXISTS Publisher");
        db.execSQL("DROP TABLE IF EXISTS Book");
        onCreate(db);
    }

    public boolean insertBook(String bookId, String title, String publisherName) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("BOOK_ID", bookId);
            values.put("TITLE", title);
            values.put("PUBLISHER_NAME", publisherName);
            return db.insert("Book", null, values) != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Book", null);
    }

    public boolean updateBook(String bookId, String title, String publisherName) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("TITLE", title);
            values.put("PUBLISHER_NAME", publisherName);
            return db.update("Book", values, "BOOK_ID = ?", new String[]{bookId}) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(String bookId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            return db.delete("Book", "BOOK_ID = ?", new String[]{bookId}) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
