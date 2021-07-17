package cc.nuplex.api;

import cc.nuplex.api.config.Configuration;
import cc.nuplex.api.config.mongo.MongoSettings;
import cc.nuplex.api.config.spark.SparkSettings;
import cc.nuplex.api.endpoint.EndpointController;
import cc.nuplex.api.profile.ProfileManager;
import cc.nuplex.api.storage.Mongo;
import cc.nuplex.api.util.log.LogFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import spark.Spark;
import spark.debug.DebugScreen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Application {

    public static ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static final Logger LOGGER = Logger.getLogger("API");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final JsonParser JSON_PARSER = new JsonParser();

    @Getter private static Application instance;

    @Getter private final Configuration configuration;

    @Getter private Mongo mongo;
    @Getter private MongoDatabase database;

    @Getter private ProfileManager profileManager;
    @Getter private EndpointController endpointService;

    public static void main(String[] args) {
        new Application();
    }


    public Application() {
        // Remove parent handler & use our own.
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogFormat());

        long start = System.currentTimeMillis();

        instance = this;

        this.configuration = new Configuration();

        // Configuration failed to load, lets not start anything else.
        if (!this.configuration.load()) {
            return;
        }

        SparkSettings sparkSettings = this.configuration.getSparkSettings();

        Spark.port(sparkSettings.getPort());

        // Configuration for the Multi-Threading in Spark.
        if (sparkSettings.getMaxThreads() > 1
                && sparkSettings.getMinThreads() > 1
                && sparkSettings.getThreadTimeout() > 1) {
            Spark.threadPool(sparkSettings.getMaxThreads(), sparkSettings.getMinThreads(), sparkSettings.getThreadTimeout());
        }

        // Enable Debug screen for Spark
        if (sparkSettings.isDebug()) {
            DebugScreen.enableDebugScreen();
        }

        MongoSettings mongoSettings = this.configuration.getMongoSettings();

        this.mongo = new Mongo();

        this.mongo.connect(mongoSettings.getHost(), mongoSettings.getPort());
        this.database = this.mongo.getClient().getDatabase(mongoSettings.getDb());

        //this.rankManager = new RankManager();
        this.profileManager = new ProfileManager();
        this.endpointService = new EndpointController();

        LOGGER.info("Started Web API in " + (System.currentTimeMillis() - start) + "ms!");
    }

    public void stop() {
        // Stop Spark
        Spark.stop();

        // Completely save the configuration
        this.configuration.save();

        // Stop all managers
        if (this.profileManager != null) this.profileManager.stop();

        // Close mongo connection
        if (this.mongo != null) this.mongo.close();
    }

}
