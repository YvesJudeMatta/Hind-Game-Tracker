package com.yvesmatta.hindgametracker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance of the FragmentManager and FragmentTransaction to add/remove fragments
        fragmentManager = getFragmentManager();

        // Add fragment to frgContainer and commit the transaction
        GameListFragment gameListFragment = new GameListFragment();
        fragmentManager.beginTransaction().add(R.id.frgContainer, gameListFragment).commit();
    }

    // Load the GameSetupFragment
    public void loadGameSetupFragment(View view) {
        // Replace fragment in frgContainer with new fragment and commit the transaction
        GameSetupFragment gameSetupFragment = new GameSetupFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frgContainer, gameSetupFragment)
                .addToBackStack(null)
                .commit();
    }

    // Load the GameSetupFragment
    public void loadGamePlayFragment(View view) {
        // Replace fragment in frgContainer with new fragment and commit the transaction
        GamePlayFragment gamePlayFragment = new GamePlayFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frgContainer, gamePlayFragment)
                .addToBackStack(null)
                .commit();
    }
}
