package com.malcolmcrum.spacetrader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.malcolmcrum.spacetrader.GameStates.GameOver;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import com.malcolmcrum.spacetrader.Serializers.GalaxySerializer;
import com.malcolmcrum.spacetrader.Serializers.InSystemSerializer;
import com.malcolmcrum.spacetrader.Serializers.ShipTypesSerializer;

import static spark.Spark.*;

/**
 * Created by Malcolm on 9/10/2015.
 */
public class TraderAPI {
    public TraderAPI() {
        GameManager manager = new GameManager();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(InSystem.class, new InSystemSerializer());
        builder.registerTypeAdapter(Galaxy.class, new GalaxySerializer());
        builder.registerTypeAdapter(GameManager.ShipTypes.class, new ShipTypesSerializer());
        builder.registerTypeAdapter(Captain.class, new CaptainSerializer());
        builder.registerTypeAdapter(Bank.class, new BankSerializer());
        builder.registerTypeAdapter(GameOver.class, new GameOverSerializer());
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        get("/", (request, response) -> {
            return gson.toJson(manager.getState());
        });

        get("/galaxy", (request, response) -> {
            return gson.toJson(manager.getGalaxy());
        });

        get("/ships", (request, response) -> {
            return gson.toJson(manager.getShipTypes());
        });

        get("/captain", (request, response) -> {
            return gson.toJson(manager.getCaptain());
        });

        get("/bank", (request, response) -> {
            return gson.toJson(manager.getBank());
        });

        post("/action/:action", (request, response) -> {
            String action = request.params(":action");
            if (manager.isActionValid(action, request.body())) {
                return gson.toJson(manager.action(action, request.body()));
            } else {
                return gson.toJson(new APIError(404, "Invalid action: " + action));
            }
        });

    }

    private class APIError {
        int code;
        String message;
        APIError(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
