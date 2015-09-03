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

    public boolean ahab() {
        return ahab;
    }

    public void encounteredAhab() {
        ahab = true;
    }

    public boolean conrad() {
        return conrad;
    }

    public void encounteredConrad() {
        conrad = true;
    }

    public boolean huie() {
        return huie;
    }

    public void encounteredHuie() {
        huie = true;
    }

    public boolean oldBottle() {
        return oldBottle;
    }

    public void encounteredOldBottle() {
        oldBottle = true;
    }

    public boolean goodBottle() {
        return goodBottle;
    }

    public void encounteredGoodBottle() {
        goodBottle = true;
    }
}
