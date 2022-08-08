package com.github.DiachenkoMD.entities.adapters;

import com.github.DiachenkoMD.entities.enums.CarSegments;
import com.github.DiachenkoMD.entities.enums.Cities;
import com.github.DiachenkoMD.entities.enums.DBCoupled;
import com.google.gson.*;

import java.lang.reflect.Type;

public class DBCoupledAdapter implements JsonSerializer<DBCoupled>, JsonDeserializer<DBCoupled>{
    @Override
    public JsonElement serialize(DBCoupled dbCoupled, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dbCoupled.id());
    }

    @Override
    public DBCoupled deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(type == CarSegments.class){
            return CarSegments.getById(jsonElement.getAsInt());
        }else if(type == Cities.class){
            return Cities.getById(jsonElement.getAsInt());
        }

        return null;
    }
}
