package com.chernenkovit.tasker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.chernenkovit.tasker.model.ModelTask;

/** Helper class for database creating and CRUD operations. */
public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasker_database";

    public static final String TASKS_TABLE = "tasks_table";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_PRIORITY_COLUMN = "task_priority";
    public static final String TASK_STATUS_COLUMN = "task_status";
    public static final String TASK_TIMESTAMP_COLUMN = "task_timestamp";

    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE " + TASKS_TABLE
            + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_TITLE_COLUMN + " TEXT NOT NULL, "
            + TASK_DATE_COLUMN + " LONG, "
            + TASK_PRIORITY_COLUMN + " INTEGER, "
            + TASK_STATUS_COLUMN + " INTEGER, "
            + TASK_TIMESTAMP_COLUMN + " LONG);";

    public static final String SELECTION_STATUS = TASK_STATUS_COLUMN + " = ?";
    public static final String SELECTION_TIMESTAMP = TASK_TIMESTAMP_COLUMN + " = ?";
    public static final String SELECTION_LIKE_TITLE = TASK_TITLE_COLUMN + " LIKE ?";

    private DBQueryManager dbQueryManager;
    private DBUpdateManager dbUpdateManager;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbQueryManager = new DBQueryManager(getReadableDatabase());
        dbUpdateManager = new DBUpdateManager(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TASKS_TABLE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + TASKS_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void saveTask(ModelTask task) {
        ContentValues newValues = new ContentValues();
        newValues.put(TASK_TITLE_COLUMN, task.getTitle());
        newValues.put(TASK_DATE_COLUMN, task.getDate());
        newValues.put(TASK_PRIORITY_COLUMN, task.getPriority());
        newValues.put(TASK_STATUS_COLUMN, task.getStatus());
        newValues.put(TASK_TIMESTAMP_COLUMN, task.getTimestamp());

        getWritableDatabase().insert(TASKS_TABLE, null, newValues);
    }

    public DBQueryManager query() {
        return dbQueryManager;
    }

    public DBUpdateManager update() {
        return dbUpdateManager;
    }

    public void removeTask(long timestamp) {
        getWritableDatabase().delete(TASKS_TABLE,
                SELECTION_TIMESTAMP,
                new String[]{Long.toString(timestamp)});
    }
}
