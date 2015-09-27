package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.Encounters.Encounter;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Pirate;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.PlunderState;
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
		game.getShip().getShields().clear();
		game.getShip().addShield(ShieldType.LightningShield);

		Method attack = findMethodNamed("actionAttack", encounter.getActions());

		GameState currentState = encounter;
		int attacks = 0;
		while (currentState == encounter) {
			++attacks;
			if (attacks == 100) break;
			currentState = (GameState)attack.invoke(encounter);
		}
		assertTrue(attacks < 100);
	}

	@Test
	public void testPirateSurrender() throws InvocationTargetException, IllegalAccessException {
		Pirate encounter = new Pirate(game, transit);
		game.setShip(new PlayerShip(ShipType.Beetle, game));
		game.getShip().addCrew(game.getCaptain());
		game.getShip().addWeapon(Weapon.PulseLaser);
		game.getShip().addShield(ShieldType.LightningShield);

		Method attack = findMethodNamed("actionAttack", encounter.getActions());

		int attacks = 0;
		GameState currentState = encounter;
		while (encounter.getStatus() != Encounter.Status.Surrendered) {
			++attacks;
			if (attacks == 100) break;
			game.getShip().repair(999);

			currentState = (GameState) attack.invoke(currentState);
		}

		Method plunder = findMethodNamed("actionPlunder", encounter.getActions());
		currentState = (GameState)plunder.invoke(currentState);
		assertTrue(currentState.getClass() == PlunderState.class);
	}

}
