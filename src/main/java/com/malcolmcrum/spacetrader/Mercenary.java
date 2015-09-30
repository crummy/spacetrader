package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Mercenary extends Crew {

    private final Name name;

    public static int getTotalMercenaries() {
        return Name.values().length;
    }

    public Mercenary(int index) {
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
        return (getPilotSkill() + getFighterSkill() + getTraderSkill() + getEngineerSkill()) * 3;
    }

    public Name getType() {
        return name;
    }

    public String getName() {
        return name.getTitle();
    }

    protected enum Name {
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
