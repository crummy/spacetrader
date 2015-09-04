package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class PoliceEncounter extends Encounter {
    PoliceEncounter(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    void initialAttack() {
        super.initialAttack();

        if (game.getShip().getCargoCount(TradeItem.Narcotics) > 0
                && game.getShip().getCargoCount(TradeItem.Firearms) > 0
                && opponentStatus == Status.Awake) {
            game.addAlert(Alert.SureToFleeOrBribe);
        }
        if (!game.getCaptain().isCriminal()) {
            game.addAlert(Alert.AttackedPoliceNowCriminal);
            game.getCaptain().makeCriminal();
        }
        game.getCaptain().attackedPolice();
    }

    @Override
    String getString() {
        return null;
    }

    @Override
    GameState init() {
        return this;
    }
}
