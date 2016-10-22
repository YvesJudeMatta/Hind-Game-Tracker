package com.yvesmatta.hindscoreboard.utils;

import com.yvesmatta.hindscoreboard.models.Player;

import java.util.ArrayList;

public class MyUtilities {

    // Return a String of list of winning players with grammer to display
    public static String buildWinningPlayersMessage(ArrayList<Player> winningPlayers, int round, boolean isCompleted) {
        // Create a StringBuilder
        StringBuilder builder = new StringBuilder();

        if (isCompleted) {
            // Loop for every player in the list
            for (Player player : winningPlayers) {
                // If its the first instance, only place the name
                // otherwise, place some grammer
                if (player == winningPlayers.get(0)) {
                    builder.append(player.getName());
                } else {
                    String grammer = (player == winningPlayers.get(winningPlayers.size() - 1)) ? " and " : ", ";
                    builder.append(grammer);
                    builder.append(player.getName());
                }
            }

            // Check the size of the winning players to see if the players tied
            if (winningPlayers.size() > 1) {
                builder.append(" tied the game!");
            } else {
                builder.append(" won the game!");
            }
        } else {
            String onRound = "Game is on round " + round + "...";
            builder.append(onRound);
        }

        // Return the String from the StringBuilder
        return builder.toString();
    }
}
