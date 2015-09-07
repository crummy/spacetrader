package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.Game;
import com.malcolmcrum.spacetrader.GameStates.Transit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Malcolm on 9/6/2015.
 */
public class PostMariePolice extends Police {
    public PostMariePolice(Game game, Transit transit) {
        super(game, transit);
    }

    @Override
    public List<Method> getActions() {
        List<Method> actions = new ArrayList<>();
        try {
            switch(opponentStatus) {
                case Ignoring:
                    break;
                case Awake:
                    actions.add(PostMariePolice.class.getMethod("actionAttack"));
                    actions.add(PostMariePolice.class.getMethod("actionFlee"));
                    actions.add(PostMariePolice.class.getMethod("actionYield"));
                    actions.add(PostMariePolice.class.getMethod("actionBribe"));
                    break;
                case Attacking:
                    actions.add(PostMariePolice.class.getMethod("actionAttack"));
                    actions.add(PostMariePolice.class.getMethod("actionFlee"));
                    actions.add(PostMariePolice.class.getMethod("actionSurrender"));
                    break;
                case Fleeing:
                    actions.add(PostMariePolice.class.getMethod("actionAttack"));
                    actions.add(PostMariePolice.class.getMethod("actionIgnore"));
                    break;
                case Fled:
                    break;
                case Surrendered:
                    break;
                case Destroyed:
                    break;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return actions;
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
