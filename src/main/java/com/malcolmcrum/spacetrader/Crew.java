package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 8/28/2015.
 */
public enum Crew {
    Captain(0, "You"),
    Alyssa(1, "Alyssa"),
    Armatur(2, "Armatur"),
    Bentos(3, "Bentos"),
    C2U2(4, "C2U2"),
    ChiTi(5, "Chi'Ti"),
    Crystal(6, "Crystal"),
    Dane(7, "Dane"),
    Deirdre(8, "Deirdre"),
    Doc(9, "Doc"),
    Draco(10, "Draco"),
    Iranda(11, "Iranda"),
    Jeremiah(12, "Jeremiah"),
    Jujubal(13, "Jujubal"),
    Krydon(14, "Krydon"),
    Luis(15, "Luis"),
    Mercedez(16, "Mercedez"),
    Milete(17, "Milete"),
    MuriL(18, "Muri-L"),
    Mystyc(19, "Mystyc"),
    Nandi(20, "Nandi"),
    Orestes(21, "Orestes"),
    Pancho(22, "Pancho"),
    PS37(23, "PS37"),
    Quarck(24, "Quarck"),
    Sosumu(25, "Sosumi"),
    Uma(26, "Uma"),
    Wesley(27, "Wesley"),
    Wonton(28, "Wonton"),
    Yorvick(29, "Yorvick"),
    Zeethibal(30, "Zeethibal");

    private String name;
    private int index;
    private int pilot;
    private int fighter;
    private int trader;
    private int engineer;

    Crew(int index, String name) {
        this.index = index;
        this.name = name;
        pilot = randomSkill();
        fighter = randomSkill();
        trader = randomSkill();
        engineer = randomSkill();
    }

    static Crew getCrew(int index) {
        for (Crew crew : Crew.values()) {
            if (crew.index == index) {
                return crew;
            }
        }
        return null;
    }

    private int randomSkill() {
        return 1 + GetRandom(5) + GetRandom(6);
    }
}
