package com.yvesmatta.hindscoreboard.models;

import java.util.ArrayList;

public class Game {

    // Constants
    public static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int MIN_NUMBER_OF_PLAYERS = 2;

    // Class variables
    private int id;
    private int roundsPlayed;
    private int maxRounds;
    private final ArrayList<Player> allPlayers;
    private ArrayList<Player> winningPlayers;
    private boolean isCompleted;

    // Constructor
    public Game(ArrayList<Player> allPlayers) {
        this.id = 0;
        this.roundsPlayed = 0;
        this.maxRounds = 10;
        this.allPlayers = allPlayers;
        this.winningPlayers = new ArrayList<>();
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() { return this.id; }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public ArrayList<Player> getWinningPlayers() {
        return winningPlayers;
    }

    public void setWinningPlayers(ArrayList<Player> winningPlayers) {
        this.winningPlayers = winningPlayers;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setRoundsPlayed(int roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }
}
