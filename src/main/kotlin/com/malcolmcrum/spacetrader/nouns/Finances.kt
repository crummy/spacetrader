package com.malcolmcrum.spacetrader.nouns

data class Finances(var credits: Int,
                    var debt: Int) {
    fun spend(amount: Int) {
        credits -= amount
        assert(credits > 0)
    }

    fun payInterest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}