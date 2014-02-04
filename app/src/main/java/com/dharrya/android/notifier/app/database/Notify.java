package com.dharrya.android.notifier.app.database;

import android.content.ContentValues;
import android.database.Cursor;

public class Notify {

    public static final String TABLE_NAME = "Notify";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_MESSAGE = "message";
    public static final String COL_TYPE = "type";
    public static final String COL_DATE = "date";

    // For database projection so order is consistent
    public static final String[] FIELDS = { COL_ID, COL_TITLE, COL_MESSAGE,
            COL_TYPE, COL_DATE};

    /*
     * The SQL code that creates a Table for storing Persons in.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_TITLE + " TEXT NOT NULL DEFAULT '',"
                    + COL_MESSAGE + " TEXT NOT NULL DEFAULT '',"
                    + COL_TYPE + " INTEGER NOT NULL DEFAULT 1,"
                    + COL_DATE + " INTEGER NOT NULL DEFAULT 0"
                    + ")";

    public long id = 0;
    public String title = "";
    public String message = "";
    public int type = 0;
    public int date = 0;

    public Notify() {
    }

    public Notify(String title, String message, int type, int date) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.date = date;
    }

    public Notify(int id, String title, String message, int type, int date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.date = date;
    }

    public Notify(final Cursor cursor) {
        this.id = cursor.getInt(0);
        this.title = cursor.getString(1);
        this.message = cursor.getString(2);
        this.type = cursor.getInt(3);
        this.date = cursor.getInt(4);
    }

    public Notify(final ContentValues values) {
        this.title = values.getAsString(COL_TITLE);
        this.message = values.getAsString(COL_MESSAGE);
        this.date = Integer.parseInt(values.getAsString(COL_DATE));
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContent() {
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_TITLE, title);
        values.put(COL_MESSAGE, message);
        values.put(COL_TYPE, type);
        values.put(COL_DATE, date);

        return values;
    }
}
