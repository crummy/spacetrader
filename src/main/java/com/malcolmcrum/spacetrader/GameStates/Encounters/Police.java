package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class Police extends Encounter {
    Police(Game game, Transit transit) {
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
    protected void surrenderToPlayer() throws InvalidOpponentAction {
        throw new InvalidOpponentAction();
    }

    @Override
    String getString() {
        return "police";
    }

    @Override
    public GameState fleeAction() throws InvalidPlayerAction {
        boolean hasNarcotics = game.getShip().getCargoCount(TradeItem.Narcotics) > 0;
        boolean hasFirearms = game.getShip().getCargoCount(TradeItem.Firearms) > 0;
        boolean hasWild = game.getWildStatus() == Wild.OnBoard;
        boolean hasReactor = game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered;
        if (!hasNarcotics && !hasFirearms && !hasWild && !hasReactor) {
            game.addAlert(Alert.SureToFleeOrBribe);
        }

        opponentStatus = Status.Attacking;
        game.getCaptain().fledPolice();

        return super.fleeAction();
    }

    public GameState actionSurrender() {
        if (game.getCaptain().isPsychopathic()) {
            game.addAlert(Alert.NoSurrender);
            return this;
        }

        if (game.getWildStatus() == Wild.OnBoard) {
            game.addAlert(Alert.SurrenderWithWild);
        }
        if (game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered) {
            game.addAlert(Alert.SurrenderWithReactor);
        }

        arrestPlayer();
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

        int credits = game.getCaptain().getCredits();
        if (credits >= fine) {
            game.getCaptain().subtractCredits(fine);
        } else {
            game.getCaptain().subtractCredits(credits);
            game.addAlert(Alert.ShipSold);
            if (game.getShip().getTribbles() > 0) {
                game.addAlert(Alert.TribblesSold);
            }
            game.addAlert(Alert.FleaRecieved);
            game.setShip(new PlayerShip(ShipType.Flea, game));
        }

        game.getCaptain().makeDubious();

        credits = game.getCaptain().getCredits();
        int debt = game.getCaptain().getDebt();
        if (debt > 0) {
            if (credits >= debt) {
                game.getCaptain().subtractCredits(debt);
                game.getCaptain().setDebt(0);
            } else {
                game.getCaptain().setDebt(debt - credits);
                game.getCaptain().subtractCredits(credits);
            }
        }

        for (int i = 0; i < imprisonment; ++i) {
            game.dayPasses();
            game.getCaptain().payInterest();
        }
    }
}
