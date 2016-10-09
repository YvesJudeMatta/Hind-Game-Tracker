package com.yvesmatta.hindscoreboard.Models;

import java.util.ArrayList;
import java.util.List;

public class Game {

    // Constants
    public static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int MAX_ROUNDS = 8;
    public static final int MIN_NUMBER_OF_PLAYERS = 2;

    // Class variables
    private int id;
    private final int numberOfPlayers;
    private final List<Player> allPlayers;
    private ArrayList<Player> winningPlayers;
    private boolean isCompleted;

    // Constructor
    public Game(int numberOfPlayers, List<Player> allPlayers) {
        this.id = 0;
        this.numberOfPlayers = numberOfPlayers;
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

    public List<Player> getAllPlayers() {
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
}
