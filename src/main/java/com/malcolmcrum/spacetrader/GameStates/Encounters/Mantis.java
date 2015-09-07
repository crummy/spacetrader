package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Mantis extends Pirate {
    public Mantis(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getTitle() {
        return "alien ship";
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter an alien " + ship + ".";
    }
}
