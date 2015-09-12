package com.malcolmcrum.spacetrader.GameStates;

import com.malcolmcrum.spacetrader.Game;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malcolm on 9/3/2015.
 */
public class GameOver extends GameState {
    private final endStatus status;

    public GameOver(Game game, endStatus status) {
        super(game);
        this.status = status;
    }

    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            actions.add(GameOver.class.getMethod("newGame"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
    }

    @Override
    public GameState init() {
        return this;
    }

    @Override
    public String getName() {
        return "GameOver";
    }

    public GameState newGame() {
        // TODO
        return this;
    }

    public int getScore() {
        int worth = game.getCaptain().getWorth();
        worth = (worth < 1000000 ? worth : 1000000 + ((worth - 1000000) / 10));
        int difficulty = game.getDifficulty().getValue();

        if (status == endStatus.Killed) {
            return (difficulty + 1)*(worth * 90) / 50000;
        } else if (status == endStatus.Retired) {
            return (difficulty + 1)*(worth * 95) / 50000;
        } else if (status == endStatus.RetiredOnMoon) {
            int d = ((difficulty + 1) * 100) - game.getDays();
            if (d < 0) {
                d = 0;
            }
            return (difficulty + 1) * ((worth + (d * 1000)) / 500);
        } else {
            return -1;
        }
    }

    public enum endStatus {
        Killed,
        Retired,
        RetiredOnMoon
    }
}
