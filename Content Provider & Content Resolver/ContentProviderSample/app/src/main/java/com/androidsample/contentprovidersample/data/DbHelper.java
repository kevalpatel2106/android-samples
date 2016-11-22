package com.androidsample.contentprovidersample.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Keval on 21-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "names.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE " + NameContract.NAMES_TABLE +
            "(" + NameContract.NamesEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NameContract.NamesEntry.NAME + " TEXT NOT NULL);";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
