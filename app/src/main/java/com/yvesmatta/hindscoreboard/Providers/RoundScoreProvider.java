package com.yvesmatta.hindscoreboard.Providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yvesmatta.hindscoreboard.DBOpenHelper;

public class RoundScoreProvider extends ContentProvider {

    private static final String AUTHORITY = "com.yvesmatta.hindscoreboard.roundscoreprovider";
    private static final String BASE_PATH = "hind_round_score";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constants to identify the requested operation
    private static final int ROUND_SCORE = 1;
    private static final int ROUND_SCORE_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, ROUND_SCORE);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ROUND_SCORE_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        if (uriMatcher.match(uri) == ROUND_SCORE_ID) {
            s = DBOpenHelper.ROUND_SCORE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(
                DBOpenHelper.TABLE_ROUND_SCORE,
                DBOpenHelper.ALL_COLUMNS,
                s,
                null, null, null,
                DBOpenHelper.CREATED_AT
        );
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        long id = database.insert(DBOpenHelper.TABLE_ROUND_SCORE, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return database.delete(DBOpenHelper.TABLE_ROUND_SCORE, s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return database.update(DBOpenHelper.TABLE_ROUND_SCORE, contentValues, s, strings);
    }
}
