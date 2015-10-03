package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Monster extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(Monster.class);


    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    actions.add(Monster.class.getMethod("actionAttack"));
                    actions.add(Monster.class.getMethod("actionIgnore"));
                    break;
                case Awake:
                    break;
                case Attacking:
                    actions.add(Monster.class.getMethod("actionAttack"));
                    actions.add(Monster.class.getMethod("actionFlee"));
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

    public Monster(Game game, Transit transit) {
        super(game, transit);
        opponent = new Ship(ShipType.SpaceMonster, difficulty);

        int difficulty = game.getDifficulty().getValue();
        opponent.addCrew(new Crew(8 + difficulty, 8 + difficulty, 1, 1 + difficulty));

        // Monster continues health from previous encounter.
        int savedHealth = quests.getMonsterHullStrength();
        opponent.setHullStrength(savedHealth);

        if (game.getShip().isInvisibleTo(opponent)) {
            opponentStatus = Status.Ignoring;
        } else {
            opponentStatus = Status.Attacking;
        }
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter " + ship + ".";
    }

    @Override
    public String getTitle() {
        return "monster";
    }

    @Override
    protected String descriptionAwake() {
        return null;
    }

    @Override
    // Overridden just so we can set the monster's health after a successful flee
    public GameState actionFlee() throws InvalidOpponentAction, InvalidPlayerAction {
        isPlayerFleeing = true;

        opponentAction();

        if (game.getDifficulty() == Difficulty.Beginner) {
            game.addAlert(Alert.YouEscaped);
            return transit;
        }
        int difficulty = game.getDifficulty().getValue();
        int playerFleeChance = (GetRandom(7) + (game.getShip().getPilotSkill() / 3)) * 2;
        int opponentChaseChance = GetRandom(opponent.getPilotSkill()) * (2 * difficulty);
        if (playerFleeChance >= opponentChaseChance) {
            if (playerWasHit) {
                tribblesOnScreen = getTribbles();
                game.addAlert(Alert.YouEscapedWithDamage);
            } else {
                game.addAlert(Alert.YouEscaped);
            }
            quests.setMonsterHullStrength(opponent.getHullStrength());
            return transit;
        }

        return actionResult();
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getCaptain().killedAPirate();
        quests.destroyedMonster();
        return transit;
    }
}
