package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class ShipDestroyed extends GameState {
    final SolarSystem newSystem; // system we wake up on

    private final Quests quests;
    private final Captain captain;
    private final PlayerShip ship;
    private final Difficulty difficulty;

    public ShipDestroyed(Game game, SolarSystem newSystem) {
        super(game);
        this.newSystem = newSystem;
        this.quests = game.getQuests();
        this.captain = game.getCaptain();
        this.ship = game.getShip();
        this.difficulty = game.getDifficulty();
    }

    public List<Method> getActions() {
        return new ArrayList<>();
    }

    @Override
    public GameState init() {
        if (game.getCaptain().hasEscapePod()) {
            return escapeWithPod();
        } else {
            return gameOver();
        }
    }

    @Override
    public String getName() {
        return "ShipDestroyed";
    }

    public GameState gameOver() {
        game.addAlert(Alert.GameOverKilled);
        return new GameOver(game, GameOver.endStatus.Killed);
    }

    public GameState escapeWithPod() {
        if (quests.isReactorOnBoard()) {
            game.addAlert(Alert.ReactorDestroyed);
            quests.lostReactor();
        }
        if (quests.isAntidoteOnBoard()) {
            game.addAlert(Alert.AntidoteDestroyed);
            quests.lostAntidote();
        }
        if (quests.isArtifactOnBoard()) {
            game.addAlert(Alert.ArtifactNotSaved);
            quests.lostArtifact();
        }
        if (quests.isJarekOnBoard()) {
            game.addAlert(Alert.JarekTakenHome);
            quests.lostJarek();
        }
        if (quests.isWildOnBoard()) {
            game.addAlert(Alert.WildArrested);
            captain.policeRecord.caughtWithWild();
            game.getNews().addNotableEvent(News.NotableEvent.WildArrested);
            quests.lostWild();
        }
        if (game.getShip().getTribbles() > 0) {
            game.addAlert(Alert.TribbleSurvived);
            // unnecessary to set ship.tribbles = 0 I think
        }
        if (captain.bank.hasInsurance()) {
            game.addAlert(Alert.InsurancePays);
            int payout = ship.getPriceWithoutCargo(true, true);
            captain.addCredits(payout);
        }

        game.addAlert(Alert.FleaBuilt);

        captain.subtractCredits(500); // Normally it costs 2000 for a flea. Hmm.

        game.dayPasses();
        game.dayPasses();
        game.dayPasses();

        PlayerShip flea = new PlayerShip(ShipType.Flea, captain, quests, difficulty);

        game.setShip(flea);

        return new InSystem(game, newSystem);
    }
}
