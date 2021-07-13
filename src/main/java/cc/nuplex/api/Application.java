package cc.nuplex.api;

import cc.nuplex.api.endpoint.EndpointController;
import cc.nuplex.api.profile.ProfileManager;
import cc.nuplex.api.storage.Mongo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import spark.Spark;

import java.util.logging.Logger;

public class Application {

    public static final Logger LOGGER = Logger.getLogger("API");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter private static Application instance;

    @Getter private final Mongo mongo;
    @Getter private final MongoDatabase database;

    @Getter private final ProfileManager profileManager;
    @Getter private final EndpointController endpointService;

    public Application() {
        instance = this;

        Spark.port(90);

        this.mongo = new Mongo();
        this.mongo.connect("mongodb://localhost:27017");
        this.database = this.mongo.getClient().getDatabase("API");

        this.profileManager = new ProfileManager();
        this.endpointService = new EndpointController();
    }

    public static void main(String[] args) {
        new Application();
    }

}
