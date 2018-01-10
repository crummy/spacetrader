package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.PoliceRecord
import com.malcolmcrum.spacetrader.model.ShipType
import com.malcolmcrum.spacetrader.nouns.random

class TravelController(private val travel: GameState.Travel,
                       private val policeRecord: PoliceRecord,
                       private val difficulty: Difficulty,
                       private val type: ShipType,
                       private val opponentGenerator: OpponentGenerator) {
    private val destination = travel.destination
    private var raidedByPirates = false

    fun warp(): GameState {
        assert(travel.clicksLeft >= 0)
        if (travel.clicksLeft == 0) {
            // TODO: handle post-Marie looting
            return arrival()
        }
        --travel.clicksLeft

        // TODO: handle going through rip

        // TODO: random chance of repairs

        // TODO: encounter spacemonster

        // TODO: encounter scarab

        // TODO: encounter dragonfly

        // TODO: encounter mantis

        var encounterTest = (0..(44 - 2 * difficulty.ordinal)).random()

        if (type == ShipType.FLEA) {
            encounterTest *= 2
        }
        if (encounterTest < pirateStrength() && !raidedByPirates) {
            return pirateEncounter()
        } else if (encounterTest < pirateStrength() + policeStrength()) {
            return policeEncounter()
        } else if (encounterTest < pirateStrength() + policeStrength() + traderStrength()) {
            return traderEncounter()
        } else {
            // TODO: jonathan wild status

            // TODO: mantis
        }

        return warp()
    }

    private fun arrival(): GameState { // TODO: should this be in OnPlanet init?
        // TODO: handle reactor status

        // TODO: handle tribbles

        // TODO: repair a little bit

        // TODO: easter egg

        return GameState.OnPlanet(destination)
    }

    private fun traderStrength(): Int {
        return destination.politics.strengthTraders
    }

    private fun pirateStrength(): Int {
        return destination.politics.strengthPirates
    }

    private fun policeStrength(): Int {
        return destination.politics.strengthPolice * policeRecord.policeStrengthModifier()
    }

    private fun policeEncounter(): GameState {
        val opponent = opponentGenerator.generatePolice()
        return GameState.PoliceInspection(opponent, travel)
    }

    private fun pirateEncounter(): GameState {
        val opponent = opponentGenerator.generatePirate()
        TODO()
    }

    private fun traderEncounter(): GameState {
        val opponent = opponentGenerator.generateTrader()
        TODO()
    }
}
