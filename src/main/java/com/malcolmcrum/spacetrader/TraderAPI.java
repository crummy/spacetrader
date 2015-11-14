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
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        get("games", (request, response) -> {
            return gson.toJson(manager.getGames());
        });

        get("game/:id/state", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getState(id));
        });

        get("game/:id/galaxy", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getGalaxy(id));
        });

        get("game/:id/ships", (request, response) -> {
            return gson.toJson(manager.getShipTypes());
        });

        get("game/:id/captain", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getCaptain(id));
        });

        get("game/:id/bank", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            return gson.toJson(manager.getBank(id));
        });

        post("game/:id/action/:action", (request, response) -> {
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
        });

        before((request, response) -> {
           logger.info("request from " + request.ip() + ": " + request.requestMethod() + " " + request.url());
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
