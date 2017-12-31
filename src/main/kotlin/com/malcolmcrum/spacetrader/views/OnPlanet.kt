package com.malcolmcrum.spacetrader.views

import com.malcolmcrum.spacetrader.controllers.MarketController
import com.malcolmcrum.spacetrader.model.Difficulty
import com.malcolmcrum.spacetrader.model.ShipType
import com.malcolmcrum.spacetrader.model.SolarSystem
import com.malcolmcrum.spacetrader.model.TradeItem
import com.malcolmcrum.spacetrader.nouns.Cargo
import com.malcolmcrum.spacetrader.nouns.GameState
import com.malcolmcrum.spacetrader.nouns.Player

class OnPlanet(val system: SolarSystem, private val player: Player, private val difficulty: Difficulty) : GameState {
    val market = MarketController(system.market, system, difficulty)
    val shipyard = Shipyard(system)

    fun repairShip(amount: Int) {
        val newHullLeft = player.hullLeft + amount
        if (newHullLeft > player.ship.hullStrength) {
            throw Exception("Adding $amount to hull leaves us with greater hull strength than our ship can take")
        }
        val repairCost = amount * player.ship.repairCosts
        if (! player.finances.canAfford(repairCost)) {
            throw Exception("Cannot afford $repairCost of repairs")
        }
        player.hullLeft = newHullLeft
        player.finances.spend(repairCost)
    }

    fun refuelShip(amount: Int) {
        val newFuel = player.fuelLeft + amount
        if (newFuel > player.ship.fuelTanks) {
            throw Exception("Adding $amount to fuel leaves us with more fuel than we can carry")
        }
        val fuelCost = amount * player.ship.costOfFuel
        if (! player.finances.canAfford(fuelCost)) {
            throw Exception("Cannot afford $fuelCost of fuel")
        }
        player.fuelLeft = newFuel
    }

    fun buyEscapePod() {
        if (player.hasEscapePod) {
            throw Exception("Player already has escape pod")
        }
        if (! shipyard.escapePodAvailable) {
            throw Exception("No escape pod available")
        }
        val escapePodCost = 2000
        if (! player.finances.canAfford(escapePodCost)) {
            throw Exception("Cannot afford $escapePodCost")
        }
        player.hasEscapePod = true
    }

    fun buyShip(type: ShipType) {
        // TODO
    }

    fun buyTradeItem(item: TradeItem, amount: Int) {
        val buyPrice = market.getBuyPrice(item, player.traderSkill(), player.policeRecordScore)
        val cost = buyPrice * amount
        if (! player.finances.canAfford(cost)) {
            throw Exception("Cannot afford $cost of $item")
        }
        val freeCargoSpace = player.ship.cargoBays - player.cargo.size
        if (amount > freeCargoSpace) {
            throw Exception("No room for $amount of $item")
        }
        market.remove(item, amount)
        repeat(amount) {
            player.cargo.add(Cargo(item, buyPrice))
        }
    }

    fun warp(destination: SolarSystem): GameState {
        // TODO: Ensure destination is in range

        payCosts(destination)

        player.rechargeShields()

        val travelViaWormhole = system.hasWormholeTo(destination)
        if (travelViaWormhole) { // TODO: or viaSingularity
            // TODO: special handling
            var distance = 0
        } else {
            var distance = system.distanceTo(destination)
            player.fuelLeft -= distance // TODO: or min(distance, getFuel())?
        }

        // TODO: if !viaSingularity:
        player.dayPasses()

        // TODO: travel
        return OnPlanet(destination, player, difficulty)
    }

    private fun payCosts(destination: SolarSystem) {
        // TODO: if wild, weapon check
        if (player.tooMuchDebtToWarp()) {
            throw Exception("Debt ${player.finances.debt} too great to warp")
        }
        val crewCosts = player.crewCost()
        if (! player.finances.canAfford(crewCosts)) {
            throw Exception("Cannot afford to pay $crewCosts to mercenaries")
        }
        val insuranceCost = player.getInsuranceCost()
        if (!player.finances.canAfford(insuranceCost + crewCosts)) {
            throw Exception("Cannot afford to pay $insuranceCost for insurance and $crewCosts to mercenaries")
        }
        val wormholeCost = wormholeCost(system, destination)
        if (!player.finances.canAfford(insuranceCost + crewCosts + wormholeCost)) {
            throw Exception("Cannot afford to pay $wormholeCost for wormhole")
        }
        // TODO: skip costs if !viaSingularity
        player.finances.spend(wormholeCost)
        player.finances.spend(insuranceCost)
        player.finances.spend(wormholeCost)
        player.finances.payInterest()
    }

    private fun wormholeCost(system: SolarSystem, destination: SolarSystem): Int {
        val travelViaWormhole = system.hasWormholeTo(destination)
        return if (travelViaWormhole) player.ship.costOfFuel * 25 else 0
    }

}