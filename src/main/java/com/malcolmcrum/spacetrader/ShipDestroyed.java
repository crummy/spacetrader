package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/2/2015.
 */
public class ShipDestroyed extends GameState {
    ShipDestroyed(Game game) {
        super(game);
    }

    @Override
    GameState init() {
        if (game.getReactorStatus() != Reactor.Unavailable
                && game.getReactorStatus() != Reactor.Delivered) {
            game.addAlert(Alert.ReactorDestroyed);
            game.setReactorStatus(Reactor.Unavailable);
        }
        if (game.getJaporiDiseaseStatus() == Japori.GoToJapori) {
            game.addAlert(Alert.AntidoteDestroyed);
            game.setJaporiDiseaseStatus(Japori.NoDisease);
        }
        if (game.getArtifactStatus() == true) {
            game.addAlert(Alert.ArtifactNotSaved);
            game.setArtifactOnBoard(false);
        }
        if (game.getJarekStatus() == Jarek.OnBoard) {
            game.addAlert(Alert.JarekTakenHome);
            game.setJarekStatus(Jarek.Unavailable);
        }
    }
}
