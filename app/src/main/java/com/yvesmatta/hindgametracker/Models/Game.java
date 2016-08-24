package com.yvesmatta.hindgametracker.Models;

import java.util.List;

public class Game {

    // Constants
    public static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int MAX_ROUNDS = 3;
    public static final int MIN_NUMBER_OF_PLAYERS = 2;

    // Class variables
    private int id;
    private final int numberOfPlayers;
    private final List<Player> allPlayers;
    private Player winner;
    private boolean isCompleted;

    // Constructor
    public Game(int numberOfPlayers, List<Player> allPlayers) {
        this.id = 0;
        this.numberOfPlayers = numberOfPlayers;
        this.allPlayers = allPlayers;
        this.winner = null;
        this.isCompleted = false;
    }

    // Getters and Setters
    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
