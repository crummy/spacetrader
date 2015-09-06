package com.malcolmcrum.spacetrader.GameStates.Encounters;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Somewhere in here, if they loot, don't forget to transit.setJustLootedMarie(true);
 * Created by Malcolm on 9/6/2015.
 */
public class MarieCeleste extends Encounter {

    @Override
    public String getTitle() {
        return MISSING_TITLE;
    }

    @Override
    protected String descriptionAwake() {
        return "The Marie Celeste appears to be completed abandoned.";
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a a drifting ship.";
    }
}
