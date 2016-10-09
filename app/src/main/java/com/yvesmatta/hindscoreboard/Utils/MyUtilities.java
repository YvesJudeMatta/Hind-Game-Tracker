package com.yvesmatta.hindscoreboard.Utils;

import com.yvesmatta.hindscoreboard.Models.Player;
import java.util.ArrayList;

public class MyUtilities {

    // Return a String of list of winning players with grammer to display
    public static String buildWinningPlayersMessage(ArrayList<Player> winningPlayers) {
        // Create a StringBuilder
        StringBuilder builder = new StringBuilder();

        // Loop for every player in the list
        for (int i = 0; i < winningPlayers.size(); i++) {
            // If its the first instance, only place the name
            // otherwise, place some grammer
            if (i == 0) {
                builder.append(winningPlayers.get(i).getName());
            } else {
                String grammer = (i == winningPlayers.size() - 1) ? " and " : ", ";
                builder.append(grammer);
                builder.append(winningPlayers.get(i).getName());
            }
        }

        // Check the size of the winning players to see if the players tied
        if (winningPlayers.size() > 1){
            builder.append(" tied the game!");
        } else {
            builder.append(" won the game!");
        }

        // Return the String from the StringBuilder
        return builder.toString();
    }
}
