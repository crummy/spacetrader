package com.malcolmcrum.spacetrader;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Malcolm on 8/31/2015.
 */
public class GameTest {
    @Test
    public void testStartNewGame() throws Exception {
        Game game = new Game();

        boolean success = game.startNewGame("Bill", 5, 5, 5, 5, Difficulty.Easy);
        assertTrue("Game wouldn't let us start with correct skillpoints", success);
        assertTrue("lottery winner", game.currentSystem.getSpecialEvent() == SolarSystem.SpecialEvent.LotteryWinner);

        boolean negativeSkillGame = game.startNewGame("Bill", -1, 5, 5, 5, Difficulty.Easy);
        assertFalse("negative skills", negativeSkillGame);

        boolean missingSkills = game.startNewGame("Bill", 4, 5, 5, 5, Difficulty.Easy);
        assertFalse("missing skills", missingSkills);

        boolean extraSkills = game.startNewGame("Bill", 4, 9, 5, 5, Difficulty.Easy);
        assertFalse("extra skills", extraSkills);
    }
}