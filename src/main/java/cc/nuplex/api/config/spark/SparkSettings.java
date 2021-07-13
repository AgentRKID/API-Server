package cc.nuplex.api.config.spark;

import cc.nuplex.api.util.json.JsonHandler;
import com.google.gson.JsonObject;
import lombok.Getter;

public class SparkSettings implements JsonHandler<JsonObject> {

    @Getter private int port = 90;
    @Getter private int maxThreads = -1;
    @Getter private int minThreads = -1;
    @Getter private int threadTimeout = -1;

    @Override
    public void load(JsonObject object) {
        this.port = object.get("port").getAsInt();
        this.maxThreads = object.get("max-threads").getAsInt();
        this.minThreads = object.get("min-threads").getAsInt();
        this.threadTimeout = object.get("thread-timeout-ms").getAsInt();
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();

        object.addProperty("port", this.port);
        object.addProperty("max-threads", this.maxThreads);
        object.addProperty("min-threads", this.minThreads);
        object.addProperty("thread-timeout-ms", this.threadTimeout);

        return object;
    }

}
