package com.dharrya.android.notifier.app.database;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import com.dharrya.android.notifier.app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifyAdapter {

    public static SimpleCursorAdapter getAdapter(Context context) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                context,
                R.layout.notify_listitem,
                null,
                new String[] {
                        Notify.COL_TITLE, Notify.COL_DATE, Notify.COL_MESSAGE,
                },
                new int[] {
                        R.id.notifyTitle, R.id.notifyDate, R.id.notifyMessage
                },
                0
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                if( column == 4 ){ // let's suppose that the column 4 is the date
                    TextView tv = (TextView) view;
                    Integer timestampInt = Integer.parseInt(cursor.getString(4));
                    String timestampStr = String.valueOf(timestampInt);
                    Long timestamp = Long.parseLong(timestampStr) * 1000;
                    Date date = new Date(timestamp);
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    tv.setText(dateFormat.format(date));
                    return true;
                }
                return false;
            }
        });

        return adapter;
    }
}
