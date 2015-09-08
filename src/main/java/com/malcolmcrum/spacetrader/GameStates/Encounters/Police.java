package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;
import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/4/2015.
 */
public class Police extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(Police.class);


    public Police(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    actions.add(Police.class.getMethod("actionAttack"));
                    actions.add(Police.class.getMethod("actionIgnore"));
                    break;
                case Awake:
                    actions.add(Police.class.getMethod("actionAttack"));
                    actions.add(Police.class.getMethod("actionFlee"));
                    actions.add(Police.class.getMethod("actionSubmit"));
                    actions.add(Police.class.getMethod("actionBribe"));
                    break;
                case Attacking:
                    actions.add(Police.class.getMethod("actionAttack"));
                    actions.add(Police.class.getMethod("actionFlee"));
                    actions.add(Police.class.getMethod("actionSurrender"));
                    break;
                case Fleeing:
                    actions.add(Police.class.getMethod("actionAttack"));
                    actions.add(Police.class.getMethod("actionIgnore"));
                    break;
                case Fled:
                    break;
                case Surrendered:
                    break;
                case Destroyed:
                    break;
            }
        } catch (NoSuchMethodException e) {
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    public GameState actionSubmit() {
        // TODO
        return this;
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a police " + ship + ".";
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
    public String getTitle() {
        return "police ship";
    }

    @Override
    protected String descriptionAwake() {
        if (game.getCaptain().isCriminal()) {
            return "The police hail they want you to surrender.";
        } else {
            return "The police summon you to submit to an inspection.";
        }
    }

    @Override
    public GameState actionFlee() throws InvalidPlayerAction, InvalidOpponentAction {
        boolean hasNarcotics = game.getShip().getCargoCount(TradeItem.Narcotics) > 0;
        boolean hasFirearms = game.getShip().getCargoCount(TradeItem.Firearms) > 0;
        boolean hasWild = game.getWildStatus() == Wild.OnBoard;
        boolean hasReactor = game.getReactorStatus() != Reactor.Unavailable && game.getReactorStatus() != Reactor.Delivered;
        if (!hasNarcotics && !hasFirearms && !hasWild && !hasReactor) {
            game.addAlert(Alert.SureToFleeOrBribe);
        }

        opponentStatus = Status.Attacking;
        game.getCaptain().fledPolice();

        return super.actionFlee();
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getCaptain().killedACop();
        return super.destroyedOpponent();
    }

    /**
     * Decide whether to change tactics (e.g. flee)
     */
    protected void opponentHasBeenDamaged() {
        if (opponent.getHullStrength() < opponent.getFullHullStrength() >> 1) {
            if (game.getShip().getHullStrength() < game.getShip().getFullHullStrength() >> 1) {
                if (GetRandom(10) > 5) {
                    opponentStatus = Status.Fleeing;
                }
            } else {
                opponentStatus = Status.Fleeing;
            }
        }
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
        return new InSystem(game, transit.getDestination());
    }

    public GameState actionBribe() {
        // TODO;
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
