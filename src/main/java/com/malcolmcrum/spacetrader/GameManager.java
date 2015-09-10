package com.malcolmcrum.spacetrader;

import com.malcolmcrum.spacetrader.GameStates.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Eventually will handle multiple games. Right now, just one.
 * Created by Malcolm on 9/10/2015.
 */
public class GameManager {
    Game game;
    GameState state;

    public GameManager() {
        newGame();
    }

    public void newGame() {
        game = new Game();
        state = game.startNewGame("Billy Bob", 5, 5, 5, 5, Difficulty.Normal);
    }


    public GameState getState() {
        return state;
    }

    public Galaxy getGalaxy() {
        return game.getGalaxy();
    }
}
