package com.malcolmcrum.spacetrader.states

import com.malcolmcrum.spacetrader.model.*
import com.malcolmcrum.spacetrader.model.TradeItem.*
import com.malcolmcrum.spacetrader.nouns.Ship
import com.malcolmcrum.spacetrader.nouns.random

class TravelController(private val travel: GameState.Travel,
                       private val policeRecord: PoliceRecord,
                       private val reputation: Int,
                       private val difficulty: Difficulty,
                       private val playerShip: Ship,
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

        if (playerShip.type == ShipType.FLEA) {
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
        return when {
            playerShip.isCloaked -> GameState.TraderIgnore(opponent, travel)
            // If trader is cloaked, they never attempt to contact player https://github.com/videogamepreservation/spacetrader/blob/64aec5d376679c0a9d1ca50f19af2951d33ea87c/Src/Traveler.c#L2075
            opponent.isCloaked -> GameState.Travel(destination, travel.clicksLeft - 1)
            // If you terrify them, they may flee
            policeRecord.scaresTraders && traderFlees(opponent.type) -> GameState.TraderFlee(opponent, travel)
            playerShip.hasFreeCargoBay && traderCanSellItems(opponent) -> GameState.TraderSell(opponent, travel)
            traderCanBuyItems(playerShip) -> GameState.TraderBuy(opponent, travel)
        }
    }

    // Determine if ship has goods that can be traded.
    // Note the original claimed to take destination system into account but did not, I suspect this is a bug:
    // https://github.com/videogamepreservation/spacetrader/blob/64aec5d376679c0a9d1ca50f19af2951d33ea87c/Src/Cargo.c#L199
    private fun traderCanSellItems(traderShip: Ship): Boolean {
        return values().any { item ->
            val hasItem = traderShip.hold.count(item) > 0
            val systemSellsItem = travel.destination.market.basePrices[item]!! > 0
            // Criminals can only buy illegal goods. Noncriminals cannot buy illegal goods.
            val allowedToBuy = policeRecord.mustBuyIllegalGoods == (item == FIREARMS || item == NARCOTICS)
            return hasItem && systemSellsItem && allowedToBuy
        }
    }

    private fun traderCanBuyItems(playerShip: Ship): Boolean {
        return values().any { item ->
            val hasItem = playerShip.hold.count(item) > 0
            val systemSellsItem = travel.destination.market.basePrices[item]!! > 0
            // Criminals can only buy illegal goods. Noncriminals cannot buy illegal goods.
            val allowedToBuy = policeRecord.mustBuyIllegalGoods == (item == FIREARMS || item == NARCOTICS)
            return hasItem && systemSellsItem && allowedToBuy
        }
    }

    private fun traderFlees(traderShip: ShipType): Boolean {
        return (0..Reputation.ELITE.score).random() <= (reputation * 10) / (1 + traderShip.ordinal)
    }
}
