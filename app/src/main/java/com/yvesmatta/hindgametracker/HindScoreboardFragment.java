package com.yvesmatta.hindgametracker;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HindScoreboardFragment extends Fragment {

    private static final String TAG = HindScoreboardFragment.class.getSimpleName();
    private View view;
    private TableLayout tlScoreBoard;
    private TableLayout tlTotalScores;
    private TableRow.LayoutParams trLPWWHalfWeight;
    private TableRow.LayoutParams trLPWWOneWeight;
    private TableRow.LayoutParams trLPMW;

    private Game game;
    private int round;

    public HindScoreboardFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        trLPWWHalfWeight = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
        trLPWWOneWeight = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        trLPMW = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        // Retrieve the game from the main activity
        game = MainActivity.game;

        // Define round
        round = 1;

        // Create the score layout
        createScoreLayout();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_scoreboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_update:
                updateScores();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateScores() {
        // Reset player total scores
        resetPlayerTotalScores();

        // foreach round, check the value and recalculate the total score
        for (int r = 1; r < round; r++) {
            if (validateRoundRow(r)) {
                updateTotalScoreRow(r);
            }
        }

        // If the game is complete, show the winner
        if (game.isCompleted()){
            // Update winner
            ArrayList<Player> winningPlayers = showWinningPlayersDialog();
            if (winningPlayers.size() == 1) {
                game.setWinner(winningPlayers.get(0));
            }
        }
    }

    private void resetPlayerTotalScores() {
        for (int p = 0; p < game.getNumberOfPlayers(); p++) {
            game.getAllPlayers().get(p).setTotalScore(0);
        }
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
        tvBlank.setLayoutParams(trLPWWHalfWeight);
        tvBlank.setText(" ");

        // Add blank view to row
        trPlayerNamesRow.addView(tvBlank);

        // Add all the player name views to the row
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            TextView tvPlayerName = new TextView(getActivity());
            tvPlayerName.setLayoutParams(trLPWWOneWeight);
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
        tvRound.setLayoutParams(trLPWWHalfWeight);
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
            etRoundPlayer.setLayoutParams(trLPWWOneWeight);
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
        tvTotal.setLayoutParams(trLPWWHalfWeight);
        tvTotal.setText(" ");

        // Add the round label to the row
        trPlayerTotalScoresRow.addView(tvTotal);

        // Add all the player score views to the row
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            TextView tvPlayerScore = new TextView(getActivity());
            tvPlayerScore.setLayoutParams(trLPWWOneWeight);
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
        if (validateRoundRow(round)) {

            // Update total score row
            updateTotalScoreRow(round);

            // Check which if its the last round
            if (round == Game.MAX_ROUNDS) {
                // Grey out the current round
                greyOutRoundRow(round);

                // Make the button invisible
                view.setVisibility(View.INVISIBLE);
                
                // Update winner
                ArrayList<Player> winningPlayers = showWinningPlayersDialog();
                if (winningPlayers.size() == 1) {
                    game.setWinner(winningPlayers.get(0));
                }

                // Update game to completed
                game.setCompleted(true);
            } else {
                // Check if its the second last round
                if(round == Game.MAX_ROUNDS -1) {
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
        }
    }

    private ArrayList<Player> showWinningPlayersDialog() {
        // Find the winning players
        ArrayList<Player> winningPlayers = findWinners();

        // If there are winners
        if (winningPlayers.size() >= 0) {
            // Create a DialogInterface OnClickListener
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int button) {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
                    }
                }
            };

            String msg = buildWinningPlayersMessage(winningPlayers);

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(msg)
                    .setPositiveButton(R.string.okay, dialogClickListener)
                    .show();
        }

        return winningPlayers;
    }

    private String buildWinningPlayersMessage(ArrayList<Player> winningPlayers) {
        // Create the string builder instance
        StringBuilder stringBuilder = new StringBuilder();

        // Loop through the winning players and build the winning string
        for (int i = 0; i < winningPlayers.size(); i++) {
            if (i == 0) {
                stringBuilder.append(winningPlayers.get(i).getName());
            } else {
                String grammer = (i == winningPlayers.size()-1) ? " and " : ", ";
                stringBuilder.append(grammer);
                stringBuilder.append(winningPlayers.get(i).getName());
            }
        }

        // Check the size of the winning players to see if the players tied
        if (winningPlayers.size() > 1){
            stringBuilder.append(" tied the game!");
        } else {
            stringBuilder.append(" won the game!");
        }

        // Return the string
        return stringBuilder.toString();
    }

    private ArrayList<Player> findWinners() {
        // Create an array list to hold all the winning players
        ArrayList<Player> winningPlayers = new ArrayList<>();
        int winningScore = 0;

        // Loop through all the players and find the best total score
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            Player player = game.getAllPlayers().get(i-1);
            if (player.getTotalScore() > winningScore) {
                winningScore = player.getTotalScore();
            }
        }

        // Loop through all the players and
        for (int i = 0; i < game.getNumberOfPlayers(); i++){
            Player player = game.getAllPlayers().get(i);
            if (player.getTotalScore() == winningScore) {
                winningPlayers.add(player);
            }
        }

        // Return the winning players
        return  winningPlayers;
    }

    private void greyOutRoundRow(int round) {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + i);
            etPlayer.setBackgroundColor(Color.TRANSPARENT);
            etPlayer.setTextColor(Color.GRAY);
        }
    }

    private boolean validateRoundRow(int round) {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + i);
            String text = etPlayer.getText().toString();
            if (text.equalsIgnoreCase("") || text.contains(" ")) {
                Toast.makeText(getActivity(), R.string.please_fill_in_all_fields, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void updateTotalScoreRow(int round) {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            // Update the total score in the Player object
            updateTotalScore(i, round);

            // Update the total score in the view
            TextView tvPlayerScore = (TextView) view.findViewWithTag("Player" + i + "Score");
            Log.d(TAG, game.getAllPlayers().get(i-1).getTotalScore() + "");
            tvPlayerScore.setText(game.getAllPlayers().get(i-1).getTotalScore() + "");
        }
    }

    private void updateTotalScore(int playerIndex, int round) {
        EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + playerIndex);
        int roundScore = Integer.parseInt(etPlayer.getText().toString());
        game.getAllPlayers().get(playerIndex-1).calculateTotalScore(roundScore);
    }

    public boolean onBackPressed() {
        if (game.isCompleted()) {
            loadDatabase();
        }
        return true;
    }

    private void loadDatabase() {
        // Insert players
        insertPlayers();

        // Insert game
        insertGame();
    }

    private void insertPlayers() {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            // Load the content values with the player data
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.PLAYER_NAME, game.getAllPlayers().get(i).getName());
            contentValues.put(DBOpenHelper.PLAYER_TOTAL_SCORE, game.getAllPlayers().get(i).getTotalScore());
            contentValues.put(DBOpenHelper.PLAYER_CREATED, System.currentTimeMillis());

            // Insert the player into the database and set the id for the player
            Uri playerUri = getActivity().getContentResolver().insert(PlayerProvider.CONTENT_URI, contentValues);
            if (playerUri != null)
                game.getAllPlayers().get(i).setId(Integer.parseInt(playerUri.getLastPathSegment()));
        }
    }

    private void insertGame() {
        // Load the content values with the game data
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.GAME_PLAYER_COUNT, game.getNumberOfPlayers());
        if (game.getWinner() != null) {
            contentValues.put(DBOpenHelper.GAME_PLAYER_WINNER, game.getWinner().getName());
        }
        contentValues.put(DBOpenHelper.GAME_CREATED, System.currentTimeMillis());
        contentValues.put(DBOpenHelper.GAME_PLAYER_COMPLETED, game.isCompleted());

        // Load the players into content values
        contentValues = loadPlayersInContentValues(contentValues);

        // Insert the game into the database and set the id for the game
        Uri uriGame = getActivity().getContentResolver().insert(GameProvider.CONTENT_URI, contentValues);
        if (uriGame != null)
            game.setId(Integer.parseInt(uriGame.getLastPathSegment()));
    }

    private ContentValues loadPlayersInContentValues(ContentValues contentValues) {
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            contentValues.put("player_" + i, game.getAllPlayers().get(i-1).getId());
        }
        return contentValues;
    }
}
