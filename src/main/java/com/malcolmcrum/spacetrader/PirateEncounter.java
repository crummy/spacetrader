package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class PirateEncounter extends Encounter {
    @Override
    String getTitle() {
        return "a pirate";
    }

    @Override
    GameState executeAction() {
        // Fire shots
        if (status == Status.Attacking) {
            commanderGotHit = attackPlayer();
        }

        if (!playerFlees) {

        }
        return null;
    }

    @Override
    GameState init() {
        return this;
    }
}
