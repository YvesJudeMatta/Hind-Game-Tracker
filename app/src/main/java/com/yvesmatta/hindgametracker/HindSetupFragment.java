package com.yvesmatta.hindgametracker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HindSetupFragment extends Fragment {

    private static final String TAG = HindSetupFragment.class.getSimpleName();
    private static final int MIN_NUMBER_OF_PLAYERS = 2;
    private EditText etPlayerOne;
    private EditText etPlayerTwo;
    private EditText etPlayerThree;
    private EditText etPlayerFour;

    private int numberOfPlayers;
    private List<Player> allPlayers;

    private Game game = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.hind_setup_fragment, container, false);

        // Show the back butting in the menu bar
        showBackButton();

        // Retrieve views from layout
        etPlayerOne = (EditText) view.findViewById(R.id.etPlayerOne);
        etPlayerTwo = (EditText) view.findViewById(R.id.etPlayerTwo);
        etPlayerThree = (EditText) view.findViewById(R.id.etPlayerThree);
        etPlayerFour = (EditText) view.findViewById(R.id.etPlayerFour);

        // Initialize all players
        allPlayers = new ArrayList<>();

        // Return the view
        return view;
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

        // Retrieve the values from the views
        String playerOneName = etPlayerOne.getText().toString();
        String playerTwoName = etPlayerTwo.getText().toString();
        String playerThreeName = etPlayerThree.getText().toString();
        String playerFourName = etPlayerFour.getText().toString();

        // Initialize the players if validated
        if (validatePlayerName(playerOneName)) {
            Player playerOne = new Player(playerOneName);
            allPlayers.add(playerOne);
            numberOfPlayers++;
        }
        if (validatePlayerName(playerTwoName)) {
            Player playerTwo = new Player(playerTwoName);
            allPlayers.add(playerTwo);
            numberOfPlayers++;
        }
        if (validatePlayerName(playerThreeName)) {
            Player playerThree = new Player(playerThreeName);
            allPlayers.add(playerThree);
            numberOfPlayers++;
        }
        if (validatePlayerName(playerFourName)) {
            Player playerFour = new Player(playerFourName);
            allPlayers.add(playerFour);
            numberOfPlayers++;
        }

        if (numberOfPlayers < MIN_NUMBER_OF_PLAYERS) {
            String errorMsg = "You need at least " + MIN_NUMBER_OF_PLAYERS + " players to play";
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validatePlayerName(String playerName) {
        if (!playerName.isEmpty()) return true;
        else return false;
    }
}
