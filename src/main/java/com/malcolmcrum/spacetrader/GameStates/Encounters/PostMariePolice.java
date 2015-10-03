package com.malcolmcrum.spacetrader.GameStates.Encounters;

import com.malcolmcrum.spacetrader.*;
import com.malcolmcrum.spacetrader.GameStates.GameState;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import com.malcolmcrum.spacetrader.GameStates.Transit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Malcolm on 9/6/2015.
 */
public class PostMariePolice extends Police {
    private static final Logger logger = LoggerFactory.getLogger(PostMariePolice.class);


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
            logger.error("Method does not exist: " + e.getMessage());
        }
        return actions;
    }

    public GameState actionYield() {
        if (quests.isWildOnBoard() || quests.isReactorOnBoard()) {
            arrestPlayer();
            return new InSystem(game, transit.getDestination());
        } else {
            if (captain.policeRecord.is(PoliceRecord.Status.Dubious)) { // TODO: should this be negated?
                captain.policeRecord.make(PoliceRecord.Status.Dubious);
                // TODO: should this be outside the if?
                game.getShip().removeCargo(TradeItem.Narcotics, game.getShip().getCargoCount(TradeItem.Narcotics));
                game.getShip().removeCargo(TradeItem.Firearms, game.getShip().getCargoCount(TradeItem.Firearms));
                game.addAlert(Alert.YieldNarcotics);
            }
        }
        return this;
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
