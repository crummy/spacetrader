package com.malcolmcrum.spacetrader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args )
    {
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.info("Initializing Space Trader");
        Game game = new Game();
        game.startNewGame("Billy Bob", 5, 5, 5, 5, Difficulty.Normal);
    }
}
