package cc.nuplex.api.service;

import cc.nuplex.api.Application;
import cc.nuplex.api.endpoint.Endpoint;
import cc.nuplex.api.endpoint.filter.Filter;
import cc.nuplex.api.endpoint.route.RouteFunction;
import cc.nuplex.api.endpoint.transformer.JsonTransformer;
import cc.nuplex.api.util.ClassUtils;
import lombok.Getter;
import spark.Spark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EndpointControllerService {

    private static final JsonTransformer JSON_TRANSFORMER = new JsonTransformer();

    @Getter private final List<Endpoint> endpoints = new ArrayList<>();

    public EndpointControllerService() {
        Collection<Class<?>> classes = ClassUtils.getClassesInPackage(Application.class, Application.class.getPackage().getName());

        for (Class<?> clazz : classes) {
            if (clazz.getSuperclass() == Endpoint.class) {
                try {
                    Endpoint endpoint = (Endpoint) clazz.getConstructor().newInstance();
                    this.endpoints.add(endpoint);
                } catch (Exception ex) {
                    System.out.println("Failed to create Endpoint for " + clazz.getSimpleName());
                }
            }
        }

        for (Endpoint endpoint : this.endpoints) {
            for (Map.Entry<String, RouteFunction> entry : endpoint.getRoutes().entrySet()) {
                Endpoint.Type type = endpoint.getRouteTypes().get(entry.getKey());
                String completeEndpoint = endpoint.getEndpoint() + "/" + entry.getKey();

                switch(type) {
                    case GET: {
                        Spark.get(completeEndpoint, (request, response) -> entry.getValue().handle(request, response), JSON_TRANSFORMER);
                        break;
                    }

                    case POST: {
                        Spark.post(completeEndpoint, (request, response) -> entry.getValue().handle(request, response), JSON_TRANSFORMER);
                        break;
                    }

                    case DELETE: {
                        Spark.delete(completeEndpoint, (request, response) -> entry.getValue().handle(request, response), JSON_TRANSFORMER);
                        break;
                    }
                }

                List<Filter> filters = endpoint.getFilters().get(entry.getKey());

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
