package cc.nuplex.api.endpoint;

import cc.nuplex.api.endpoint.filter.Filter;
import cc.nuplex.api.endpoint.route.RouteFunction;
import cc.nuplex.api.util.json.JsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Endpoint {

    public static final JsonObject SUCCESS = new JsonBuilder().add("success", true).build();

    @Getter private final String endpoint;

    @Getter private final Map<String, RouteFunction> routes = new HashMap<>();
    @Getter private final Map<String, List<Filter>> filters = new HashMap<>();
    @Getter private final Map<String, Type> routeTypes = new HashMap<>();

    public Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void addRoute(String endpoint, Type type, RouteFunction route, Filter... filters) {
        this.routes.put(endpoint, route);
        this.routeTypes.put(endpoint, type);

        for (Filter filter : filters) {
            this.filters.computeIfAbsent(endpoint, v -> new ArrayList<>()).add(filter);
        }
    }

    public enum Type {
        GET,
        POST,
        DELETE,
    }

}
