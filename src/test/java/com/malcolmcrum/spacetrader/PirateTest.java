package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.Encounters.Pirate;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by crummy on 25.09.15.
 */
public class PirateTest extends GameStateTest {

	Transit transit;

	@Before
	public void setUp() {
		super.setUp();
		transit = new Transit(game, game.getGalaxy().getRandomSystem(), false);
	}

	@Test
	public void testDestroyPirate() throws InvocationTargetException, IllegalAccessException {
		Pirate encounter = new Pirate(game, transit);
		game.getShip().addWeapon(Weapon.MilitaryLaser);
		game.getShip().addShield(ShieldType.LightningShield);

		Method attack = findMethodNamed("actionAttack", encounter.getActions());

		GameState currentState = encounter;
		int attacks = 0;
		while (currentState == encounter) {
			++attacks;
			if (attacks == 100) break;
			currentState = (GameState)attack.invoke(encounter);
			System.out.println("Ship health: " + game.getShip().getHullStrength()
					+ ", enemy health: " + encounter.getOpponent().getHullStrength());
		}
		System.out.println("state following attack: " + currentState.getClass());
		assertTrue(attacks < 100);
	}

}
