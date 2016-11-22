package com.androidsample.contentprovidersample.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Keval on 21-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class NameProvider extends ContentProvider {
    private DbHelper mDbHelper;

    private static final UriMatcher URI_MATCHER;

    private static final int ALL_CONTENT = 1;
    private static final int URI_WITH_ID = 2;
    private static final int URI_WITH_QUERY = 3;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(NameContract.CONTENT_AUTHORITY, NameContract.NAMES_TABLE, ALL_CONTENT);
        URI_MATCHER.addURI(NameContract.CONTENT_AUTHORITY, NameContract.NAMES_TABLE + "/#", URI_WITH_ID);
        URI_MATCHER.addURI(NameContract.CONTENT_AUTHORITY, NameContract.NAMES_TABLE + "/*", URI_WITH_QUERY);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projs, String where, String[] args, String s1) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int res = URI_MATCHER.match(uri);
        switch (res) {
            case ALL_CONTENT:
                return db.query(NameContract.NAMES_TABLE, projs, null, null, null, null, null);
            case URI_WITH_ID:
                return db.query(NameContract.NAMES_TABLE, projs, NameContract.NamesEntry.ID + " =?",
                        new String[]{uri.getLastPathSegment()}, null, null, null);
            case URI_WITH_QUERY:
                return db.query(NameContract.NAMES_TABLE, projs, NameContract.NamesEntry.NAME + " LIKE ?%",
                        new String[]{uri.getLastPathSegment()}, null, null, null);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int res = URI_MATCHER.match(uri);
        switch (res) {
            case ALL_CONTENT:
            case URI_WITH_QUERY:
            default:
                return NameContract.NamesEntry.CONTENT_DIR_TYPE;
            case URI_WITH_ID:
                return NameContract.NamesEntry.CONTENT_ITEM_TYPE;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int res = URI_MATCHER.match(uri);
        switch (res) {
            case ALL_CONTENT:
                long l = db.insert(NameContract.NAMES_TABLE, null, contentValues);
                return uri.buildUpon().appendPath(l + "").build();
            default:
                throw new IllegalArgumentException("Unsupported uri : " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int res = URI_MATCHER.match(uri);
        switch (res) {
            case URI_WITH_ID:
                return db.delete(NameContract.NAMES_TABLE, NameContract.NamesEntry.ID + " =?",
                        new String[]{uri.getLastPathSegment() + ""});
            default:
                throw new IllegalArgumentException("Unsupported uri : " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int res = URI_MATCHER.match(uri);
        switch (res) {
            case URI_WITH_ID:
                return db.delete(NameContract.NAMES_TABLE, NameContract.NamesEntry.ID + " =?",
                        new String[]{uri.getLastPathSegment() + ""});
            default:
                throw new IllegalArgumentException("Unsupported uri : " + uri);
        }
    }
}
