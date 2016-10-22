package com.yvesmatta.hindscoreboard.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
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

import com.yvesmatta.hindscoreboard.models.Game;
import com.yvesmatta.hindscoreboard.models.Player;
import com.yvesmatta.hindscoreboard.R;

import java.util.ArrayList;

public class HindSetupFragment extends Fragment {

    // Views
    private View view;
    private LinearLayout llPlayers;
    private ViewGroup.LayoutParams lpMW;
    private Spinner spinNumberOfPlayers;

    // Game
    private Game game = null;
    private int maxRounds;
    private ArrayList<Player> allPlayers;

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
        Spinner spinNumberOfRounds = (Spinner) view.findViewById(R.id.spinNumberOfRounds);

        // Options for the spinner
        ArrayList<String> numberOfPlayers = new ArrayList<>();

        // Add the options for the spinner
        for (int i = Game.MIN_NUMBER_OF_PLAYERS; i <= Game.MAX_NUMBER_OF_PLAYERS; i++) {
            numberOfPlayers.add(i + "");
        }

        // Initialize the array adapter
        ArrayAdapter<String> spinnerPlayersArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, numberOfPlayers);
        spinnerPlayersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // Assign the array adapter to the spinner
        spinNumberOfPlayers.setAdapter(spinnerPlayersArrayAdapter);
        spinNumberOfPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                // Get the selected option from the spinner
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

        // Options for the spinner
        ArrayList<String> numberOfRounds = new ArrayList<>();
        int numberOfRoundsToPick = 10;

        // Add the options for the spinner
        for (int i = 1; i <= numberOfRoundsToPick; i++) {
            numberOfRounds.add(i + "");
        }

        // Initialize the array adapter
        ArrayAdapter<String> spinnerRoundsArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, numberOfRounds);
        spinnerRoundsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // Assign the array adapter to the spinner
        spinNumberOfRounds.setAdapter(spinnerRoundsArrayAdapter);
        spinNumberOfRounds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                // Get the selected option from the spinner and set the max round
                maxRounds = Integer.parseInt(parent.getItemAtPosition(pos).toString());
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
            // Create the player name view
            EditText etPlayer = createEditView();
            etPlayer.setTag("Player" + i + "Name");
            etPlayer.setHint("Player " + i);

            // Add player name view to the layout
            llPlayers.addView(etPlayer);
        }
    }

    private EditText createEditView() {
        // Create a EditView and set its attributes
        EditText et = new EditText(getActivity());
        et.setLayoutParams(lpMW);
        et.setTextSize(16);
        et.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorText));
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER);
        et.setPadding(
                0, (int) getResources().getDimension(R.dimen.view_vertical_margin),
                0, (int) getResources().getDimension(R.dimen.view_vertical_margin));

        // return the EditView
        return et;
    }

    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public Game setupGame() {
        if (validatePlayers()) {
            game = new Game(allPlayers);
            game.setMaxRounds(maxRounds);
        }
        return game;
    }

    private boolean validatePlayers() {
        // Initialize with no players
        int numberOfPlayers = 0;

        int numberOfPlayersSelected = Integer.parseInt(spinNumberOfPlayers.getSelectedItem().toString());
        for (int i = 1; i <= numberOfPlayersSelected; i++) {
            EditText etPlayer = (EditText) view.findViewWithTag("Player" + i + "Name");
            if (etPlayer != null) {
                // Grab the string from the view
                String playerName = etPlayer.getText().toString();


                // Initialize the players name is not empty
                if (!playerName.isEmpty()) {
                    // Create a player and add it to the list of players
                    Player player = new Player(playerName);
                    allPlayers.add(player);
                    numberOfPlayers++;
                }
            }
        }

        // Validations for min number of players and unique names
        if (numberOfPlayers < Game.MIN_NUMBER_OF_PLAYERS) {
            allPlayers.clear();
            String errorMsg = "You need at least " + Game.MIN_NUMBER_OF_PLAYERS + " players to play";
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validateUniqueNames()) {
            allPlayers.clear();
            String errorMsg = "All player names must be unique";
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Return true if everything is okay
        return true;
    }

    private boolean validateUniqueNames() {
        // Create an empty array to store the names and a temp size variable
        ArrayList<String> names = new ArrayList<>();
        int tempSize = 0;

        // Loop through all the players
        for (Player player : allPlayers) {
            // If its the first player
            if (player == allPlayers.get(0)) {
                // Add it to the list of names and increase the tempSize
                names.add(player.getName());
                tempSize++;
            } else {
                // Loop until it has reached tempSize
                for (int i = 0; i < tempSize; i++) {
                    // Check the list of names to see if the player name is identical
                    // otherwise, add it ot the list of names
                    if (player.getName().equals(names.get(i))) {
                        return false;
                    } else {
                        names.add(player.getName());
                    }
                }
            }

        }

        // Return true everything is okay
        return true;
    }
}
