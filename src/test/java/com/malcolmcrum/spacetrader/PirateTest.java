package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.Encounters.Encounter;
import com.malcolmcrum.spacetrader.GameStates.Encounters.InvalidOpponentAction;
import com.malcolmcrum.spacetrader.GameStates.Encounters.InvalidPlayerAction;
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
	public void testPirateSurrender() throws InvalidOpponentAction {
		Pirate encounter = new Pirate(game, transit);
		game.setShip(new PlayerShip(ShipType.Beetle, game.getQuests(), game.getDifficulty()));
		game.getShip().addCrew(game.getCaptain());
		game.getShip().addWeapon(Weapon.PulseLaser);
		game.getShip().addShield(ShieldType.LightningShield);

		int attacks = 0;
		GameState currentState = encounter;
		while (encounter.getStatus() != Encounter.Status.Surrendered) {
			++attacks;
			if (attacks == 100) break;
			game.getShip().repair(999);

			encounter.actionAttack();
		}

		currentState = encounter.actionPlunder();
		assertTrue("plunder pirate after they surrender", currentState.getClass() == PlunderState.class);
	}

	@Test
	public void testSurrenderToPirate() {
		Pirate encounter = new Pirate(game, transit);

		game.getQuests().gotWild();
		game.getQuests().gotReactor();
		game.getShip().addCargo(TradeItem.Food, 1, 0);
		game.getShip().addCargo(TradeItem.Robots, 2, 2);
		GameState state = encounter.actionSurrender();
		assertTrue("pirates plundered food", game.getShip().getCargoCount(TradeItem.Food) == 0);
		assertTrue("pirates plundered robots", game.getShip().getCargoCount(TradeItem.Robots) == 0);
		assertTrue("wild goes with pirates if they have space", encounter.getOpponent().getCrewQuarters() == 1 || !game.getQuests().isWildOnBoard());
		assertTrue("reactor untouched", game.getQuests().isReactorOnBoard());
		assertTrue("transitioned to next state after surrender", state != encounter);

		encounter = new Pirate(game, transit);

		game.getCaptain().setCredits(1);
		int credits = game.getCaptain().getCredits();
		int debt = game.getCaptain().bank.getDebt();
		encounter.actionSurrender();
		assertTrue("can't afford blackmail? lose cash", credits > game.getCaptain().getCredits());
		assertTrue("can't afford blackmail? gain debt", debt < game.getCaptain().bank.getDebt());
	}

	@Test
	public void testFleePirate() throws InvalidPlayerAction, InvalidOpponentAction {
		Pirate pirate = new Pirate(game, transit);

		game.getShip().setHullStrength(9999);
		GameState state = pirate;
		while (state.getClass() == Pirate.class) {
			state = pirate.actionFlee();
		}
		assertTrue("escaped pirate", state.getClass() == Transit.class);
	}

}
