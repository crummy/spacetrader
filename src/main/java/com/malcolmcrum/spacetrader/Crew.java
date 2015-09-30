package com.malcolmcrum.spacetrader;

import java.util.Objects;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/30/2015.
 */
public class Crew {
    // skills
    protected int pilot;
    protected int fighter;
    protected int trader;
    protected int engineer;

    public Crew() {
        this.pilot = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        this.fighter = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        this.engineer = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        this.trader = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
    }

    public Crew(int pilot, int fighter, int trader, int engineer) {
        this.pilot = pilot;
        this.fighter = fighter;
        this.engineer = engineer;
        this.trader = trader;
    }

    public int getPilotSkill() {
        return pilot;
    }

    public int getFighterSkill() {
        return fighter;
    }

    public int getTraderSkill() {
        return trader;
    }

    public int getEngineerSkill() {
        return engineer;
    }

    public int getDailyCost() {
        return 0;
    }

    public String getName() {
        return "CREW";
    }
}
