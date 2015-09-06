package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.Transit;


/**
 * Created by Malcolm on 9/6/2015.
 */
public class PostMariePolice extends Police {
    PostMariePolice(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public String descriptionAwake() {
        return "'We know you removed illegal goods from the Marie Celeste. You must give them up at once!'";
    }

    /**
     * Decide whether to change tactics (e.g. flee)
     */
    protected void opponentHasBeenDamaged() {
        // The Post-Marie police don't flee.
    }
}
