package com.malcolmcrum.spacetrader.model

data class Finances(var credits: Int,
                    var debt: Int) {
    fun spend(amount: Int) {
        credits -= amount
        assert(credits > 0)
    }

    fun payInterest() { // TODO: this probably doesn't belong in a data class
        if (debt > 0) {
            val increment = Math.max(1, debt / 10)
            if (credits > increment) {
                credits -= increment
            } else {
                debt += (increment - credits)
                credits = 0
            }
        }
    }

    fun canAfford(price: Int): Boolean {
        return credits > price
        // TODO: ReserveMoney feature... or does it belong in the UI?
    }

}