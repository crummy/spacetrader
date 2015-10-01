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
public class Ahab extends FamousCaptain {
    private static final Logger logger = LoggerFactory.getLogger(Ahab.class);


    public Ahab(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter the famous Captain Ahab.";
    }

    @Override
    void initialAttack() {
        super.initialAttack();
        game.getNews().addNotableEvent(News.NotableEvent.CaptainAhabAttacked);
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getNews().replaceEvent(News.NotableEvent.CaptainAhabAttacked, News.NotableEvent.CaptainAhabDestroyed);
        return super.destroyedOpponent();
    }

    public GameState actionMeet() {
        Optional<Ship.Shield> reflective = game.getShip().getShields()
                .stream()
                .filter(s -> s.getType() == ShieldType.ReflectiveShield)
                .findAny();
        if (reflective.isPresent()) {
            game.getShip().getShields().remove(reflective.get());
            if (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) {
                game.getCaptain().addPilotSkills(1);
            } else {
                game.getCaptain().addPilotSkills(2);
            }
            game.addAlert(Alert.TrainingCompleted);
        } else {
            logger.error("Trying to trade with Captain Ahab but have no reflective shield!");
        }
        return transit;
    }
}
