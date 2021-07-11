package cc.nuplex.api;

import cc.nuplex.api.service.EndpointControllerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import spark.Spark;

public class Application {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter private static Application instance;

    @Getter private final EndpointControllerService endpointService;

    public Application() {
        instance = this;

        Spark.port(90);

        this.endpointService = new EndpointControllerService();
    }

    public static void main(String[] args) {
        new Application();
    }

}
