package cc.nuplex.api.endpoint;

import cc.nuplex.api.endpoint.filter.Filter;
import cc.nuplex.api.endpoint.impl.ProfileEndpoint;
import cc.nuplex.api.endpoint.impl.StatusEndpoint;
import cc.nuplex.api.endpoint.route.RouteFunction;
import cc.nuplex.api.endpoint.transformer.JsonTransformer;
import lombok.Getter;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EndpointController {

    private static final JsonTransformer JSON_TRANSFORMER = new JsonTransformer();

    @Getter private final List<Endpoint> endpoints = new ArrayList<>();

    public EndpointController() {
        /*Collection<Class<?>> classes = ClassUtils.getClassesInPackage(Application.class, Application.class.getPackage().getName());

        for (Class<?> clazz : classes) {
            if (clazz.getSuperclass() == Endpoint.class) {
                try {
                    Endpoint endpoint = (Endpoint) clazz.getConstructor().newInstance();
                    this.endpoints.add(endpoint);
                } catch (Exception ex) {
                    System.out.println("Failed to create Endpoint for " + clazz.getSimpleName());
                }
            }
        }*/

        this.endpoints.add(new StatusEndpoint());
        this.endpoints.add(new ProfileEndpoint());

        // Set default response transformer,
        // everything should be returned as JSON.
        Spark.defaultResponseTransformer(JSON_TRANSFORMER);

        // Configure the 404 Page
        Spark.notFound((request, response) -> EndpointErrors.INVALID_PAGE);

        // Load all found endpoints
        for (Endpoint endpoint : this.endpoints) {
            for (Map.Entry<String, RouteFunction> entry : endpoint.getRoutes().entrySet()) {
                Endpoint.Type type = endpoint.getRouteTypes().get(entry.getKey());
                String completeEndpoint = endpoint.getEndpoint() + "/" + entry.getKey();

                switch(type) {
                    case GET: {
                        Spark.get(completeEndpoint, (request, response) -> entry.getValue().handle(request, response));
                        break;
                    }

                    case POST: {
                        Spark.post(completeEndpoint, (request, response) -> entry.getValue().handle(request, response));
                        break;
                    }

                    case DELETE: {
                        Spark.delete(completeEndpoint, (request, response) -> entry.getValue().handle(request, response));
                        break;
                    }
                }

                List<Filter> filters = endpoint.getFilters().get(entry.getKey());

                // Load filters
                if (filters != null && !filters.isEmpty()) {
                    for (Filter filter : filters) {
                        if (filter.isBefore()) {
                            Spark.before(completeEndpoint, filter::handle);
                        } else {
                            Spark.after(completeEndpoint, filter::handle);
                        }
                    }
                }
            }
        }
    }

}
