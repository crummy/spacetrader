package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import com.malcolmcrum.spacetrader.Ship;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class OldBottle extends Encounter {
    OldBottle(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a floating bottle.";
    }

    @Override
    public String getTitle() {
        return MISSING_TITLE;
    }

    @Override
    protected String descriptionAwake() {
        return "It appears to be a rare bottle of Captain Marmoset's Skill Tonic!";
    }

    @Override
    protected String descriptionAttacking() {
        return INVALID_DESCRIPTION;
    }

    @Override
    protected String descriptionFleeing() {
        return INVALID_DESCRIPTION;
    }

    @Override
    protected String descriptionSurrendered() {
        return INVALID_DESCRIPTION;
    }
}
