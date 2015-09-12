package com.malcolmcrum.spacetrader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Malcolm on 9/11/2015.
 */
public class BankSerializer implements JsonSerializer {
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        assert(type == Bank.class);
        Bank bank = (Bank)o;

        JsonObject json = new JsonObject();
        json.addProperty("debt", bank.getDebt());
        json.addProperty("maxDebt", bank.maxLoan());
        json.addProperty("hasInsurance", bank.hasInsurance());
        json.addProperty("noClaim", bank.getDaysWithoutClaim());
        json.addProperty("insuranceDailyCost", bank.getInsuranceCost());

        return json;
    }
}
