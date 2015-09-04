package com.malcolmcrum.spacetrader;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class LootShipState extends GameState {
    GameState nextState;
    Ship ship;
    TradeItem item;

    public LootShipState(Game game, Transit transit, Ship ship) {
        super(game);
        this.ship = ship;
        nextState = transit;
    }

    @Override
    public GameState init() {
        // Chance 50% to pick something up on Normal level, 33% on Hard level, 25% on
        // Impossible level, and 100% on Easy or Beginner
        if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
            if (GetRandom(game.getDifficulty().getValue()) != 1) {
                return nextState;
            }
        }

        item = RandomEnum(TradeItem.class);
        // More chance to pick up a cheap good
        if (item == TradeItem.Firearms
                || item == TradeItem.Medicine
                || item == TradeItem.Narcotics
                || item == TradeItem.Robots) {
            item = RandomEnum(TradeItem.class);
        }

        return this;
    }

    public GameState scoop() {
        game.getShip().addCargo(item, 1, 0);
        return nextState;
    }

    public GameState skip() {
        return nextState;
    }

}
