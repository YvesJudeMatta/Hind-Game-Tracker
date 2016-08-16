package com.yvesmatta.hindgametracker;

public class Player {

    // Class variables
    private int id;
    private String name;
    private int totalScore;

    // Constructor
    public Player(String name) {
        this.id = 0;
        this.name = name;
        this.totalScore = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void calculateTotalScore(int score) {
        this.totalScore += score;
    }
}
