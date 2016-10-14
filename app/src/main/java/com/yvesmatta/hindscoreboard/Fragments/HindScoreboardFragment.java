package com.yvesmatta.hindscoreboard.Fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.yvesmatta.hindscoreboard.DBOpenHelper;
import com.yvesmatta.hindscoreboard.MainActivity;
import com.yvesmatta.hindscoreboard.Models.Game;
import com.yvesmatta.hindscoreboard.Models.Player;
import com.yvesmatta.hindscoreboard.Providers.GameProvider;
import com.yvesmatta.hindscoreboard.Providers.GameWinnerProvider;
import com.yvesmatta.hindscoreboard.Providers.PlayerProvider;
import com.yvesmatta.hindscoreboard.Providers.RoundScoreProvider;
import com.yvesmatta.hindscoreboard.R;
import com.yvesmatta.hindscoreboard.Utils.MyUtilities;

import java.util.ArrayList;

public class HindScoreboardFragment extends Fragment {
    // Constants
    private static final int SHOW = 1;
    private static final int NEW = 2;
    private static final int UPDATE = 3;

    // Views
    private View view;
    private TableLayout tlScoreBoard;
    private TableLayout tlTotalScores;
    private TableRow.LayoutParams trLPWWHalfWeight;
    private TableRow.LayoutParams trLPWWOneWeight;
    private TableRow.LayoutParams trLPMW;

    // Game
    private Game game;
    private Uri uri;
    private int round;
    private int lastRound;
    private int action;

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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.hind_scoreboard_fragment, container, false);

        // Show the back butting in the menu bar
        showBackButton();

        // Retrieve the views for the table layout
        tlScoreBoard = (TableLayout) view.findViewById(R.id.tlScoreBoard);
        tlTotalScores = (TableLayout) view.findViewById(R.id.tlTotalScores);

        // Define layout params
        trLPWWHalfWeight = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
        trLPWWOneWeight = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        trLPMW = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        // Retrieve the game and uri from the main activity
        game = MainActivity.game;
        uri = MainActivity.uri;

        // Set the action depending on conditions
        if (uri != null) {
            // Load data from database
            loadFromDatabase();
            round = game.getRoundsPlayed();
            lastRound = round;

            if (game.isCompleted()) {
                action = SHOW;
            } else {
                action = UPDATE;
            }
        } else {
            action = NEW;
            round = 1;
        }

        // Create the score layout
        createScoreLayout();

        // Recall creating the menu
        getActivity().invalidateOptionsMenu();

        // Return the view
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_scoreboard, menu);

        // Set the visibility depending on action
        if (action == SHOW) {
            menu.getItem(0).setVisible(false);
        } else {
            menu.getItem(0).setVisible(true);
        }

        // Call super method
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Store the id that was clicked
        int id = item.getItemId();

        // Switch to find which item was clicked
        switch (id) {
            case R.id.action_update:
                // Call the method to update the total score
                updateScoresFromMenu();
                break;
        }

        // Return the supers class method call
        return super.onOptionsItemSelected(item);
    }

    private void updateScoresFromMenu() {
        // Assign the last round
        lastRound = round - 1;

        // If the game is complete
        if (game.isCompleted()) {
            // Assign the last round to the current round
            lastRound = round;
        }

        // If the rounds we want to calculate up to is validated
        if (validateRowsUpToRound(lastRound)) {
            // Reset player total scores
            resetPlayerScores();

            // Update the total scores
            updateTotalScoreRow(lastRound);
        }

        // Set winners
        setWinners();

        // If the game is complete
        if (game.isCompleted()){
            // Show a dialog of who won
            showWinningPlayersDialog(game.getWinningPlayers());
        }
    }

    private void createScoreLayout() {
        if (action == SHOW) {
            // Hide button
            Button btnFinish = (Button) view.findViewById(R.id.btnFinish);
            btnFinish.setVisibility(View.INVISIBLE);
        }

        // Create the player names row
        createPlayerNamesRow();

        // Create round score row
        if (action != NEW) {
            for (int r = 1; r <= round; r++) {
                createRoundRow(r);
            }
        } else {
            createRoundRow(round);
        }

        // Create total score row
        createTotalScoreRow();
    }

    private void createPlayerNamesRow() {
        // Create the row
        TableRow trPlayerNamesRow = createTableRow();
        trPlayerNamesRow.setTag(getString(R.string.scoreboard_player_names_row));

        // Create the blank view
        TextView tvBlank = createTextView(" ", false);

        // Add blank view to row
        trPlayerNamesRow.addView(tvBlank);

        // Add all the player name views to the row
        for (Player player : game.getAllPlayers()) {
            // Create the player name view
            TextView tvPlayerName = createTextView(player.getName(), true);
            tvPlayerName.setTag("Player" + player.getName());

            // Add player name view to row
            trPlayerNamesRow.addView(tvPlayerName);
        }

        // Add the row to the table layout
        tlScoreBoard.addView(trPlayerNamesRow);
    }

    private void createRoundRow(int round) {
        // Create the row
        TableRow trPlayerScoresRow = createTableRow();
        trPlayerScoresRow.setTag("Round" + round);

        // Create the round label view
        TextView tvRound = createTextView(String.valueOf(round), false);

        // Add the round label view to the row
        trPlayerScoresRow.addView(tvRound);

        // Add all the player score views to the row
        for (Player player : game.getAllPlayers()) {
            // Create the player score view
            EditText etRoundPlayer = createEditView();
            etRoundPlayer.setTag("Round" + round + "Player" + player.getName());

            // if action is set to SHOW disable the view, and load the values
            // Otherwise, request the first players view to focus
            if (action == SHOW) {
                etRoundPlayer.setText(player.getScores().get(round-1).toString());
                etRoundPlayer.setFocusable(false);
            } else if (action == UPDATE && round <= game.getRoundsPlayed()) {
                Log.d("Hi", player.getName() + ": " + player.getScores().get(0).toString());
                etRoundPlayer.setText(player.getScores().get(round - 1).toString());
            } else if (player == game.getAllPlayers().get(0)) {
                etRoundPlayer.requestFocus();
            }

            // Add player score view to row
            trPlayerScoresRow.addView(etRoundPlayer);
        }

        // Add the row to the table layout
        tlScoreBoard.addView(trPlayerScoresRow);

        if (action == UPDATE && round <= game.getRoundsPlayed()) {
            greyOutRoundRow(round);
        }
    }

    private void createTotalScoreRow() {
        // Create the row
        TableRow trPlayerTotalScoresRow = createTableRow();
        trPlayerTotalScoresRow.setTag("TotalScoresRow");

        // Create the blank view
        TextView tvTotal = createTextView(" ", false);

        // Add the blank view to the row
        trPlayerTotalScoresRow.addView(tvTotal);

        // Add all the player total score views to the row
        for (Player player : game.getAllPlayers()) {
            // Create the player total score view
            TextView tvPlayerScore = createTextView("0", true);
            tvPlayerScore.setTextSize(20);
            tvPlayerScore.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTotalScoreLosing));
            tvPlayerScore.setTag("Player" + player.getName() + "Score");

            // if the action is not NEW disable the view, and load the values
            if (action != NEW) {
                tvPlayerScore.setText(String.valueOf(player.getTotalScore()));
            }

            // Add player total score view to row
            trPlayerTotalScoresRow.addView(tvPlayerScore);
        }

        // Add the row to the table layout
        tlTotalScores.addView(trPlayerTotalScoresRow);

        // If the action is not NEW
        if (action != NEW) {
            setWinners();
        }
    }

    private TableRow createTableRow() {
        // Create a TableRow and set its attributes
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(trLPMW);
        tr.setPadding(
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin),
                0, (int) getResources().getDimension(R.dimen.table_row_vertical_margin));

        // Return the TableRow
        return tr;
    }

    private TextView createTextView(String text, boolean isForPlayerView) {
        // Create a TextView and set its attributes
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);

        // Decides if its for the player view and assign appropriate values
        if (isForPlayerView) {
            tv.setLayoutParams(trLPWWOneWeight);
        } else {
            tv.setLayoutParams(trLPWWHalfWeight);
            tv.setPadding(
                    0, (int) getResources().getDimension(R.dimen.table_row_horizontal_margin),
                    0, (int) getResources().getDimension(R.dimen.table_row_horizontal_margin));
        }

        // return the TextView
        return tv;
    }

    private EditText createEditView() {
        // Create a EditView and set its attributes
        EditText et = new EditText(getActivity());
        et.setLayoutParams(trLPWWOneWeight);
        et.setGravity(Gravity.CENTER);
        et.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(4)});
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

        // return the EditView
        return et;
    }

    public void addRound(View view) {
        // Check if the row is validated
        if (validateRowsUpToRound(round)) {
            // Reset player scores
            resetPlayerScores();

            // Update total score row
            updateTotalScoreRow(round);

            // Set winners
            setWinners();

            // Check which if its the last round
            if (round == Game.MAX_ROUNDS) {
                // Grey out the current round
                greyOutRoundRow(round);

                // Make the button invisible
                view.setVisibility(View.INVISIBLE);

                // Show a dialog of who won
                showWinningPlayersDialog(game.getWinningPlayers());

                // Update game to completed
                game.setCompleted(true);

                // Assign the last round to the current round
                lastRound = round;
            } else {
                // Check if its the second last round
                if(round == Game.MAX_ROUNDS -1) {
                    // Change the text of the button
                    Button btnFinish = (Button) view.findViewById(R.id.btnFinish);
                    btnFinish.setText(R.string.finish);
                }

                // Increase the round
                round++;
                lastRound = round - 1;

                // Create the round row
                createRoundRow(round);

                // Grey out the previous row
                greyOutRoundRow(round - 1);
            }
        }
    }

    private void showWinningPlayersDialog(ArrayList<Player> winningPlayers) {
        // If there are winners
        if (winningPlayers.size() > 0) {
            // Create a DialogInterface OnClickListener
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int button) {
                    // No required action
                }
            };

            // Build the winning players message with a method from the MyUtilities class
            String winningPlayersMessage = MyUtilities.buildWinningPlayersMessage(winningPlayers, round);

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(winningPlayersMessage)
                    .setPositiveButton(R.string.okay, dialogClickListener)
                    .show();
        }
    }

    private void setWinners() {
        // Create an array list to hold all the winning players
        ArrayList<Player> winningPlayers = new ArrayList<>();
        int winningScore = 0;

        // Loop through all the players and find the best total score
        for (Player player : game.getAllPlayers()) {
            // If its the first player, set it to be the first total score
            // Else if player score is higher then the winning score
            if (game.getAllPlayers().get(0) == player) {
                winningScore = player.getTotalScore();
            } else if (player.getTotalScore() < winningScore) {
                winningScore = player.getTotalScore();
            }
        }

        // Loop through all the players and update each player that won
        for (Player player : game.getAllPlayers()) {
            // View for the players total score
            TextView tvTotalScore = (TextView) view.findViewWithTag("Player" + player.getName() + "Score");

            // If the player matches the winningScore
            if (player.getTotalScore() == winningScore) {
                // Add the player to the winning players list
                winningPlayers.add(player);

                // Update the total score colour
                tvTotalScore.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTotalScoreWinning));
            } else {
                // Update the total score colour
                tvTotalScore.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTotalScoreLosing));
            }
        }

        // Add the winning players to the game
        game.setWinningPlayers(winningPlayers);
    }

    private void greyOutRoundRow(int round) {
        // Loop through all the players and grey out the view for the specified round
        for (Player player : game.getAllPlayers()) {
            EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + player.getName());
            etPlayer.setBackgroundColor(Color.TRANSPARENT);
            etPlayer.setTextColor(Color.GRAY);
        }
    }

    private boolean validateRowsUpToRound(int round) {
        // Loop through each round
        for (int r = 1; r <= round; r++) {
            // Loop through all the players
            for (Player player : game.getAllPlayers()) {
                // Find the view with the specified tag
                EditText etPlayer = (EditText) view.findViewWithTag("Round" + r + "Player" + player.getName());
                String text = etPlayer.getText().toString();

                // Validate
                if (text.equalsIgnoreCase("") || text.contains(" ")) {
                    Toast.makeText(getActivity(), R.string.please_fill_in_all_fields, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private void resetPlayerScores() {
        // Loop through all the players and reset their scores and total scores
        for (Player player : game.getAllPlayers()) {
            player.setScores(new ArrayList<Integer>());
            player.setTotalScore(0);
        }
    }

    private void updateTotalScoreRow(int round) {
        for (int r = 1; r <= round; r++) {
            for (Player player : game.getAllPlayers()) {
                // Add the round score in the Player object
                addScore(player, r);
            }
        }

        // Update the total score in the Player object
        for (Player player : game.getAllPlayers()) {
            player.calculateTotalScore();

            // Update the total score in the view
            TextView tvPlayerScore = (TextView) view.findViewWithTag("Player" + player.getName() + "Score");
            tvPlayerScore.setText(String.valueOf(player.getTotalScore()));
        }
    }

    private void addScore(Player player, int round) {
        EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + player.getName());
        int roundScore = Integer.parseInt(etPlayer.getText().toString());
        player.addScore(roundScore);
    }

    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onBackPressed() {
        if (action == NEW && round > 1) {
            loadDatabase();
            Toast.makeText(getActivity(), "Game saved", Toast.LENGTH_SHORT).show();
        } else if (action == UPDATE) {
            updateDatabase();
            Toast.makeText(getActivity(), "Game updated", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void updateDatabase() {
        // Update game
        updateGame();

        // Update players
        updatePlayers();

        // Update winners
        if (game.isCompleted()) {
            insertWinners();
        }

        // Update scores
        updateScores();
    }

    private void updateGame() {
        // Filter
        String gameFilter = DBOpenHelper.GAME_ID + "=" + game.getId();

        // Load the content values with the game data
        ContentValues contentValues = new ContentValues();
        Log.d("hi", lastRound+"");
        contentValues.put(DBOpenHelper.GAME_COMPLETED, game.isCompleted());
        contentValues.put(DBOpenHelper.GAME_ROUNDS_PLAYED, lastRound);
        contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

        // Update the game in the database depending on filter
        getActivity().getContentResolver().update(GameProvider.CONTENT_URI, contentValues, gameFilter, null);
    }

    private void updatePlayers() {
        // Filters
        String gameFilter = DBOpenHelper.GAME_FOREIGN_ID + "=" + game.getId();

        for (Player player : game.getAllPlayers()) {
            // Filters
            String playerFilter = DBOpenHelper.PLAYER_ID + "=" + player.getId();
            String filters = gameFilter + " AND " + playerFilter;

            // Load the content values with the player data
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.PLAYER_TOTAL_SCORE, player.getTotalScore());
            contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

            // Update the players in the database depending on filter
            getActivity().getContentResolver().update(PlayerProvider.CONTENT_URI, contentValues, filters, null);
        }
    }

    private void updateScores() {
        // Filter
        String gameFilter = DBOpenHelper.GAME_FOREIGN_ID + "=" + game.getId();

        for (Player player : game.getAllPlayers()) {
            // Filters
            String playerFilter = DBOpenHelper.PLAYER_ID + "=" + player.getId();
            String filters = gameFilter + " AND " + playerFilter;

            for (int r = 1; r <= lastRound; r++){
                // Load the content values with the player score data
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBOpenHelper.ROUND_SCORE_ROUND, r);
                contentValues.put(DBOpenHelper.ROUND_SCORE_SCORE, player.getScores().get(r-1));
                contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

                // Update the winning players in the database depending on filter
                getActivity().getContentResolver().update(RoundScoreProvider.CONTENT_URI, contentValues, filters, null);
            }
        }

        if (game.getRoundsPlayed() < lastRound) {
            insertScores(game.getRoundsPlayed()+1);
        }
    }

    private void loadDatabase() {
        // Insert game
        insertGame();

        // Insert players
        insertPlayers();

        // Insert winners
        if (game.isCompleted()) {
            insertWinners();
        }

        // Insert scores
        insertScores(1);
    }

    private void insertGame() {
        // Load the content values with the game data
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.GAME_COMPLETED, game.isCompleted());
        contentValues.put(DBOpenHelper.GAME_ROUNDS_PLAYED, lastRound);
        contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
        contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

        // Insert the game into the database and set the id for the game
        Uri uriGame = getActivity().getContentResolver().insert(GameProvider.CONTENT_URI, contentValues);
        if (uriGame != null)
            game.setId(Integer.parseInt(uriGame.getLastPathSegment()));
    }

    private void insertPlayers() {
        for (Player player : game.getAllPlayers()) {
            // Load the content values with the player data
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.GAME_FOREIGN_ID, game.getId());
            contentValues.put(DBOpenHelper.PLAYER_NAME, player.getName());
            contentValues.put(DBOpenHelper.PLAYER_TOTAL_SCORE, player.getTotalScore());
            contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
            contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

            // Insert the player into the database and set the id for the player
            Uri playerUri = getActivity().getContentResolver().insert(PlayerProvider.CONTENT_URI, contentValues);
            if (playerUri != null)
                player.setId(Integer.parseInt(playerUri.getLastPathSegment()));
        }
    }

    private void insertWinners() {
        for (Player player : game.getWinningPlayers()) {
            // Load the content values with the winning player data if the players are winners
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.GAME_FOREIGN_ID, game.getId());
            contentValues.put(DBOpenHelper.PLAYER_FOREIGN_ID, player.getId());
            contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
            contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

            // Insert the winning players into the database
            getActivity().getContentResolver().insert(GameWinnerProvider.CONTENT_URI, contentValues);

        }
    }

    private void insertScores(int startRound) {
        for (Player player : game.getAllPlayers()) {
            for (int r = startRound; r <= lastRound; r++){
                // Load the content values with the player score data
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBOpenHelper.GAME_FOREIGN_ID, game.getId());
                contentValues.put(DBOpenHelper.PLAYER_FOREIGN_ID, player.getId());
                contentValues.put(DBOpenHelper.ROUND_SCORE_ROUND, r);
                contentValues.put(DBOpenHelper.ROUND_SCORE_SCORE, player.getScores().get(r-1));
                contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
                contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

                // Insert the player score into the database
                getActivity().getContentResolver().insert(RoundScoreProvider.CONTENT_URI, contentValues);
            }
        }
    }

    private void loadFromDatabase() {
        // Load players
        ArrayList<Player> players = loadPlayers();

        // If there are players in the list of players
        if (players.size() > 0) {
            // Load the scores for each player
            for (Player player : players) {
                player.setScores(loadScores(player));
            }

            // Load game
            loadGame(players);

            if (game != null) {
                // Load the winners
                game.setWinningPlayers(loadWinners());
            }
        }
    }

    private ArrayList<Player> loadPlayers() {
        // ArrayList for all the players
        ArrayList<Player> players = new ArrayList<>();

        // Grab all the player object data
        String gameFilter = DBOpenHelper.GAME_FOREIGN_ID + "=" + uri.getLastPathSegment();
        Cursor playerCursor = getActivity().getContentResolver().query(PlayerProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, gameFilter, null, null);

        // Grab all the players
        if (playerCursor != null) {
            while (playerCursor.moveToNext()) {
                // Get data from the database
                int id = playerCursor.getInt(playerCursor.getColumnIndex(DBOpenHelper.PLAYER_ID));
                String name = playerCursor.getString(playerCursor.getColumnIndex(DBOpenHelper.PLAYER_NAME));
                int totalScore = playerCursor.getInt(playerCursor.getColumnIndex(DBOpenHelper.PLAYER_TOTAL_SCORE));

                // Create the player
                Player player = new Player(name);
                player.setId(id);
                player.setTotalScore(totalScore);

                // Add the player to the list of players
                players.add(player);
            }

            // Close the cursor
            playerCursor.close();
        }

        // Return the list of players
        return players;
    }

    private ArrayList<Integer> loadScores(Player player) {
        // ArrayList for all the players scores
        ArrayList<Integer> scores = new ArrayList<>();

        // Grab all the round_score object data
        String gameFilter = DBOpenHelper.GAME_FOREIGN_ID + "=" + uri.getLastPathSegment();
        String playerFilter = DBOpenHelper.PLAYER_FOREIGN_ID + "=" + player.getId();
        String roundScoreFilter = gameFilter + " AND " + playerFilter;
        Cursor roundScoreCursor = getActivity().getContentResolver().query(RoundScoreProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, roundScoreFilter, null, null);

        // Grab all the players
        if (roundScoreCursor != null) {
            while (roundScoreCursor.moveToNext()) {
                // Get data from the database
                int score = roundScoreCursor.getInt(roundScoreCursor.getColumnIndex(DBOpenHelper.ROUND_SCORE_SCORE));

                // Add the score to the list of scores
                scores.add(score);
            }

            // Close the cursor
            roundScoreCursor.close();
        }

        // Return the list of players
        return scores;
    }

    private void loadGame(ArrayList<Player> players) {
        // Grab all the game object data
        String gameFilter = DBOpenHelper.GAME_ID + "=" + uri.getLastPathSegment();
        Cursor gameCursor = getActivity().getContentResolver().query(GameProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, gameFilter, null, null);

        // Grab all the players
        if (gameCursor != null) {
            gameCursor.moveToNext();

            // Get data from the database
            int id = gameCursor.getInt(gameCursor.getColumnIndex(DBOpenHelper.GAME_ID));
            int completed = gameCursor.getInt(gameCursor.getColumnIndex(DBOpenHelper.GAME_COMPLETED));
            int roundsPlayed = gameCursor.getInt(gameCursor.getColumnIndex(DBOpenHelper.GAME_ROUNDS_PLAYED));
            boolean isCompleted = false;

            if (completed == 1) {
                isCompleted = true;
            }

            // Create the game
            game = new Game(players.size(), players);
            game.setId(id);
            game.setRoundsPlayed(roundsPlayed);
            game.setCompleted(isCompleted);

            // Close the cursor
            gameCursor.close();
        }
    }

    private ArrayList<Player> loadWinners() {
        // ArrayList to store winning players and another one to store the ids
        ArrayList<Player> winningPlayers = new ArrayList<>();
        ArrayList<Integer> winnerIds = new ArrayList<>();

        String gameWinnerFilter = DBOpenHelper.GAME_FOREIGN_ID + "=" + game.getId();
        Cursor gameWinnerCursor = getActivity().getContentResolver().query(GameWinnerProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, gameWinnerFilter, null, null);

        // If data is found
        if (gameWinnerCursor != null) {
            // Grab all the rows
            while (gameWinnerCursor.moveToNext()) {
                // Add the player ids to the ArrayList
                winnerIds.add(gameWinnerCursor.getInt(gameWinnerCursor.getColumnIndex(DBOpenHelper.PLAYER_FOREIGN_ID)));
            }

            // Close the cursor
            gameWinnerCursor.close();
        }

        // For every Id in the winning id ArrayList
        for (int p = 0; p < winnerIds.size(); p++) {
            // Grab all the player that won
            String winningPlayerFilter = DBOpenHelper.PLAYER_ID + "=" + winnerIds.get(p);
            Cursor winningPlayerCursor = getActivity().getContentResolver().query(PlayerProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, winningPlayerFilter, null, null);

            // If data is found
            if (winningPlayerCursor != null) {
                // Add the player to the winning players ArrayList
                winningPlayerCursor.moveToNext();
                String name = winningPlayerCursor.getString(winningPlayerCursor.getColumnIndex(DBOpenHelper.PLAYER_NAME));
                Player player = new Player(name);
                player.setId(winningPlayerCursor.getInt(winningPlayerCursor.getColumnIndex(DBOpenHelper.PLAYER_ID)));
                winningPlayers.add(player);

                // Close the cursor
                winningPlayerCursor.close();
            }
        }

        return winningPlayers;
    }
}
