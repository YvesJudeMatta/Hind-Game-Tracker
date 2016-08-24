package com.yvesmatta.hindgametracker.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.yvesmatta.hindgametracker.Models.Game;
import com.yvesmatta.hindgametracker.Models.Player;
import com.yvesmatta.hindgametracker.R;

import java.util.ArrayList;
import java.util.List;

public class HindSetupFragment extends Fragment {

    private Spinner spinNumberOfPlayers;

    private int numberOfPlayers;
    private List<Player> allPlayers;

    private LinearLayout llPlayers;
    private ViewGroup.LayoutParams lpMW;
    
    private Game game = null;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.hind_setup_fragment, container, false);

        // Show the back butting in the menu bar
        showBackButton();

        // Retrieve the views for the table layout
        llPlayers = (LinearLayout) view.findViewById(R.id.llPlayers);

        // Define layout params
        lpMW = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        // Retrieve views from layout
        spinNumberOfPlayers = (Spinner) view.findViewById(R.id.spinNumberOfPlayers);

        // Options for the spinner
        ArrayList<String> numberOfPlayers = new ArrayList<>();

        // Add the options for the spinner
        for (int i = Game.MIN_NUMBER_OF_PLAYERS; i <= Game.MAX_NUMBER_OF_PLAYERS; i++) {
            numberOfPlayers.add(i+"");
        }

        // Initialize the array adapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, numberOfPlayers);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // Assign the array adapter to the spinner
        spinNumberOfPlayers.setAdapter(spinnerArrayAdapter);
        spinNumberOfPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                String selectedNumberOfPlayers = parent.getItemAtPosition(pos).toString();

                // Remove the current player views
                removeGeneratePlayerViews();

                // generate the number of selected views
                generatePlayerViews(selectedNumberOfPlayers);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Initialize all players
        allPlayers = new ArrayList<>();

        // Return the view
        return view;
    }

    private void removeGeneratePlayerViews() {
        llPlayers.removeAllViews();
    }

    private void generatePlayerViews(String selectedNumberOfPlayers) {
        int numberOfPlayersSelected = Integer.parseInt(selectedNumberOfPlayers);
        for (int i = 1; i <= numberOfPlayersSelected; i++) {
            EditText etPlayer = new EditText(getActivity());
            etPlayer.setLayoutParams(lpMW);
            etPlayer.setTag("Player" + i + "Name");
            etPlayer.setHint("Player " + i);
            etPlayer.setTextSize(16);
            etPlayer.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorText));
            etPlayer.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10)});
            etPlayer.setInputType(InputType.TYPE_CLASS_TEXT);
            etPlayer.setGravity(Gravity.CENTER);
            etPlayer.setPadding(
                    0, (int) getResources().getDimension(R.dimen.view_vertical_margin),
                    0, (int) getResources().getDimension(R.dimen.view_vertical_margin));

            // Add player view to the layout
            llPlayers.addView(etPlayer);
        }
    }

    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public Game setupGame() {
        if (validatePlayers()) {
            game = new Game(numberOfPlayers, allPlayers);
        }
        return game;
    }

    private boolean validatePlayers() {
        // Initialize with the minimum number of players
        numberOfPlayers = 0;

        int numberOfPlayersSelected = Integer.parseInt(spinNumberOfPlayers.getSelectedItem().toString());
        for (int i = 1; i <= numberOfPlayersSelected; i++) {
            EditText etPlayer  = (EditText) view.findViewWithTag("Player" + i + "Name");
            if (etPlayer != null) {
                // Grab the string from the view
                String playerName = etPlayer.getText().toString();

                // Initialize the players if validated
                if (validatePlayerName(playerName)) {
                    Player player = new Player(playerName);
                    allPlayers.add(player);
                    numberOfPlayers++;
                }
            }
        }

        if (numberOfPlayers < Game.MIN_NUMBER_OF_PLAYERS) {
            String errorMsg = "You need at least " + Game.MIN_NUMBER_OF_PLAYERS + " players to play";
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validatePlayerName(String playerName) {
        return !playerName.isEmpty();
    }
}
