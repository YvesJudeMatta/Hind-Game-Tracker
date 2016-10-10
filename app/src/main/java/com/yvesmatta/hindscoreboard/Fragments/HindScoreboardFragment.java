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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
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

    // Fragment to be read only
    private boolean readOnly;

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

        // Define round
        round = 1;

        // If its a new game or an existing game
        readOnly = uri != null;

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

        // Set the visibility depending on readOnly
        if (readOnly) {
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
                updateScores();
                break;
        }

        // Return the supers class method call
        return super.onOptionsItemSelected(item);
    }

    private void updateScores() {
        // Assign the last round
        int lastRound = round - 1;

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

        // If the game is complete
        if (game.isCompleted()){
            // Set winners
            setWinners();

            // Show a dialog of who won
            showWinningPlayersDialog(game.getWinningPlayers());
        }
    }

    private void createScoreLayout() {
        if (readOnly) {
            // Load data from database
            loadFromDatabase();

            // Hide button
            Button btnFinish = (Button) view.findViewById(R.id.btnFinish);
            btnFinish.setVisibility(View.INVISIBLE);
        }

        // Create the player names row
        createPlayerNamesRow();

        // Create round score row
        if (readOnly) {
            for (int r = 1; r <= game.getRoundsPlayed(); r++) {
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
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            // Create the player name view
            TextView tvPlayerName = createTextView(game.getAllPlayers().get(i-1).getName(), true);
            tvPlayerName.setTag("Player" + i + "Name");

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
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            // Create the player score view
            EditText etRoundPlayer = createEditView();
            etRoundPlayer.setTag("Round" + round + "Player" + i);

            // if readOnly disable the view, and load the values
            if (readOnly) {
                etRoundPlayer.setText(game.getAllPlayers().get(i-1).getScores().get(round-1).toString());
                etRoundPlayer.setFocusable(false);
            }

            // Add player score view to row
            trPlayerScoresRow.addView(etRoundPlayer);
        }

        // Add the row to the table layout
        tlScoreBoard.addView(trPlayerScoresRow);
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
        for (int i = 1; i <= game.getNumberOfPlayers(); i++) {
            // Create the player total score view
            TextView tvPlayerScore = createTextView("0", true);
            tvPlayerScore.setTag("Player" + i + "Score");

            // if readOnly disable the view, and load the values
            if (readOnly) {
                tvPlayerScore.setText(game.getAllPlayers().get(i-1).getTotalScore()+"");
            }

            // Add player total score view to row
            trPlayerTotalScoresRow.addView(tvPlayerScore);
        }

        // Add the row to the table layout
        tlTotalScores.addView(trPlayerTotalScoresRow);
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

            // Check which if its the last round
            if (round == Game.MAX_ROUNDS) {
                // Grey out the current round
                greyOutRoundRow(round);

                // Make the button invisible
                view.setVisibility(View.INVISIBLE);

                // Set winners
                setWinners();

                // Show a dialog of who won
                showWinningPlayersDialog(game.getWinningPlayers());

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
            String winningPlayersMessage = MyUtilities.buildWinningPlayersMessage(winningPlayers);

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
        for (int i = 0; i < game.getNumberOfPlayers(); i++){
            // Assign the current player in the list
            Player player = game.getAllPlayers().get(i);

            // If its the first player, set it to be the first total score
            // Else if player score is higher then the winning score
            if (i == 0) {
                // Set the winning score to the player score
                winningScore = player.getTotalScore();
            } else if (player.getTotalScore() < winningScore) {
                // Set the winning score to the player score
                winningScore = player.getTotalScore();
            }
        }

        // Loop through all the players and update each player that won
        for (int i = 0; i < game.getNumberOfPlayers(); i++){
            // Assign the current player in the list
            Player player = game.getAllPlayers().get(i);

            // If the player matches the winningScore
            if (player.getTotalScore() == winningScore) {
                // Add the player to the winning players list
                winningPlayers.add(player);
            }
        }

        // Add the winning players to the game
        game.setWinningPlayers(winningPlayers);
    }

    private void greyOutRoundRow(int round) {
        // Loop through all the players and grey out the view for the specified round
        for (int i = 1; i <= game.getNumberOfPlayers(); i++){
            EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + i);
            etPlayer.setBackgroundColor(Color.TRANSPARENT);
            etPlayer.setTextColor(Color.GRAY);
        }
    }

    private boolean validateRowsUpToRound(int round) {
        // Loop through each round
        for (int r = 1; r <= round; r++) {
            // Loop through all the players
            for (int p = 1; p <= game.getNumberOfPlayers(); p++) {
                // Find the view with the specified tag
                EditText etPlayer = (EditText) view.findViewWithTag("Round" + r + "Player" + p);
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
        for (int p = 0; p < game.getNumberOfPlayers(); p++) {
            game.getAllPlayers().get(p).setScores(new ArrayList<Integer>());
            game.getAllPlayers().get(p).setTotalScore(0);
        }
    }

    private void updateTotalScoreRow(int round) {
        for (int r = 1; r <= round; r++) {
            for (int p = 1; p <= game.getNumberOfPlayers(); p++) {
                // Add the round score in the Player object
                addScore(p, r);
            }
        }

        // Update the total score in the Player object
        for (int p = 1; p <= game.getNumberOfPlayers(); p++) {
            game.getAllPlayers().get(p-1).calculateTotalScore();

            // Update the total score in the view
            TextView tvPlayerScore = (TextView) view.findViewWithTag("Player" + p + "Score");
            tvPlayerScore.setText(String.valueOf(game.getAllPlayers().get(p-1).getTotalScore()));
        }
    }

    private void addScore(int playerIndex, int round) {
        EditText etPlayer = (EditText) view.findViewWithTag("Round" + round + "Player" + playerIndex);
        int roundScore = Integer.parseInt(etPlayer.getText().toString());
        game.getAllPlayers().get(playerIndex-1).addScore(roundScore);
    }

    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onBackPressed() {
        if (game.isCompleted() && !readOnly) {
            loadDatabase();
        }
        return true;
    }

    private void loadDatabase() {
        // Insert game
        insertGame();

        // Insert players
        insertPlayers();

        // Insert the winners
        insertWinners();

        // Insert the scores
        insertScores();
    }

    private void insertGame() {
        // Load the content values with the game data
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.GAME_COMPLETED, game.isCompleted());
        contentValues.put(DBOpenHelper.GAME_ROUNDS_PLAYED, round);
        contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
        contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

        // Insert the game into the database and set the id for the game
        Uri uriGame = getActivity().getContentResolver().insert(GameProvider.CONTENT_URI, contentValues);
        if (uriGame != null)
            game.setId(Integer.parseInt(uriGame.getLastPathSegment()));
    }

    private void insertPlayers() {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            // Load the content values with the player data
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.GAME_FOREIGN_ID, game.getId());
            contentValues.put(DBOpenHelper.PLAYER_NAME, game.getAllPlayers().get(i).getName());
            contentValues.put(DBOpenHelper.PLAYER_TOTAL_SCORE, game.getAllPlayers().get(i).getTotalScore());
            contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
            contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

            // Insert the player into the database and set the id for the player
            Uri playerUri = getActivity().getContentResolver().insert(PlayerProvider.CONTENT_URI, contentValues);
            if (playerUri != null)
                game.getAllPlayers().get(i).setId(Integer.parseInt(playerUri.getLastPathSegment()));
        }
    }

    private void insertWinners() {
        for (int i = 0; i < game.getWinningPlayers().size(); i++) {
            // Load the content values with the winning player data if the players are winners
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.GAME_FOREIGN_ID, game.getId());
            contentValues.put(DBOpenHelper.PLAYER_FOREIGN_ID, game.getWinningPlayers().get(i).getId());
            contentValues.put(DBOpenHelper.CREATED_AT, System.currentTimeMillis());
            contentValues.put(DBOpenHelper.UPDATED_AT, System.currentTimeMillis());

            // Insert the winning player into the database
            getActivity().getContentResolver().insert(GameWinnerProvider.CONTENT_URI, contentValues);

        }
    }

    private void insertScores() {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            for (int r = 1; r <= round; r++){
                // Load the content values with the player score data
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBOpenHelper.GAME_FOREIGN_ID, game.getId());
                contentValues.put(DBOpenHelper.PLAYER_FOREIGN_ID, game.getAllPlayers().get(i).getId());
                contentValues.put(DBOpenHelper.ROUND_SCORE_ROUND, r);
                contentValues.put(DBOpenHelper.ROUND_SCORE_SCORE, game.getAllPlayers().get(i).getScores().get(r-1));
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
