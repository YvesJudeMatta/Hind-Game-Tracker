package com.yvesmatta.hindgametracker;

import java.util.List;

public class Game {

    // Class variables
    private int id;
    private int numberOfPlayers;
    private List<Player> allPlayers;
    private Player winner;
    private boolean isCompleted;

    // Constructor
    public Game(int numberOfPlayers, List<Player> allPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.allPlayers = allPlayers;
        this.winner = null;
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(List<Player> allPlayers) {
        this.allPlayers = allPlayers;
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
