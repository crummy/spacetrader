package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class TraderEncounter extends Encounter {
    public TraderEncounter(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    String getString() {
        return null;
    }

    @Override
    GameState init() {
        return null;
    }
}
