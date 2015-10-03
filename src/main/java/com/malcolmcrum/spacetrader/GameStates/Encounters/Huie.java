package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Huie extends FamousCaptain {
    private static final Logger logger = LoggerFactory.getLogger(Huie.class);


    public Huie(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
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

    @Override
    public GameState actionMeet() {
        Optional<Weapon> military = game.getShip().getWeapons()
                .stream()
                .filter(s -> s == Weapon.MilitaryLaser)
                .findAny();
        if (military.isPresent()) {
            game.getShip().getWeapons().remove(military.get());
            if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
                game.getCaptain().addEngineerSkills(1);
            } else {
                game.getCaptain().addEngineerSkills(2);
            }
            game.addAlert(Alert.TrainingCompleted);
        } else {
            logger.error("Trying to trade with Captain Huie but have no military laser!");
        }
        return transit;
    }
}
