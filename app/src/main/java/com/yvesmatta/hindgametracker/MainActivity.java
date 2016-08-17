package com.yvesmatta.hindgametracker;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Fragments
    private FragmentManager fragmentManager;
    private HindListFragment hindListFragment;
    private HindSetupFragment hindSetupFragment;
    private HindScoreboardFragment hindScoreboardFragment;
    // Game
    public static Game game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance of the FragmentManager and FragmentTransaction to add/remove fragments
        fragmentManager = getFragmentManager();

        // Add fragment to frgContainer and commit the transaction
        hindListFragment = new HindListFragment();
        fragmentManager.beginTransaction()
                .add(R.id.frgContainer, hindListFragment)
                .commit();
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

    @Override
    public void onBackPressed() {
        // Skip the hindSetupFragment when the hindScoreboardFragment is visible on back pressed
        if (hindScoreboardFragment != null && hindScoreboardFragment.isVisible()) {
            if (hindScoreboardFragment.onBackPressed()) {
                fragmentManager.beginTransaction()
                        .replace(R.id.frgContainer, hindListFragment)
                        .commit();
            }
        } else {
            super.onBackPressed();
        }
    }

    // Load the GameSetupFragment
    public void loadGameSetupFragment(View view) {
        // Replace fragment in frgContainer with new fragment and commit the transaction
        hindSetupFragment = new HindSetupFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frgContainer, hindSetupFragment)
                .addToBackStack(hindListFragment.getClass().getName())
                .commit();
    }

    // Load the GamePlayFragment
    public void loadGamePlayFragment(View view) {
        game = hindSetupFragment.setupGame();
        if (game != null) {
            // Replace fragment in frgContainer with new fragment and commit the transaction
            hindScoreboardFragment = new HindScoreboardFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.frgContainer, hindScoreboardFragment)
                    .commit();
        }
    }

    public void fragmentAddRound(View view) {
        hindScoreboardFragment.addRound(view);
    }

}
