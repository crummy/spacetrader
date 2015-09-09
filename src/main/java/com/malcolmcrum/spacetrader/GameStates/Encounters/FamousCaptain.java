package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/6/2015.
 */
public abstract class FamousCaptain extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(FamousCaptain.class);

    FamousCaptain(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    protected ShipType chooseShipType(int tries) {
        return ShipType.Wasp;
    }

    @Override
    protected void addCrew() {
        opponent.addCrew(new Crew(Game.MAX_POINTS_PER_SKILL, Game.MAX_POINTS_PER_SKILL, Game.MAX_POINTS_PER_SKILL, Game.MAX_POINTS_PER_SKILL));
    }

    @Override
    protected void addGadgets(int tries) {
        opponent.addGadget(Gadget.Targeting);
        opponent.addGadget(Gadget.Navigation);
    }

    @Override
    protected void addShields(int tries) {
        for (int i = 0; i < ShipType.Wasp.getShieldSlots(); ++i) {
            opponent.addShield(ShieldType.ReflectiveShield);
        }
    }

    @Override
    protected void addWeapons(int tries) {
        for (int i = 0; i < ShipType.Wasp.getWeaponSlots(); ++i) {
            opponent.addWeapon(Weapon.MilitaryLaser);
        }
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
                    actions.add(FamousCaptain.class.getMethod("actionAttack"));
                    actions.add(FamousCaptain.class.getMethod("actionFlee"));
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
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    public GameState actionMeet() {
        // TODO
        return this;
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
