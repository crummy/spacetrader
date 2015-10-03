package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Malcolm on 10/3/2015.
 */
public class Bank {
    private static final Logger logger = LoggerFactory.getLogger(Bank.class);

    private static final int DEBT_TOO_LARGE = 100000;

    private int debt;
    private boolean hasInsurance;
    private int daysWithoutClaim;
    private boolean reserveMoney;
    private final Captain captain;
    private final Game game;

    public Bank(Captain captain, Game game) {
        this.captain = captain;
        this.game = game;
        hasInsurance = false;
        reserveMoney = false;
        debt = 0;
        daysWithoutClaim = 0;
    }

    public int getAvailableCash() {
        if (!reserveMoney) {
            return captain.getCredits();
        } else {
            return Math.max(0, captain.getCredits() - game.getShip().getMercenaryDailyCost() - getInsuranceCost());
        }
    }

    public int maxLoan() {
        if (captain.policeRecord.is(PoliceRecord.Status.Clean)) {
            return Math.min(25000, Math.max(1000, ((captain.getWorth() / 10) / 500 * 500)));
        } else {
            return 500;
        }
    }

    public void getLoan(int loan) {
        if (loan > maxLoan()) {
            logger.error("Trying to get a larger loan than maxLoan()");
        }
        int amount = Math.min(maxLoan() - debt, loan);
        captain.addCredits(amount);
        debt += amount;
    }

    public void payBack(int cash) {
        if (cash > debt) {
            logger.error("Trying to pay back more than we owe!");
        }
        if (cash > captain.getCredits()) {
            logger.error("Trying to pay back more than we have!");
        }
        int amount = Math.min(debt, cash);
        amount = Math.min(amount, captain.getCredits());
        captain.subtractCredits(amount);
        debt -= amount;
    }

    public boolean hasInsurance() {
        return hasInsurance;
    }

    public void addDebt(int debt) {
        this.debt += debt;
    }

    public int getDebt() {
        return debt;
    }

    public boolean veryLargeDebt() {
        return debt > DEBT_TOO_LARGE;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public void payInterest() {
        if (debt > 0) {
            int additionalDebt = Math.max(1, debt/10);
            if (captain.getCredits() > additionalDebt) {
                captain.subtractCredits(additionalDebt);
            } else {
                debt += (additionalDebt - captain.getCredits());
                captain.setCredits(0);
            }
        }
    }

    public void cancelInsurance() {
        hasInsurance = false;
        daysWithoutClaim = 0;
    }

    public int getInsuranceCost() {
        if (hasInsurance) {
            return 0;
        } else {
            return Math.max(1, (((game.getShip().getPriceWithoutCargo(true, true) * 5) / 2000) *
                    (100-Math.min(daysWithoutClaim, 90)) / 100));
        }
    }

    public void incrementNoClaim() {
        daysWithoutClaim++;
    }

    public int getDaysWithoutClaim() {
        return daysWithoutClaim;
    }

    public void setInsurance(boolean insurance) {
        this.hasInsurance = insurance;
    }
}