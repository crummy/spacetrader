package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class ShipDestroyed extends GameState {
    SolarSystem newSystem; // system we wake up on

    ShipDestroyed(Game game, SolarSystem newSystem) {
        super(game);
        this.newSystem = newSystem;
    }

    @Override
    GameState init() {
        return this;
    }

    public GameState gameOver() {
        game.addAlert(Alert.GameOver);
        return new GameOver(game);
    }

    public GameState escape() {
        if (game.getReactorStatus() != Reactor.Unavailable
                && game.getReactorStatus() != Reactor.Delivered) {
            game.addAlert(Alert.ReactorDestroyed);
            game.setReactorStatus(Reactor.Unavailable);
        }
        if (game.getJaporiDiseaseStatus() == Japori.GoToJapori) {
            game.addAlert(Alert.AntidoteDestroyed);
            game.setJaporiDiseaseStatus(Japori.NoDisease);
        }
        if (game.getArtifactStatus()) {
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
        if (game.getCurrentShip().getTribbles() > 0) {
            game.addAlert(Alert.TribbleSurvived);
            // unnecessary to set ship.tribbles = 0 I think
        }
        if (game.getCurrentShip().isInsured()) {
            game.addAlert(Alert.InsurancePays);
            int payout = game.getCurrentShip().getPriceWithoutCargo(true);
            game.getCaptain().addCredits(payout);
        }

        game.addAlert(Alert.FleaBuilt);

        game.getCaptain().subtractCredits(500);

        game.dayPasses();
        game.dayPasses();
        game.dayPasses();

        Ship flea = new Ship(ShipType.Flea, game);
        flea.setEscapePod(false);

        game.setShip(flea);

        return new InSystem(game, newSystem);
    }
}
