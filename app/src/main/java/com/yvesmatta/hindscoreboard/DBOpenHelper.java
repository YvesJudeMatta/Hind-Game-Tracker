package com.yvesmatta.hindscoreboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    // Constants for db name and version
    private static final String DATABASE_NAME = "hind.db";
    private static final int DATABASE_VERSION = 1;

    // Constant for all column queries
    public static final String[] ALL_COLUMNS = { "*" };

    // Constants for identifying table and columns
    // shared constants
    public static final String GAME_FOREIGN_ID = "game_id";
    public static final String PLAYER_FOREIGN_ID = "player_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";


    // game
    public static final String TABLE_GAME = "game";
    public static final String GAME_ID = "_id";
    public static final String GAME_PLAYER_COMPLETED = "completed";

    // SQL to create the game table
    private static final String GAME_TABLE_CREATE =
            "CREATE TABLE " + TABLE_GAME + " (" +
            GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GAME_PLAYER_COMPLETED + " BIT, " +
            CREATED_AT + " DATETIME, " +
            UPDATED_AT + " DATETIME);";

    // player
    public static final String TABLE_PLAYER = "player";
    public static final String PLAYER_ID = "_id";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_TOTAL_SCORE = "total_score";

    // SQL to create the player table
    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + TABLE_PLAYER + " (" +
                    PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GAME_FOREIGN_ID + " INTEGER NOT NULL, " +
                    PLAYER_NAME + " TEXT NOT NULL, " +
                    PLAYER_TOTAL_SCORE + " INTEGER, " +
                    CREATED_AT + " DATETIME, " +
                    UPDATED_AT + " DATETIME, " +
                    "FOREIGN KEY (" + GAME_FOREIGN_ID + ") REFERENCES " + TABLE_GAME + "(" + GAME_ID + "))";

    // round_score
    public static final String TABLE_ROUND_SCORE = "round_score";
    public static final String ROUND_SCORE_ID = "_id";
    public static final String ROUND_SCORE_ROUND = "round";
    public static final String ROUND_SCORE_SCORE = "score";

    // SQL to create the round_score table
    private static final String ROUND_SCORE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ROUND_SCORE + " (" +
                    ROUND_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GAME_FOREIGN_ID + " INTEGER NOT NULL, " +
                    PLAYER_FOREIGN_ID + " INTEGER NOT NULL, " +
                    ROUND_SCORE_ROUND + " INTEGER NOT NULL, " +
                    ROUND_SCORE_SCORE + " INTEGER NOT NULL, " +
                    CREATED_AT + " DATETIME, " +
                    UPDATED_AT + " DATETIME, " +
                    "FOREIGN KEY (" + GAME_FOREIGN_ID + ") REFERENCES " + TABLE_GAME + "(" + GAME_ID + "), " +
                    "FOREIGN KEY (" + PLAYER_FOREIGN_ID + ") REFERENCES " + TABLE_PLAYER + "(" + PLAYER_ID + "))";

    // game_winner
    public static final String TABLE_GAME_WINNER = "game_winner";
    public static final String GAME_WINNER_ID = "_id";

    // SQL to create the round_score table
    private static final String GAME_WINNER_TABLE_CREATE =
            "CREATE TABLE " + TABLE_GAME_WINNER + " (" +
                    GAME_WINNER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GAME_FOREIGN_ID + " INTEGER NOT NULL, " +
                    PLAYER_FOREIGN_ID + " INTEGER NOT NULL, " +
                    CREATED_AT + " DATETIME, " +
                    UPDATED_AT + " DATETIME, " +
                    "FOREIGN KEY (" + GAME_FOREIGN_ID + ") REFERENCES " + TABLE_GAME + "(" + GAME_ID + "), " +
                    "FOREIGN KEY (" + PLAYER_FOREIGN_ID + ") REFERENCES " + TABLE_PLAYER + "(" + PLAYER_ID + "))";



    // Constructor
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Execute create table queries in correct order
        sqLiteDatabase.execSQL(GAME_TABLE_CREATE);
        sqLiteDatabase.execSQL(PLAYER_TABLE_CREATE);
        sqLiteDatabase.execSQL(ROUND_SCORE_TABLE_CREATE);
        sqLiteDatabase.execSQL(GAME_WINNER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Execute drop table queries in correct order
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GAME_WINNER_TABLE_CREATE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUND_SCORE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);

        // Create the database
        onCreate(sqLiteDatabase);
    }
}
