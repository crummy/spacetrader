package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Alert;
import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import com.malcolmcrum.spacetrader.TradeItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/6/2015.
 */
public abstract class FamousCaptain extends Encounter {
    FamousCaptain(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    break;
                case Awake:
                    actions.add(FamousCaptain.class.getMethod("actionAttack"));
                    actions.add(FamousCaptain.class.getMethod("actionIgnore"));
                    actions.add(FamousCaptain.class.getMethod("actionMeet"));
                    break;
                case Attacking:
                    break;
                case Fleeing:
                    break;
                case Fled:
                    break;
                case Surrendered:
                    break;
                case Destroyed:
                    break;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
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
