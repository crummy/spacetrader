package com.malcolmcrum.spacetrader;

/**
 * Keeps track of very rare encounters, to ensure we hit each one only once
 * Created by Malcolm on 9/2/2015.
 */
public class RareEncounters {
    private boolean marie = false;
    private boolean ahab = false;
    private boolean conrad = false;
    private boolean huie = false;
    private boolean oldBottle = false;
    private boolean goodBottle = false;

    public boolean marie() {
        return marie;
    }

    public void encounteredMarie() {
        marie = true;
    }

    public boolean hasEncounteredAhab() {
        return ahab;
    }

    public void justEncounteredAhab() {
        ahab = true;
    }

    public boolean hasEncounteredConrad() {
        return conrad;
    }

    public void justEncounteredConrad() {
        conrad = true;
    }

    public boolean hasEncounteredHuie() {
        return huie;
    }

    public void justEncounteredHuie() {
        huie = true;
    }

    public boolean hasEncounteredOldBottle() {
        return oldBottle;
    }

    public void justEncounteredOldBottle() {
        oldBottle = true;
    }

    public boolean hasEncounteredGoodBottle() {
        return goodBottle;
    }

    public void justEncounteredGoodBottle() {
        goodBottle = true;
    }
}
