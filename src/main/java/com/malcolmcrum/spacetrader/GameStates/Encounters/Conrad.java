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
public class Conrad extends FamousCaptain {
    private static final Logger logger = LoggerFactory.getLogger(Conrad.class);

    public Conrad(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter Captain Conrad.";
    }

    @Override
    void initialAttack() {
        super.initialAttack();
        game.getNews().addNotableEvent(News.NotableEvent.CaptainConradAttacked);
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getNews().replaceEvent(News.NotableEvent.CaptainConradAttacked, News.NotableEvent.CaptainConradDestroyed);
        return super.destroyedOpponent();
    }

    public GameState actionMeet() {
        Optional<Weapon> military = game.getShip().getWeapons()
                .stream()
                .filter(s -> s == Weapon.MilitaryLaser)
                .findAny();
        if (military.isPresent()) {
            game.getShip().getWeapons().remove(military.get());
            if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
                game.getCaptain().addTraderSkills(1);
            } else {
                game.getCaptain().addTraderSkills(2);
            }
            game.addAlert(Alert.TrainingCompleted);
        } else {
            logger.error("Trying to trade with Captain Huie but have no military laser!");
        }
        game.getCurrentSystem().getMarket().recalculateBuyPrice();
        return transit;
    }
}
