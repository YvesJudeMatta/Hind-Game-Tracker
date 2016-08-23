package com.yvesmatta.hindgametracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    // Constants for db name and version
    private static final String DATABASE_NAME = "hind.db";
    private static final int DATABASE_VERSION = 1;

    public static final String[] ALL_COLUMNS = { "*" };

    // Constants for identifying table and columns

    // player
    public static final String TABLE_PLAYER = "player";
    public static final String PLAYER_ID = "_id";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_TOTAL_SCORE = "total_score";
    public static final String PLAYER_CREATED = "created";

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
    public static final String GAME_PLAYER_WINNER = "winner";
    public static final String GAME_PLAYER_COMPLETED = "completed";
    public static final String GAME_CREATED = "created";

    // score
    public static final String TABLE_SCORE = "score";
    public static final String SCORE_ID = "_id";
    public static final String SCORE_PLAYER = "player";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PLAYER_TABLE_CREATE);
        sqLiteDatabase.execSQL(generateScoreTableQuery());
        sqLiteDatabase.execSQL(generateGameTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        onCreate(sqLiteDatabase);
    }

    private String generateScoreTableQuery() {
        // round column query
        String roundColumns = "";
        for (int i = 1; i <= Game.MAX_ROUNDS; i++) {
            roundColumns += "round_" + i + " INTEGER, ";
        }

        // score table query
        String scoreTableCreate =
                "CREATE TABLE " + TABLE_SCORE + " (" +
                        SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        roundColumns +
                        " FOREIGN KEY (" + SCORE_PLAYER + ") REFERENCES " + TABLE_PLAYER + "(" + PLAYER_ID + ")" +
                        ")";

        // return the score table query
        return scoreTableCreate;
    }

    private String generateGameTableQuery() {
        // player column query
        String playerColumns = "";
        for (int i = 1; i <= Game.MAX_NUMBER_OF_PLAYERS; i++) {
           playerColumns += "player_" + i + " TEXT, ";
        }

        // Foreign key constraints for the game table
        String playerForeignKeyConstraints = "";
        for (int i = 1; i <= Game.MAX_NUMBER_OF_PLAYERS; i++) {
            if (i == Game.MAX_NUMBER_OF_PLAYERS) {
                playerForeignKeyConstraints += " FOREIGN KEY (player_" + i + ") REFERENCES " + TABLE_PLAYER + "(" + PLAYER_ID + ")";
            } else {
                playerForeignKeyConstraints += " FOREIGN KEY (player_"  + i + ") REFERENCES " + TABLE_PLAYER + "(" + PLAYER_ID + "), ";
            }
        }

        // game table query
        String gameTableCreate =
                "CREATE TABLE " + TABLE_GAME + " (" +
                        GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        GAME_PLAYER_COUNT + " INTEGER NOT NULL, " +
                        playerColumns +
                        GAME_PLAYER_WINNER + " TEXT, " +
                        GAME_PLAYER_COMPLETED + " BIT, " +
                        GAME_CREATED + " TEXT, " +
                        playerForeignKeyConstraints +
                        ")";

        // return the game table query
        return gameTableCreate;
    }
}
