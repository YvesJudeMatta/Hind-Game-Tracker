package com.yvesmatta.hindgametracker;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class HindScoreboardFragment extends Fragment {

    private static final String TAG = HindScoreboardFragment.class.getSimpleName();
    private View view;
    private TableLayout tlScoreBoard;
    private TableLayout tlTotalScores;
    private TableRow.LayoutParams trLPMM;
    private TableRow.LayoutParams trLPWW;
    private TableRow.LayoutParams trLPMW;

    private Game game;
    private int round;

    private static final int MAX_ROUNDS = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.hind_scoreboard_fragment, container, false);

        // Retrieve the views for the table layout
        tlScoreBoard = (TableLayout) view.findViewById(R.id.tlScoreBoard);
        tlTotalScores = (TableLayout) view.findViewById(R.id.tlTotalScores);

        // Define layout params
        trLPMM = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        trLPWW = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        trLPMW = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        // Retrieve the game from the main activity
        game = MainActivity.game;

        // Define round
        round = 1;

        // Create the score layout
        createScoreLayout();

        return view;
    }

    private void createScoreLayout() {
        // Create the player names row
        createPlayerNamesRow();

        // Create round score row
        createRoundRow();

        // Create total score row
        createTotalScoreRow();
    }

    private void createPlayerNamesRow() {
        // Create the row
        TableRow trPlayerNamesRow = new TableRow(getActivity());
        trPlayerNamesRow.setLayoutParams(trLPMW);
        trPlayerNamesRow.setTag(getString(R.string.scoreboard_player_names_row));
        trPlayerNamesRow.setPadding(
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin),
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin));

        // Create the views within the row
        TextView tvBlank = new TextView(getActivity());
        tvBlank.setLayoutParams(trLPWW);

        // Add blank view to row
        trPlayerNamesRow.addView(tvBlank);

        // Add all the player name views to the row
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            TextView tvPlayerName = new TextView(getActivity());
            tvPlayerName.setLayoutParams(trLPWW);
            tvPlayerName.setTag("Player" + i + "Name");
            tvPlayerName.setText(game.getAllPlayers().get(i-1).getName());
            tvPlayerName.setTypeface(null, Typeface.BOLD);
            tvPlayerName.setGravity(Gravity.CENTER);

            // Add player view to row
            trPlayerNamesRow.addView(tvPlayerName);
        }

        // Add the row to the table layout
        tlScoreBoard.addView(trPlayerNamesRow);
    }

    private void createRoundRow() {
        // Create the row
        TableRow trPlayerScoresRow = new TableRow(getActivity());
        trPlayerScoresRow.setLayoutParams(trLPMW);
        trPlayerScoresRow.setTag("Round" + round);
        trPlayerScoresRow.setPadding(
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin),
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin));

        // Create the views within the row
        TextView tvRound = new TextView(getActivity());
        tvRound.setLayoutParams(trLPWW);
        tvRound.setTag("Round" + round + "Label");
        tvRound.setText(round + "");
        tvRound.setTypeface(null, Typeface.BOLD);
        tvRound.setGravity(Gravity.CENTER);
        tvRound.setPadding(
                0, (int) getResources().getDimension(R.dimen.table_row_horizontal_margin),
                0, (int) getResources().getDimension(R.dimen.table_row_horizontal_margin));

        // Add the round label to the row
        trPlayerScoresRow.addView(tvRound);

        // Add all the players score views to the row
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            EditText etRoundPlayer = new EditText(getActivity());
            etRoundPlayer.setLayoutParams(trLPWW);
            etRoundPlayer.setTag("Round" + round + "Player" + i);
            etRoundPlayer.setGravity(Gravity.CENTER);
            etRoundPlayer.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(4)});
            etRoundPlayer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

            // Add player score view to row
            trPlayerScoresRow.addView(etRoundPlayer);
        }

        // Add the row to the table layout
        tlScoreBoard.addView(trPlayerScoresRow);
    }


    private void createTotalScoreRow() {
        // Create the row
        TableRow trPlayerTotalScoresRow = new TableRow(getActivity());
        trPlayerTotalScoresRow.setLayoutParams(trLPMW);
        trPlayerTotalScoresRow.setTag("TotalScoresRow");
        trPlayerTotalScoresRow.setPadding(
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin),
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin));

        // Create the views within the row
        TextView tvTotal = new TextView(getActivity());
        tvTotal.setLayoutParams(trLPWW);

        // Add the round label to the row
        trPlayerTotalScoresRow.addView(tvTotal);

        // Add all the player score views to the row
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            TextView tvPlayerScore = new TextView(getActivity());
            tvPlayerScore.setLayoutParams(trLPWW);
            tvPlayerScore.setTag("Player" + i + "Score");
            tvPlayerScore.setText("0");
            tvPlayerScore.setTypeface(null, Typeface.BOLD);
            tvPlayerScore.setGravity(Gravity.CENTER);

            // Add player view to row
            trPlayerTotalScoresRow.addView(tvPlayerScore);
        }

        // Add the row to the table layout
        tlTotalScores.addView(trPlayerTotalScoresRow);
    }

    public void addRound(View view) {
        // Check if the row is validated
        if (validateRow()) {

            // Update total score row
            updateTotalScoreRow();

            // Check which if its the last round
            if (round == MAX_ROUNDS) {
                // Grey out the current round
                greyOutRoundRow(round);

                // Make the button invisible
                view.setVisibility(View.INVISIBLE);

                // Create a dialog to show the winner
            } else {
                // Check if its the second last round
                if(round == MAX_ROUNDS -1) {
                    // Change the text of the button
                    Button btnFinish = (Button) view.findViewById(R.id.btnFinish);
                    btnFinish.setText(R.string.finish);
                }

                // Increase the round
                round++;

                // Create the round row
                createRoundRow();

                // Grey out the previous row
                greyOutRoundRow(round - 1);
            }
        } else {
            Toast.makeText(getActivity(), R.string.please_fill_in_all_fields, Toast.LENGTH_SHORT).show();
        }
    }

    private void greyOutRoundRow(int round) {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + i);
            etPlayer.setBackgroundColor(Color.TRANSPARENT);
            etPlayer.setTextColor(Color.GRAY);
        }
    }

    private boolean validateRow() {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + i);
            String text = etPlayer.getText().toString();
            if (text.equalsIgnoreCase("") || text.contains(" ")) {
                return false;
            }
        }
        return true;
    }

    private void updateTotalScoreRow() {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            // Update the total score in the Player object
            updateTotalScore(i);

            // Update the total score in the view
            TextView tvPlayerScore = (TextView) view.findViewWithTag("Player" + i + "Score");
            Log.d(TAG, game.getAllPlayers().get(i-1).getTotalScore() + "");
            tvPlayerScore.setText(game.getAllPlayers().get(i-1).getTotalScore() + "");
        }
    }

    private void updateTotalScore(int playerIndex) {
        EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + playerIndex);
        int roundScore = Integer.parseInt(etPlayer.getText().toString());
        game.getAllPlayers().get(playerIndex-1).calculateTotalScore(roundScore);
    }
}
