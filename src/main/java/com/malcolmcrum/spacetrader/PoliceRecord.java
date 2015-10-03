package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 10/3/2015.
 */
public class PoliceRecord {

    private int score;
    private final Difficulty difficulty;

    public PoliceRecord(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void add(int i) {
        score += i;
    }

    public boolean is(Status status) {
        if (status.score < 0) {
            return score <= status.score;
        } else {
            return score >= status.score;
        }
    }

    public void make(Status status) {
        score = status.score;
    }

    public void caughtWithWild() {
        score += Actions.CaughtWithWild.modifier;
    }

    public void attackedPolice() {
        score += Actions.AttackPolice.modifier;
    }

    public void attackedTrader() {
        score += Actions.AttackTrader.modifier;
    }

    public void fledPolice() {
        if (score > Status.Dubious.score) {
            boolean easierThanNormal = difficulty == Difficulty.Beginner || difficulty == Difficulty.Easy;
            score = Status.Dubious.score - (easierThanNormal ? 0 : 1);
        } else {
            score += Actions.FleeFromInspection.modifier;
        }
    }

    public void caughtTrafficking() {
        score += Actions.Trafficking.modifier;
    }

    public void passedInspection() {
        score += Actions.PassedInspection.modifier;
    }

    public int getScore() {
        return score;
    }

    public enum Status {
        Psychopath("Psycopath", -70),
        Villain("Villain", -30),
        Criminal("Criminal", -10),
        Dubious("Dubious", -5),
        Clean("Clean", 0),
        Lawful("Lawful", 5),
        Trusted("Trusted", 10),
        Helper("Helper", 25),
        Hero("Hero", 75);

        final String name;
        final int score;

        Status(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    enum Actions {
        AttackPolice(-3),
        KillPolice(-6),
        CaughtWithWild(-4),
        AttackTrader(-2),
        PlunderTrader(-2),
        KillTrader(-4),
        AttackPirate(0),
        KillPirate(1),
        PlunderPirate(-1),
        Trafficking(-1),
        PassedInspection(1),
        FleeFromInspection(-2),
        TakeMarieNarcotics(-4);

        protected final int modifier;

        Actions(int recordModifier) {
            this.modifier = recordModifier;
        }
    }
}
