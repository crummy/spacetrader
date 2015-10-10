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
        if (ship.isInvisibleTo(opponent)) {
            opponentStatus = Status.Ignoring;
        } else if (captain.policeRecord.is(PoliceRecord.Status.Dubious)) {
            if (opponent.weaponStrength() == 0) {
                if (opponent.isInvisibleTo(ship)) {
                    opponentStatus = Status.Ignoring;
                } else {
                    opponentStatus = Status.Fleeing;
                }
            }
            if (!captain.reputation.is(Reputation.Status.Average)) {
                opponentStatus = Status.Attacking;
            } else if (GetRandom(captain.reputation.getEliteScore()) > (captain.reputation.getScore() / (1 + opponent.getType().ordinal()))) {
                opponentStatus = Status.Attacking;
            } else if (opponent.isInvisibleTo(ship)) {
                opponentStatus = Status.Ignoring;
            } else {
                opponentStatus = Status.Fleeing;
            }
        } else if (!captain.policeRecord.is(PoliceRecord.Status.Dubious)
                && !captain.policeRecord.is(PoliceRecord.Status.Clean)
                && transit.hasBeenInspected()) {
            opponentStatus = Status.Awake;
            transit.policeInspectedPlayer();
        } else if (!captain.policeRecord.is(PoliceRecord.Status.Lawful)) {
            if (GetRandom(12 - difficulty.value) < 1 && !transit.hasBeenInspected()) {
                opponentStatus = Status.Awake;
                transit.policeInspectedPlayer();
            }
        } else {
            if (GetRandom(40) == 1 && !transit.hasBeenInspected()) {
                opponentStatus = Status.Awake;
                transit.policeInspectedPlayer();
            }
        }

        // Police don't flee if your ship is weaker.
        if (opponentStatus == Status.Fleeing && opponent.getType().ordinal() > ship.getType().ordinal()) {
            if (captain.policeRecord.is(PoliceRecord.Status.Dubious)) {
                opponentStatus = Status.Attacking;
            } else {
                opponentStatus = Status.Awake;
            }
        }
    }

    @Override
    public GameState init() {
        if (opponentStatus == Status.Ignoring && opponent.isInvisibleTo(ship)) {
            return transit;
        }
        return this;
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
        int drugs = ship.getCargoCount(TradeItem.Narcotics);
        int guns = ship.getCargoCount(TradeItem.Firearms);
        if (drugs > 0 || guns > 0) {
            // caught with illegal goods
            game.addAlert(Alert.FinedAndLostCargo);
            ship.removeCargo(TradeItem.Narcotics, drugs);
            ship.removeCargo(TradeItem.Firearms, guns);
            captain.subtractCredits(getIllegalGoodsFine());
            captain.policeRecord.caughtTrafficking();
        } else if (!quests.isWildOnBoard()) {
            // no illegal goods
            game.addAlert(Alert.NoIllegalGoods);
            captain.policeRecord.passedInspection();
        }
        if (quests.isWildOnBoard()) {
            arrestPlayer();
            return new InSystem(game, transit.getDestination());
        }
        if (quests.isReactorOnBoard()) {
            game.addAlert(Alert.PoliceConfiscateReactorAlert);
            quests.lostReactor();
        }
        return transit;
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
        if (!game.getCaptain().policeRecord.is(PoliceRecord.Status.Criminal)) {
            game.addAlert(Alert.AttackedPoliceNowCriminal);
            game.getCaptain().policeRecord.make(PoliceRecord.Status.Criminal);
        }
        game.getCaptain().policeRecord.attackedPolice();
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
        if (game.getCaptain().policeRecord.is(PoliceRecord.Status.Criminal)) {
            return "The police hail they want you to surrender.";
        } else {
            return "The police summon you to submit to an inspection.";
        }
    }

    @Override
    public GameState actionFlee() throws InvalidPlayerAction, InvalidOpponentAction {
        boolean hasNarcotics = game.getShip().getCargoCount(TradeItem.Narcotics) > 0;
        boolean hasFirearms = game.getShip().getCargoCount(TradeItem.Firearms) > 0;
        if (!hasNarcotics && !hasFirearms && !quests.isWildOnBoard() && !quests.isReactorOnBoard()) {
            game.addAlert(Alert.SureToFleeOrBribe);
        }

        opponentStatus = Status.Attacking;
        captain.policeRecord.fledPolice();

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
            if (ship.getHullStrength() < ship.getFullHullStrength() >> 1) {
                if (GetRandom(10) > 5) {
                    opponentStatus = Status.Fleeing;
                }
            } else {
                opponentStatus = Status.Fleeing;
            }
        }
    }

    public GameState actionSurrender() {
        if (captain.policeRecord.is(PoliceRecord.Status.Psychopath)) {
            game.addAlert(Alert.NoSurrender);
            return this;
        }

        if (quests.isWildOnBoard()) {
            game.addAlert(Alert.SurrenderWithWild);
        }
        if (quests.isReactorOnBoard()) {
            game.addAlert(Alert.SurrenderWithReactor);
        }

        arrestPlayer();
        return new InSystem(game, transit.getDestination());
    }

    public GameState actionBribe() {
        if (transit.getDestination().getPolitics().getBribeLevel() == BribeLevel.Impossible) {
            game.addAlert(Alert.BribeNotPossible);
            return this;
        } else if (captain.getCredits() < getBribeCost()) {
            game.addAlert(Alert.NoMoneyForBribe);
            return this;
        } else {
            captain.subtractCredits(getBribeCost());
            return transit;
        }
    }

    protected void arrestPlayer() {
        int policeRecordScore = captain.policeRecord.getScore();
        int fine = (1 + (((captain.getWorth() * Math.min(80, -policeRecordScore)) / 100) / 500)) * 500;
        if (quests.isWildOnBoard()) {
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

        if (captain.bank.hasInsurance()) {
            game.addAlert(Alert.InsuranceLost);
            captain.bank.cancelInsurance();
        }

        if (game.getShip().getMercenaryCount() > 0) {
            game.addAlert(Alert.MercenariesLeave);
            game.getShip().removeAllMercenaries();
        }

        if (quests.isAntidoteOnBoard()) {
            game.addAlert(Alert.AntidoteRemoved);
            quests.cancelJapori();
        }

        if (quests.isJarekOnBoard()) {
            game.addAlert(Alert.JarekTakenHome);
            quests.jarekLeft();
        }

        if (quests.isWildOnBoard()) {
            game.addAlert(Alert.WildArrested);
            game.getNews().addNotableEvent(News.NotableEvent.WildArrested);
            quests.wildArrested();
        }

        if (quests.isReactorOnBoard()) {
            game.addAlert(Alert.PoliceConfiscateReactorAlert);
            quests.lostReactor();
        }

        int credits = captain.getCredits();
        if (credits >= fine) {
            captain.subtractCredits(fine);
        } else {
            captain.subtractCredits(credits);
            game.addAlert(Alert.ShipSold);
            if (ship.getTribbles() > 0) {
                game.addAlert(Alert.TribblesSold);
            }
            game.addAlert(Alert.FleaRecieved);
            game.setShip(new PlayerShip(ShipType.Flea, quests, difficulty));
        }

        captain.policeRecord.make(PoliceRecord.Status.Dubious);

        credits = captain.getCredits();
        int debt = captain.bank.getDebt();
        if (debt > 0) {
            if (credits >= debt) {
                captain.subtractCredits(debt);
                captain.bank.setDebt(0);
            } else {
                captain.bank.setDebt(debt - credits);
                captain.subtractCredits(credits);
            }
        }

        for (int i = 0; i < imprisonment; ++i) {
            game.dayPasses();
        }
    }

    // The police will try to hunt you down with better ships if you are
    // a villain, and they will try even harder when you are considered to
    // be a psychopath (or are transporting Jonathan Wild)
    @Override
    protected int getShipTypeTries() {
        if (captain.policeRecord.is(PoliceRecord.Status.Villain) && !quests.isWildOnBoard()) {
            return 3;
        } else if (captain.policeRecord.is(PoliceRecord.Status.Psychopath) || quests.isWildOnBoard()) {
            return 5;
        } else {
            return super.getShipTypeTries();
        }
    }

    @Override
    protected boolean shipTypeAcceptable(ShipType betterShip) {
        int difficultyValue = difficulty.value;
        int normal = Difficulty.Normal.ordinal();
        if (betterShip.getMinStrengthForPirateEncounter() == null) {
            return false;
        }
        int shipLevel = betterShip.getMinStrengthForPoliceEncounter().getStrength();
        int difficultyModifier = (difficulty == Difficulty.Hard || difficulty == Difficulty.Impossible) ? difficultyValue - normal : 0;
        int destinationRequirement = transit.getDestination().getPoliceStrength().getStrength();
        return destinationRequirement + difficultyModifier >= shipLevel;
    }

    @Override
    protected int getCargoToGenerate() {
        return 0; // I'm doing this for consistency with the original code, but it doesn't do
                  // anything really - it gets replaced with 1 a bit later in Encounter.addCargo().
    }

    public int getBribeCost() {
        int difficultyModifier = Difficulty.Impossible.value - difficulty.value;
        int bribeDifficultyModifier = transit.getDestination().getPolitics().getBribeLevel().value;
        int bribe = captain.getWorth() / ((10 + 5 * difficultyModifier) * bribeDifficultyModifier);
        if (bribe % 100 != 0) {
            bribe += (100 - (bribe % 100));
        }

        if (quests.isWildOnBoard() || quests.isReactorOnBoard()) {
            if (difficulty == Difficulty.Hard || difficulty == Difficulty.Impossible) {
                bribe *= 3;
            } else {
                bribe *= 2;
            }
        }
        bribe = Utils.Clamp(bribe, 100, 10000);

        return bribe;
    }

    public int getIllegalGoodsFine() {
        int difficultyModifier = Difficulty.Impossible.value + 2 - difficulty.value;
        int fine = captain.getWorth() / (difficultyModifier * 10);
        if (fine % 50 != 0) {
            fine += (50 - (fine % 50));
        }
        fine = Utils.Clamp(fine, 100, 10000);
        return fine;
    }
}
