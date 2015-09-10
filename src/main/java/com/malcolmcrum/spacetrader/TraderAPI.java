package com.malcolmcrum.spacetrader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.malcolmcrum.spacetrader.GameStates.InSystem;
import com.malcolmcrum.spacetrader.Serializers.InSystemSerializer;

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
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        get("/", (request, response) -> {
            return gson.toJson(manager.getState());
        });

        get("/galaxy", (request, response) -> {
            return gson.toJson(manager.getGalaxy());
        });

    }
}
