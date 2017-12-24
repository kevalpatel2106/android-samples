package com.example.android.sqlitedemo.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nishita Aggarwal on 25-11-2017.
 */

public class DataBase extends SQLiteOpenHelper{


        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "Database_form";
        private static final String TABLE_NAME = "Form";
        private static final String KEY_NAME = "Name";
        private static final String KEY_DATE="Date";
        private static final String KEY_PERCENTAGE="PERCENTAGE";

        public DataBase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            String CREATE_SUGARMORNING_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                    + KEY_NAME + " TEXT, "
                    + KEY_DATE + " TEXT, "
                    + KEY_PERCENTAGE + " REAL" + ")";
            database.execSQL(CREATE_SUGARMORNING_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            // Create tables again
            onCreate(db);
        }

        void addData(Data d)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NAME,d.getName());
            values.put(KEY_DATE, d.getDoj());
            values.put(KEY_PERCENTAGE,d.getPercentage());
            try {
                db.insert(TABLE_NAME, null, values);
            }
            catch (Exception e)
            {
                Log.e("ins","failed");
            }
            //2nd argument is String containing nullColumnHack
            db.close(); // Closing database connection
        }

        public List<Data> readData() {
            List<Data> dataList = new ArrayList<Data>();
            String selectQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Data d = new Data();
                    d.setName(cursor.getString(0));
                    d.setDoj(cursor.getString(1));
                    d.setPercentage(Float.parseFloat(cursor.getString(2)));
                    dataList.add(d);
                } while (cursor.moveToNext());
            }
            return dataList;
        }

}
