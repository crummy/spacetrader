package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 10/3/2015.
 */
public class Reputation {
    private int score;

    public boolean is(Status status) {
        return score >= status.score;
    }

    public void make(Status status) {
        score = status.score;
    }

    public void add(int rep) {
        score += rep;
    }

    public int getEliteScore() {
        return Status.Elite.score;
    }

    public int getScore() {
        return score;
    }

    public enum Status {
        Harmless("Harmless", 0),
        MostlyHarmless("Mostly harmless", 10),
        Poor("Poor", 20),
        Average("Average", 40),
        AboveAverage("Above average", 80),
        Competent("Competent", 150),
        Dangerous("Dangerous", 300),
        Deadly("Deadly", 600),
        Elite("Elite", 1500);

        final String name;
        final int score;

        Status(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
