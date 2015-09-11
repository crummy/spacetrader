package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.malcolmcrum.spacetrader.Utils.*;

/**
 * TODO: consider splitting this into traderSell and traderBuy encounters
 * Created by Malcolm on 9/4/2015.
 */
public class Trader extends Encounter {
    private static final Logger logger = LoggerFactory.getLogger(Trader.class);
    private static final int CHANCE_OF_TRADE_IN_ORBIT = 100 ;

    private boolean isBuying;
    private TradeItem item;
    private int price;

    public Trader(Game game, Transit transit) {
        super(game, transit);

        if (GetRandom(1000) < CHANCE_OF_TRADE_IN_ORBIT) {
            if (game.getShip().hasFreeCargoBay() && hasTradeableItem(opponent, transit.getDestination(), false)) {
                isBuying = true;
            } else if (hasTradeableItem(game.getShip(), transit.getDestination(), true)) {
                isBuying = false;
            } else { // Nothing suitable to trade: Ignore player
                opponentStatus = Status.Ignoring;
            }
        }

        item = randomItemForTrade();
        price = randomPriceForTrade();

        // If player is cloaked, they don't see him, and ignore
        if (game.getShip().isInvisibleTo(opponent)) {
            opponentStatus = Status.Ignoring;
        } else if (game.getCaptain().isCriminal()) {
            // If you're a criminal with significant reputation, traders tend to flee
            if (GetRandom(game.getCaptain().getEliteScore()) <= (game.getCaptain().getReputationScore() * 10) / (1 + opponent.getType().ordinal())) {
                opponentStatus = Status.Fleeing;
            }
        }
    }

    private boolean hasTradeableItem(Ship ship, SolarSystem destination, boolean isBuying) {
        for (TradeItem item : TradeItem.values()) {
            // Trade only if trader is selling and the item has a buy price on the local system
            // OR if the trader is buying and the item has a sell price on the local system
            boolean foundItem = false;
            if (ship.getCargoCount(item) > 0 && !isBuying && destination.getMarket().getBuyPrice(item) > 0) {
                foundItem = true;
            } else if (ship.getCargoCount(item) > 0 && isBuying && destination.getMarket().getSellPrice(item) > 0) {
                foundItem = true;
            }

            // Criminals can only buy or sell illegal goods, noncriminals cannot buy or sell illegal goods.
            if (foundItem && game.getCaptain().isDubious()
                    && (item == TradeItem.Narcotics || item == TradeItem.Firearms)) {
                return true;
            } else if (foundItem && !game.getCaptain().isDubious()
                    && item != TradeItem.Narcotics && item != TradeItem.Firearms) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GameState init() {
        if (opponentStatus == Status.Ignoring || opponentStatus == Status.Awake || opponentStatus == Status.Fleeing) {
            if (opponent.isInvisibleTo(game.getShip())) {
                return transit;
            }
        }
        return this;
    }

    private int randomPriceForTrade() {
        price = isBuying ? game.getCurrentSystem().getMarket().getSellPrice(item) : game.getCurrentSystem().getMarket().getBuyPrice(item);
        if (item == TradeItem.Narcotics || item == TradeItem.Firearms) {
            if (GetRandom(100) <= 45) {
                price *= isBuying? 0.8 : 1.1;
            } else {
                price *= isBuying? 1.1 : 0.8;
            }
        } else {
            if (GetRandom(100) <= 10) {
                price *= isBuying? 1.1 : 0.9;
            } else {
                price *= isBuying? 0.9 : 1.1;
            }
        }
        price /= item.getRoundOff();
        price *= item.getRoundOff();
        price = Clamp(price, item.getMinTradePrice(), item.getMaxTradePrice());
        return price;
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    actions.add(Trader.class.getMethod("actionAttack"));
                    actions.add(Trader.class.getMethod("actionIgnore"));
                    break;
                case Awake:
                    actions.add(Trader.class.getMethod("actionAttack"));
                    actions.add(Trader.class.getMethod("actionIgnore"));
                    actions.add(Trader.class.getMethod("actionTrade"));
                    break;
                case Attacking:
                    actions.add(Trader.class.getMethod("actionAttack"));
                    actions.add(Trader.class.getMethod("actionFlee"));
                    break;
                case Fleeing:
                    actions.add(Trader.class.getMethod("actionAttack"));
                    actions.add(Trader.class.getMethod("actionIgnore"));
                    break;
                case Fled:
                    break;
                case Surrendered:
                    actions.add(Trader.class.getMethod("actionAttack"));
                    actions.add(Trader.class.getMethod("actionPlunder"));
                    break;
                case Destroyed:
                    break;
            }
        } catch (NoSuchMethodException e) {
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    @Override
    public String getTitle() {
        return "trader ship";
    }

    public GameState actionPlunder() {
        // TODO
        return this;
    }

    public GameState actionTrade() {
        // TODO
        return this;
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter a trader " + ship + ".";
    }

    @Override
    public String descriptionAwake() {
        return "You are hailed with an offer to trade goods.";
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getCaptain().killedATrader();
        return super.destroyedOpponent();
    }

    @Override
    protected ShipType baseShipType() {
        return ShipType.Gnat;
    }

    /**
     * Decide whether to change tactics (e.g. flee)
     */
    protected void opponentHasBeenDamaged() {
        if (opponent.getHullStrength() < (opponent.getFullHullStrength() * 2) / 3) {
            if (GetRandom(10) > 3) {
                opponentStatus = Status.Surrendered;
            } else {
                opponentStatus = Status.Fleeing;
            }
        } else if (opponent.getHullStrength() < (opponent.getFullHullStrength() * 9) / 10) {
            if (game.getShip().getHullStrength() < (game.getShip().getFullHullStrength() * 2) / 3) {
                // If you get damaged a lot, the trader tends to keep fighting
                if (GetRandom(10) > 7) {
                    opponentStatus = Status.Fleeing;
                }
            } else if (game.getShip().getHullStrength() < (game.getShip().getFullHullStrength() * 9) / 10) {
                if (GetRandom(10) > 3) {
                    opponentStatus = Status.Fleeing;
                }
            } else {
                opponentStatus = Status.Fleeing;
            }
        }
    }

    /**
     * Returns a trade good for trade
     * @return The item to be traded
     */
    public TradeItem randomItemForTrade() {
        // First try to pick a random item
        for (int i = 0; i < 10; ++i) {
            TradeItem item = RandomEnum(TradeItem.class);
            // It's not as ugly as it may look! If the ship has a particular item, the following
            // conditions must be met for it to be tradeable:
            // if the trader is buying, there must be a valid sale price for that good on the local system
            // if the trader is selling, there must be a valid buy price for that good on the local system
            // if the player is criminal, the good must be illegal
            // if the player is not criminal, the good must be legal
            boolean isLegal = item != TradeItem.Firearms && item != TradeItem.Narcotics;
            boolean playerIsDubious = game.getCaptain().isDubious();
            if (isBuying) {
                boolean playerHasCargo = game.getShip().getCargoCount(item) > 0;
                boolean systemIsBuying = game.getCurrentSystem().getMarket().isBuying(item);
                if (playerHasCargo && systemIsBuying && ((playerIsDubious && !isLegal) || (!playerIsDubious && isLegal))) {
                    return item;
                }
            } else {
                boolean traderHasCargo = opponent.getCargoCount(item) > 0;
                boolean systemIsSelling = game.getCurrentSystem().getMarket().isSelling(item);
                if (traderHasCargo && systemIsSelling && ((playerIsDubious && !isLegal) || (!playerIsDubious && isLegal))) {
                    return item;
                }
            }
        }
        // if we didn't succeed in picking randomly, we'll pick sequentially. We can do this, because
        // this routine is only called if there are tradeable goods.
        for (TradeItem item : TradeItem.values()) {
            boolean isLegal = item != TradeItem.Firearms && item != TradeItem.Narcotics;
            boolean playerIsDubious = game.getCaptain().isDubious();
            if (isBuying) {
                boolean playerHasCargo = game.getShip().getCargoCount(item) > 0;
                boolean systemIsBuying = game.getCurrentSystem().getMarket().isBuying(item);
                if (playerHasCargo && systemIsBuying && ((playerIsDubious && !isLegal) || (!playerIsDubious && isLegal))) {
                    return item;
                }
            } else {
                boolean traderHasCargo = opponent.getCargoCount(item) > 0;
                boolean systemIsSelling = game.getCurrentSystem().getMarket().isSelling(item);
                if (traderHasCargo && systemIsSelling && ((playerIsDubious && !isLegal) || (!playerIsDubious && isLegal))) {
                    return item;
                }
            }
        }
        logger.error("Could not find a valid trade item for trader!");
        return null;
    }

    @Override
    protected boolean shipTypeAcceptable(ShipType betterShip) {
        int difficulty = game.getDifficulty().getValue();
        int normal = Difficulty.Normal.ordinal();
        int shipLevel = betterShip.getMinStrengthForTraderEncounter().getStrength();
        int difficultyModifier = (game.getDifficulty() == Difficulty.Hard || game.getDifficulty() == Difficulty.Impossible) ? difficulty - normal : 0;
        int destinationRequirement = transit.getDestination().getTraderStrength().getStrength();
        return destinationRequirement + difficultyModifier >= shipLevel;
    }
}
