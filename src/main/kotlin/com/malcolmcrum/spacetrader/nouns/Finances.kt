package com.malcolmcrum.spacetrader.nouns

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

}