package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import java.util.Optional;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Huie extends FamousCaptain {
    public Huie(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter Captain Huie.";
    }

    @Override
    void initialAttack() {
        super.initialAttack();
        game.getNews().addNotableEvent(News.NotableEvent.CaptainHuieAttacked);
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getNews().replaceEvent(News.NotableEvent.CaptainHuieAttacked, News.NotableEvent.CaptainHuieDestroyed);
        return super.destroyedOpponent();
    }

    public GameState actionMeet() {
        Optional<Weapon> military = game.getShip().getWeapons()
                .stream()
                .filter(s -> s == Weapon.MilitaryLaser)
                .findAny();
        if (military.isPresent()) {
            game.getShip().getShields().remove(military.get());
            if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
                game.getCaptain().addEngineerSkills(1);
            } else {
                game.getCaptain().addEngineerSkills(2);
            }
            game.addAlert(Alert.TrainingCompleted);
        } else {
            logger.error("Trying to trade with Captain Ahab but have no reflective shield!");
        }
        return transit;
    }
}
