package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Difficulty;
import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.Ship;
import com.malcolmcrum.spacetrader.TradeItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.RandomEnum;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class LootShip extends GameState {
    GameState nextState;
    Ship ship;
    TradeItem item;

    public LootShip(Game game, Transit transit, Ship ship) {
        super(game);
        this.ship = ship;
        nextState = transit;
    }

    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            actions.add(LootShip.class.getMethod("scoop"));
            actions.add(LootShip.class.getMethod("skip"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
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

    @Override
    public String getName() {
        return "LootShip";
    }

    public GameState scoop() {
        game.getShip().addCargo(item, 1, 0);
        return nextState;
    }

    public GameState skip() {
        return nextState;
    }

}
