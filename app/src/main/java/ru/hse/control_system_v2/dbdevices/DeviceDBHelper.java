package ru.hse.control_system_v2.dbdevices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.hse.control_system_v2.R;

public class DeviceDBHelper extends SQLiteOpenHelper {
    static DeviceDBHelper instance;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "addedDevices";
    public static final String TABLE_DEVICES = "device";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MAC = "address";
    public static final String KEY_URL_PH = "photo";
    public static final String KEY_CLASS = "class";
    public static final String KEY_TYPE = "id_type";
    public static final String KEY_PROTO = "id_protocol";
    public static final String KEY_PANEL = "id_panel";
    private final Context contextmy;

    public DeviceDBHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION); contextmy = context;}

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("myLog", "Device helper is created!");
        db.execSQL("create table " + TABLE_DEVICES + "(" + KEY_ID + " integer primary key AUTOINCREMENT,"
                + KEY_NAME + " text," + KEY_MAC + " text," + KEY_CLASS + " text," + KEY_TYPE + " text,"
                + KEY_URL_PH + " text," + KEY_PROTO + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DEVICES);

        onCreate(db);
    }

    public int count() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_DEVICES;
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();
    }

    public void viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_DEVICES;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Log.d("SQL", cursor.getInt(0) + " " + cursor.getString(1) +" "+ cursor.getString(2) +
                    " "+ cursor.getString(3) +" "+ cursor.getString(4)+" "+ cursor.getString(5)
                    +" "+ cursor.getString(6));
            cursor.moveToNext();
        }
    }

    public int insert(ContentValues contentValues) {
        int result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="select * from " + TABLE_DEVICES + " where " + KEY_MAC + " = '" + contentValues.get(KEY_MAC) + "';";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0) {
            db.insert(TABLE_DEVICES, null, contentValues);
            result = 1;
        }
        return result;
    }

    public void update(ContentValues contentValues, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_DEVICES, contentValues, "_id=?", new String[]{String.valueOf(id)});
    }

    public void deleteDevice(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DELETE FROM " + TABLE_DEVICES + " WHERE _id = " + id + ";";
        Log.d("SQL", query);
        db.execSQL(query);
    }

    public void deleteProto(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="select * from " + TABLE_DEVICES + " where " + KEY_PROTO + " = '" + name + "';";
        Log.d("delpro", "I'm in deviceDBHelper");
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String id = cursor.getString(cursor.getColumnIndex(DeviceDBHelper.KEY_ID));
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME, cursor.getString(cursor.getColumnIndex(DeviceDBHelper.KEY_NAME)));
            contentValues.put(KEY_MAC, cursor.getString(cursor.getColumnIndex(DeviceDBHelper.KEY_MAC)));
            contentValues.put(KEY_CLASS, cursor.getString(cursor.getColumnIndex(DeviceDBHelper.KEY_CLASS)));
            contentValues.put(KEY_TYPE, cursor.getString(cursor.getColumnIndex(DeviceDBHelper.KEY_TYPE)));
            contentValues.put(KEY_URL_PH, cursor.getString(cursor.getColumnIndex(DeviceDBHelper.KEY_URL_PH)));
            contentValues.put(KEY_PROTO, contextmy.getResources().getString(R.string.TAG_default_protocol));
            String deleteQuery = "DELETE FROM " + TABLE_DEVICES + " WHERE _id = " + id + ";";
            Log.d("delpro", cursor.getString(1));
            this.getWritableDatabase().execSQL(deleteQuery);
            insert(contentValues);
            cursor.moveToNext();
        }
    }

    public static synchronized DeviceDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceDBHelper(context);
        }
        return instance;
    }
}