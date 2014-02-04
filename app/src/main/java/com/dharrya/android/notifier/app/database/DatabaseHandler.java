package com.dharrya.android.notifier.app.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler
        extends SQLiteOpenHelper
{
    private static DatabaseHandler singleton;

    public static DatabaseHandler getInstance(final Context context) {
        if (singleton == null) {
            singleton = new DatabaseHandler(context);
        }
        return singleton;
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "providerDharrya";

    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Good idea to have the context that doesn't die with the window
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Notify.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized Notify getNotify(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(Notify.TABLE_NAME, Notify.FIELDS,
                Notify.COL_ID + " IS ?", new String[] { String.valueOf(id) },
                null, null, Notify.COL_DATE + " DESC", null);
        if (cursor == null || cursor.isAfterLast()) {
            return null;
        }

        Notify item = null;
        if (cursor.moveToFirst()) {
            item = new Notify(cursor);
        }
        cursor.close();
        return item;
    }


    public synchronized boolean updateNotify(final Notify notify) {
        int result = 0;
        final SQLiteDatabase db = this.getWritableDatabase();

        result += db.update(Notify.TABLE_NAME, notify.getContent(),
                    Notify.COL_ID + " IS ?",
                    new String[] { String.valueOf(notify.id) });
        if (result > 0) {
            notifyProviderOnNotifyChange();
            return true;
        }

        return false;
    }

    public synchronized long putNotify(final Notify notify) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final long id = db.insert(Notify.TABLE_NAME, null, notify.getContent());

        if (id > -1) {
            removeExtraNotifies();
            notifyProviderOnNotifyChange();
            notify.id = id;
            return id;
        }

        return -1;
    }

    public synchronized int removeNotify(final Notify notify) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(Notify.TABLE_NAME,
                Notify.COL_ID + " IS ?",
                new String[] { Long.toString(notify.id) });

        if (result > 0) {
            notifyProviderOnNotifyChange();
        }
        return result;
    }

    public synchronized int clearAllNotify() {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(Notify.TABLE_NAME, null, null);
        notifyProviderOnNotifyChange();
        return result;
    }

    public synchronized void removeExtraNotifies() {
        final SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + Notify.TABLE_NAME + " WHERE " +  Notify.COL_ID + " NOT in (SELECT " + Notify.COL_ID + " FROM " + Notify.TABLE_NAME + " ORDER BY " + Notify.COL_ID + " DESC LIMIT 20 );";
        db.execSQL(query);
    }

    private void notifyProviderOnNotifyChange() {
        context.getContentResolver().notifyChange(
                NotifyProvider.URI_NOTIFIES, null, false);
    }
}
