package com.yvesmatta.hindscoreboard.Models;

import java.util.ArrayList;

public class Player {

    // Class variables
    private int id;
    private String name;
    private ArrayList<Integer> scores;
    private int totalScore;

    // Constructor
    public Player(String name) {
        this.id = 0;
        this.name = name;
        this.scores = new ArrayList<>();
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

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void calculateTotalScore() {
        for (int i = 0; i < scores.size(); i++) {
            totalScore += scores.get(i);
        }
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public void setScores(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    public void addScore(int score) {
        this.scores.add(score);
    }
}
