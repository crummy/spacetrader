package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.Encounters.*;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Dragonfly;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Monster;
import com.malcolmcrum.spacetrader.GameStates.Encounters.Scarab;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/7/2015.
 */
public class EncounterTest extends GameStateTest {
    private final static String ATTACK_ACTION = "actionAttack";
    private final static String FLEE_ACTION = "actionFlee";
    private final static String SUBMIT_ACTION = "actionSubmit";
    private final static String BRIBE_ACTION = "actionBribe";
    private final static String IGNORE_ACTION = "actionIgnore";
    private final static String SURRENDER_ACTION = "actionSurrender";
    private final static String PLUNDER_ACTION = "actionPlunder";
    private final static String DRINK_ACTION = "actionDrink";
    private final static String BOARD_ACTION = "actionBoard";
    private final static String MEET_ACTION = "actionMeet";
    private final static String YIELD_ACTION = "actionYield";
    private final static String TRADE_ACTION = "actionTrade";
    private final static String[] allActions = {ATTACK_ACTION, FLEE_ACTION,
            SUBMIT_ACTION, BRIBE_ACTION, IGNORE_ACTION, SURRENDER_ACTION,
            PLUNDER_ACTION, DRINK_ACTION, BOARD_ACTION, MEET_ACTION, YIELD_ACTION,
            TRADE_ACTION};

    @Test
    public void testPolice() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        Police police = new Police(game, transit);
        police.init();
        List<String> inspectionActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION, SUBMIT_ACTION, BRIBE_ACTION);
        checkActions(police, Encounter.Status.Awake, inspectionActions);

        List<String> fleeingActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(police, Encounter.Status.Fleeing, fleeingActions);

        List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION, SURRENDER_ACTION);
        checkActions(police, Encounter.Status.Attacking, attackingActions);

        List<String> ignoringActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(police, Encounter.Status.Ignoring, ignoringActions);
    }

    @Test
    public void testPostMariePolice() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        PostMariePolice police = new PostMariePolice(game, transit);
        police.init();
        List<String> inspectionActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION, YIELD_ACTION, BRIBE_ACTION);
        checkActions(police, Encounter.Status.Awake, inspectionActions);
    }


    @Test
    public void testTrader() {
        Transit transit = new Transit(game, game.getGalaxy().getRandomSystem(), false);

        Trader trader = new Trader(game, transit);
        trader.init();

        List<String> tradeActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION, TRADE_ACTION);
        checkActions(trader, Encounter.Status.Awake, tradeActions);

        List<String> fleeingActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(trader, Encounter.Status.Fleeing, fleeingActions);

        List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION);
        checkActions(trader, Encounter.Status.Attacking, attackingActions);

        List<String> ignoringActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(trader, Encounter.Status.Ignoring, ignoringActions);

        List<String> surrenderActions = Arrays.asList(ATTACK_ACTION, PLUNDER_ACTION);
        checkActions(trader, Encounter.Status.Surrendered, surrenderActions);

    }

    @Test
    public void testPirate() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        Pirate pirate = new Pirate(game, transit);
        pirate.init();

        List<String> fleeingActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(pirate, Encounter.Status.Fleeing, fleeingActions);

        List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION, SURRENDER_ACTION);
        checkActions(pirate, Encounter.Status.Attacking, attackingActions);

        List<String> ignoringActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(pirate, Encounter.Status.Ignoring, ignoringActions);

        List<String> surrenderActions = Arrays.asList(ATTACK_ACTION, PLUNDER_ACTION);
        checkActions(pirate, Encounter.Status.Surrendered, surrenderActions);
    }

    @Test
    public void testMonster() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        Monster monster = new Monster(game, transit);
        monster.init();
        List<String> ignoringActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(monster, Encounter.Status.Ignoring, ignoringActions);

        List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION);
        checkActions(monster, Encounter.Status.Attacking, attackingActions);
    }

    @Test
    public void testDragonfly() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        Dragonfly dragonfly = new Dragonfly(game, transit);
        dragonfly.init();
        List<String> ignoringActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(dragonfly, Encounter.Status.Ignoring, ignoringActions);

        List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION);
        checkActions(dragonfly, Encounter.Status.Attacking, attackingActions);
    }

    @Test
    public void testScarab() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        Scarab scarab = new Scarab(game, transit);
        List<String> ignoringActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION);
        checkActions(scarab, Encounter.Status.Ignoring, ignoringActions);

        List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION, SURRENDER_ACTION);
        checkActions(scarab, Encounter.Status.Attacking, attackingActions);
    }

    @Test
    public void testMarieCeleste() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        MarieCeleste marie = new MarieCeleste(game, transit);
        marie.init();
        List<String> boardActions = Arrays.asList(BOARD_ACTION, IGNORE_ACTION);
        checkActions(marie, Encounter.Status.Awake, boardActions);
    }

    @Test
    public void testBottles() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        GoodBottle good = new GoodBottle(game, transit);
        good.init();
        List<String> bottleActions = Arrays.asList(DRINK_ACTION, IGNORE_ACTION);
        checkActions(good, Encounter.Status.Awake, bottleActions);

        OldBottle old = new OldBottle(game, transit);
        old.init();
        checkActions(old, Encounter.Status.Awake, bottleActions);
    }

    @Test
    public void testFamousCaptains() {
        Transit transit = new Transit(game, game.getCurrentSystem(), false);

        List<FamousCaptain> famousCaptains = new ArrayList<>();
        famousCaptains.add(new Ahab(game, transit));
        famousCaptains.add(new Conrad(game, transit));
        famousCaptains.add(new Huie(game, transit));

        for (FamousCaptain captain : famousCaptains) {
            List<String> attackingActions = Arrays.asList(ATTACK_ACTION, FLEE_ACTION);
            checkActions(captain, Encounter.Status.Attacking, attackingActions);

            List<String> meetActions = Arrays.asList(ATTACK_ACTION, IGNORE_ACTION, MEET_ACTION);
            checkActions(captain, Encounter.Status.Awake, meetActions);
        }
    }

    private void checkActions(Encounter opponent, Encounter.Status status, List<String> allowedActions) {
        opponent.setStatus(status);
        List<Method> methods = opponent.getActions();
        for (String action : allActions) {
            if (allowedActions.contains(action)) {
                assertNotNull(action + " should be allowed for " + status, findMethodNamed(action, methods));
            } else {
                assertNull(action + " should not be allowed for " + status, findMethodNamed(action, methods));
            }
        }
    }

}