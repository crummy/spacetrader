package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import com.malcolmcrum.spacetrader.News;

import static com.malcolmcrum.spacetrader.Utils.Pluralize;

/**
 * Created by Malcolm on 9/6/2015.
 */
public class Ahab extends FamousCaptain {
    public Ahab(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String getEncounterDescription() {
        String clicks = Pluralize(transit.getClicksRemaining(), "click");
        String destination = transit.getDestination().getName();
        String ship = opponent.getName();
        return "At " + clicks + " from " + destination + ", you encounter the famous Captain Ahab.";
    }

    @Override
    void initialAttack() {
        super.initialAttack();
        game.getNews().addNotableEvent(News.NotableEvent.CaptainAhabAttacked);
    }

    @Override
    protected GameState destroyedOpponent() {
        game.getNews().replaceEvent(News.NotableEvent.CaptainAhabAttacked, News.NotableEvent.CaptainAhabDestroyed);
        return super.destroyedOpponent();
    }
}
