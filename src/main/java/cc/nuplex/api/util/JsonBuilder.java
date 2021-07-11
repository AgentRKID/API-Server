package cc.nuplex.api.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {

    private final JsonObject element = new JsonObject();

    public JsonBuilder add(String key, int value) {
        element.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, String value) {
        element.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, boolean value) {
        element.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, long value) {
        element.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, JsonElement element) {
        this.element.add(key, element);
        return this;
    }

    public JsonObject build() {
        return element;
    }

}
