package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Alert;
import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Mantis extends Pirate {
    public Mantis(Game game, Transit transit) {
        super(game, transit);
        opponentStatus = Status.Attacking;
    }

    @Override
    public GameState init() {
        return this; // necessary to override pirate.init() behaviour
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

    @Override
    public GameState actionSurrender() {
        if (quests.isArtifactOnBoard()) {
            game.addAlert(Alert.ArtifactStolen);
            quests.lostArtifact();
            return transit;
        } else {
            game.addAlert(Alert.NoSurrender);
            return this;
        }
    }

    @Override
    protected int getShipTypeTries() {
        return 1 + game.getDifficulty().getValue();
    }
}
