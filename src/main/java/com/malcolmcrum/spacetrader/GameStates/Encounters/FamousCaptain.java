package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Alert;
import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import com.malcolmcrum.spacetrader.TradeItem;

/**
 * Created by Malcolm on 9/6/2015.
 */
public abstract class FamousCaptain extends Encounter {
    FamousCaptain(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getTitle() {
        return "Captain";
    }

    @Override
    protected String descriptionAwake() {
        return "The Captain requests a brief meeting with you.";
    }

    @Override
    protected GameState destroyedOpponent() {
        if (!game.getCaptain().isDangerous()) {
            game.getCaptain().makeDangerous();
        } else {
            game.getCaptain().addReputation(100);
        }
        return super.destroyedOpponent();
    }

    @Override
    void initialAttack() {
        super.initialAttack();

        if (game.getCaptain().isVillainous()) {
            game.getCaptain().makeVillain();
        }
        game.getCaptain().attackedTrader();
    }
}
