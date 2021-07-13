package cc.nuplex.api.config.mongo;

import cc.nuplex.api.util.json.JsonHandler;
import com.google.gson.JsonObject;
import lombok.Getter;

public class MongoSettings implements JsonHandler<JsonObject> {

    @Getter private String uri = "mongodb://localhost:27017";
    @Getter private String db = "API";

    @Override
    public void load(JsonObject object) {
        this.uri = object.get("uri").getAsString();
        this.db = object.get("db").getAsString();
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();

        object.addProperty("uri", this.uri);
        object.addProperty("db", this.db);

        return object;
    }

}
