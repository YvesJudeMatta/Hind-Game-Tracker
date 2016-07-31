package com.yvesmatta.hindgametracker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class GameProvider extends ContentProvider {

    private static final String AUTHORITY = "com.yvesmatta.hindgametracker.gameprovider";
    private static final String BASE_PATH = "hind";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constants to identify the requested operation
    private static final int GAME = 1;
    private static final int GAME_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String CONTENT_ITEM_TYPE = "Game";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, GAME);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", GAME_ID);
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
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        if (uriMatcher.match(uri) == GAME_ID) {
            s = DBOpenHelper.GAME_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(
                DBOpenHelper.TABLE_GAME,
                DBOpenHelper.GAME_ALL_COLUMNS,
                s,
                null, null, null,
                DBOpenHelper.GAME_CREATED
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = database.insert(DBOpenHelper.TABLE_GAME, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return database.delete(DBOpenHelper.TABLE_GAME, s, strings);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return database.update(DBOpenHelper.TABLE_GAME, contentValues, s, strings);
    }
}
