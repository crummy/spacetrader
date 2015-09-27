package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.Ship;
import com.malcolmcrum.spacetrader.TradeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/27/2015.
 */
public class PlunderState extends GameState {
    private static final Logger logger = LoggerFactory.getLogger(PlunderState.class);


    Transit transit;
    Ship opponent;

    public PlunderState(Game game, Transit transit, Ship opponent) {
        super(game);
        this.transit = transit;
        this.opponent = opponent;
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            actions.add(PlunderState.class.getMethod("plunder"));
            actions.add(PlunderState.class.getMethod("jettison"));
            actions.add(PlunderState.class.getMethod("done"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
    }

    public GameState done() {
        return transit;
    }

    public GameState plunder(TradeItem item, int amount) {
        if (amount > opponent.getCargoCount(item)) {
            logger.error("Trying to plunder more than is available!");
            amount = opponent.getCargoCount(item);
        }
        if (amount > game.getShip().getFreeCargoBays()) {
            logger.error("Trying to plunder more than player has room for!");
            amount = game.getShip().getFreeCargoBays();
        }
        opponent.removeCargo(item, amount);
        game.getShip().addCargo(item, amount, 0);
        return this;
    }

    public GameState jettison(TradeItem item, int amount) {
        if (amount > game.getShip().getCargoCount(item)) {
            logger.error("Trying to dump more than we have available!");
            amount = game.getShip().getCargoCount(item);
        }
        game.getShip().removeCargo(item, amount);
        return this;
    }

    @Override
    public GameState init() {
        return this;
    }

    @Override
    public String getName() {
        return "Plunder";
    }
}
