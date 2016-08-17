package com.yvesmatta.hindgametracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    // Constants for db name and version
    private static final String DATABASE_NAME = "hind.db";
    private static final int DATABASE_VERSION = 1;

    // Constants for identifying table and columns

    // player
    public static final String TABLE_PLAYER = "player";
    public static final String PLAYER_ID = "_id";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_TOTAL_SCORE = "total_score";
    public static final String PLAYER_CREATED = "created";

    public static final String[] PLAYER_ALL_COLUMNS = {
            PLAYER_ID,
            PLAYER_NAME,
            PLAYER_TOTAL_SCORE,
            PLAYER_CREATED
    };

    // SQL to create the table
    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + TABLE_PLAYER + " (" +
                    PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PLAYER_NAME + " TEXT NOT NULL, " +
                    PLAYER_TOTAL_SCORE + " INTEGER, " +
                    PLAYER_CREATED + "  TEXT" +
                    ")";

    // game
    public static final String TABLE_GAME = "game";
    public static final String GAME_ID = "_id";
    public static final String GAME_PLAYER_COUNT = "player_count";
    public static final String GAME_PLAYER_ONE = "player_one";
    public static final String GAME_PLAYER_TWO = "player_two";
    public static final String GAME_PLAYER_THREE = "player_three";
    public static final String GAME_PLAYER_FOUR = "player_four";
    public static final String GAME_PLAYER_WINNER = "winner";
    public static final String GAME_PLAYER_COMPLETED = "completed";
    public static final String GAME_CREATED = "created";

    public static final String[] GAME_ALL_COLUMNS = {
            GAME_ID,
            GAME_PLAYER_COUNT,
            GAME_PLAYER_ONE,
            GAME_PLAYER_TWO,
            GAME_PLAYER_THREE,
            GAME_PLAYER_FOUR,
            GAME_PLAYER_WINNER,
            GAME_PLAYER_COMPLETED,
            GAME_CREATED
    };

    // SQL to create the table
    private static final String GAME_TABLE_CREATE =
            "CREATE TABLE " + TABLE_GAME + " (" +
                    GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GAME_PLAYER_COUNT + " INTEGER NOT NULL, " +
                    GAME_PLAYER_ONE + " TEXT NOT NULL, " +
                    GAME_PLAYER_TWO + " TEXT NOT NULL, " +
                    GAME_PLAYER_THREE + " TEXT, " +
                    GAME_PLAYER_FOUR + " TEXT, " +
                    GAME_PLAYER_WINNER + " TEXT, " +
                    GAME_PLAYER_COMPLETED + " BIT, " +
                    GAME_CREATED + " TEXT, " +
                    " FOREIGN KEY(" + GAME_PLAYER_ONE + ") REFERENCES " + TABLE_PLAYER + "("+ PLAYER_ID +"), " +
                    " FOREIGN KEY(" + GAME_PLAYER_TWO + ") REFERENCES " + TABLE_PLAYER + "("+ PLAYER_ID +"), " +
                    " FOREIGN KEY(" + GAME_PLAYER_THREE + ") REFERENCES " + TABLE_PLAYER + "("+ PLAYER_ID +"), " +
                    " FOREIGN KEY(" + GAME_PLAYER_FOUR + ") REFERENCES " + TABLE_PLAYER + "("+ PLAYER_ID +") " +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PLAYER_TABLE_CREATE);
        sqLiteDatabase.execSQL(GAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        onCreate(sqLiteDatabase);
    }
}
