package com.malcolmcrum.spacetrader;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Game {
    Galaxy galaxy;
    Commander commander;
    Ship ship;

    public void startNewGame(String commanderName) {
        if (commanderName == null || commanderName.length() == 0) {
            commanderName = "Shelby";
        }

        galaxy = new Galaxy(Difficulty.Beginner);
        ship = new Ship(ShipType.Gnat);
        ship.addWeapon(Weapon.PulseLaser);
        ship.addCrew(Crew.Captain);
        commander = new Commander(commanderName, galaxy.getStartSystem(ship.type));
    }


}
