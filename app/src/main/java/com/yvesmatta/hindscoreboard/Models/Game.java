package com.yvesmatta.hindscoreboard.Models;

import java.util.ArrayList;

public class Game {

    // Constants
    public static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int MAX_ROUNDS = 8;
    public static final int MIN_NUMBER_OF_PLAYERS = 2;

    // Class variables
    private int id;
    private final int numberOfPlayers;
    private int roundsPlayed;
    private final ArrayList<Player> allPlayers;
    private ArrayList<Player> winningPlayers;
    private boolean isCompleted;

    // Constructor
    public Game(int numberOfPlayers, ArrayList<Player> allPlayers) {
        this.id = 0;
        this.numberOfPlayers = numberOfPlayers;
        this.roundsPlayed = 0;
        this.allPlayers = allPlayers;
        this.winningPlayers = new ArrayList<>();
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() { return this.id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
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
}
