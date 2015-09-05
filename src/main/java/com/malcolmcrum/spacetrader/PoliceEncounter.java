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

    private void arrestPlayer() {
        int policeRecordScore = game.getCaptain().getPoliceRecordScore();
        int fine = (1 + (((game.getCaptain().getWorth() * Math.min(80, -policeRecordScore)) / 100) / 500)) * 500;
        if (game.getWildStatus() == Wild.OnBoard) {
            fine *= 1.05;
        }

        int imprisonment = Math.max(30, -policeRecordScore);

        game.addAlert(Alert.Arrested);

        // TODO: conviction form

        int narcoticsOnBoard = game.getShip().getCargoCount(TradeItem.Narcotics);
        int firearmsOnBoard = game.getShip().getCargoCount(TradeItem.Firearms);
        if (narcoticsOnBoard > 0 || firearmsOnBoard > 0) {
            game.addAlert(Alert.Impound);
            game.getShip().removeCargo(TradeItem.Narcotics, narcoticsOnBoard);
            game.getShip().removeCargo(TradeItem.Firearms, firearmsOnBoard);
        }

        if (game.getShip().isInsured()) {
            game.addAlert(Alert.InsuranceLost);
            game.getShip().cancelInsurance();
        }

        if (game.getShip().getMercenaryCount() > 0) {
            game.addAlert(Alert.MercenariesLeave);
            game.getShip().removeAllMercenaries();
        }

        if (game.getJaporiDiseaseStatus() == Japori.GoToJapori) {
            game.addAlert(Alert.AntidoteRemoved);
            game.setJaporiDiseaseStatus(Japori.FinishedOrCancelled);
        }

        if (game.getJarekStatus() == Jarek.OnBoard) {
            game.addAlert(Alert.JarekTakenHome);
            game.setJarekStatus(Jarek.Unavailable);
        }

        if (game.getWildStatus() == Wild.OnBoard) {
            game.addAlert(Alert.WildArrested);
            game.getNews().addNotableEvent(News.NotableEvent.WildArrested);
            game.setWildStatus(Wild.Unavailable);
        }

        if (game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered) {
            game.addAlert(Alert.PoliceConfiscateReactorAlert);
            game.setReactorStatus(Reactor.Unavailable);
        }

        for (int i = 0; i < imprisonment; ++i) {
            game.dayPasses();
        }

        if (game.getCaptain().getCredits() >= fine) {
            game.getCaptain().subtractCredits(fine);
        } else {
            game.getCaptain().subtractCredits(game.getCaptain().getCredits());
            game.addAlert(Alert.ShipSold);
            if (game.getShip().getTribbles() > 0) {
                game.addAlert(Alert.TribblesSold);
            }
            game.addAlert(Alert.FleaRecieved);
        }
    }
}
