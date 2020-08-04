package com.lirancaduri.secendfire.sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lirancaduri.secendfire.data.Shift;

import java.util.ArrayList;
import java.util.List;

public class ShiftSQLite extends SQLiteOpenHelper {



    // הצהרה על העמודות גירסא שם הבסיס נתונים, ושם הטבלא
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "shifts.db";
    private static final String TABLE_NAME = "shifts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_START = "startTime";
    private static final String COLUMN_END = "endTime";
    private static final String COLUMN_SALARY = "salary";
    private static final String COLUMN_TIP = "tip";




    public ShiftSQLite(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }


    // במקרה ולא נוצר אף פעם , יוצר עם העמודות הבאות
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_START + " INTEGER, " +
                COLUMN_END + " INTEGER, " +
                COLUMN_SALARY + " INTEGER, " +
                COLUMN_TIP + " INTEGER);");

    }


    // במקרה ויש עידכון גירסא מוחק את הכל ויוצר מחדש, כמובן צריך לשנות את הפונקציה אחרי כל גירסא כדי שתתאים
    @Override
    public void onUpgrade(SQLiteDatabase db, int lastVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }


    // בודק אם יש עבודה באותו יום
    public boolean workInDay(String date){
        SQLiteDatabase db = getReadableDatabase();
        boolean answer = false;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + "=\'" + date + "\'",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            answer = true;
            break;
        }
        cursor.close();
        db.close();
        return answer;
    }



    // מעדכן משמרת על ידי ID
    public void update(Shift shift) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID,shift.getId());
        contentValues.put(COLUMN_DATE, shift.getDate());
        contentValues.put(COLUMN_START, shift.getStart());
        contentValues.put(COLUMN_END, shift.getEnd());
        contentValues.put(COLUMN_SALARY, shift.getSalary());
        contentValues.put(COLUMN_TIP, shift.getTip());
        db.update(TABLE_NAME,contentValues,COLUMN_ID + "=" + shift.getId(),null);
        db.close();
    }

    // מוסיף משמרת
    public void insert(Shift shift) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, shift.getDate());
        contentValues.put(COLUMN_START, shift.getStart());
        contentValues.put(COLUMN_END, shift.getEnd());
        contentValues.put(COLUMN_SALARY, shift.getSalary());
        contentValues.put(COLUMN_TIP, shift.getTip());
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }


    public void deleteByDate(Shift shift) {
        deleteByDate(shift.getDate());
    }

    // מוחק משמרת על ידי תאריך
    public void deleteByDate(String date){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " =\"" + date + "\";";
        db.execSQL(query);
        db.close();
    }

    // מקבל את ID הכי גבוה שיש עד עכשיו
    public int getMaxId(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            db.close();
            return id;
        }
        return 0;
    }



    public void deleteById(Shift shift) {
        deleteById(shift.getId());
    }
    // מוחק על ידי ID
    public void deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " =\"" + id + "\";";
        db.execSQL(query);
        db.close();
    }


    // מקבל את כל המשמרות שנשמרו
    public List<Shift> getAllShifts() {
        List<Shift> shifts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null, null);
        cursor.moveToFirst();
        int id, salary, tip;
        String date;
        long start, end;
        while (!cursor.isAfterLast()) {
            // לפי מסמר עמודה שמקבל לפי שם העמודה
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            salary = cursor.getInt(cursor.getColumnIndex(COLUMN_SALARY));
            tip = cursor.getInt(cursor.getColumnIndex(COLUMN_TIP));
            start = cursor.getLong(cursor.getColumnIndex(COLUMN_START));
            end = cursor.getLong(cursor.getColumnIndex(COLUMN_END));
            date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            shifts.add(new Shift(date, start, end, salary, tip, id));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return shifts;
    }


    // CRUD

    // Create
    // Read
    // Update
    // Delete

}
