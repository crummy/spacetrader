package com.malcolmcrum.spacetrader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 8/29/2015.
 */
public class Ship {
    ShipType type;
    List<TradeItem> cargo;
    List<Gadget> gadgets;
    List<Weapon> weapons;
    List<Shield> shields;
    List<Integer> shieldStrength;
    List<Crew> crew;
    private int fuel;
    private int hullStrength;
    private int tribbles;


    public Ship(ShipType type) {
        this.type = type;
        initShip();
    }

    private void initShip() {
        cargo = new ArrayList<>(type.getCargoBays());
        weapons = new ArrayList<>(type.getWeaponSlots());
        gadgets = new ArrayList<>(type.getGadgetSlots());
        shields = new ArrayList<>(type.getShieldSlots());
        shieldStrength = new ArrayList<>(type.getShieldSlots());
        crew = new ArrayList<>(type.getCrewQuarters());
        fuel = type.getFuelTanks();
        hullStrength = type.getHullStrength();
        tribbles = 0;
    }

    public void addCrew(Crew member) {
        crew.add(member);
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }
}
