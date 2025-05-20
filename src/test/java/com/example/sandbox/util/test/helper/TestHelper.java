package com.example.sandbox.util.test.helper;

import com.example.sandbox.util.swagger.definitions.Item;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.response.Response;

public class TestHelper {

    /**
     * Get {@link Item} from response
     *
     * @param response
     *         Response
     */
    public static Item convertCategoryMapToItem(Response response){
        Gson gson = new Gson();
        String jsonStr = response.getBody().asString();
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        JsonObject itemObject = jsonObject.getAsJsonObject("category");
        return gson.fromJson(itemObject, Item.class);
    }

    /**
     * Get {@link Item} from response
     *
     * @param response
     *         Response
     */
    public static Item convertTagsMapToItem(Response response){
        Gson gson = new Gson();


        String jsonStr = response.getBody().asString();
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        JsonObject itemObject = jsonObject.getAsJsonObject("tags");
        JsonArray jsonArray = (JsonArray) jsonObject.get("items");

        return gson.fromJson(jsonArray.get(0), Item.class);
    }
}
