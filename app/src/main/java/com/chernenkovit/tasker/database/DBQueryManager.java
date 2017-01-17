package com.chernenkovit.tasker.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chernenkovit.tasker.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

import static com.chernenkovit.tasker.database.DBHelper.SELECTION_TIMESTAMP;
import static com.chernenkovit.tasker.database.DBHelper.TASKS_TABLE;
import static com.chernenkovit.tasker.database.DBHelper.TASK_DATE_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_PRIORITY_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_STATUS_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_TIMESTAMP_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_TITLE_COLUMN;

public class DBQueryManager {

    private SQLiteDatabase database;

    DBQueryManager(SQLiteDatabase database) {
        this.database = database;
    }

    public ModelTask getTask(long timestamp) {
        ModelTask modelTask = null;
        Cursor cursor = database.query(TASKS_TABLE,
                null,
                SELECTION_TIMESTAMP,
                new String[]{Long.toString(timestamp)},
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(TASK_TITLE_COLUMN));
            long date = cursor.getLong(cursor.getColumnIndex(TASK_DATE_COLUMN));
            int priority = cursor.getInt(cursor.getColumnIndex(TASK_PRIORITY_COLUMN));
            int status = cursor.getInt(cursor.getColumnIndex(TASK_STATUS_COLUMN));

            modelTask = new ModelTask(title, date, priority, status, timestamp);
        }
        cursor.close();

        return modelTask;
    }

    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<ModelTask> tasks = new ArrayList<>();

        Cursor cursor = database.query(TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(TASK_TITLE_COLUMN));
                long date = cursor.getLong(cursor.getColumnIndex(TASK_DATE_COLUMN));
                int priority = cursor.getInt(cursor.getColumnIndex(TASK_PRIORITY_COLUMN));
                int status = cursor.getInt(cursor.getColumnIndex(TASK_STATUS_COLUMN));
                long timestamp = cursor.getLong(cursor.getColumnIndex(TASK_TIMESTAMP_COLUMN));

                ModelTask task = new ModelTask(title, date, priority, status, timestamp);
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }
}
