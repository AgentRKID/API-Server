package cc.nuplex.api.config;

import cc.nuplex.api.Application;
import cc.nuplex.api.config.mongo.MongoSettings;
import cc.nuplex.api.config.spark.SparkSettings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.*;

public class Configuration {

    private static final File directory = new File("config");
    private static final File config = new File(directory + File.separator + "config.json");

    @Getter private boolean debug;

    @Getter private final SparkSettings sparkSettings = new SparkSettings();
    @Getter private final MongoSettings mongoSettings = new MongoSettings();

    public boolean load() {
        try {
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    Application.LOGGER.info("Created Configuration Directory.");
                } else {
                    Application.LOGGER.warning("Failed to create Configuration Directory");
                }
            }

            if (!config.exists()) {
                Application.getInstance().stop();
                Application.LOGGER.info("***********************************************");
                Application.LOGGER.info("Looks like its the first time this is being ran, giving you time to configure.");
                Application.LOGGER.info("***********************************************");
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(config));
            JsonElement element = Application.JSON_PARSER.parse(reader);
            reader.close();

            if (element.isJsonNull() || !element.isJsonObject()) {
                Application.getInstance().stop();
                return false;
            }

            JsonObject object = element.getAsJsonObject();

            this.debug = object.get("debug").getAsBoolean();

            this.sparkSettings.load(object.get("spark-settings").getAsJsonObject());
            this.mongoSettings.load(object.get("mongo-settings").getAsJsonObject());

            Application.LOGGER.info("Finished loading configuration.");
        } catch (Exception ex) {
            Application.getInstance().stop();
        }
        return true;
    }

    public void save() {
        JsonObject object = new JsonObject();

        object.addProperty("debug", this.debug);

        object.add("spark-settings", this.sparkSettings.save());
        object.add("mongo-settings", this.mongoSettings.save());

        try {
            if (!config.exists() && config.createNewFile()) {
                Application.LOGGER.info("Created Configuration File");
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(config));
            writer.write(Application.GSON.toJson(Application.GSON.toJsonTree(object)));
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
