package net.lenni0451.mcstructs.text.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class TextComponentJsonUtils {

    public static boolean getBoolean(final JsonElement element, final String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) return element.getAsBoolean();
        else throw new JsonSyntaxException("Expected " + key + " to be a boolean, was " + element);
    }

    public static boolean getBoolean(final JsonObject object, final String key) {
        if (object.has(key)) return getBoolean(object.get(key), key);
        else throw new JsonSyntaxException("Missing " + key + ", expected to find a boolean");
    }

    public static boolean getBoolean(final JsonObject object, final String key, final boolean fallback) {
        if (object.has(key)) return getBoolean(object, key);
        else return fallback;
    }


    public static int getInt(final JsonElement element, final String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) return element.getAsInt();
        else throw new JsonSyntaxException("Expected " + key + " to be a boolean, was " + element);
    }

    public static int getInt(final JsonObject object, final String key) {
        if (object.has(key)) return getInt(object.get(key), key);
        else throw new JsonSyntaxException("Missing " + key + ", expected to find a boolean");
    }

    public static int getInt(final JsonObject object, final String key, final int fallback) {
        if (object.has(key)) return getInt(object, key);
        else return fallback;
    }


    public static String getString(final JsonElement element, final String key) {
        if (element.isJsonPrimitive()) return element.getAsString();
        else throw new JsonSyntaxException("Expected " + key + " to be a string, was " + element);
    }

    public static String getString(final JsonObject object, final String key) {
        if (object.has(key)) return getString(object.get(key), key);
        else throw new JsonSyntaxException("Missing " + key + ", expected to find a string");
    }

    public static String getString(final JsonObject object, final String key, final String fallback) {
        if (object.has(key)) return getString(object, key);
        else return fallback;
    }


    public static JsonObject getJsonObject(final JsonElement element, final String key) {
        if (element.isJsonObject()) return element.getAsJsonObject();
        else throw new JsonSyntaxException("Expected " + key + " to be a JsonObject, was " + element);
    }

    public static JsonObject getJsonObject(final JsonObject object, final String key) {
        if (object.has(key)) return getJsonObject(object.get(key), key);
        else throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
    }

}
