package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.TradeItem;
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

    private boolean isBuying;
    private TradeItem item;
    private int price;

    public Trader(Game game, Transit transit) {
        super(game, transit);
        isBuying = GetRandom(1) == 1;
        item = getRandomItemForTrade();
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
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    public String getTitle() {
        return "trader ship";
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
    public TradeItem getRandomItemForTrade() {
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
        for (int i = 0; i < TradeItem.values().length; ++i) {
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
}
