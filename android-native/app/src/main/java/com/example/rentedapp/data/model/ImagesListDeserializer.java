package com.example.rentedapp.data.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ImagesListDeserializer implements JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<String> list = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    list.add(element.getAsString());
                } else if (element.isJsonObject()) {
                    JsonElement urlElement = element.getAsJsonObject().get("url");
                    if (urlElement != null && urlElement.isJsonPrimitive()) {
                        list.add(urlElement.getAsString());
                    }
                }
            }
        }
        return list;
    }
}
