package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Alert;
import com.malcolmcrum.spacetrader.Difficulty;
import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Monster extends Encounter {


    Monster(Game game, Transit transit) {
        super(game, transit);
        opponent.setHullStrength(game.getMonsterHullStrength());
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
    public GameState fleeAction() throws InvalidOpponentAction, InvalidPlayerAction {
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
            game.setMonsterHullStrength(opponent.getHullStrength());
            return transit;
        }

        return actionResult();
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getCaptain().killedAPirate();
        game.setMonsterStatus(com.malcolmcrum.spacetrader.Monster.Destroyed);
        return transit;
    }
}
