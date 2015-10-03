package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Difficulty;
import com.malcolmcrum.spacetrader.Game;
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
public class GoodBottle extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(GoodBottle.class);


    public GoodBottle(Game game, Transit transit) {
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
                    actions.add(GoodBottle.class.getMethod("actionDrink"));
                    actions.add(GoodBottle.class.getMethod("actionIgnore"));
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
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    public GameState actionDrink() {
        increaseRandomSkill();
        if (game.getDifficulty() != Difficulty.Impossible && game.getDifficulty() != Difficulty.Hard) {
            increaseRandomSkill();
        }
        return transit;
    }

    private void increaseRandomSkill() {
        boolean didIncreaseSkill = false;
        while (didIncreaseSkill) {
            int skill = GetRandom(4);
            if (skill == 0) {
                didIncreaseSkill = game.getCaptain().addEngineerSkills(1);
            } else if (skill == 1) {
                didIncreaseSkill = game.getCaptain().addFighterSkills(1);
            } else if (skill == 2) {
                didIncreaseSkill = game.getCaptain().addPilotSkills(1);
            } else if (skill == 3) {
                didIncreaseSkill = game.getCaptain().addTraderSkills(1);
            }
        }
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a floating bottle.";
    }

    @Override
    public String getTitle() {
        return MISSING_TITLE;
    }

    @Override
    protected String descriptionAwake() {
        return "It appears to be a rare bottle of Captain Marmoset's Skill Tonic!";
    }

    @Override
    protected String descriptionAttacking() {
        return INVALID_DESCRIPTION;
    }

    @Override
    protected String descriptionFleeing() {
        return INVALID_DESCRIPTION;
    }

    @Override
    protected String descriptionSurrendered() {
        return INVALID_DESCRIPTION;
    }
}
