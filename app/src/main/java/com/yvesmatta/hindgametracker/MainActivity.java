package com.yvesmatta.hindgametracker;

import android.app.FragmentManager;
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
        HindListFragment hindListFragment = new HindListFragment();
        fragmentManager.beginTransaction().add(R.id.frgContainer, hindListFragment).commit();
    }

    // Load the HindSetupFragment
    public void loadGameSetupFragment(View view) {
        // Replace fragment in frgContainer with new fragment and commit the transaction
        HindSetupFragment hindSetupFragment = new HindSetupFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frgContainer, hindSetupFragment)
                .addToBackStack(null)
                .commit();
    }

    // Load the HindSetupFragment
    public void loadGamePlayFragment(View view) {
        // Replace fragment in frgContainer with new fragment and commit the transaction
        HindPlayFragment hindPlayFragment = new HindPlayFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frgContainer, hindPlayFragment)
                .addToBackStack(null)
                .commit();
    }
}
