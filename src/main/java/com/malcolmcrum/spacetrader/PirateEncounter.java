package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class PirateEncounter extends Encounter {

    PirateEncounter(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    String getString() {
        return "a pirate";
    }

    @Override
    GameState finishAttacks() {
        if (opponent.isDestroyed()) {
            if (!game.getCaptain().isDubious()) {
                game.addAlert(Alert.BountyEarned);
                // NOTE: In the original, it seems the bounty is added whether or not the player is dubious.
                // I suspect the bounty should only be added if the BountyEarned message is sent, so that
                // is how I have implemented it.
                game.getCaptain().addCredits(getBounty());
            }
            game.getCaptain().killedAPirate();
        }
        return super.finishAttacks();
    }

    @Override
    GameState init() {
        return this;
    }

    private int getBounty() {
        int bounty = opponent.getPrice();
        bounty /= 200;
        bounty /= 25;
        bounty *= 25;
        if (bounty <= 0) {
            bounty = 25;
        }
        if (bounty > 2500) {
            bounty = 2500;
        }
        return bounty;
    }
}
