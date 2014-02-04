package com.dharrya.android.notifier.app.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class NotifyProvider
        extends ContentProvider
{

    // All URIs share these parts
    public static final String AUTHORITY = "com.dharrya.notifier.provider";
    public static final String SCHEME = "content://";

    // URIs
    // Used for all persons
    public static final String NOTIFIES = SCHEME + AUTHORITY + "/notify";
    public static final Uri URI_NOTIFIES = Uri.parse(NOTIFIES);
    // Used for a single person, just add the id to the end
    public static final String NOTIFY_BASE = NOTIFIES + "/";

    public NotifyProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result = 0;
        if (selection == null) {
            result = DatabaseHandler
                    .getInstance(getContext())
                    .clearAllNotify();
        }

        return result;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Notify notify = new Notify(values);
        long id = DatabaseHandler
                .getInstance(getContext())
                .putNotify(notify);

        return Uri.parse(NOTIFY_BASE + id);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor result = null;
        if (URI_NOTIFIES.equals(uri)) {
            result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(Notify.TABLE_NAME, Notify.FIELDS, null, null, null,
                            null, Notify.COL_ID + " DESC", null);
            result.setNotificationUri(getContext().getContentResolver(), URI_NOTIFIES);
        } else if (uri.toString().startsWith(NOTIFY_BASE)) {
            final long id = Long.parseLong(uri.getLastPathSegment());
            result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(Notify.TABLE_NAME, Notify.FIELDS,
                            Notify.COL_ID + " IS ?",
                            new String[] { String.valueOf(id) }, null, null,
                            Notify.COL_ID + " DESC", null);
            result.setNotificationUri(getContext().getContentResolver(), URI_NOTIFIES);
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
