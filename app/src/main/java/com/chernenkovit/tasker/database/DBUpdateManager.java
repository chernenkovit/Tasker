package com.chernenkovit.tasker.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.chernenkovit.tasker.model.ModelTask;

import static com.chernenkovit.tasker.database.DBHelper.TASKS_TABLE;
import static com.chernenkovit.tasker.database.DBHelper.TASK_DATE_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_PRIORITY_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_STATUS_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_TIMESTAMP_COLUMN;
import static com.chernenkovit.tasker.database.DBHelper.TASK_TITLE_COLUMN;

/** Helper class for records updating in database. */
public class DBUpdateManager {

    SQLiteDatabase database;

    DBUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }

    public void title(long timestamp, String title) {
        update(TASK_TITLE_COLUMN, timestamp, title);
    }

    public void date(long timestamp, long date) {
        update(TASK_DATE_COLUMN, timestamp, date);
    }

    public void priority(long timestamp, int priority) {
        update(TASK_PRIORITY_COLUMN, timestamp, priority);
    }

    public void status(long timestamp, int status) {
        update(TASK_STATUS_COLUMN, timestamp, status);
    }

    public void task(ModelTask task) {
        title(task.getTimestamp(), task.getTitle());
        date(task.getTimestamp(), task.getDate());
        priority(task.getTimestamp(), task.getPriority());
        status(task.getTimestamp(), task.getStatus());
    }

    private void update(String column, long key, String value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        database.update(TASKS_TABLE, values, TASK_TIMESTAMP_COLUMN + " = " + key, null);
    }

    private void update(String column, long key, long value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        database.update(TASKS_TABLE, values, TASK_TIMESTAMP_COLUMN + " = " + key, null);
    }

}
