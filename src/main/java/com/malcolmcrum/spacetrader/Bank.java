package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Malcolm on 9/11/2015.
 */
public class Bank {
    private static final Logger logger = LoggerFactory.getLogger(Bank.class);
    private static final int DEBT_TOO_LARGE = 100000;

    private Game game;
    private int debt;
    private boolean hasInsurance;
    private int daysWithoutClaim;

    public Bank(Game game) {
        this.game = game;
        hasInsurance = false;
        debt = 0;
        daysWithoutClaim = 0;
    }

    public int maxLoan() {
        if (game.getCaptain().isClean()) {
            return Math.min(25000, Math.max(1000, ((game.getCaptain().getWorth() / 10) / 500 * 500)));
        } else {
            return 500;
        }
    }

    public void getLoan(int loan) {
        if (loan > maxLoan()) {
            logger.error("Trying to get a larger loan than maxLoan()");
        }
        int amount = Math.min(maxLoan() - debt, loan);
        game.getCaptain().addCredits(amount);
        debt += amount;
    }

    public void payBack(int cash) {
        if (cash > debt) {
            logger.error("Trying to pay back more than we owe!");
        }
        if (cash > game.getCaptain().getCredits()) {
            logger.error("Trying to pay back more than we have!");
        }
        int amount = Math.min(debt, cash);
        amount = Math.min(amount, game.getCaptain().getCredits());
        game.getCaptain().subtractCredits(amount);
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
            if (game.getCaptain().getCredits() > additionalDebt) {
                game.getCaptain().subtractCredits(additionalDebt);
            } else {
                debt += (additionalDebt - game.getCaptain().getCredits());
                game.getCaptain().setCredits(0);
            }
        }
    }

    public void cancelInsurance() {
        hasInsurance = false;
        daysWithoutClaim = 0;
    }
}
