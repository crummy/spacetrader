package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.nouns.random

class OpponentGenerator(private val player: Player, private val destination: SolarSystem) {

    // Police hunt you down harder if you are villanous or are transporting Jonathan Wild
    fun generatePolice() {
        var attempts = 1
        if (player.policeRecordScore < PoliceRecord.VILLAIN.score) { // TODO: handle wild Status
            attempts = 3
        } else if (player.policeRecordScore < PoliceRecord.PSYCHOPATH.score) {
            attempts = 5
        }
        attempts = Math.max(1, attempts + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)

        return generate(attempts, ShipType.GNAT)
    }

    // Pirates hunt you down harder if you are rich
    fun generatePirate() {
        var attempts = 1 + (player.currentWorth() / 100000)
        attempts = Math.max( 1, attempts + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)
    }

    fun generateTrader() {
        var attempts = 1
    }

    // TODO: clean the hell out of this
    private fun generate(attempts: Int, initialShipType: ShipType) {
        val k = Math.max(player.difficulty.ordinal - Difficulty.NORMAL.ordinal, 0)
        var opponentShip = initialShipType

        for (attempt in 0 until attempts) {

            var redo = true
            var i = 0
            while (redo) {

                var d = (0..100).random()
                i = 0
                var sum = ShipType.FLEA.occurrence
                while (sum < d) {
                    if (i >= ShipType.WASP.ordinal - 1) {
                        break
                    }
                    ++i
                    sum += ShipType.values()[i].occurrence
                }

                val potentialShipType = ShipType.values()[i]
                if (potentialShipType.policeLevel < 0 || destination.politics.strengthPolice + k < potentialShipType.policeLevel) {
                    continue
                } // TODO handle other types of encounters

                redo = false
            }

            if (i > initialShipType.ordinal) {
                opponentShip = ShipType.values()[i]
            }
        }

        // TODO: special handling for mantis

        // attempts = Math.max( 1, (player.currentWorth() / 150000) + player.difficulty.ordinal - Difficulty.NORMAL.ordinal)

        // TODO: add gadgets

        // TODO: add cargo bays

        // TODO: fill cargo bays

        // TODO: fill fuel tanks

        // TODO: fill weapon slots if possible

        // TODO: fill shield slots

        // TODO: handle mantis or famous captain hull strength

        // TODO: add crew
    }
}