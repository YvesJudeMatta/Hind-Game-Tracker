package com.yvesmatta.hindgametracker;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // Fragments
    private FragmentManager fragmentManager;
    private HindListFragment hindListFragment;
    private HindSetupFragment hindSetupFragment;
    private HindPlayFragment hindPlayFragment;

    // Game
    public Game game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance of the FragmentManager and FragmentTransaction to add/remove fragments
        fragmentManager = getFragmentManager();

        // Add fragment to frgContainer and commit the transaction
        hindListFragment = new HindListFragment();
        fragmentManager.beginTransaction().add(R.id.frgContainer, hindListFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Load the GameSetupFragment
    public void loadGameSetupFragment(View view) {
        // Replace fragment in frgContainer with new fragment and commit the transaction
        hindSetupFragment = new HindSetupFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frgContainer, hindSetupFragment)
                .addToBackStack(null)
                .commit();
    }

    // Load the GamePlayFragment
    public void loadGamePlayFragment(View view) {
        game = hindSetupFragment.setupGame();
        if (game != null) {
            // Load the database with the game and players
            loadDatabase();

            // Replace fragment in frgContainer with new fragment and commit the transaction
            hindPlayFragment = new HindPlayFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.frgContainer, hindPlayFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void loadDatabase() {
        // insert players
        insertPlayers();

        // insert game
        insertGame();
    }

    private void insertPlayers() {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            // Load the content values with the player data
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.PLAYER_NAME, game.getAllPlayers().get(i).getName());
            contentValues.put(DBOpenHelper.PLAYER_TOTAL_SCORE, game.getAllPlayers().get(i).getTotalScore());
            contentValues.put(DBOpenHelper.PLAYER_CREATED, true);

            // Insert the player into the database and set the id for the player
            Uri playerUri = getContentResolver().insert(PlayerProvider.CONTENT_URI, contentValues);
            if (playerUri != null)
                game.getAllPlayers().get(i).setId(Integer.parseInt(playerUri.getLastPathSegment()));
        }
    }

    private void insertGame() {
        // Load the content values with the game data
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.GAME_PLAYER_COUNT, game.getNumberOfPlayers());
        contentValues.put(DBOpenHelper.GAME_PLAYER_COMPLETED, false);
        contentValues.put(DBOpenHelper.GAME_CREATED, true);

        // Load the player id if the players id is not 0
        contentValues = loadPlayersInContentValues(contentValues, game.getNumberOfPlayers());

        // Insert the game into the database and set the id for the game
        Uri uriGame = getContentResolver().insert(GameProvider.CONTENT_URI, contentValues);
        if (uriGame != null)
            game.setId(Integer.parseInt(uriGame.getLastPathSegment()));
    }

    private ContentValues loadPlayersInContentValues(ContentValues contentValues, int numberOfPlayers) {
        if (numberOfPlayers == 2) {
            contentValues.put(DBOpenHelper.GAME_PLAYER_ONE, game.getAllPlayers().get(0).getId());
            contentValues.put(DBOpenHelper.GAME_PLAYER_TWO, game.getAllPlayers().get(1).getId());
        } else if (numberOfPlayers == 3) {
            contentValues.put(DBOpenHelper.GAME_PLAYER_ONE, game.getAllPlayers().get(0).getId());
            contentValues.put(DBOpenHelper.GAME_PLAYER_TWO, game.getAllPlayers().get(1).getId());
            contentValues.put(DBOpenHelper.GAME_PLAYER_THREE, game.getAllPlayers().get(2).getId());
        } else if (numberOfPlayers == 4) {
            contentValues.put(DBOpenHelper.GAME_PLAYER_ONE, game.getAllPlayers().get(0).getId());
            contentValues.put(DBOpenHelper.GAME_PLAYER_TWO, game.getAllPlayers().get(1).getId());
            contentValues.put(DBOpenHelper.GAME_PLAYER_THREE, game.getAllPlayers().get(2).getId());
            contentValues.put(DBOpenHelper.GAME_PLAYER_FOUR, game.getAllPlayers().get(3).getId());
        }
        return contentValues;
    }
}
