package com.malcolmcrum.spacetrader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.malcolmcrum.spacetrader.GameStates.GameOver;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import com.malcolmcrum.spacetrader.Serializers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

/**
 * Created by Malcolm on 9/10/2015.
 */
public class TraderAPI {
    private static final Logger logger = LoggerFactory.getLogger(TraderAPI.class);


    public TraderAPI() {
        GameManager manager = new GameManager();

        enableCORS("*", "*", "*");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(InSystem.class, new InSystemSerializer());
        builder.registerTypeAdapter(Galaxy.class, new GalaxySerializer());
        builder.registerTypeAdapter(GameManager.ShipTypes.class, new ShipTypesSerializer());
        builder.registerTypeAdapter(Captain.class, new CaptainSerializer());
        builder.registerTypeAdapter(Bank.class, new BankSerializer());
        builder.registerTypeAdapter(GameOver.class, new GameOverSerializer());
        builder.registerTypeAdapter(Game.class, new GameSerializer());
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        get("games", (request, response) -> {
            return gson.toJson(manager.getGames());
        });

        post("games/new", (request, response) -> {
            String name = request.queryParams("name");
            int fighter = Integer.parseInt(request.queryParams("fighter"));
            int pilot = Integer.parseInt(request.queryParams("pilot"));
            int trader = Integer.parseInt(request.queryParams("trader"));
            int engineer = Integer.parseInt(request.queryParams("engineer"));
            int difficulty = Integer.parseInt(request.queryParams("difficulty"));
            return gson.toJson(manager.newGame(name, fighter, pilot, trader, engineer, difficulty));
        });

        get("games/:id/state", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getState(id));
        });

        get("games/:id/galaxy", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getGalaxy(id));
        });

        get("games/:id/ships", (request, response) -> {
            return gson.toJson(manager.getShipTypes());
        });

        get("games/:id/captain", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getCaptain(id));
        });

        get("games/:id/bank", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getBank(id));
        });

        post("games/:id/action/:action", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            String action = request.params(":action");
            if (manager.isActionValid(id, action, request.body())) {
                return gson.toJson(manager.action(id, action, request.body()));
            } else {
                throw new APIError("No action found called " + action);
            }
        });

        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.status(400);
            response.body(gson.toJson(new APIError(e.getMessage())));
            logger.error("Error occurred", e);
        });

        before((request, response) -> {
           logger.info("request from " + request.ip() + ": " + request.requestMethod() + " " + request.url() + " (" + request.body() + ")");
        });

        after((request, response) -> {
            response.type("application/json");
        });

    }

    private class APIError extends IllegalArgumentException {
        int code;
        String message;
        APIError(int code, String message) {
            this.code = code;
            this.message = message;
        }
        APIError(String message) {
            this.code = 400;
            this.message = message;
        }
    }

    private static void enableCORS(final String origin, final String methods, final String headers) {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}
