package com.yvesmatta.hindgametracker;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private FragmentManager fragmentManager;
    private HindListFragment hindListFragment;
    private HindSetupFragment hindSetupFragment;
    private HindPlayFragment hindPlayFragment;

    public Game game = null;
    private List<Player> allPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance of the FragmentManager and FragmentTransaction to add/remove fragments
        fragmentManager = getFragmentManager();

        // Add fragment to frgContainer and commit the transaction
        hindListFragment = new HindListFragment();
        fragmentManager.beginTransaction().add(R.id.frgContainer, hindListFragment).commit();

        // Initialize allPlayers
        allPlayers = new ArrayList<>();
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
        allPlayers = game.getAllPlayers();

        // insert players
        insertPlayers();

        // insert game
        insertGame();
    }

    private void insertPlayers() {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.PLAYER_NAME, allPlayers.get(i).getName());
            contentValues.put(DBOpenHelper.PLAYER_TOTAL_SCORE, allPlayers.get(i).getTotalScore());
            contentValues.put(DBOpenHelper.PLAYER_CREATED, true);
            getContentResolver().insert(PlayerProvider.CONTENT_URI, contentValues);
        }
    }

    private void insertGame() {
    }
}
