package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.malcolmcrum.spacetrader.Utils.GetRandom;

/**
 * Created by Malcolm on 9/3/2015.
 */
public class Market {
    private static final Logger logger = LoggerFactory.getLogger(Market.class);

    private final Game game;
    private final SolarSystem system;
    private final Map<TradeItem, Integer> sellPrices;
    private final Map<TradeItem, Integer> buyPrices;
    private final Map<TradeItem, Integer> quantities;
    private int tradeResetCountdown;

    public Market(Game game, SolarSystem system) {
        this.game = game;
        this.system = system;

        sellPrices = new HashMap<>();
        buyPrices = new HashMap<>();
        quantities = new HashMap<>();
        tradeResetCountdown = 0;
        initializeQuantities();
        determinePrices();
    }

    public void initializeQuantities() {
        for (TradeItem item : TradeItem.values()) {
            boolean bannedItem = (item == TradeItem.Narcotics && narcoticsOK()) ||
                    (item == TradeItem.Firearms && firearmsOK());
            boolean itemTooAdvanced = item.getTechLevelRequiredForProduction().isBeyond(techLevel());

            if (bannedItem || itemTooAdvanced) {
                quantities.put(item, 0);
                continue;
            }

            Integer quantity = ((9 + GetRandom(5)) - techLevel().erasBetween(item.getTechLevelForTopProduction())) * (1 + size().getMultiplier());

            // Cap robots and narcotics due to potential for easy profits
            if (item == TradeItem.Robots || item == TradeItem.Narcotics) {
                int difficultyValue = game.getDifficulty().getValue();
                quantity = ((quantity * (5 - difficultyValue)) / (6 - difficultyValue)) + 1;
            }

            if (item.getCheapResourceTrigger() != SolarSystem.SpecialResource.Nothing
                    && specialResource() == item.getCheapResourceTrigger()) {
                quantity = (quantity * 4) / 3;
            }

            if (item.getExpensiveResourceTrigger() != SolarSystem.SpecialResource.Nothing
                    && specialResource() == item.getExpensiveResourceTrigger()) {
                quantity = (quantity * 3) >> 2;
            }

            if (item.getDoublePriceTrigger() != SolarSystem.Status.Uneventful
                    && status() == item.getDoublePriceTrigger()) {
                quantity = quantity / 5;
            }

            quantity = quantity - GetRandom(10) + GetRandom(10);
            if (quantity < 0) {
                quantity = 0;
            }

            quantities.put(item, quantity);
        }
    }

    /**
     * Called when LEAVING a system on the DESTINATION - seems odd, but it's necessary
     * because traders have to know what stuff was available at the destination
     */
    public void determinePrices() { // TODO: Consider putting this in InSystem?
        for (TradeItem item : TradeItem.values()) {
            int buyingPrice = getStandardPrice(item);
            if (buyingPrice <= 0) {
                buyPrices.put(item, 0);
            } else {
                // In case of a special status, adjust price accordingly
                if (item.getDoublePriceTrigger() == status()) {
                    buyingPrice = (buyingPrice * 3) >> 1;
                }

                // Randomize price a bit
                buyingPrice = buyingPrice + GetRandom(item.getPriceVariance()) - GetRandom(item.getPriceVariance());

                if (buyingPrice <= 0) {
                    logger.error("Buying price is <= 0!!");
                }

                buyPrices.put(item, buyingPrice);

                // Criminals have to pay off an intermediary
                if (game.getCaptain().isDubious()) {
                    sellPrices.put(item, (buyingPrice * 90) / 100);
                } else {
                    sellPrices.put(item, buyingPrice);
                }
            }
        }
        recalculateBuyPrice();
    }

    /**
     * After entering a system, the quantities of items available from a system change
     * slightly. After tradeResetCountdown reaches zero, the quantities are reset.
     * This ensures that it isn't really worth the player's time to just travel between
     * two neighbouring systems.
     * Called on every system when arriving in a system.
     */
    public void performTradeCountdown() { // TODO: Better function name?
        if (tradeResetCountdown > 0) {
            --tradeResetCountdown;
            if (tradeResetCountdown > initialTradeResetCountdown()) {
                tradeResetCountdown = initialTradeResetCountdown();
            } else if (tradeResetCountdown <= 0) {
                initializeQuantities();
            } else {
                boostQuantitiesSlightly();
            }
        }
    }

    private int initialTradeResetCountdown() {
        return 3 + game.getDifficulty().getValue();
    }

    /**
     * When a player re-visits a system, but the system's item stock has not been
     * refreshed due to tradeResetCountdown reaching 0, add a few extra items.
     */
    private void boostQuantitiesSlightly() {
        for (TradeItem item : TradeItem.values()) {
            boolean itemNotAllowed = (item == TradeItem.Narcotics && !narcoticsOK())
                    || (item == TradeItem.Firearms && !firearmsOK())
                    || (techLevel().isBefore(item.getTechLevelForTopProduction()));
            if (itemNotAllowed) {
                quantities.put(item, 0);
            } else {
                int currentQuantity = quantities.get(item);
                int newQuantity = currentQuantity + GetRandom(5) - GetRandom(5);
                if (newQuantity < 0) {
                    newQuantity = 0;
                }
                quantities.put(item, newQuantity);
            }
        }
    }

    private void recalculateBuyPrice() {
        for (TradeItem item : TradeItem.values()) {
            int buyPrice;
            if (techLevel().isBefore(item.getTechLevelRequiredForProduction())) {
                buyPrice = 0;
            } else if ((item == TradeItem.Narcotics && narcoticsOK())
                    || (item == TradeItem.Firearms && firearmsOK())) {
                buyPrice = 0;
            } else {
                int sellPrice = sellPrices.get(item);
                if (game.getCaptain().isDubious()) {
                    buyPrice = (sellPrice * 100) / 90;
                } else {
                    buyPrice = sellPrice;
                }
                // BuyPrice = SellPrice + 1 to 12% (depending on trader skill (minimum is 1, max 12))
                int traderSkill = game.getShip().getTraderSkill();
                buyPrice = (buyPrice * (103 + (Game.MAX_POINTS_PER_SKILL - traderSkill)) / 100);
                if (buyPrice <= sellPrice) {
                    buyPrice = sellPrice + 1;
                }
            }
            buyPrices.put(item, buyPrice);
        }
    }

    private int getStandardPrice(TradeItem item) {
        int price;

        if ((item == TradeItem.Narcotics && narcoticsOK())
                || (item == TradeItem.Firearms && firearmsOK())) {
            price = 0;
        } else if (techLevel().isBefore(item.getTechLevelRequiredForUsage())) {
            price = 0;
        } else {
            // Determine base price on TechLevel of system
            price = item.getPriceAtLowestTech() + (techLevel().getEra() * item.getPriceIncreasePerTechLevel());

            // If item is highly requested, increase price
            if (politics().getWantedTradeItem() == item) {
                price = (price * 4) / 3;
            }

            // High trader activity decreases price
            price = (price * (100 - (2 * politics().getTraderStrength().getStrength()))) / 100;

            // Large system = high production; decrease price
            price = (price * (100 - size().getMultiplier())) / 100;

            // Special resources modifiers
            if (item.getCheapResourceTrigger() == specialResource()) {
                price = (price * 3) / 4;
            }
            if (item.getExpensiveResourceTrigger() == specialResource()) {
                price = (price * 4) / 3;
            }
        }

        if (price < 0) {
            price = 0;
        }
        return price;
    }

    private SolarSystem.Size size() {
        return system.getSize();
    }

    private Politics politics() {
        return system.getPolitics();
    }

    private boolean narcoticsOK() {
        return system.getPolitics().getDrugsOK();
    }

    private boolean firearmsOK() {
        return system.getPolitics().getFirearmsOK();
    }

    private TechLevel techLevel() {
        return system.getTechLevel();
    }

    private SolarSystem.SpecialResource specialResource() {
        return system.getSpecialResource();
    }

    private SolarSystem.Status status() {
        return system.getStatus();
    }

    public boolean isBuying(TradeItem item) {
        for (TradeItem i : quantities.keySet()) {
            logger.error("System has item " + i + ", price " + buyPrices.get(i));
        }
        logger.error("Looking up item " + item + ": " + buyPrices.get(item));
        return buyPrices.get(item) != 0;
    }

    public boolean isSelling(TradeItem item) {
        return sellPrices.get(item) != 0;
    }

    public Integer getBuyPrice(TradeItem item) {
        return buyPrices.get(item);
    }

    public Integer getSellPrice(TradeItem item) {
        return buyPrices.get(item);
    }

    public int getQuantity(TradeItem item) {
        return quantities.get(item);
    }
}
