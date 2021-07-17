package cc.nuplex.api.config.mongo;

import cc.nuplex.api.util.json.JsonHandler;
import com.google.gson.JsonObject;
import lombok.Getter;

public class MongoSettings implements JsonHandler<JsonObject> {

    @Getter private String host = "localhost";
    @Getter private int port = 27017;
    @Getter private String db = "API";

    @Override
    public void load(JsonObject object) {
        this.host = object.get("host").getAsString();
        this.port = object.get("port").getAsInt();
        this.db = object.get("db").getAsString();
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();

        object.addProperty("host", this.host);
        object.addProperty("port", this.port);
        object.addProperty("db", this.db);

        return object;
    }

}
