package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * TODO: Split into Crew and Mercenary
 * Created by Malcolm on 8/28/2015.
 */
public class Crew {

    private final Name name;

    // skills
    private int pilot;
    private int fighter;
    private int trader;
    private int engineer;

    private int dailyCost;

    public Crew() {
        this.name = null;
        this.pilot = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        this.fighter = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        this.engineer = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
        this.trader = 1 + GetRandom(Game.MAX_POINTS_PER_SKILL);
    }

    public static int getMaxCrew() {
        return Name.values().length;
    }

    public Crew(int pilot, int fighter, int trader, int engineer) {
        this.name = null;
        this.pilot = pilot;
        this.fighter = fighter;
        this.engineer = engineer;
        this.trader = trader;
    }

    public Crew(int index) {
        this.name = Name.values()[index];
        pilot = randomSkill();
        fighter = randomSkill();
        trader = randomSkill();
        engineer = randomSkill();
    }

    private static int randomSkill() {
        return 1 + GetRandom(5) + GetRandom(6);
    }

    public int getDailyCost() {
        return dailyCost;
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

    public Name getName() {
        return name;
    }

    protected enum Name {
        Captain("You"),
        Alyssa("Alyssa"),
        Armatur("Armatur"),
        Bentos("Bentos"),
        C2U2("C2U2"),
        ChiTi("Chi'Ti"),
        Crystal("Crystal"),
        Dane("Dane"),
        Deirdre("Deirdre"),
        Doc("Doc"),
        Draco("Draco"),
        Iranda("Iranda"),
        Jeremiah("Jeremiah"),
        Jujubal("Jujubal"),
        Krydon("Krydon"),
        Luis("Luis"),
        Mercedez("Mercedez"),
        Milete("Milete"),
        MuriL("Muri-L"),
        Mystyc("Mystyc"),
        Nandi("Nandi"),
        Orestes("Orestes"),
        Pancho("Pancho"),
        PS37("PS37"),
        Quarck("Quarck"),
        Sosumu("Sosumi"),
        Uma("Uma"),
        Wesley("Wesley"),
        Wonton("Wonton"),
        Yorvick("Yorvick"),
        Zeethibal("Zeethibal");

        private final String title;

        Name(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

}
