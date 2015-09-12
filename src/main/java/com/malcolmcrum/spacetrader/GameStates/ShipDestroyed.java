package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class ShipDestroyed extends GameState {
    SolarSystem newSystem; // system we wake up on

    public ShipDestroyed(Game game, SolarSystem newSystem) {
        super(game);
        this.newSystem = newSystem;
    }

    public List<Method> getActions() {
        return new ArrayList<>();
    }

    @Override
    public GameState init() {
        if (game.getShip().hasEscapePod()) {
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
        if (game.getReactorStatus() != Reactor.Unavailable
                && game.getReactorStatus() != Reactor.Delivered) {
            game.addAlert(Alert.ReactorDestroyed);
            game.setReactorStatus(Reactor.Unavailable);
        }
        if (game.getJaporiDiseaseStatus() == Japori.GoToJapori) {
            game.addAlert(Alert.AntidoteDestroyed);
            game.setJaporiDiseaseStatus(Japori.NoDisease);
        }
        if (game.getArtifactOnBoard()) {
            game.addAlert(Alert.ArtifactNotSaved);
            game.setArtifactOnBoard(false);
        }
        if (game.getJarekStatus() == Jarek.OnBoard) {
            game.addAlert(Alert.JarekTakenHome);
            game.setJarekStatus(Jarek.Unavailable);
        }
        if (game.getWildStatus() == Wild.OnBoard) {
            game.addAlert(Alert.WildArrested);
            game.getCaptain().caughtWithWild();
            game.getNews().addNotableEvent(News.NotableEvent.WildArrested);
            game.setWildStatus(Wild.Unavailable);
        }
        if (game.getShip().getTribbles() > 0) {
            game.addAlert(Alert.TribbleSurvived);
            // unnecessary to set ship.tribbles = 0 I think
        }
        if (game.getBank().hasInsurance()) {
            game.addAlert(Alert.InsurancePays);
            int payout = game.getShip().getPriceWithoutCargo(true, true);
            game.getCaptain().addCredits(payout);
        }

        game.addAlert(Alert.FleaBuilt);

        game.getCaptain().subtractCredits(500); // Normally it costs 2000 for a flea. Hmm.

        game.dayPasses();
        game.dayPasses();
        game.dayPasses();

        PlayerShip flea = new PlayerShip(ShipType.Flea, game);

        game.setShip(flea);

        return new InSystem(game, newSystem);
    }
}
